/*******************************************************************************
 * Copyright (c) 2011 Antonio García-Domínguez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Antonio García-Domínguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.eunit.dt.cmp.emf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSetSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.DiffResourceSet;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchResourceSet;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.emc.emf.AbstractEmfModel;
import org.eclipse.epsilon.emc.emf.EmfModelResourceSet;
import org.eclipse.epsilon.eol.models.IAdaptableModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eunit.extensions.IModelComparator;

/**
 * Model comparator for EMF models, using EMF Compare.
 */
public class EMFModelComparator implements IModelComparator {

	@Override
	public boolean canCompare(IModel m1, IModel m2) {
		return adaptToEMF(m1) != null && adaptToEMF(m2) != null;
	}

	@Override
	public Object compare(IModel m1, IModel m2) throws Exception {
		final AbstractEmfModel emfM1 = adaptToEMF(m1);
		final AbstractEmfModel emfM2 = adaptToEMF(m2);
		if (emfM1 == null || emfM2 == null) {
			throw new IllegalArgumentException("Cannot compare non-EMF models");			
		}

		// For later viewing, we need to ensure the models are saved. If they are not, just
		// store them to a temporary file.
		final ResourceSet myResourceSet = cloneToTmpFiles(emfM1.getResource().getResourceSet());
		final ResourceSet otherResourceSet = cloneToTmpFiles(emfM2.getResource().getResourceSet());

		final HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
		MatchResourceSet match = MatchService.doResourceSetMatch(myResourceSet, otherResourceSet, options);
		DiffResourceSet diff = DiffService.doDiff(match);
		boolean bAnyDifferences = false;
		for (DiffModel diffModel : diff.getDiffModels()) {
			if (!diffModel.getDifferences().isEmpty()) {
				bAnyDifferences = true;
			}
		}
		if (!bAnyDifferences) {
			return null;
		}

		ComparisonResourceSetSnapshot snap = DiffFactory.eINSTANCE.createComparisonResourceSetSnapshot();
		snap.setDate(new Date());
		snap.setDiffResourceSet(diff);
		snap.setMatchResourceSet(match);
		return snap;
	}

	private AbstractEmfModel adaptToEMF(IModel model) {
		if (model instanceof AbstractEmfModel) {
			return (AbstractEmfModel)model;
		}
		else if (model instanceof IAdaptableModel) {
			return ((IAdaptableModel)model).adaptTo(AbstractEmfModel.class);
		}
		else {
			return null;
		}
	}

	/**
	 * This method is used to take a snapshot of an entire resource set. Resources which
	 * do not have platform:// URIs are saved into temporary files. Cross-references are
	 * preserved: all non-platform:// URIs are rewritten to the proper temporary files.
	 */
	private ResourceSet cloneToTmpFiles(ResourceSet resourceSet) throws IOException {
		EcoreUtil.resolveAll(resourceSet);
		ResourceSet newResourceSet = new EmfModelResourceSet();

		// Save the original non-platform URIs
		final Map<Resource, URI> originalURIMap = new HashMap<Resource, URI>();
		for (Resource res : resourceSet.getResources()) {
			if ("platform".equals(res.getURI().scheme())) {
				// skip platform: models
				continue;
			}
			originalURIMap.put(res, res.getURI());
		}

		try {
			// Map each non-platform resource to a new temporary file
			for (Resource res : originalURIMap.keySet()) {
				File tmpFile = File.createTempFile(res.getURI().lastSegment() + ".", ".model");
				res.setURI(URI.createFileURI(tmpFile.getAbsolutePath()));
			}

			// Save all the non-platform resources and add them to the new resource set.
			// The platform: resources will be resolved implicitly by the comparison.
			final List<Resource> newResources = new ArrayList<Resource>(originalURIMap.size());
			for (Resource res : originalURIMap.keySet()) {
				res.save(Collections.EMPTY_MAP);
				final Resource newResource = newResourceSet.createResource(res.getURI());
				newResources.add(newResource);
			}

			// Reuse the metamodels loaded by the user in the new resource set, by
			// using the same package registry as in the original resource set.
			newResourceSet.setPackageRegistry(resourceSet.getPackageRegistry());

			// Load all the resources in the new resource set.
			for (Resource res : newResources) {
				res.load(null);
				EcoreUtil.resolveAll(res);
			}
		}
		finally {
			// Restore the original URIs to the original resource set
			for (Map.Entry<Resource, URI> entry : originalURIMap.entrySet()) {
				entry.getKey().setURI(entry.getValue());
			}
		}

		return newResourceSet;
	}

}

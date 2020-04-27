/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.emf.xml;

import java.util.Collection;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.eclipse.epsilon.common.util.CollectionUtil;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.common.util.StringUtil;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.execute.introspection.IReflectivePropertySetter;
import org.eclipse.epsilon.eol.execute.operations.contributors.IOperationContributorProvider;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

public class XmlModel extends EmfModel implements IOperationContributorProvider {
	
	public static final String PROPERTY_XSD_FILE = "xsdFile";
	protected MixedElementOperationContributor mixedElementOperationContributor = new MixedElementOperationContributor();
	
	protected String xsdFile = "";
	
	public XmlModel() {
	}
	
	@Override
	public IReflectivePropertySetter getPropertySetter() {
		return new XmlPropertySetter();
	}
	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		this.xsdFile = resolver.resolve(properties.getProperty(PROPERTY_XSD_FILE));
		super.load(properties, resolver);
	}
	
	@Override
	protected void determinePackagesFrom(ResourceSet resourceSet) throws EolModelLoadingException {
		super.determinePackagesFrom(resourceSet);
		
		if (xsdFile != null && xsdFile.endsWith("xsd")) {
			XSDEcoreBuilder xsdEcoreBuilder = new XSDEcoreBuilder();
			
		    Collection<?> ePackages = xsdEcoreBuilder.generate(URI.createFileURI(xsdFile));
		    
		    CollectionUtil.addCapacityIfArrayList(packages, ePackages.size());
		    for (Object eObj : ePackages) {
		    	EPackage ePackage = (EPackage) eObj;
		    	if (StringUtil.isEmpty(ePackage.getNsURI())) {
		    		ePackage.setNsURI(ePackage.getName());
		    	}
		    	packages.add(ePackage);
		    }
		}
	}
	
	@Override
	protected ResourceSet createResourceSet() {
		return new CachedResourceSet() {
			@Override
			public Resource createNewResource(URI uri, String contentType) {
				return new GenericXMLResourceFactoryImpl().createResource(uri);
			}
		};
	}

	@Override
	public OperationContributor getOperationContributor() {
		return mixedElementOperationContributor;
	}
}

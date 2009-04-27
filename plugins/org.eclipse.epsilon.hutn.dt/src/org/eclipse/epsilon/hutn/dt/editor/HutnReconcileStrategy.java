/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.hutn.dt.editor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.epsilon.commons.parse.problem.ParseProblem;
import org.eclipse.epsilon.hutn.HutnModule;
import org.eclipse.epsilon.hutn.IHutnModule;
import org.eclipse.epsilon.hutn.dt.util.WorkspaceUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class HutnReconcileStrategy implements IReconcilingStrategy {

	private final HutnEditor  editor;
	private final HutnKeywordManager keywordManager;
	
	private IDocument document;
	
	public HutnReconcileStrategy(HutnEditor editor, HutnScanner scanner) {
		this.editor = editor;
		this.keywordManager = new HutnKeywordManager(scanner);
	}
	
	private List<ParseProblem> collectHutnParseErrors(IRegion partition) {
		try {
			final String text = document.get(partition.getOffset(), partition.getLength());
			
			final IHutnModule hutnModule = new HutnModule();
			
			if (editor.getEditorInput() instanceof FileEditorInput) {	
				final IFile hutn = ((FileEditorInput)editor.getEditorInput()).getFile();
				
				hutnModule.setConfigFileDirectory(WorkspaceUtil.getAbsolutePath(hutn.getParent()));
			}
			
			try {
				hutnModule.parse(text);
			} catch (Exception e) {}
			
			if (keywordManager.keywordsHaveChanged(hutnModule.getNsUris())) {
				keywordManager.updateKeywordsFrom(hutnModule.getNsUris());
			}
			
			return hutnModule.getParseProblems();
		
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new LinkedList<ParseProblem>();
		}
	}

	private void markParseErrors(IRegion partition) {
		try {
			final List<ParseProblem> errors = collectHutnParseErrors(partition);
			
			if (editor.getEditorInput() instanceof FileEditorInput) {		
				final IFile file = ((FileEditorInput)editor.getEditorInput()).getFile();
				file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
				
				for (ParseProblem error : errors) {
					final Map<String, Object> attribute = new HashMap<String, Object>();
					attribute.put(IMarker.LINE_NUMBER, error.getLine());
					attribute.put(IMarker.MESSAGE,     error.getReason());				
					attribute.put(IMarker.SEVERITY,    error.getSeverity() == ParseProblem.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
					
					MarkerUtilities.createMarker(file, attribute, IMarker.PROBLEM);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reconcile(IRegion partition) {
		markParseErrors(partition);
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		throw new UnsupportedOperationException("Incremental reconciliation not supported");
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}
}

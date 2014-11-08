/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.evl.execute;

import java.util.ArrayList;

import org.eclipse.epsilon.evl.dom.Constraint;

public class UnsatisfiedConstraint {
	
	protected Constraint constraint;
	protected String message;
	protected Object instance;
	protected ArrayList<FixInstance> fixes = new ArrayList<FixInstance>();
	protected boolean fixed = false;
	
	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public UnsatisfiedConstraint() {
		super();
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object result) {
		this.instance = result;
	}
	
	public ArrayList<FixInstance> getFixes() {
		return fixes;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
}
/*********************************************************************
 * Copyright (c) 2020 The University of York.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.epsilon.evl.dom;

import org.eclipse.epsilon.eol.dom.IEolVisitor;

public interface IEvlVisitor extends IEolVisitor {

	public void visit(ConstraintContext constraintContext);

	public void visit(Constraint constraint);

	public void visit(Fix fix);
	
}
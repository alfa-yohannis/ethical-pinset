/*********************************************************************
 * Copyright (c) 2020 The University of York.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.epsilon.evl.parse;

import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.erl.parse.ErlUnparser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.dom.IEvlVisitor;

public class EvlUnparser extends ErlUnparser implements IEvlVisitor {
	
	@Override
	protected void unparseRules() {
		((EvlModule) module).getDeclaredConstraintContexts().forEach(c -> {c.accept(this); newline();});
	}
	
	public String unparse(EvlModule module) {
		return super.unparse(module);
	}
	
	@Override
	public void visit(ConstraintContext constraintContext) {
		if (constraintContext.getTypeExpression() != null) {
			unparseAnnotations(constraintContext);
			buffer.append("context ");
			constraintContext.getTypeExpression().accept(this);
			buffer.append(" {");
			newline();
			newline();
			indentation++;
			print("guard", constraintContext.getGuardBlock());
			constraintContext.getConstraints().forEach(c -> {c.accept(this); newline();});
			indentation--;
			buffer.append("}");
			newline();
		}
		else {
			constraintContext.getConstraints().forEach(c -> {c.accept(this); newline();});
		}
	}

	@Override
	public void visit(Constraint constraint) {
		unparseAnnotations(constraint);
		indent();
		buffer.append(constraint.isCritique() ? "critique" : "constraint");
		space();
		buffer.append(constraint.getName());
		space();
		buffer.append("{");
		newline();
		indentation++;
		
		print("guard", constraint.getGuardBlock());
		print("check", constraint.getCheckBlock());
		print("message", constraint.getMessageBlock());
		
		constraint.getFixes().forEach(f -> f.accept(this));
		
		indentation--;
		newline();
		indent();
		buffer.append("}");
		
	}
	
	protected void print(String role, ExecutableBlock<?> executableBlock) {
		if (executableBlock != null) {
			newline();
			indent();
			buffer.append(role);
			executableBlock.accept(this);
			newline();
		}
	}
	
	@Override
	public void visit(ExecutableBlock<?> executableBlock) {
		if (executableBlock.getBody() instanceof StatementBlock) {
			space();
			((StatementBlock) executableBlock.getBody()).accept(this);
		}
		else {
			buffer.append(": ");
			((Expression) executableBlock.getBody()).accept(this);
		}
	}
	
	@Override
	public void visit(Fix fix) {
		newline();
		indent();
		buffer.append("fix {");
		indentation++;
		print("guard", fix.getGuardBlock());
		print("title", fix.getTitleBlock());
		print("do", fix.getBodyBlock());
		indentation--;
		indent();
		buffer.append("}");
		newline();
	}

}
/*********************************************************************
* Copyright (c) 2008 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.epsilon.eol.dom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.common.module.IModule;
import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.EolTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolMapType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.eol.types.EolNativeType;
import org.eclipse.epsilon.eol.types.EolNoType;
import org.eclipse.epsilon.eol.types.EolPrimitiveType;
import org.eclipse.epsilon.eol.types.EolTupleType;
import org.eclipse.epsilon.eol.types.EolType;

public class TypeExpression extends Expression {
	
	protected EolType type = EolAnyType.Instance;
	protected String name;
	protected List<TypeExpression> parameterTypeExpressions = new ArrayList<>();
	protected StringLiteral nativeType;
	
	public TypeExpression() {}
	
	public TypeExpression(String typeName) {
		setName(typeName);
	}
	
	@Override
	public void build(AST cst, IModule module) {
		super.build(cst, module);
		
		setName(cst.getText());
		for (AST child : cst.getChildren()) {
			ModuleElement moduleElement = module.createAst(child, this);
			
			if (moduleElement instanceof TypeExpression) {
				parameterTypeExpressions.add((TypeExpression) moduleElement);
			}
			else if ("Native".equals(name)) {
				nativeType = (StringLiteral) moduleElement;	
			}
		}
	}
	
	@Override
	public EolType execute(IEolContext context) throws EolRuntimeException {
		if (type != null) return type;
		
		if ("Native".equals(getName())) {
			return new EolNativeType(nativeType, context);
		}
		
		try {
			return new EolModelElementType(name, context);
		}
		catch (EolModelNotFoundException | EolModelElementTypeNotFoundException ex) {
			throw new EolTypeNotFoundException(getName(), this);
		}
	}
	
	public String getName() {
		return name;
	}
		
	/**
	 * 
	 * @param name
	 * @return
	 * @since 2.1
	 */
	public static EolType getType(String name) {
		switch (name) {
			case "Integer":
				return EolPrimitiveType.Integer;
			case "Any":
				return EolAnyType.Instance;
			case "Boolean":
				return EolPrimitiveType.Boolean;
			case "String":
				return EolPrimitiveType.String;
			case "Real":
				return EolPrimitiveType.Real;
			case "Map": case "ConcurrentMap":
				return new EolMapType(name);
			case "List": name = "Sequence";
			case "Bag": case "Sequence": case "Collection":
			case "Set": case "OrderedSet":
			case "ConcurrentSet": case "ConcurrentBag":
				return new EolCollectionType(name);
			case "Nothing": case "None":
				return EolNoType.Instance;
			case "Tuple":
				return new EolTupleType();
			default:
				return null;
		}
	}
	
	public void setName(String name) {
		this.type = getType(this.name = name);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+": "+getName();
	}

	public List<TypeExpression> getParameterTypeExpressions() {
		return parameterTypeExpressions;
	}
	
	public StringLiteral getNativeType() {
		return nativeType;
	}
	
	public void accept(IEolVisitor visitor) {
		visitor.visit(this);
	}
}

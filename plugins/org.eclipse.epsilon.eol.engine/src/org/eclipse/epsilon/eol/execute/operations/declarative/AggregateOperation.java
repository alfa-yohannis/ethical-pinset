/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.eol.execute.operations.declarative;

import java.util.Collection;
import java.util.List;
import org.eclipse.epsilon.common.util.CollectionUtil;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.Parameter;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.FrameStack;
import org.eclipse.epsilon.eol.execute.context.FrameType;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.types.EolMap;
import org.eclipse.epsilon.eol.types.EolType;

public class AggregateOperation extends FirstOrderOperation {
	
	@Override
	public EolMap<?, ?> execute(Object target,
			NameExpression operationNameExpression, List<Parameter> iterators,
			List<Expression> expressions, IEolContext context)
			throws EolRuntimeException {

		Collection<?> source = CollectionUtil.asCollection(target);
		EolMap<Object, Object> result = new EolMap<>();
		
		if (source.isEmpty()) return result;
		
		Parameter iterator = iterators.get(0);
		EolType iteratorType = iterator.getType(context);
		Expression keyExpression = expressions.get(0);
		Expression valueExpression = expressions.get(1);
		Expression initialExpression = expressions.size() > 2 ? expressions.get(2) : null;
		
		FrameStack scope = context.getFrameStack();
		String iteratorName = iterator.getName();
		
		for (Object item : source) {
			if (iteratorType == null || iteratorType.isKind(item)) {
				scope.enterLocal(FrameType.UNPROTECTED, keyExpression,
					Variable.createReadOnlyVariable(iteratorName, item)
				);
				
				Object total, keyResult = context.getExecutorFactory().execute(keyExpression, context);
				
				if (result.containsKey(keyResult)) {
					total = result.get(keyResult);
				}
				else {
					total = context.getExecutorFactory().execute(initialExpression, context);
				}
				
				scope.put(Variable.createReadOnlyVariable("total", total));
				Object valueResult = context.getExecutorFactory().execute(valueExpression, context);
				result.put(keyResult, valueResult);
				scope.leaveLocal(keyExpression);
			}
		}
		
		return result;
	}

	@Override
	public final Object execute(Object target, Variable iterator, Expression expression,
			IEolContext context) throws EolRuntimeException {
		return null;
	}
}

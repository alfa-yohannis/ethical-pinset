/*******************************************************************************
 * Copyright (c) 2009 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************
 *
 * $Id$
 */
package suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.strong2strong.AutomaticallyCopyModelElementsThatHaveNoStrategy;
import test.strong2strong.MigrateToDifferentType;
import test.strong2strong.Simple;
import test.strong2strong.SingleGuard;

@RunWith(Suite.class)
@SuiteClasses({Simple.class, AutomaticallyCopyModelElementsThatHaveNoStrategy.class,
               MigrateToDifferentType.class,
               SingleGuard.class})
public class EpsilonMigrationLanguageAcceptanceTestSuite {
	public static Test suite() {
		return new JUnit4TestAdapter(EpsilonMigrationLanguageAcceptanceTestSuite.class);
	}
}

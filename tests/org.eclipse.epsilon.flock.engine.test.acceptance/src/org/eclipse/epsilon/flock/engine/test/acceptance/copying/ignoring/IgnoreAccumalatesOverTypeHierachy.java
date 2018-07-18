/*******************************************************************************
 * Copyright (c) 2011 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.flock.engine.test.acceptance.copying.ignoring;

import org.eclipse.epsilon.flock.engine.test.acceptance.util.FlockAcceptanceTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class IgnoreAccumalatesOverTypeHierachy extends FlockAcceptanceTest {

	private static final String strategy = "migrate NamedElement ignoring name" + "\n" +
	                                       "migrate Family ignoring numberOfChildren";

	private static final String originalModel = "Families {"                  +
	                                            "	Family {"                 +
	                                            "		name: \"The Smiths\"" +
	                                            "       members: Person \"p\" { name: \"John\" } " +
	                                            "		numberOfChildren: 2"  +
	                                            "	}"                        +
	                                            "}";

	@BeforeClass
	public static void setup() throws Exception {
		migrateFamiliesToFamilies(strategy, originalModel);

		migrated.setVariable("family", "Family.all.first");
	}
	
	@Test
	public void ignoredPropertiesShouldNotBeCopied() {
		migrated.assertTrue("family.name.isUndefined()");
		migrated.assertEquals(0, "family.numberOfChildren");		
	}

	@Test
	public void otherPropertiesShouldBeCopied() {
		migrated.assertEquals(1, "family.members.size");
	}
}

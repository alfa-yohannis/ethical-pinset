/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************
 *
 * $Id$
 */
package org.eclipse.epsilon.hutn.xmi.test.integration.consistent.slots.multiValued;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ManyStringValues.class, ManyIntegerValues.class,
               ManyReferenceValues.class, ManyFragmentReferenceValues.class,
               MixedUUIDAndFragmentReferenceValues.class,
               ManyCrossReferencesForSameFeature.class})
public class ManyValuedSlotSuite {
	public static Test suite() {
		return new JUnit4TestAdapter(ManyValuedSlotSuite.class);
	}
}

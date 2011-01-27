/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.aspects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;

@RunWith(ARunner.class)
public class ARunner02Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	/* Simple check that junit test without load time weaving is working. */
	@Test
	public void testBasic01() {
		if(DEBUG)
			System.out.println("# ARunner02Test.testBasic01()");
		int[] a = { 1, 2, 3, 4 };
		assertEquals(4, a.length);
	}

	/* Check that junit test with load time weaving is working. */
	@Test
	public void testBasic02() {
		if(DEBUG)
			System.out.println("# ARunner02Test.testBasic02()");
		fail();
	}

	public static aspect ARunner02Aspect {

		void around() : execution(public void ARunner02Test.testBasic02()) {
			if (DEBUG) {
				System.out.println("# ARunner02Aspect");
			}
			return;
		}
	}
}

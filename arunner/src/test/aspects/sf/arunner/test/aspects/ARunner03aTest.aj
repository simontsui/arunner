/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.aspects;

import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;

@RunWith(ARunner.class)
public class ARunner03aTest {

	static final boolean DEBUG = TestConfig.DEBUG;

	/* 
	 * Check that load time weaving on a class other than the test class,
	 * and thus not explicitly loaded by ARunnerWeavingClassLoader, is working.
	 * Similar to ARunner03Test but invoke method through reflection.
	 */
	@Test
	public void testBasic01() throws Exception {
		if (DEBUG)
			System.out.println("# ARunner03aTest.testBasic01()");
		Class<?> c=Class.forName("sf.arunner.test.aspects.Basic02Test");
		c.getMethod("testBasic00").invoke(c);
	}

	public static aspect ARunner03aAspect {
		void around() : execution(public void Basic02Test.testBasic00()) {
			if (DEBUG) {
				System.out.println("# ARunner03aAspect");
			}
			return;
		}
	}
}

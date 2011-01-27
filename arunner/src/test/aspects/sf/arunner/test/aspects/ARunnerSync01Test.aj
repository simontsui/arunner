/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.aspects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;

@RunWith(ARunner.class)
public class ARunnerSync01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	/* 
	 * Dummy tests that always pass, just for running tests with ARunnerSync01Aspect
	 */
	@Test
	public void test01() {
		if (DEBUG) 
			System.out.println("# ARunnerSync01Test.test01()");
		assertEquals(4, "abcd".length());
	}

	@Test
	public void test02() {
		if (DEBUG) 
			System.out.println("# ARunnerSync01Test.test02()");
		assertEquals(4, "1234".length());
	}

	@Test
	public void test03() {
		if (DEBUG) 
			System.out.println("# ARunnerSync01Test.test03()");
		assertEquals(4, "cdef".length());
	}

	/*
	 * Add an 50ms delay before any call to Blockqueue.take() in ATestRunner.test().
	 */
	public static aspect ARunnerSync01Aspect {
		private static final int DELAY = 50;
		before() : call(* *.take()) && withincode(* *.ATestRunner.test(String)) {
			if (DEBUG) 
				System.out.println("# ARunnerSync01Aspect");
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
		
	}
}

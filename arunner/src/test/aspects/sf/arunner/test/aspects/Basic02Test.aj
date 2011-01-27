/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.aspects;

import static org.junit.Assert.fail;

import org.junit.Test;

import sf.arunner.test.TestConfig;

public class Basic02Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	public static void testBasic00() {
		if (DEBUG)
			System.out.println("# Basic02Test.testBasic00()");
		fail();
	}

	/* Check that each test use its own aspects and if none is present, no weaving would be done. */
	@Test(expected = AssertionError.class)
	public void testBasic01() {
		if (DEBUG)
			System.out.println("# Basic02Test.testBasic01()");
		fail();
	}
}

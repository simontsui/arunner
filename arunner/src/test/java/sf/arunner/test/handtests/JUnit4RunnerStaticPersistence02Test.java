/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.handtests;

import org.junit.Test;

import sf.arunner.test.TestConfig;

public class JUnit4RunnerStaticPersistence02Test {

	static final boolean DEBUG = TestConfig.DEBUG;
	public static int counter = 0;

	/*
	 * This is a handtest, use jvmarg -Darunner.handtest=true to run the test manually.
	 *
	 * This test should be run before JUnit4RunnerStaticPersistence03Test.
	 */
	@Test
	public void test01() {
		if (DEBUG)
			System.out.println("# JUnit4RunnerStaticPersistence02Test.test01(): counter=" + counter);
		++counter;
	}
}

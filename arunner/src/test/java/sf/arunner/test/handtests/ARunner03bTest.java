/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.handtests;

import static org.hamcrest.core.Is.is;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;

@RunWith(ARunner.class)
public class ARunner03bTest {

	static final boolean DEBUG = TestConfig.DEBUG;

	/*
	 * This is a handtest, use jvmarg -Darunner.handtest=true to run the test manually.
	 *
	 * This test is expected to fail.
	 * Check that result is an AssertionError.
	 */
	@Test
	public void testBasic01() throws Exception {
		Assume.assumeThat(System.getProperty("arunner.handtest"), is("true"));
		if (DEBUG)
			System.out.println("# ARunner03bTest.testBasic01()");
		Class<?> c = Class.forName("sf.arunner.test.aspects.Basic02Test");
		c.getMethod("testBasic00").invoke(c);
	}
}

/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.handtests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;

@RunWith(ARunner.class)
public class ARunnerStaticPersistence03Test {

	/*
	 * This is a handtest, use jvmarg -Darunner.handtest=true to run the test manually.
	 *
	 * Check that static variables in a test class not persist in other tests running by
	 * the same runner. This test should be run after ARunnerStaticPersistence02Test.
	 */
	@Test
	public void test01() {
		Assume.assumeThat(System.getProperty("arunner.handtest"), is("true"));
		if (ARunnerStaticPersistence02Test.DEBUG)
			System.out.println(
				"# JUnit4RunnerStaticPersistence03Test.test01(): counter="
					+ ARunnerStaticPersistence02Test.counter);
		assertEquals(0, ARunnerStaticPersistence02Test.counter);
	}
}

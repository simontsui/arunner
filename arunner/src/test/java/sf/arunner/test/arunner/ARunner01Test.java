/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.arunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;

@RunWith(ARunner.class)
public class ARunner01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	/* Simple test that passes. */
	@Test
	public void testBasic01() {
		if (DEBUG)
			System.out.println("# ARunner01Test.testBasic01()");
		assertThat("12345".length(), is(5));
	}
}

/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.basic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.test.util.TestRunner;

/* Simple test to check that using RunWith works. */
@RunWith(TestRunner.class)
public class TestRunner01Test {

	@Test
	public void test01() {
		assertEquals(4, "abcd".length());
	}
}

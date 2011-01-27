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

import java.io.File;

import org.junit.Assume;
import org.junit.Test;

import sf.arunner.test.TestConfig;

/* Don't run this test directly, run sf/arunner/test/builders/Basic01BuilderTest instead. */
public class Basic01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	private static final String AOPXML = "org.aspectj.weaver.loadtime.configuration";
	static {
		System.clearProperty(AOPXML);
	}

	/* Simple check that junit test is executed. */
	@Test
	public void testBasic01() {
		if (DEBUG)
			System.out.println("# Basic01Test.testBasic01()");
		int[] a = { 1, 2, 3, 4 };
		assertEquals(4, a.length);
	}

	/* Check that ARunner load time weaving is working. */
	@Test
	public void testBasic02() {
		if (DEBUG)
			System.out.println("# Basic01Test.testBasic02(): before assumption");
		String aopxml = System.getProperty(AOPXML);
		Assume.assumeTrue(aopxml != null && aopxml.startsWith("file:")
			&& new File(aopxml.substring(5)).exists());
		if (DEBUG)
			System.out.println("# Basic01Test.testBasic02(): after assumption: aopxml: " + aopxml);
		fail();
	}

	public static aspect Basic01Aspect {

		void around() : execution(public void Basic01Test.testBasic02()) {
			if (DEBUG) {
				System.out.println("# Basic01Aspect");
			}
			return;
		}
	}
}

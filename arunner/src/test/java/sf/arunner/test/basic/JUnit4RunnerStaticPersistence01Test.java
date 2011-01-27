/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.basic;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import sf.arunner.util.FileUtil;

public class JUnit4RunnerStaticPersistence01Test {

	public static int counter = 0;

	/* Check that static variables persist across tests using BlockJUnit4ClassRunner. */
	@Test
	public void test01() {
		File marker = new File("trash/JUnit4Runner01");
		if (marker.exists()) {
			// test02 has been executed
			assertEquals(1, counter);
			FileUtil.delete(marker);
		} else {
			assertEquals(0, counter);
			++counter;
			FileUtil.mkdirs(marker);
	}}

	@Test
	public void test02() {
		File marker = new File("trash/JUnit4Runner01");
		if (marker.exists()) {
			// test01 has been executed
			assertEquals(1, counter);
			FileUtil.delete(marker);
		} else {
			assertEquals(0, counter);
			++counter;
			FileUtil.mkdirs(marker);
	}}
}

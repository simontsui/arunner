/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.arunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;
import sf.arunner.test.TestConfig;
import sf.arunner.util.FileUtil;

@RunWith(ARunner.class)
public class ARunnerRules01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	private int value = 0;
	private File file1 = new File("trash/ARunnerRules01Test-1");
	private File file2 = new File("trash/ARunnerRules01Test-2");

	/*
	 * Check that ARunner execute @Before and @After
	 */
	@Before
	public void setup() {
		++value;
	}

	@After
	public void teardown() {
		FileUtil.delete(file1, file2);
	}

	@Test
	public void testBeforeAfter01() {
		FileUtil.mkdirs(file1);
		assertThat(value, is(1));
		assertFalse(file2.exists());
	}

	@Test
	public void testBeforeAfter02() {
		FileUtil.mkdirs(file2);
		assertThat(value, is(1));
		assertFalse(file1.exists());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testBeforeAfter03() {
		throw new UnsupportedOperationException("Expected exception");
	}
}

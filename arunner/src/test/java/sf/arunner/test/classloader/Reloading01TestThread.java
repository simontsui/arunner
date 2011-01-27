/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.classloader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import sf.arunner.test.TestConfig;

public class Reloading01TestThread extends Thread {

	private static final boolean DEBUG = TestConfig.DEBUG;

	Throwable e;

	public Reloading01TestThread() {
	}

	public void test() throws Exception {
		setContextClassLoader(getClass().getClassLoader());
		start();
		try {
			join();
		} catch (InterruptedException e) {
		}
		assertThat(e, nullValue());
	}

	public Throwable getThrowable() {
		return e;
	}

	@Override
	public void run() {
		if (DEBUG) {
			System.out.println("# NotDelegate01Test.modified: " + Reloading01Test.isModified());
			System.out.println(
				"# DummyClass01 classloader: "
					+ Reloading01TestDummy.class.getClassLoader().getClass().getName());
			System.out.println(
				"# Reloading01Test classloader: "
					+ Reloading01Test.class.getClassLoader().getClass().getName());
		}
		try {
			assertThat(
				Reloading01TestDummy.class.getClassLoader().getClass().getSimpleName(),
				is("ReloadingClassLoader"));
			assertThat(Reloading01TestDummy.isModified(), is(false));
			assertThat(Reloading01Test.isModified(), is(false));
		} catch (Throwable e) {
			this.e = e;
	}}
}

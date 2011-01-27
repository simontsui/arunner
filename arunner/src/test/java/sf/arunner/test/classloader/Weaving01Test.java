/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.classloader;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import sf.arunner.AspectUtil;
import sf.arunner.test.TestConfig;
import sf.arunner.test.aspects.Basic01Test;
import sf.arunner.test.aspects.Basic01Test.Basic01Aspect;
import sf.arunner.test.aspects.Basic02Test;
import sf.arunner.util.FileUtil;
import sf.arunner.util.TextUtil;

public class Weaving01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	/*
	 * Reload a class with WeavingClassLoader and check that load time weaving is performed.
	 */
	@Test
	public void testWeavingAnAspect01() throws Exception {
		ClassLoader cl = createClassLoader(getClasspath(), this.getClass().getClassLoader().getParent());
		Class<?> c = cl.loadClass(Weaving01TestThread.class.getName());
		c.getMethod("test").invoke(c.newInstance());
	}

	private ClassLoader createClassLoader(final URL[] urls, final ClassLoader parent) {
		if (System.getSecurityManager() == null)
			return new TestWeavingClassLoader(urls, parent);
		return AccessController.doPrivileged(
			new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					return new TestWeavingClassLoader(urls, parent);
				}
			});
	}

	public static class TestWeavingClassLoader extends WeavingURLClassLoader {
		public TestWeavingClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}
		@Override
		protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			if (DEBUG)
				System.out.println("# loadClass(): " + name);
			return super.loadClass(name, resolve);
		}
	}

	private URL[] getClasspath() {
		List<URL> ret = new ArrayList<URL>();
		for (String path:
			new String[] {
				System.getProperty("java.class.path"), //
				System.getProperty("java.ext.dirs"),
			}) {
			for (String p: TextUtil.split(new StringTokenizer(path, File.pathSeparator))) {
				File file = new File(p);
				if (file.exists())
					try {
						ret.add(new URL("file://" + FileUtil.apath(file)));
					} catch (MalformedURLException e) {
						e.printStackTrace();
		}}}
		if (DEBUG) {
			System.out.println("### classpath:");
			for (URL s: ret) {
				System.out.println("# " + s);
		}}
		return ret.toArray(new URL[ret.size()]);
	}

	public static class Weaving01TestThread extends Thread {
		Throwable e;
		public Weaving01TestThread() {
			try {
				File aopxml = AspectUtil.createAopXml(
					getClass().getSimpleName(), Basic01Aspect.class.getName()
					/* , Basic02Aspect.class.getName() */
					);
				if (DEBUG) {
					System.out.println("# aop.xml: " + aopxml);
					System.setProperty("org.aspectj.tracing.enabled", "true");
				}
				System.setProperty(
					"org.aspectj.weaver.loadtime.configuration", "file:" + FileUtil.apath(aopxml));
			} catch (IOException e) {
			}
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
				System.out.println("# classloader: " + AspectUtil.getClassLoaderName(this));
			}
			try {
				runX();
			} catch (Throwable e) {
				if (DEBUG)
					e.printStackTrace();
				this.e = e;
		}}
		private void runX() throws Throwable {
			if (DEBUG) {
				System.out.println("# runX()");
			}
			Result result = JUnitCore.runClasses(Basic01Test.class, Basic02Test.class);
			System.out.println(new PrintableResult(result.getFailures()));
		}
	}
}

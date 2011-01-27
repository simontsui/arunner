/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.ClassPathManager;
import org.junit.Test;

import sf.arunner.test.TestConfig;
import sf.arunner.util.FileUtil;
import sf.arunner.util.TextUtil;

public class Reloading01Test {

	static final boolean DEBUG = TestConfig.DEBUG;
	static boolean modified = false;
	public static synchronized void setModified(boolean b) {
		modified = b;
	}
	public static synchronized boolean isModified() {
		return modified;
	}

	/*
	 * Simple test to check that it is possible to create a classloader that can reload
	 * classes that is already loaded in its parent classloader by not delegate class
	 * loading to its parent first.
	 */
	@Test
	public void testNotDelegate01() throws Exception {
		setModified(true);
		ReloadingClassLoader cl = createClassLoader(this.getClass().getClassLoader().getParent());
		Class<?> c = cl.loadClass(Reloading01TestThread.class.getName());
		c.getMethod("test").invoke(c.newInstance());
	}

	private ReloadingClassLoader createClassLoader(final ClassLoader parent) {
		// Create the classloader as previleged code if security manager is present.
		if (System.getSecurityManager() == null)
			return new ReloadingClassLoader(parent);
		return AccessController.doPrivileged(
			new PrivilegedAction<ReloadingClassLoader>() {
				public ReloadingClassLoader run() {
					return new ReloadingClassLoader(parent);
				}
			});
	}


	static class ReloadingClassLoader extends ClassLoader {
		private ClassPathManager manager;
		public ReloadingClassLoader(ClassLoader parent) {
			super(parent);
			List<String> paths = new ArrayList<String>();
			for (String path:
				new String[] {
					System.getProperty("java.class.path"),
					/* System.getProperty("java.ext.dirs"), */
				}) {
				for (String p: TextUtil.split(new StringTokenizer(path, File.pathSeparator))) {
					if (new File(p).exists())
						paths.add(p);
			}}
			if (DEBUG) {
				System.out.println("### classpath");
				for (String p: paths)
					System.out.println("# " + p);
			}
			manager = new ClassPathManager(paths, null);
		}
		@Override
		protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			if (DEBUG)
				System.out.println("# loadClass(): " + name);
			Class<?> c = findLoadedClass(name);
			if (c != null) {
				if (DEBUG)
					System.out.println("# loadClass(): exists: " + name);
				return c;
			}
			ClassPathManager.ClassFile file = manager.find(UnresolvedType.forName(name));
			if (file != null) {
				InputStream is = null;
				try {
					is = file.getInputStream();
					byte[] b = FileUtil.asBytes(is);
					Class<?> ret = defineClass(name, b, 0, b.length);
					if (resolve)
						resolveClass(ret);
					if (DEBUG)
						System.out.println("# loadClass(): loaded: " + name);
					return ret;
				} catch (IOException e) {
					throw new ClassNotFoundException(name, e);
				} finally {
					FileUtil.close(is);
			}}
			return super.loadClass(name, resolve);
		}
	}
}

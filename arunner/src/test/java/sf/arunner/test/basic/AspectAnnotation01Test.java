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
import java.lang.annotation.Annotation;
import java.util.Collection;

import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;

import sf.arunner.test.TestConfig;
import sf.arunner.util.FileUtil;
import sf.arunner.util.RegexFileFilter;
import sf.arunner.util.TextUtil;

public class AspectAnnotation01Test {

	static final boolean DEBUG = TestConfig.DEBUG;

	@Test
	public void printAnnotations() throws ClassNotFoundException {
		Collection<String> paths = FileUtil.listRecursive(
			new File("target/test-classes"),
			false,
			new RegexFileFilter("^(.*/[^/]*)?Aspect\\.class$", null));
		ClassLoader classloader = getClass().getClassLoader();
		for (String path: paths) {
			path = TextUtil.removeExt(path).replace('/', '.');
			if (DEBUG) {
				System.out.println("### " + path);
			}
			Class<?> c = Class.forName(path, false, classloader);
			Annotation[] annotations = c.getAnnotations();
			if (DEBUG) {
				System.out.println("# Class: " + c);
				for (Annotation a: annotations)
					System.out.println(a.annotationType().getName());
			}
			assertEquals(path, 1, annotations.length);
			assertEquals(Aspect.class, annotations[0].annotationType());
	}}
}

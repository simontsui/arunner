/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.weaver.loadtime.definition.Definition;

import sf.arunner.util.FileUtil;
import sf.arunner.util.IXmlWriter;
import sf.arunner.util.XmlWriter;

public class AspectUtil {

	public static final String LWT_AOP_XML = "org.aspectj.weaver.loadtime.configuration";

	private AspectUtil() {
	}

	public static File createAopXml(String name, String...aspects) throws IOException {
		File aopxml = File.createTempFile("arunner-test-" + name + "-", "-aop.xml");
		PrintWriter out = new PrintWriter(new FileOutputStream(aopxml, true));
		try {
			IXmlWriter w = new XmlWriter(out, "  ");
			w.startAll("aspectj", "aspects");
			for (String aspect: aspects)
				// Expected aspect name as outer.inner
				w.empty("aspect", "name", aspect);
			w.end();
			w.start("weaver", "options", "-verbose");
			w.empty("include", "within", "*..*");
			w.endAll();
		} finally {
			FileUtil.close(out);
		}
		return aopxml;
	}

	public static File createAopXml(Class<?> testclass) throws IOException {
		String[] aspects = AspectUtil.findAspects(testclass);
		File aopxml = AspectUtil.createAopXml(testclass.getSimpleName(), aspects);
		//		if (DEBUG) {
		//			System.out.println("# aop.xml: " + aopxml);
		//			System.out.println("# testClass: " + testclass.getName());
		//			System.setProperty("org.aspectj.tracing.enabled", "true");
		//		}
		System.setProperty(LWT_AOP_XML, "file:" + FileUtil.apath(aopxml));
		return aopxml;
	}

	public static List<Definition> createAopDefinitions(Class<?> testclass) {
		Definition def = new Definition();
		List<String> a = def.getAspectClassNames();
		for (String aspect: findAspects(testclass))
			a.add(aspect);
		def.appendWeaverOptions("-verbose");
		def.getIncludePatterns().add("*..*");
		return Collections.singletonList(def);
	}

	public static String[] findAspects(Class<?> testclass) {
		List<String> ret = new ArrayList<String>();
		Class<?>[] a = testclass.getDeclaredClasses();
		for (Class<?> c: a) {
			String name = c.getName();
			if (AspectUtil.isAspect(c)) {
				ret.add(name);
		}}
		return ret.toArray(new String[ret.size()]);
	}

	public static String getClassLoaderName(Object a) {
		return a.getClass().getClassLoader().getClass().getName();
	}

	public static boolean isAspect(Class<?> c) {
		for (Annotation a: c.getAnnotations()) {
			if (Aspect.class.equals(a.annotationType()))
				return true;
		}
		return false;
	}
}

/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.bridge.AbortException;
import org.aspectj.weaver.bcel.ExtensibleURLClassLoader;
import org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.tools.WeavingAdaptor;
import org.aspectj.weaver.tools.WeavingClassLoader;

/** Load time weaving classloader for ARunner. */
public class ARunnerWeavingClassLoader extends ExtensibleURLClassLoader implements WeavingClassLoader {

	////////////////////////////////////////////////////////////////////

	private static final boolean DEBUG = ARunnerConfig.DEBUG;

	private List<Definition> definitions;
	private WeavingAdaptor adapter;
	private boolean initializingAdaptor;
	private Map<String, byte[]> generatedClasses = new HashMap<String, byte[]>();

	////////////////////////////////////////////////////////////////////

	public ARunnerWeavingClassLoader(URL[] urls, List<Definition> definitions, ClassLoader parent) {
		super(urls, parent);
		this.definitions = definitions;
	}

	////////////////////////////////////////////////////////////////////

	public void acceptClass(String name, byte[] bytes) {
		generatedClasses.put(name, bytes);
	}

	public URL[] getAspectURLs() {
		throw new UnsupportedOperationException();
	}

	////////////////////////////////////////////////////////////////////

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (DEBUG)
			System.out.println("# loadClass(): " + name);
		return super.loadClass(name, resolve);
	}

	protected Class<?> defineClass(String name, byte[] b, CodeSource cs) throws IOException {
		if (DEBUG)
			System.out.println("# defineClass(): " + name);
		byte saveb[] = b;
		if (!initializingAdaptor) {
			if (adapter == null) {
				createAdaptor();
			}
			try {
				b = adapter.weaveClass(name, b, false);
			} catch (AbortException e) {
				throw e;
			} catch (Throwable e) {
		}}
		try {
			return super.defineClass(name, b, cs);
		} catch (Throwable e) {
			System.err.println("Weaving failed, using unweaved class");
			e.printStackTrace();
			return super.defineClass(name, saveb, cs);
	}}

	protected byte[] getBytes(String name) throws IOException {
		byte[] bytes = super.getBytes(name);
		if (bytes == null)
			return generatedClasses.remove(name);
		return bytes;
	}

	protected void addURL(URL url) {
		throw new UnsupportedOperationException();
	}

	////////////////////////////////////////////////////////////////////

	private void createAdaptor() {
		if (DEBUG)
			System.out.println("# createAdaptor()");
		IWeavingContext context = new ARunnerWeavingContext(this, definitions) {
			public String getClassLoaderName() {
				ClassLoader loader = getClassLoader();
				return loader.getClass().getName();
			}
		};
		ClassLoaderWeavingAdaptor a = new ClassLoaderWeavingAdaptor();
		initializingAdaptor = true;
		a.initialize(this, context);
		initializingAdaptor = false;
		adapter = a;
	}

	////////////////////////////////////////////////////////////////////

}

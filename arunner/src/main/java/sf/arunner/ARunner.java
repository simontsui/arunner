/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.tools.WeavingAdaptor;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

import sf.arunner.util.FileUtil;
import sf.arunner.util.TextUtil;

/** JUnit4 test runner for running unit tests with load time aspect weaving. */
public class ARunner extends ParentRunner<FrameworkMethod> {

	////////////////////////////////////////////////////////////////////

	private static final boolean DEBUG = ARunnerConfig.DEBUG;
	private static final boolean TRACE = ARunnerConfig.TRACE;
	protected static final String NOOP = "";
	protected static final Throwable PASS = new Throwable();

	protected URL[] classpath;
	protected Class<?> runnerClass;
	protected Object runnerInstance;

	////////////////////////////////////////////////////////////////////

	public ARunner(Class<?> testclass) throws InitializationError {
		super(testclass);
		if (TRACE) {
			new Throwable("DEBUG: ARunner()").printStackTrace(System.out);
			System.setProperty(WeavingAdaptor.TRACE_MESSAGES_PROPERTY, "true");
			System.setProperty("org.aspectj.tracing.enabled", "true");
		}
		if (DEBUG) {
			System.setProperty(WeavingAdaptor.WEAVING_ADAPTOR_VERBOSE, "true");
			// System.setProperty(WeavingAdaptor.SHOW_WEAVE_INFO_PROPERTY, "true");
		}
		if (getTestClass().getAnnotatedMethods(Test.class).size() == 0)
			throw new InitializationError("No tests found");
		setScheduler(
			new RunnerScheduler() {
				@Override
				public void schedule(Runnable childStatement) {
					childStatement.run();
				}
				@Override
				public void finished() {
					try {
						runnerClass.getMethod("shutdown").invoke(runnerInstance);
					} catch (Throwable e) {
						throw new RuntimeException(e);
				}}
			});
		if (DEBUG)
			System.out.println("# ARunner(): classloader: " + getClass().getClassLoader());
		classpath = getClassPath();
		ClassLoader cl = createClassLoader(
			classpath.clone(),
			AspectUtil.createAopDefinitions(testclass),
			getBootClassLoader(this.getClass().getClassLoader()));
		try {
			runnerClass = cl.loadClass(ATestRunner.class.getName());
			runnerInstance = runnerClass.getConstructor().newInstance();
			runnerClass.getMethod("setup", String.class).invoke(runnerInstance, testclass.getName());
		} catch (Throwable e) {
			throw new InitializationError(e);
		}
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		return getTestClass().getAnnotatedMethods(Test.class);
	}

	@Override
	protected Description describeChild(FrameworkMethod child) {
		return Description.createTestDescription(
			getTestClass().getJavaClass(), child.getName(), child.getAnnotations());
	}

	@Override
	protected void runChild(final FrameworkMethod child, RunNotifier notifier) {
		if (TRACE) {
			new Throwable("DEBUG: runChild()").printStackTrace(System.out);
		}
		Description desc = describeChild(child);
		notifier.fireTestStarted(desc);
		try {
			new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					runnerClass.getMethod("test", String.class).invoke(
						runnerInstance, child.getName());
					return null;
				}
			}.run();
		} catch (Throwable e) {
			// Note that the execption object may be coming from the other world, so don't catch it.
			Throwable ee = recreateThrowable(e);
			if (AssumptionViolatedException.class.getName().equals(ee.getClass().getName())) {
				notifier.fireTestAssumptionFailed(new Failure(desc, ee));
			} else {
				notifier.fireTestFailure(new Failure(desc, ee));
		}} finally {
			notifier.fireTestFinished(desc);
	}}

	ClassLoader createClassLoader(final URL[] urls, final List<Definition> definitions, final ClassLoader parent) {
		return AccessController.doPrivileged(
			new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					return new ARunnerWeavingClassLoader(urls, definitions, parent);
				}
			});
	}

	private URL[] getClassPath() {
		List<URL> ret = new ArrayList<URL>();
		for (String path:
			new String[] {
				System.getProperty("java.class.path"), /* System.getProperty("java.ext.dirs")  */
			}) {
			if (path == null)
				continue;
			for (String s: TextUtil.split(new StringTokenizer(path, File.pathSeparator))) {
				File file = new File(s);
				if (file.exists()) {
					String apath = FileUtil.apath(file);
					if (file.isDirectory())
						apath += File.separatorChar;
					try {
						ret.add(new URL("file://" + apath));
					} catch (MalformedURLException e) {
						e.printStackTrace();
			}}}
			if (DEBUG) {
				System.out.println("### classpath:");
				for (URL s: ret) {
					System.out.println("# " + s);
		}}}
		return ret.toArray(new URL[ret.size()]);
	}

	private Throwable recreateThrowable(Throwable e) {
		if (!isFromOtherWorld(e))
			return e;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(b);
			out.writeObject(e);
			out.close();
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b.toByteArray()));
			return (Throwable)in.readObject();
		} catch (Throwable ex) {
			return e;
	}}

	private boolean isFromOtherWorld(Throwable e) {
		ClassLoader loader = e.getClass().getClassLoader();
		if (loader == null)
			return false;
		String name = loader.getClass().getName();
		for (ClassLoader l = getClass().getClassLoader(); l != null; l = l.getParent()) {
			if (name.equals(l.getClass().getName()))
				return false;
		}
		return true;
	}

	private ClassLoader getBootClassLoader(ClassLoader c) {
		for (ClassLoader cc = c.getParent(); cc != null; cc = c.getParent()) {
			c = cc;
		}
		return c;
	}

	////////////////////////////////////////////////////////////////////

	public static class ATestRunner extends Thread {

		Class<?> testClass;
		AJUnit4Runner junit4Runner; // For BlockJUnit4ClassRunner.methodBlock().
		private boolean terminate;
		private BlockingDeque<String> testQueue = new LinkedBlockingDeque<String>();
		private BlockingDeque<Throwable> resultQueue = new LinkedBlockingDeque<Throwable>();

		public ATestRunner() {
		}

		public void setup(String testclass) throws Throwable {
			ClassLoader loader = getClass().getClassLoader();
			setContextClassLoader(loader);
			this.testClass = loader.loadClass(testclass);
			this.junit4Runner = new AJUnit4Runner(testClass);
			if (DEBUG) {
				System.out.println("# setup(): classloader: " + loader);
				System.out.println("# setup(): classloader.parent: " + loader.getParent());
			}
			start();
		}

		public void shutdown() {
			if (DEBUG)
				System.out.println("# shutdown()");
			this.terminate = true;
			while (isAlive()) {
				try {
					testQueue.put(NOOP);
					sleep(1);
				} catch (InterruptedException e) {
		}}}

		public void test(String testmethod) throws Throwable {
			if (DEBUG)
				System.out.println("# test(): " + testmethod);
			testQueue.put(testmethod);
			Throwable e = resultQueue.take();
			if (e != PASS)
				throw e;
		}

		public void run() {
			if (DEBUG)
				System.out.println("# run(): context classloader: " + getContextClassLoader());
			while (!terminate) {
				try {
					if (DEBUG)
						System.out.println("# run(): wait");
					String testmethod = testQueue.take();
					if (DEBUG)
						System.out.println("# run(): waken: " + testmethod);
					if (NOOP.equals(testmethod))
						continue;
					try {
						runTest(testmethod.toString());
						result(PASS);
					} catch (MultipleFailureException e) {
						if (DEBUG) {
							for (Throwable ee: e.getFailures()) {
								ee.printStackTrace();
						}}
						result(e);
					} catch (Throwable e) {
						if (DEBUG)
							e.printStackTrace();
						result(e);
				}} catch (InterruptedException e) {
			}}
			if (DEBUG)
				System.out.println("# run(): terminate");
		}

		private void result(Throwable e) {
			while (true) {
				try {
					resultQueue.put(e);
					break;
				} catch (InterruptedException e1) {
		}}}

		private void runTest(final String testmethod) throws Throwable {
			if (DEBUG)
				System.out.println("# runTest()");
			new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					FrameworkMethod method = new FrameworkMethod(testClass.getMethod(testmethod));
					junit4Runner.methodBlock(method).evaluate();
					return null;
				}
			}.run();
		}

		private static class AJUnit4Runner extends BlockJUnit4ClassRunner {
			public AJUnit4Runner(Class<?> klass) throws InitializationError {
				super(klass);
			}
			@Override
			public Statement methodBlock(FrameworkMethod method) {
				return super.methodBlock(method);
			}
		}
	}

	////////////////////////////////////////////////////////////////////
}

/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.util;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import sf.arunner.test.TestConfig;

@Ignore
/* A primitive custom test runner. */
public class TestRunner extends ParentRunner<FrameworkMethod> {
	
	static final boolean DEBUG = TestConfig.DEBUG;

	public TestRunner(Class<?> testclass) throws InitializationError {
		super(testclass);
		if (DEBUG) {
			System.out.println("### TestRunner()");
			new Throwable().printStackTrace(System.out);
		}
	}
	
	@Override
	protected List<FrameworkMethod> getChildren() {
		if (DEBUG) {
			System.out.println("### getChildren()");
			new Throwable().printStackTrace(System.out);
		}
		return getTestClass().getAnnotatedMethods(Test.class);
	}
	
	@Override
	protected Description describeChild(FrameworkMethod child) {
		if (DEBUG) {
			System.out.println("### describeChild()");
			new Throwable().printStackTrace(System.out);
		}
		return Description.createTestDescription(
			getTestClass().getJavaClass(), child.getName(), child.getAnnotations());
	}
	
	@Override
	protected void runChild(FrameworkMethod child, RunNotifier notifier) {
		if (DEBUG) {
			System.out.println("### runChild()");
			new Throwable().printStackTrace(System.out);
		}
		Description desc = describeChild(child);
		notifier.fireTestStarted(desc);
		Object test;
		try {
			test = new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
			new InvokeMethod(child, test).evaluate();
		} catch (Throwable e) {
			notifier.fireTestFailure(new Failure(desc, e));
		} finally {
			notifier.fireTestFinished(desc);
	}}
	
	protected Object createTest() throws Exception {
		return getTestClass().getOnlyConstructor().newInstance();
	}
}

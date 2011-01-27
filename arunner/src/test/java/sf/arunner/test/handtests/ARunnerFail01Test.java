/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.handtests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;

@RunWith(ARunner.class)
public class ARunnerFail01Test {

	/*
	 * This is a handtest, use jvmarg -Darunner.handtest=true to run the test manually.
	 *
	 * This test is expected to fail.
	 * This simple test check how a simple failure is reported.
	 * Check that result is an AssertionError.
	 *
	 * Expected result:
	java.lang.AssertionError:
	at org.junit.Assert.fail(Assert.java:91)
	at org.junit.Assert.fail(Assert.java:98)
	at sf.arunner.test.handtests.ARunnerFail01Test.testBasic02(ARunnerFail01Test.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:616)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at sf.arunner.ARunner$ATestRunner$1.runReflectiveCall(ARunner.java:263)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at sf.arunner.ARunner$ATestRunner.run(ARunner.java:266)
	 */
	@Test
	public void test01() {
		Assume.assumeThat(System.getProperty("arunner.handtest"), is("true"));
		fail("Expected AssertionError");
	}
}

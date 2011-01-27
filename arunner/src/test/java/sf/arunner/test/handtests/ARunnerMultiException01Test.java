/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.handtests;

import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;
import org.junit.internal.runners.model.MultipleFailureException;

public class ARunnerMultiException01Test {

	/*
	 * This is a handtest, use jvmarg -Darunner.handtest=true to run the test manually.
	 *
	 * This simple test check that MultipleFailureException is properly reported.
	 * The test is expectd to fail. With properly serialization/deserialization,
	 * the MultipleFailureException should report multiple stack traces instead
	 * of the MultipleFailureException itself.
	 *
	 * Expected result:
	java.lang.Throwable: Expected Throwable
	at sf.arunner.test.handtests.ARunnerMultiException01Test.test01(ARunnerMultiException01Test.java:25)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:616)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:76)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:49)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)

	java.lang.RuntimeException: Expected RuntimeException
	at sf.arunner.test.handtests.ARunnerMultiException01Test.test01(ARunnerMultiException01Test.java:26)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	...
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)

	sf.arunner.test.handtests.ARunnerMultiException01Test$MyException: MyException
	at sf.arunner.test.handtests.ARunnerMultiException01Test.test01(ARunnerMultiException01Test.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	...
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)
	 *
	 */
	@Test
	public void test01() throws Exception {
		Assume.assumeThat(System.getProperty("arunner.handtest"), is("true"));
		List<Throwable> errors = new ArrayList<Throwable>();
		for (Throwable e:
			new Throwable[] {
				new Throwable("Expected Throwable"),
				new RuntimeException("Expected RuntimeException"),
				new MyException("Expected MyException", 101)
			})
			errors.add(e);
		throw new MultipleFailureException(errors);
	}

	public static class MyException extends Exception {
		private static final long serialVersionUID = 1L;
		private int errCode;
		public MyException(String msg, int errcode) {
			super(msg);
		}
		public int getErrCode() {
			return errCode;
		}
	}
}

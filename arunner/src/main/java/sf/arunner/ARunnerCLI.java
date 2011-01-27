/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.JUnitSystem;
import org.junit.internal.TextListener;
import org.junit.runner.Computer;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import sf.arunner.util.FileUtil;

/** Simple command line tools to run JUnit4 tests, including with ARunner. */
public class ARunnerCLI {

	////////////////////////////////////////////////////////////////////

	private RunNotifier fNotifier = new RunNotifier();

	////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		new ARunnerCLI().run(args);
	}

	public static void usage() {
		System.out.println(String.format("Usage: java %s <class> [ ... ]", ARunnerCLI.class.getName()));
	}

	////////////////////////////////////////////////////////////////////

	public void run(String...args) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		OutputStream out = null;
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			if (arg.equals("-o")) {
				try {
					out = new FileOutputStream(new File(args[++i]));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("ERROR: creating output file: " + args[i]);
				}
				continue;
			}
			try {
				Class<?> c = Class.forName(arg);
				classes.add(c);
			} catch (ClassNotFoundException e) {
				System.err.println("WARN: Ignored missing class: " + arg);
		}}
		try {
			run(
				new ARunnerSystem(out != null ? out : System.out),
				classes.toArray(new Class<?>[classes.size()]));
		} finally {
			FileUtil.close(out);
	}}

	public Result run(JUnitSystem system, Class<?>...classes) {
		return run(system, Request.classes(new Computer(), classes).getRunner());
	}

	public Result run(JUnitSystem system, Runner runner) {
		Result result = new Result();
		RunListener rlistener = result.createListener();
		RunListener tlistener = new TextListener(system);
		fNotifier.addListener(rlistener);
		fNotifier.addListener(tlistener);
		try {
			fNotifier.fireTestRunStarted(runner.getDescription());
			runner.run(fNotifier);
			fNotifier.fireTestRunFinished(result);
		} finally {
			fNotifier.removeListener(tlistener);
			fNotifier.removeListener(rlistener);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////

	private static class ARunnerSystem implements JUnitSystem {

		private PrintStream out;

		public ARunnerSystem(OutputStream b) {
			this.out = new PrintStream(b);
		}

		@Override
		public void exit(int i) {
		}

		@Override
		public PrintStream out() {
			return out;
		}
	}

	////////////////////////////////////////////////////////////////////
}

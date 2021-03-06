/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.ignored;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import sf.arunner.ARunner;

/*
 * This is a handtest, comment out @Ignore to run.
 *
 * Check that InitializationError is reported if test class has no test methods.
 * This test is expected to fail with InitializationError.
 */
@RunWith(ARunner.class)
@Ignore
public class ARunnerEmpty01Test {
	public void notest() {
	}
}

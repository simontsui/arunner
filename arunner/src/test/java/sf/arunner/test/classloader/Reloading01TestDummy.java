/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.test.classloader;

public class Reloading01TestDummy {
	public static boolean modified = false;
	public static boolean isModified() {
		return modified;
	}
}

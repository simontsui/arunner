/*
 * Copyright (c) 2009, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.util;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Utility class that make use of java.util.regex classes
 */
public class MatchUtil {

	private MatchUtil() {
	}

	public static boolean match(String input, Iterable<Pattern> includes, Iterable<Pattern> excludes) {
		OK: if (includes != null) {
			Iterator<Pattern> it = includes.iterator();
			if (it.hasNext()) {
				do {
					if (it.next().matcher(input).matches())
						break OK;
				} while (it.hasNext());
				return false;
		}}
		if (excludes != null) {
			for (Pattern pat: excludes) {
				if (pat.matcher(input).matches())
					return false;
		}}
		return true;
	}
}

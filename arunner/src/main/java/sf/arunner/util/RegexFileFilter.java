/*
 * Copyright (c) 2011, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexFileFilter implements FileFilter {

	private List<Pattern> includes = new ArrayList<Pattern>();
	private List<Pattern> excludes = new ArrayList<Pattern>();
	private boolean absolute;

	/**
	 * @param includes		Java regex.
	 * @param excludes	Java regex.
	 */
	public RegexFileFilter(String includes, String excludes) {
		if (includes != null)
			this.includes.add(Pattern.compile(includes));
		if (excludes != null)
			this.excludes.add(Pattern.compile(excludes));
	}

	/**
	 * @param includes		Java regex.
	 * @param excludes	Java regex.
	 */
	public RegexFileFilter(boolean absolute, String includes, String excludes) {
		this.absolute = absolute;
		if (includes != null)
			this.includes.add(Pattern.compile(includes));
		if (excludes != null)
			this.excludes.add(Pattern.compile(excludes));
	}


	public boolean accept(File file) {
		return MatchUtil.match(absolute ? file.getAbsolutePath() : file.getPath(), includes, excludes);
	}
}

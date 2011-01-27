package sf.arunner.util;

/*
 * Copyright (c) 2004, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class TextUtil {

	////////////////////////////////////////////////////////////////////////

	private static char SEP = File.separatorChar;

	protected TextUtil() {
	}

	public static List<String> split(StringTokenizer tok) {
		List<String> ret = new ArrayList<String>();
		while (tok.hasMoreTokens())
			ret.add(tok.nextToken());
		return ret;
	}

	/**
	 * Remove filename extension from a filepath if there is one. eg. dir1/dir2/file.java return
	 * dir1/dir2/file
	 */
	public static String removeExt(String path) {
		int sindex = path.lastIndexOf(SEP);
		int index = path.lastIndexOf('.');
		if (index < 0)
			return path;
		if (sindex < 0)
			return path.substring(0, index);
		if (index <= sindex)
			return path;
		return path.substring(0, index);
	}

	public static String filepath(String...segments) {
		StringBuilder b = new StringBuilder();
		for (String s: segments) {
			if (s == null)
				continue;
			int len = b.length();
			if (len > 0 && b.charAt(len - 1) != File.separatorChar)
				b.append(File.separatorChar);
			b.append(s);
		}
		return b.toString();
	}


	////////////////////////////////////////////////////////////////////////

	public static class Basename {
		public String dir;
		public String name;
		public String base;
		public String ext;
		public Basename(String dir, String name, String base, String ext) {
			this.dir = dir;
			this.name = name;
			this.base = base;
			this.ext = ext;
		}
	}

	////////////////////////////////////////////////////////////////////////

	public static class ManifestValueScanner implements Iterator<String>, Iterable<String> {
		CharSequence value;
		int length;
		int start;
		int end;
		public ManifestValueScanner(CharSequence value) {
			this.value = value;
			this.length = value.length();
			this.start = 0;
			this.end = 0;
		}
		public Iterator<String> iterator() {
			return this;
		}
		public boolean hasNext() {
			return start < length;
		}
		public String next() {
			while (end < length) {
				char c = value.charAt(end);
				if (c == '"') {
					skipString('"');
					continue;
				} else if (c == ',') {
					String ret = value.subSequence(start, end).toString();
					++end;
					start = end;
					return ret;
				}
				++end;
			}
			String ret = value.subSequence(start, end).toString();
			start = end;
			return ret;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
		private void skipString(char delim) {
			for (++end; end < length; ++end) {
				if (value.charAt(end) == delim) {
					++end;
					return;
		}}}
	}

	////////////////////////////////////////////////////////////////////////

	public static class StringComparator implements Comparator<String>, Serializable {
		private static final long serialVersionUID = -4106319796409788626L;
		public int compare(String a, String b) {
			if (a == null)
				return b == null ? 0 : -1;
			return a.compareTo(b);
		}
	}

	public static class StringIgnorecaseComparator implements Comparator<String>, Serializable {
		private static final long serialVersionUID = -1698275111742700972L;
		public int compare(String a, String b) {
			if (a == null)
				return b == null ? 0 : -1;
			return a.compareToIgnoreCase(b);
		}
	}

	////////////////////////////////////////////////////////////////////////
}

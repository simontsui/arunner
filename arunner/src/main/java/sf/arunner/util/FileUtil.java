package sf.arunner.util;

/*
 * Copyright (c) 2004, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

	////////////////////////////////////////////////////////////////////

	private static final int BUFSIZE = 64 * 1024;

	protected FileUtil() {
	}


	public static File mkdirs(File dir) {
		if (dir == null)
			return null;
		if (!dir.exists() && !dir.mkdirs())
			throw new RuntimeException("ERROR: mkdirs: " + dir);
		return dir;
	}


	public static File file(File dir, String...segments) {
		if (segments.length == 0)
			return dir;
		return new File(dir, TextUtil.filepath(segments));
	}


	public static String apath(File dir, String...segments) {
		return file(dir, segments).getAbsolutePath();
	}


	public static String rpath(File file, File basedir) {
		return rpath(file, basedir, false, true);
	}


	public static String rpath(File file, File basedir, boolean allowdotdot, boolean all) {
		if (file.equals(basedir))
			return ".";
		file = file.getAbsoluteFile();
		basedir = basedir.getAbsoluteFile();
		String path = file.getAbsolutePath();
		File dir = file;
		while ((dir = dir.getParentFile()) != null) {
			if (dir.equals(basedir)) {
				return removeLeadingSeparator(path.substring(dir.getAbsolutePath().length()));
		}}
		if (allowdotdot) {
			for (; file != null; file = file.getParentFile()) {
				// Assume file is a directory
				StringBuilder prefix = new StringBuilder();
				dir = basedir;
				while ((dir = dir.getParentFile()) != null) {
					prefix.append(".." + File.separatorChar);
					if (file.equals(dir)) {
						return removeTrailingSeparator(
							prefix.toString()
								+ removeLeadingSeparator(
									path.substring(dir.getAbsolutePath().length())));
		}}}}
		if (all)
			throw new RuntimeException("File is not under basedir: file=" + file + ", basedir=" + basedir);
		return null;
	}


	/** @return Absolute path of files/directory under dir that are accepted by the filter. */
	public static List<String> listRecursive(File dir, boolean absolute, FileFilter filter) {
		final List<String> ret = new ArrayList<String>();
		listRecursive(ret, dir, (absolute ? null : dir), filter);
		return ret;
	}


	public static Collection<String> listRecursive(
		final Collection<String> ret, File dir, final File basedir, final FileFilter filter) {
		dir.listFiles(
			new FileFilter() {
				public boolean accept(File file) {
					if (filter == null || filter.accept(file)) {
						if (ret != null) {
							ret.add(
								basedir == null
									? file.getAbsolutePath()
									: rpath(file, basedir));
					}}
					if (file.isDirectory())
						listRecursive(ret, file, basedir, filter);
					return false;
				}
			});
		return ret;
	}

	/**
	 * @param basedir	null for absolute path.
	 * @param filter	null to accept all file/directory.
	 * Print path of files/directory under dir relative to basedir that are accepted by the filter.
	 */
	public static void listRecursive(
		final PrintStream out, final File dir, final File basedir, final FileFilter filter) {
		dir.listFiles(
			new FileFilter() {
				public boolean accept(File file) {
					if (filter == null || filter.accept(file)) {
						out.println(
							basedir == null ? file.getAbsolutePath() : rpath(file, basedir));
					}
					if (file.isDirectory())
						listRecursive(out, file, basedir, filter);
					return false;
				}
			});
	}


	public static Collection<File> findRecursive(final Collection<File> ret, File base, final FileFilter filter) {
		if (!base.isDirectory())
			throw new IllegalArgumentException("Expected directory: " + base.getAbsolutePath());
		base.listFiles(
			new FileFilter() {
				public boolean accept(File file) {
					if (filter == null || filter.accept(file)) {
						if (ret != null)
							ret.add(file);
					}
					if (file.isDirectory())
						findRecursive(ret, file, filter);
					return false;
				}
			});
		return ret;
	}


	public static int rmdirRecursive(File dir, final FileFilter filter, boolean keeproot) {
		if (!dir.isDirectory())
			return 0;
		final int[] count = new int[] { 0 };
		dir.listFiles(
			new FileFilter() {
				public boolean accept(File path) {
					if (filter != null && !filter.accept(path))
						return false;
					if (path.isDirectory()) {
						rmdirRecursive(path, filter, false);
						if (path.delete())
							++count[0];
						return false;
					}
					if (path.delete())
						++count[0];
					return false;
				}
			});
		if (!keeproot && dir.delete())
			++count[0];
		return count[0];
	}


	/** Close I/O stream, ignore IOException. */
	public static void close(Closeable s) {
		if (s == null)
			return;
		try {
			s.close();
		} catch (IOException e) {
			// Nothing we can do.
	}}


	/** Do File.delete(), ignoring failure. */
	public static void delete(File file) {
		if (file.exists() && !file.delete()) {
			// ignore
			return;
	}}

	/** Do File.delete(), ignoring failure. */
	public static void delete(File...files) {
		for (File file: files) {
			if (file.exists() && !file.delete()) {
				// ignore
				return;
	}}}


	public static byte[] asBytes(InputStream r) throws IOException {
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] b = new byte[BUFSIZE];
		int start = 0;
		int n;
		while ((n = r.read(b, start, BUFSIZE - start)) != -1) {
			start += n;
			if (start == BUFSIZE) {
				list.add(b);
				b = new byte[BUFSIZE];
				start = 0;
		}}
		int size = list.size() * BUFSIZE + start;
		byte[] ret = new byte[size];
		n = 0;
		for (byte[] a: list) {
			System.arraycopy(a, 0, ret, n, BUFSIZE);
			n += BUFSIZE;
		}
		System.arraycopy(b, 0, ret, n, start);
		return ret;
	}

	public static String asString(File file) throws IOException {
		return String.valueOf(asChars(file));
	}

	/**
	 * Read file content as characters regardless of file format.
	 * To read .gz file, use ungzChars() instead.
	 */
	public static char[] asChars(File file) throws IOException {
		Reader r = null;
		try {
			return asChars(r = new FileReader(file));
		} finally {
			if (r != null)
				r.close();
	}}

	public static char[] asChars(Reader r) throws IOException {
		List<char[]> list = new ArrayList<char[]>();
		char[] b = new char[BUFSIZE];
		int start = 0;
		int n;
		while ((n = r.read(b, start, BUFSIZE - start)) != -1) {
			start += n;
			if (start == BUFSIZE) {
				list.add(b);
				b = new char[BUFSIZE];
				start = 0;
		}}
		int size = list.size() * BUFSIZE + start;
		char[] ret = new char[size];
		n = 0;
		for (char[] a: list) {
			System.arraycopy(a, 0, ret, n, BUFSIZE);
			n += BUFSIZE;
		}
		System.arraycopy(b, 0, ret, n, start);
		return ret;
	}

	////////////////////////////////////////////////////////////////////

	private static String removeLeadingSeparator(String path) {
		while (path.startsWith(File.separator))
			path = path.substring(1);
		return path;
	}

	private static String removeTrailingSeparator(String path) {
		while (path.endsWith(File.separator))
			path = path.substring(0, path.length() - 1);
		return path;
	}

	////////////////////////////////////////////////////////////////////

	public static class FileComparator implements Comparator<File>, Serializable {
		private static final long serialVersionUID = -7931411981132951772L;
		public int compare(File a, File b) {
			if (a == null)
				return b == null ? 0 : -1;
			return a.compareTo(b);
		}
	}

	public static class ReverseFileComparator implements Comparator<File>, Serializable {
		private static final long serialVersionUID = -7931411981132951772L;
		public int compare(File a, File b) {
			if (b == null)
				return a == null ? 0 : 1;
			return b.compareTo(a);
		}
	}

	public static class FileIgnorecaseComparator implements Comparator<File>, Serializable {
		private static final long serialVersionUID = 4725680571692221448L;
		public int compare(File a, File b) {
			if (a == null)
				return b == null ? 0 : -1;
			return a.getAbsolutePath().compareToIgnoreCase(b.getAbsolutePath());
		}
	}

	public static class FileOnlyFilter implements FileFilter {
		public boolean accept(File file) {
			return file.isFile();
		}
	}

	public static class DirOnlyFilter implements FileFilter {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	}

	public static class NotDirFilter implements FileFilter {
		public boolean accept(File file) {
			return !file.isDirectory();
		}
	}

	////////////////////////////////////////////////////////////////////
}

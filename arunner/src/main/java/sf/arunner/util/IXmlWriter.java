/*
 * Copyright (c) 2009, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.util;

import java.io.Closeable;

public interface IXmlWriter extends Closeable {

	/** Start n nested tags */
	public abstract IXmlWriter startAll(String...tags);

	/** @param attrs An array of key and value pairs (ie. key1, value1, key2, value2, ...). */
	public abstract IXmlWriter start(String tag, String...attrs);

	/** @param attrs An array of key and value pairs (ie. key1, value1, key2, value2, ...). */
	public abstract IXmlWriter empty(String tag, String...attrs);

	public abstract IXmlWriter end();

	public abstract IXmlWriter endAll();

	////////////////////////////////////////////////////////////////////////
}

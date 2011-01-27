/*
 * Copyright (c) 2004, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.util;

import java.io.PrintWriter;
import java.util.Stack;

public class XmlWriter implements IXmlWriter {

	////////////////////////////////////////////////////////////////////////

	PrintWriter writer;
	String indents = "";
	String tab = null;

	Stack<String> stack = new Stack<String>();

	////////////////////////////////////////////////////////////////////////

	public XmlWriter(PrintWriter w) {
		this(w, null);
	}

	public XmlWriter(PrintWriter w, String tab) {
		this.writer = w;
		this.tab = tab;
	}


	public IXmlWriter startAll(String...tags) {
		for (String tag: tags) {
			printIndent();
			writer.println("<" + tag + ">");
			indent();
			stack.push(tag);
		}
		return this;
	}

	public IXmlWriter start(String tag, String...attrs) {
		printIndent();
		writer.print("<" + tag);
		attributes(attrs);
		writer.println(">");
		indent();
		stack.push(tag);
		return this;
	}


	public IXmlWriter empty(String tag, String...attrs) {
		printIndent();
		writer.print("<" + tag);
		attributes(attrs);
		writer.println("/>");
		return this;
	}


	public IXmlWriter end() {
		unIndent();
		printIndent();
		writer.println("</" + stack.pop() + ">");
		return this;
	}


	public IXmlWriter endAll() {
		while (stack.size() > 0) {
			end();
		}
		return this;
	}


	public void close() {
		writer.close();
	}


	////////////////////////////////////////////////////////////////////////

	private void printIndent() {
		if (tab == null)
			return;
		writer.print(indents);
	}

	private void attributes(String...attrs) {
		if (attrs == null)
			return;
		for (int i = 0; i < attrs.length; i += 2) {
			if (attrs[i + 1] != null)
				writer.print(" " + attrs[i] + "=\"" + XmlUtil.escAttrValue(attrs[i + 1]) + "\"");
	}}


	private void indent() {
		if (tab != null)
			indents += tab;
	}

	private void unIndent() {
		if (tab != null) {
			indents = indents.substring(0, indents.length() - tab.length());
	}}

	////////////////////////////////////////////////////////////////////////
}

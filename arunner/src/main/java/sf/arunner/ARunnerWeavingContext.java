/*
 * Copyright (c) 2011, simontsui. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner;

import java.util.List;

import org.aspectj.weaver.loadtime.DefaultWeavingContext;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.tools.WeavingAdaptor;

/** Weaving context for ARunnerWeavingClassLoader. */
public class ARunnerWeavingContext extends DefaultWeavingContext {

	private List<Definition> definitions;

	public ARunnerWeavingContext(ARunnerWeavingClassLoader loader, List<Definition> definitions) {
		super(loader);
		this.definitions = definitions;
	}

	@Override
	public List<Definition> getDefinitions(ClassLoader loader, WeavingAdaptor adaptor) {
		return definitions;
	}
}

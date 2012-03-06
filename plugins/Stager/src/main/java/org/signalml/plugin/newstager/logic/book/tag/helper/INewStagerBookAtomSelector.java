package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;

public interface INewStagerBookAtomSelector {
	
	public boolean matches(NewStagerAdaptedAtom atom);
	
}

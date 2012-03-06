package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.Collection;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;

public interface INewStagerBookAtomFilter {
	public Collection<NewStagerAdaptedAtom> filter(NewStagerAdaptedAtom atoms[]);
}

package org.signalml.plugin.newstager.data.tag;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerConstants;

public class NewStagerBookAtomTagCreatorData {
	
	public final NewStagerConstants constants;
	public final NewStagerBookInfo bookInfo;
	
	public NewStagerBookAtomTagCreatorData(NewStagerConstants constants, NewStagerBookInfo bookInfo) {
		//TODO remove constants
		this.constants = constants;
		this.bookInfo = bookInfo;
	}
}

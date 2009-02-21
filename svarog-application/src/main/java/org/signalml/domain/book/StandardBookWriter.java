package org.signalml.domain.book;

public interface StandardBookWriter {
	
	void setBookComment(String comment);
	
	void setEnergyPercent(float eps);
	
	void setMaxIterationCount(int max);
	
	void setDictionarySize(int size);
	
	void setDictionaryType(char type);
	
	void setSamplingFrequency(float freq);
	
	void setCalibration(float conv);
	
	void setTextInfo(String text);
	
	void setWebSiteInfo(String text);
	
	void setDate(String text);
}

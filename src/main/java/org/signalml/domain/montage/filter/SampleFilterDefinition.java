/* MontageSampleFilter.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage.filter;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** MontageSampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SampleFilterDefinition implements MessageSourceResolvable, Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String description;
		
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract SampleFilterType getType();
	
	public MessageSourceResolvable getEffectDescription() {
		return null;
	}
	
	public abstract String getDefaultEffectDescription();

	public abstract SampleFilterDefinition duplicate();
	
}

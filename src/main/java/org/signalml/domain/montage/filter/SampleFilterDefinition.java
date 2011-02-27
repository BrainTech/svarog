/* MontageSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter;

import java.io.Serializable;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.MontageSampleFilter;

import org.springframework.context.MessageSourceResolvable;

/**
 * An abstract class representing definition and specification
 * of a {@link MontageSampleFilter filter}.
 * Allows to duplicate a filter, return a description (String) and
 * a {@link SampleFilterType type} of a filter.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SampleFilterDefinition implements MessageSourceResolvable, Serializable, Preset {

	private static final long serialVersionUID = 1L;

        /**
         * description of a filter
         */
	protected String description;

        /**
         * Returns a String with a description of a filter
         * @return String with a description of a filter
         */
	public String getDescription() {
		return description;
	}

        /**
         * Sets the description to a given value
         * @param description String with a description to be set
         */
	public void setDescription(String description) {
		this.description = description;
	}

        /**
         * Returns the type of a filter
         * @return the type of a filter
         */
	public abstract SampleFilterType getType();

        /**
         * Returns a message with a description of filters effect
         * @return a message with a description of filters effect
         */
	public MessageSourceResolvable getEffectDescription() {
		return null;
	}

        /**
         * Returns String with a default description of a filters effect
         * @return String with a default description of a filters effect
         */
	public abstract String getDefaultEffectDescription();

        /**
         * Creates a duplicate of a filters definition
         * @return duplicate of a filters definition
         */
	public abstract SampleFilterDefinition duplicate();

}

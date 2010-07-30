/* SignalType.java created 2007-10-23
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.eeg.EegSignalTypeConfigurer;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents the type of a signal.
 * Contains the {@link SignalTypeConfigurer configurer} which creates
 * {@link Montage montages} for that type.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalType implements MessageSourceResolvable {

        /**
         * the type of a signal - signal other then EEG
         */
	OTHER(new GenericSignalTypeConfigurer()),
        /**
         * the type of a signal - EEG signal
         */
	EEG_10_20(new EegSignalTypeConfigurer())
	;


	private final static Object[] ARGUMENTS = new Object[0];

        /**
         * configurer for this type of a signal
         */
	private SignalTypeConfigurer configurer;

        /**
         * Constructor. Creates an empty SignalType
         */
	private SignalType() {
		this.configurer = null;
	}

        /**
         * Constructor. Creates SingnalType based on a given
         * {@link SignalTypeConfigurer configurer}
         * @param configurer configurer from which we want to create a SignalType
         */
	private SignalType(SignalTypeConfigurer configurer) {
		this.configurer = configurer;
	}

        /**
         * Returns the configurer for this type of a signal
         * @return {@link SignalTypeConfigurer configurer} for this type of
         * a signal
         */
	public SignalTypeConfigurer getConfigurer() {
		return configurer;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalType." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}

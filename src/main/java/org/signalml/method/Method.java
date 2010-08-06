/* Method.java created 2007-09-12
 *
 */
package org.signalml.method;

/**
 *  This interface must be implemented by classes acting as data processing methods
 *  for SignalML. A method class instance should be able to act as a singleton, whose
 *  compute method is called with given data.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Method {

	/**
         *  Returns a unique identifier for the method. The unique identifier is best generated
	 *  once and hardcoded into the implementing class.
	 *
	 * @return the unique identifier
	 */
	String getUID();

	/**
         *  Returns a common string name for the method. The name should not contain spaces.
	 *
	 * @return the name
	 */
	String getName();

	/**
         *  Returns method's version as an array of integers. Version 1.5.3 should be
	 *  represented by <code>int[] {1,5,3}</code>.
	 *
	 * @return the version
	 */
	int[] getVersion();

	/**
         *  Returns true if and only if an object of the given class may be passed to the compute
	 *  method as input data.
	 *
	 * @param clazz the class to test
	 * @return true if supported, false otherwise
	 */
	boolean supportsDataClass(Class<?> clazz);

	/**
         *  Instantiates and returns a most standard data object for this method.
	 *  Implementations that call methods may choose not to use this method and pass
	 *  another object instead as long as it's class is accepted by supportsDataClass.
	 *
	 * @return an empty data Object
	 */
	Object createData();

	/**
         *  Returns the class of the result object returned from compute.
	 *
	 * @return the result class
	 */
	Class<?> getResultClass();

	/**
         *  Implements the actual computations. The method receives a data object
	 *  and should return a result object. The method alse receives a reference to a tracker - an.
	 *  This allows the method code to interact with the application,
	 *  check for abort requests, set progress information, status information, and the like.
	 *
	 *  <p>The computation code should periodically check if the controlling code has posted a
	 *  request for abort. {@link MethodExecutionTracker#isRequestingAbort()} returns true,
	 *  the method should terminate as soon as possible and return null. If
	 *  calculations finish normally, then a non null result must be returned.
	 *
	 *  <p>Additionally a method class may implement the {@link SuspendableMethod} interface
	 *  in which case it's compute method must also test {@link MethodExecutionTracker#isRequestingSuspend()}.
	 *  becoming true. If such situation is detected, the method should save computation state in
	 *  the data object, and return null as soon as possible. The compute method must also be prepared
	 *  to receive a data object containing suspended calculation state and behave appropriately.
	 *
	 * @param data the data object
	 * @param tracker the object used to interact with the executing code
	 * @return the result object or null if execution was aborted
	 * @throws InputDataException when the input data is invalid. Note that this exception
	 *		class implements Spring's Errors interface, so detailed information about
	 *		encountered problems should be included as errors
	 * @throws ComputationException when computation fails for reasons other than bad input data
	 *
	 * @see MethodExecutionTracker
	 */
	Object compute(Object data, MethodExecutionTracker tracker) throws InputDataException, ComputationException;

}

/* ListMarshallerTest.java created 2008-02-18
 *
 */

package org.signalml.test;

import java.util.ArrayList;

import org.signalml.method.mp5.remote.DecompositionProgressResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.xml.transform.StringResult;

/** ListMarshallerTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ListMarshallerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		CastorMarshaller marshaller = new CastorMarshaller();
		Resource mapping = new ClassPathResource("org/signalml/method/mp5/remote/castor_mapping.xml");
		marshaller.setMappingLocation(mapping);
		marshaller.setValidating(true);
		marshaller.afterPropertiesSet();

		DecompositionProgressResponse response = new DecompositionProgressResponse();
		response.setMessageCode("test");

		ArrayList<String> argList = new ArrayList<String>();
		argList.add("xyz");
		argList.add("zyx");

		response.setMessageArguments(argList);

		ArrayList<Integer> tlList = new ArrayList<Integer>();
		tlList.add(5);
		tlList.add(8);
		tlList.add(9);

		response.setTickerLimits(tlList);

		ArrayList<Integer> tList = new ArrayList<Integer>();
		tList.add(4);
		tList.add(3);
		tList.add(1);

		response.setTickers(tList);

		StringResult result = new StringResult();
		marshaller.marshal(response, result);

		System.out.println(result.toString());

	}

}

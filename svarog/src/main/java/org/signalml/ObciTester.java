package org.signalml;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.zeromq.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.zeromq.ListExperimentsResponse;
import org.signalml.app.worker.monitor.zeromq.Message;
import org.signalml.app.worker.monitor.zeromq.MessageType;
import org.zeromq.ZMQ;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import org.zeromq.ZMQ;

public class ObciTester {

	public static void main(String[] args) {
		/*ZMQ.Context ctx = ZMQ.context (1);
		ZMQ.Socket socket = ctx.socket(ZMQ.REQ);
		System.out.println("blee2");
		
		
		Message msg = new Message(MessageType.LIST_EXPERIMENTS);
		
		ObjectMapper mapper = new ObjectMapper();
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			mapper.writeValue(os, msg);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String str = os.toString();
		
		System.out.println(str);
			
		socket.connect("tcp://127.0.0.1:54654"); 
		socket.send(str.getBytes(), 0);
		
		byte[] bytes = socket.recv(0);
		
		String rec = new String(bytes);
		
		try {
			ListExperimentsResponse resp = mapper.readValue(bytes, ListExperimentsResponse.class);
			System.out.println("rec = " + rec);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		String s = null;

		try {
			s = getListExperimentsResponse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ExperimentDescriptorJSonReader reader = new ExperimentDescriptorJSonReader();
		List<ExperimentDescriptor> exp = reader.parseExperiments(s);

		// System.out.println("ble" + s);
		System.out.println("read file");

	}

	public static String getListExperimentsResponse() throws IOException {
		String filePath = "/home/kret/listexp";

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();

		return fileData.toString();
	}

}

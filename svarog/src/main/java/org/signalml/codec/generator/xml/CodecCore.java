package org.signalml.codec.generator.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CodecCore {
	protected static final String  NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";
	protected static final String  VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
	protected static final String  SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
	protected static final String  SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
	protected static final String  DEFAULT_PARSER_NAME = "org.signalml.codec.generator.xml.Xerces";
	protected static final boolean DEFAULT_NAMESPACES = true;
	protected static final boolean DEFAULT_VALIDATION = false;
	protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;
	protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;

	public static Document parse(String arg) {
		ParserWrapper parser = null;

		try {
			parser=(ParserWrapper)Class.forName(DEFAULT_PARSER_NAME).newInstance();
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser ("+DEFAULT_PARSER_NAME+")");
			return null;
		}

		try {
			parser.setFeature(NAMESPACES_FEATURE_ID, DEFAULT_NAMESPACES);
		} catch (SAXException e) {
			System.err.println("warning: Parser does not support feature ("+NAMESPACES_FEATURE_ID+")");
		}

		try {
			parser.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);
		} catch (SAXException e) {
			System.err.println("warning: Parser does not support feature ("+VALIDATION_FEATURE_ID+")");
		}

		try {
			parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, DEFAULT_SCHEMA_VALIDATION);
		} catch (SAXException e) {
			System.err.println("warning: Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
		}

		try {
			parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, DEFAULT_SCHEMA_FULL_CHECKING);
		} catch (SAXException e) {
			System.err.println("warning: Parser does not support feature ("+SCHEMA_FULL_CHECKING_FEATURE_ID+")");
		}

		try {
			return parser.parse(arg);
		} catch (SAXParseException e) {
			System.err.println("error: Parse error occurred - "+e.getMessage());
			e.printStackTrace(System.err);    ;
		} catch (Exception e) {
			System.err.println("error: Parse error occurred - "+e.getMessage());
			if (e instanceof SAXException) {
				e=((SAXException)e).getException();
			}
			e.printStackTrace(System.err);
		}

		return null;
	}

	public Node getNode(String path) {
		return new XPath().get(doc, path);
	}

	public Node []getNodes(String path) {
		return new XPath().getNodes(doc, path);
	}

	public static String getValue(Node node) {
		StringBuffer buf=new StringBuffer();

		if (node.hasChildNodes()) {
			NodeList list=node.getChildNodes();
			int len=list.getLength();

			for (int i=0 ; i<len ; i++) {
				Node next=list.item(i);

				if (next.getNodeType()==Node.TEXT_NODE)
					buf.append(next.getNodeValue());
			}
		}
		return buf.toString().trim();
	}

	public static String getAttribute(Node node, String name) {
		if (node.hasAttributes()) {
			NamedNodeMap attr=node.getAttributes();
			Node out=attr.getNamedItem(name);
			if (out!=null) {
				return out.getNodeValue();
			}
		}
		return null;
	}

	protected Document doc=null;

	public CodecCore(String filename) throws XMLCodecException {
		if ((doc=parse(filename))==null) {
			throw new XMLCodecException("parse error !");
		}
	}

	public CodecCore() {
	}

	protected String identLines(String text) {
		char arr[]=text.toCharArray();
		StringBuffer buf=new StringBuffer();
		int len=arr.length;

		buf.append('\t');
		for (int i=0 ; i<len ; i++) {
			if (arr[i]=='\n') {
				buf.append("\n\t");
			} else {
				buf.append(arr[i]);
			}
		}

		return buf.toString();
	}

	protected String identString(String text, String prefix) {
		char arr[]=text.toCharArray();
		StringBuffer buf=new StringBuffer();
		int len=arr.length;

		buf.append("\"");
		for (int i=0 ; i<len ; i++) {
			if (arr[i]=='\n') {
				buf.append("\"+\n");
				buf.append(prefix);
				buf.append("\"");
			} else if (arr[i]=='"') {
				buf.append("\\\"");
			} else {
				buf.append(arr[i]);
			}
		}
		buf.append("\"");
		return buf.toString();
	}

	protected static String expand(String text) throws XMLCodecException {
		char arr[]=text.toCharArray();
		int i, len=arr.length;

		for (i=len-1 ; i>=0 ; i--) {
			if (arr[i]=='/') {
				break;
			}
		}

		String expr=text.substring(i+1, len);
		int idx=expr.indexOf("[");

		if (idx!=-1) {
			StringBuffer buf=new StringBuffer();
			buf.append(expr.substring(0, idx));
			buf.append('_');
			idx=expr.indexOf("=");
			int idx2=expr.indexOf("]");
			if (idx==-1 || idx2==-1) {
				throw new XMLCodecException("expand: syntax error");
			}

			buf.append(expr.substring(idx+1, idx2));
			expr=buf.toString();
		}
		return "get_"+expr+"()";
	}

	protected static String eval(String text) throws XMLCodecException {
		if (text==null) {
			return null;
		}

		char arr[]=text.toCharArray();
		StringBuffer buf=new StringBuffer();
		int len=arr.length;

		for (int i=0 ; i<len ; i++) {
			if (arr[i]=='{') {
				StringBuffer v=new StringBuffer();
				for (i++ ; i<len ; i++) {
					if (arr[i]=='}') {
						break;
					} else {
						v.append(arr[i]);
					}
				}

				String value=v.toString();
				if (value.equals("number_of_channels")) {
					buf.append("get_number_of_channels()");
				} else if (value.equals("sampling_frequency")) {
					buf.append("get_sampling_frequency()");
				} else if (value.equals("calibration")) {
					buf.append("get_calibration()");
				} else if (value.equals("this")) {
					buf.append("theResult");
				} else if (value.equals("index")) {
					buf.append("index");
				} else if (value.equals("basename")) {
					buf.append("get_basename()");
				} else {
					buf.append(expand(value));
				}
			} else {
				buf.append(arr[i]);
			}
		}

		return buf.toString();
	}
}

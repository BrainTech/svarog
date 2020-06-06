/* Signal Copyright (C) 2003 Dobieslaw Ircha    <dircha@eranet.pl>
                             Artur Biesiadowski <abies@adres.pl>
                             Piotr J. Durka     <Piotr-J.Durka@fuw.edu.pl>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Linking Signal statically or dynamically with other modules is making a
    combined work based on Signal.  Thus, the terms and conditions of the GNU
    General Public License cover the whole combination.

    As a special exception, the copyright holders of Signal give you
    permission to link Signal with independent modules that communicate with
    Signal solely through the SignalBAR interface, regardless of the license
    terms of these independent modules, and to copy and distribute the
    resulting combined work under terms of your choice, provided that
    every copy of the combined work is accompanied by a complete copy of
    the source code of Signal (the version of Signal used to produce the
    combined work), being distributed under the terms of the GNU General
    Public License plus this exception.  An independent module is a module
    which is not derived from or based on Signal.

    Note that people who make modified versions of Signal are not obligated
    to grant this special exception for their modified versions; it is
    their choice whether to do so.  The GNU General Public License gives
    permission to release a modified version without this exception; this
    exception also makes it possible to release a modified version which
    carries forward this exception.
*/
package org.signalml.codec.generator.xml;

import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class PathEntry {
	public String tag=null;
	public String attribute=null;
	public String attribute_test=null;

	public PathEntry(String tag, String attr) {
		this.tag=tag;
		if (attr!=null) {
			int idx=attr.indexOf('=');
			if (idx!=-1) {
				attribute=attr.substring(0, idx);
				attribute_test=attr.substring(idx+1, attr.length());
			} else {
				attribute=attr;
			}
		}
	}

	private String is_null(String string) {
		return string==null ? "(null)" : string;
	}

	public String toString() {
		return "Tag: |"+is_null(this.tag)+
			   "| Attr: |"+is_null(this.attribute)+
			   "| Test: |"+is_null(this.attribute_test)+"|";
	}
}

public class XPath {
	public PathEntry[] parsePath(String path) {
		int i, len, beg, arr_length;
		PathEntry ret[];

		if (path.startsWith("/")) {
			path=path.substring(1, path.length());
		}

		if (!path.endsWith("/")) {
			path+='/';
		}

		char arr[]=path.toCharArray();
		arr_length=arr.length;

		for (i=0, len=0 ; i<arr_length ; i++)
			if (arr[i]=='/') {
				len++;
			}

		if (len==0) {
			return null;
		}

		ret=new PathEntry[len];
		int idx=0;

		for (i=0, beg=0 ; i<arr_length ; i++) {
			String attr=null;

			if (arr[i]=='/') {
				int k=i;

				if (arr[k-1]==']') {
					for (; k>=0 ; k--) {
						if (arr[k]=='[')
							break;
					}

					attr=new String(arr, k+1, i-k-2);
				}

				ret[idx]=new PathEntry(new String(arr, beg, k-beg), attr);
				beg=i+1;
				idx++;
			}
		}

		return ret;
	}

	private Node ret_node;
	private Vector<Node> ret_vec;

	private void find(Node node, PathEntry entry[], int level, boolean vector) {
		if (node==null || level>=entry.length) {
			return;
		}

		PathEntry ent=entry[level];
		if (ent.tag==null) {
			return;
		} else {
			if (!ent.tag.equals("*")) {
				if (!ent.tag.equals(node.getNodeName())) {
					return;
				}
			}
		}

		if (ent.attribute!=null) {
			if (node.hasAttributes()) {
				NamedNodeMap attr=node.getAttributes();
				int     len=attr.getLength();
				boolean ok=false;

				for (int i=0 ; i<len ; i++) {
					Node attr_node=attr.item(i);
					if (attr_node.getNodeName().equals(ent.attribute)) {
						if (ent.attribute_test!=null) {
							if (attr_node.getNodeValue().equals(ent.attribute_test)) {
								ok=true;
								break;
							}
						}
					}
				}

				if (!ok) return;
			} else {
				return;
			}
		}

		if (level==entry.length-1) {
			if (!vector) {
				ret_node=node;
			} else {
				ret_vec.addElement(node);
			}
		} else {
			if (node.hasChildNodes()) {
				NodeList list=node.getChildNodes();
				int len=list.getLength();

				for (int i=0 ; i<len ; i++) {
					Node next=list.item(i);

					switch (next.getNodeType()) {
					case Node.TEXT_NODE:
						break;

					case Node.ELEMENT_NODE:
						find(next, entry, level+1, vector);
						break;
					}
				}
			}
		}
	}

	public Node get(Document root, String path) {
		if (path!=null && root!=null) {
			PathEntry entry[]=parsePath(path);
			if (entry!=null) {
				ret_node=null;
				find(root.getDocumentElement(), entry, 0, false);
				return ret_node;
			}
		}
		return null;
	}

	public Node []getNodes(Document root, String path) {
		if (path!=null && root!=null) {
			PathEntry entry[]=parsePath(path);
			if (entry!=null) {
				ret_vec=new Vector<>();
				find(root.getDocumentElement(), entry, 0, true);


				if (ret_vec.size()!=0) {
					int len=ret_vec.size();
					Node []ret=new Node[len];

					for (int i=0 ; i<len ; i++) {
						ret[i]=(Node)ret_vec.get(i);
					}
					return ret;
				}
			}
		}
		return null;
	}
}

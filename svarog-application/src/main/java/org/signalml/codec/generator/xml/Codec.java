/*  Signal Copyright (C) 2003 Dobieslaw Ircha    <dircha@eranet.pl> 
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

import org.w3c.dom.Node;

public class Codec extends CodecCore {
    private static final String TAG_CODE="meta_format/parameters/code[language=java]";
    private static final String TAG_NAME="meta_format/header/format"; 
    private static final String TAG_DESC="meta_format/header/text_info";
    private static final String TAG_MAXCHN="meta_format/parameters/number_of_channels";
    private static final String TAG_SAMP="meta_format/parameters/sampling_frequency";
    private static final String TAG_CALIB="meta_format/parameters/calibration";
    private static final String TAG_CHANS="meta_format/parameters/channel_names";
    private static final String TAG_PROPERTY="meta_format/parameters/property";
    private static final String TAG_CONSTRAINT="meta_format/parameters/constraint";
    private static final String TAG_DATA="meta_format/data_format";
    
    public Codec() {
    	;
    }
    
    public Codec(String filename) throws XMLCodecException {
    	super(filename);
    }
    
   private String genClassName() throws XMLCodecException {  
     if(doc==null) {
        throw new XMLCodecException("genClassName: parse error");
     }
     
     Node node=getNode(TAG_NAME);
     String name;
     
     if(node==null) {
        throw new XMLCodecException("genClassName: node "+TAG_NAME+" not found!");
     }
     
     if((name=getAttribute(node, "id"))==null) {
        throw new XMLCodecException("genClassName: node "+TAG_NAME+":id not found!");
     }
     return name;
  }

   public String getFormatName() throws XMLCodecException {
		if (doc == null) {
			throw new XMLCodecException("genClassName: parse error");
		}

		Node node = getNode(TAG_NAME);
		String name;

		if (node == null) {
			throw new XMLCodecException("genClassName: node " + TAG_NAME + " not found!");
		}

		if ((name = getAttribute(node, "id")) == null) {
			throw new XMLCodecException("genClassName: node " + TAG_NAME + ":id not found!");
		}
		return name;
	}
   
  private String genCodeForJava() throws XMLCodecException {
     if(doc==null) {
        throw new XMLCodecException("genCodeForJava: parse error");
     }
     
     Node node=getNode(TAG_CODE);
     if(node!=null) {
        String text=getValue(node);
        return text==null ? "" : identLines(text);
     }
     return "";
  }
    
  private String genDescriptionMethod() throws XMLCodecException {
     if(doc==null) {
        throw new XMLCodecException("genCodeForJava: parse error");
     }
     
     Node node=getNode(TAG_DESC);
     if(node!=null) {
        String text=getValue(node);
        if(text!=null) {
           StringBuffer buf=new StringBuffer();
           buf.append("public String getFormatDescription() {\n");
           buf.append("   return ");
           buf.append(identString(text, "\t  "));
           buf.append(";\n}");
           return identLines(buf.toString());
        }
     }
     
     return "";
  }  

  private String genFormatIDMethod() throws XMLCodecException {
     if(doc==null) {
        throw new XMLCodecException("genCodeForJava: parse error");
     }
     
     Node node=getNode(TAG_NAME);
     if(node!=null) {
        Node idAttr = node.getAttributes().getNamedItem("id");
        if (idAttr == null) {
           throw new XMLCodecException("genCodeForJava: parse error");
        }
        
        String text=getValue(idAttr);
        if(text!=null) {
           StringBuffer buf=new StringBuffer();
           buf.append("public String getFormatID() {\n");
           buf.append("   return ");
           buf.append(identString(text, "\t  "));
           buf.append(";\n}");
           return identLines(buf.toString());
        }
     }
     
     return "";
  }  
    
  private String genConstructor(String className) {
      StringBuffer buf=new StringBuffer();
      buf.append("public ");
      buf.append(className);
      buf.append("() throws XMLCodecException {\n");
      buf.append("   super();\n}\n\n");
      
      buf.append("public void open(String name) throws XMLCodecException {\n");
      buf.append("   super.open(name);\n");
      buf.append("   if(!getConstraints()) {\n");
      buf.append("      throw new XMLCodecException(\"CONSTRAINTS ERROR !\");\n");
      buf.append("   }\n");
      buf.append("}");
      return identLines(buf.toString());   
  }

  /* Refactored by MD */
  public static String compile(Codec codec) throws XMLCodecException {
	     return compile(codec, null, true);
  }
  
  public static String compile(String file) throws XMLCodecException {
  	     return compile(file, null, true);
  }
    
  private static String className=null;
  
  public static String getClassName() {
  	  return className!=null ? className : "";
  }

  /* Refactored by MD */
  public static String compile(String file, String exClass, boolean isPublic) throws XMLCodecException {
      Codec codec=new Codec(file);
      return compile(codec,exClass,isPublic);
  }
  
  /* Refactored by MD */
  public static String compile(Codec codec, String exClass, boolean isPublic) throws XMLCodecException {
      StringBuffer buf=new StringBuffer();
      if(exClass==null) {
         buf.append("package org.signalml.codec;\n\n");
         buf.append("import org.signalml.codec.generator.xml.*;\n\n");
      } 
      
      if(isPublic) {
      	 buf.append("public ");
      }
      
      className=codec.genClassName();
      String exClassName=((exClass!=null) ? exClass : "SMLCodec");
      
      buf.append("class "+className+" extends "+exClassName+" {\n");
      buf.append("\n\t// ---- BEGIN USER CODE ---\n\n");
      buf.append(codec.genCodeForJava());
      buf.append("\n\n\t// ---- END USER CODE   ---\n\n");
      buf.append(codec.genFormatIDMethod());
      buf.append("\n\n");
      buf.append(codec.genDescriptionMethod());
      buf.append("\n\n");
      buf.append(codec.genConstructor(className));
      buf.append('\n');
      buf.append(codec.genReqMethods());
      buf.append('\n');
      buf.append(codec.genUserMethods());
      buf.append('\n');
      buf.append(codec.genConstraintMethods());
      buf.append("\n\n");
      buf.append(codec.genDataFormat());
      buf.append("\n\n");
      buf.append(codec.genInitData());
      buf.append("\n\n");
      buf.append(codec.genChannelDataFormat());
      buf.append("\n}\n");
      return buf.toString();
  }  
        
  private String getTypeSize(String type) throws XMLCodecException {
  	  StringBuffer buf=new StringBuffer();
  	  if(type.equals("int16")) {
         buf.append("2");
      } else if(type.equals("int32")) {
         buf.append("4");
      } else if(type.equals("float")) {
         buf.append("4");
      } else if(type.equals("double")) {
         buf.append("8");
      } else if(type.equals("uchar")) {
         buf.append("1");
      } else if(type.equals("uint16")) {
         buf.append("2");
      } else if(type.equals("uint32")) {
         buf.append("4"); 
      } else if(type.equals("byte")) {
         buf.append("1");
      } else {
         throw new XMLCodecException("unknown data type: "+type);
      }
  	  return buf.toString();
  }  
    
  private String genTypeName(String type, String width) throws XMLCodecException {
      StringBuffer buf=new StringBuffer();
      if(type.equals("int16")) {
         buf.append("short");
      } else if(type.equals("int32")) {
         buf.append("int");
      } else if(type.equals("float")) {
         buf.append("float");
      } else if(type.equals("double")) {
         buf.append("double");
      } else if(type.equals("bytes")) {
         buf.append("byte");
      } else if(type.equals("ascii")) {
         buf.append("String");
      } else if(type.equals("uchar")) {
         buf.append("short");
      } else if(type.equals("uint16")) {
         buf.append("int");
      } else if(type.equals("uint32")) {
         buf.append("long"); 
      } else if(type.equals("byte")) {
         buf.append("byte");
      } else {
         throw new XMLCodecException("unknown data type: "+type);
      }
      
      if(width!=null && !type.equals("ascii")) {
         buf.append("[]");
      }
      return buf.toString();
  }  
    
  private String genCallReadFunction(String jtype, String offset, String width) throws XMLCodecException {
     int idx=jtype.indexOf("[");
     if(idx==-1) {
        if(jtype.equals("String")) {
          if(width==null) {
             throw new XMLCodecException("String type without width");
          }
          return "read_String("+offset+", "+width+")";
        } else {
          return "read_"+jtype+"("+offset+")";
        }
     } else {
        return "read_"+jtype.substring(0, idx)+"_array("+offset+", "+width+")";
     }
  }
    
  private String getRange(String text, boolean start) throws XMLCodecException {
      int idx1=text.indexOf("..");
      
      if(idx1<0) { 
         throw new XMLCodecException("Bad Index specification: "+text);
      }
      
      if(start) {
         return text.substring(0, idx1);
      } else {
         return text.substring(idx1+2, text.length()); 
      }
  }  
    
  private String genProperty(Node node, String defName) throws XMLCodecException {
     if(node!=null) {
        String id=getAttribute(node, "id");
        String offset=eval(getAttribute(node, "offset"));
        String width=eval(getAttribute(node, "width"));
        String type=getAttribute(node, "type");
        String evaltype=getAttribute(node, "evaltype");
        String eval_=eval(getAttribute(node, "eval"));
        String index=eval(getAttribute(node, "index"));
        String byteOrder=getAttribute(node, "byteorder");
        boolean cached = Boolean.parseBoolean(getAttribute(node, "cached"));
        StringBuffer buf=new StringBuffer();
        String convertFun=null;
        String cacheVarName=null;

        String jtype=null;
        
        if(type!=null) {
            jtype=genTypeName(type.trim(), width);
        }

        if( jtype == null ) {
        	cached = false;
        }
        
        if( cached ) {
        	cacheVarName = "cached_" + (id!=null ? id : defName);
        	buf.append( "private " + jtype + " " + cacheVarName + " = -1;\n" );
        }
        
        buf.append("public ");
        
        if(type!=null) {
           if(evaltype==null) {
              buf.append(jtype);
           } else {
           	  String t=genTypeName(evaltype.trim(), null);
              buf.append(t);
              convertFun="to_"+t;
           }
           if(index!=null) {
              buf.append("[]");
           }
        } else {
           if(evaltype!=null) {
           	  String t=genTypeName(evaltype.trim(), null);
              buf.append(t);
              if(index!=null) {
                 buf.append("[]");
              }
           } else {
           	 throw new XMLCodecException("no type");
           }
        }
        
        buf.append(" get_");
        buf.append(id!=null ? id : defName);
        buf.append("() throws XMLCodecException {\n");
        if(byteOrder!=null) {
           buf.append("    setByteOrder(\"");
           buf.append(byteOrder);
           buf.append("\");\n");
        }
        
        if(jtype!=null) {
        
        if(index!=null) {
           String start=getRange(index, true);
           String stop =getRange(index, false);
           
           buf.append("    ");
           buf.append("int length=");
           buf.append("(");
           buf.append(stop);
           buf.append(")-(");
           buf.append(start);
           buf.append(")+1;\n");
           
           buf.append("    ");
           buf.append(jtype);
           buf.append(" theResult[]=new ");
           buf.append(jtype);
           buf.append("[length];\n");
           
           buf.append("    ");
           buf.append("for(int j=0, index=(");
           buf.append(start);
           buf.append(") ; index<=length ; index++, j++) {\n");
           buf.append("    ");
           buf.append("    theResult[j]=");
           buf.append(genCallReadFunction(jtype, offset, width));
           buf.append(";\n    }\n");
        } else if(offset!=null) {
        	if( cached ) {
        		buf.append("    if( " + cacheVarName + " < 0 ) {\n");
        		buf.append("        " + cacheVarName + " = ");
        		buf.append(genCallReadFunction(jtype, offset, width));
        		buf.append(";\n    }\n");
        	} else {
	           buf.append("    "+jtype);
	           buf.append(" theResult=");
	           buf.append(genCallReadFunction(jtype, offset, width));
	           buf.append(";\n");
        	}
        } 
       
        if(eval_!=null) {
           buf.append("    return ");
           if(convertFun!=null) {
           	  buf.append(convertFun);
           	  buf.append('(');
           }
           buf.append(eval_);
           if(convertFun!=null) {
           	  buf.append(')');
           } 	
           buf.append(";\n");
        } else {
           if(convertFun!=null) {
           	  buf.append("    return ");
           	  buf.append(convertFun);
           	  buf.append("(theResult);\n");
           } else {
        	   if( cached ) {
        		   buf.append( "    return " + cacheVarName + ";\n");
        	   } else {
        		   buf.append("    return theResult;\n");
        	   }
           
          }
        }
       } else {
       	 if(index!=null) {
       	   String t=genTypeName(evaltype.trim(), null);
           String start=getRange(index, true);
           String stop =getRange(index, false);
           
           buf.append("    ");
           buf.append("int length=");
           buf.append("(");
           buf.append(stop);
           buf.append(")-(");
           buf.append(start);
           buf.append(")+1;\n");
           
           buf.append("    ");
           buf.append(t);
           buf.append(" theResult[]=new ");
           buf.append(t);
           buf.append("[length];\n");
           
           buf.append("    ");
           buf.append("for(int j=0, index=(");
           buf.append(start);
           buf.append(") ; index<=length ; index++, j++) {\n");
           buf.append("    ");
           buf.append("    theResult[j]=");
          
           buf.append(eval_);	
        }
        buf.append(";\n    }\n");
        buf.append("    return theResult;\n");
       }
       buf.append("}\n");
        
        return identLines(buf.toString())+"\n";
     }
     
     return "";
  }  
    
  @SuppressWarnings("unused")
  private String genMethod(String path, String defName) throws XMLCodecException {
     return genProperty(getNode(path), defName);
  }  
    
  private String genCheckMethod(String defName, boolean ret) {
  	StringBuffer buf=new StringBuffer();
  	
  	buf.append("public boolean is_");
  	buf.append(defName);
  	buf.append("() {\n    return ");
  	buf.append(ret ? "true;\n}\n" : "false;\n}\n");
  	return identLines(buf.toString())+"\n";
  }

  private String genDefaultAccessors(String defName) {
  	  StringBuffer buf=new StringBuffer();
  	  String type=null;
  	  
  	  if(defName.equals("number_of_channels")) {
  	  	type="int";
  	  } else if(defName.equals("sampling_frequency")) {
  	  	type="float";
  	  }
  	  
  	  if(type!=null) {
  	     buf.append("private ");
  	     buf.append(type);
  	     buf.append(" m_");
  	     buf.append(defName);
  	     buf.append(";\n\n");
  	     
  	     buf.append("public ");
  	     buf.append(type);
  	     buf.append(" get_");
  	     buf.append(defName);
  	     buf.append("() {\n");
  	     buf.append("    return m_");
  	     buf.append(defName);
  	     buf.append(";\n}\n\n");
  	     
  	     buf.append("public void");
  	     buf.append(" set_");
  	     buf.append(defName);
  	     buf.append("(");
  	     buf.append(type);
  	     buf.append(" value) {\n");
  	     buf.append("    m_");
  	     buf.append(defName);
  	     buf.append("=value;\n}\n\n");
  	     
  	     return identLines(buf.toString())+"\n";
      } 
      return "";
  }
    
  private String genMethodWithCheck(String path, String defName) throws XMLCodecException {
  	 Node node=getNode(path);
  	 if(node!=null) {
        return genCheckMethod(defName, true)+genProperty(node, defName);
     } else {
        return genCheckMethod(defName, false)+genDefaultAccessors(defName);
     }
  }   
    
  private String genReqMethods() throws XMLCodecException {
      StringBuffer buf=new StringBuffer();
      
      try {
         buf.append(genMethodWithCheck(TAG_MAXCHN ,"number_of_channels"));
      } catch(XMLCodecException e) {
      	 ; 
      }
      
      try {
         buf.append(genMethodWithCheck(TAG_SAMP,   "sampling_frequency"));
      } catch(XMLCodecException e) {
      	 ;
      }
      
      try {
         buf.append(genMethodWithCheck(TAG_CALIB,  "calibration"));
      } catch(XMLCodecException e) {
      	 ;
      }
      
      try {
         buf.append(genMethodWithCheck(TAG_CHANS,  "channel_names"));
      } catch(XMLCodecException e) {
      	 ;
      }
      
      return buf.toString();
  }
    
  private String genConstraintMethods() throws XMLCodecException {
     StringBuffer buf=new StringBuffer();
     Node node[]=getNodes(TAG_CONSTRAINT);
     
     buf.append("private boolean getConstraints() throws XMLCodecException {\n");
     if(node!=null) {
        int len=node.length;
        for(int i=0 ; i<len ; i++) {
            String test=eval(getAttribute(node[i], "test"));
            if(test==null) {
               continue;
            }
            
            buf.append("   ");
            buf.append("if(!(");
            buf.append(test);
            buf.append(")) {\n     return false;\n   }\n");
        }
     }
     buf.append("   ");
     buf.append("return true;\n");
     buf.append("}");
     return identLines(buf.toString());
  } 
    
  private String genUserMethods() throws XMLCodecException {
     StringBuffer buf=new StringBuffer();
     Node node[]=getNodes(TAG_PROPERTY);
     
     if(node!=null) {
        int len=node.length;
        for(int i=0 ; i<len ; i++) {
            buf.append(genProperty(node[i], "method_"+(i+1)));
        }
     }
     return buf.toString();
  }

  private String genInitData() throws XMLCodecException {
  	StringBuffer buf=new StringBuffer();
  	 Node node=getNode(TAG_DATA);
  	 
  	 if(node!=null) {
  	   String frame_type=getAttribute(node,  "frame_type");
  	   if(frame_type!=null) {
  	   	  if(frame_type.equals("edf_frame")) {
  	        buf.append("private boolean m_edf_init=false;\n");
  	        buf.append("private int m_max_offset;\n");
  	        
  	        buf.append("public void init() throws XMLCodecException {\n"); 
  	        buf.append("   if(!m_edf_init) {\n");
       	    buf.append("      init_edf(get_number_of_channels(), get_nr_of_samples());\n");
       	    buf.append("      m_edf_init=true;\n");
       	    buf.append("   }\n");
       	    buf.append("   m_max_offset=get_number_of_data_records()*getMaxRecordSamples();");
       	    buf.append("   if(m_max_offset<0) m_max_offset=4*1024;");
       	    buf.append("}\n\n");
  	        
  	        buf.append("public int get_max_offset() throws XMLCodecException {\n");
  	        buf.append("   return m_max_offset;\n");
  	        buf.append("}\n");
  	        return identLines(buf.toString());
  	      } else if(frame_type.equals("multiplex")) {
  	      	String offset=eval(getAttribute(node, "offset"));
            String sample_type=getAttribute(node, "sample_type");
            
            if(sample_type==null || offset==null) {
               throw new XMLCodecException("sample type or offset not found !");
            }
            
            boolean hasSamplesInFile = false;
            Node sifNode[]=getNodes(TAG_PROPERTY);
            
            if(sifNode!=null) {
               int len=sifNode.length;
               for(int i=0 ; i<len ; i++) {
                   String sifId = getAttribute(sifNode[i],"id");
                   if( sifId.equals("SamplesInFile") ) {
                	   hasSamplesInFile = true;
                	   break;
                   }                   	            	                
               }
            }
            
            if( !hasSamplesInFile ) {
	            buf.append("public int get_max_offset() throws XMLCodecException {\n");
	            buf.append("   long __offset__=(getFileLength()-(");
	            buf.append(offset);
	            buf.append("))/(get_number_of_channels()*");
	            buf.append(getTypeSize(sample_type));
	            buf.append(");\n");
	  	        buf.append("   return (int)__offset__;\n");
	  	        buf.append("}\n");
            } else {
	            buf.append("public int get_max_offset() throws XMLCodecException {\n");
	            buf.append("	return get_SamplesInFile()-1;\n");
	  	        buf.append("}\n");
            }
  	        return identLines(buf.toString());
  	      }
  	   } else {
  	   	  throw new XMLCodecException("unknown frame type: "+frame_type);
  	   }
  	 }
  	 
  	 return "";
  }

  private String genChannelDataFormat() throws XMLCodecException {
  	 StringBuffer buf=new StringBuffer();
  	 Node node=getNode(TAG_DATA);
  	 
  	 if(node!=null) {
        String offset=eval(getAttribute(node, "offset"));
        String frame_type=getAttribute(node,  "frame_type");
        String sample_type=getAttribute(node, "sample_type");
       
        if(frame_type==null) {
          throw new XMLCodecException("no data !");
        }
  	 
  	 	buf.append("public float getChannelSample(long offset, int chn) throws XMLCodecException {\n");
        buf.append("   ");
        if(frame_type.equals("multiplex")) {
         if(sample_type==null || offset==null) {
            throw new XMLCodecException("sample type or offset not found !");
         }
         buf.append("return getMultiplexChannelSample_");
         buf.append(genTypeName(sample_type.trim(), null));
         buf.append("(");
         buf.append(offset);
         buf.append(", offset, chn, get_number_of_channels());\n");
       } else if(frame_type.equals("edf_frame")) {
         buf.append("   return getEDFChannelSample(offset, chn);\n");
       } else if(frame_type.equals("user_frame")) {
         // TODO ???
       } else {
         throw new XMLCodecException("unknown frame type: "+frame_type);
       }
       
       buf.append("}");
  	   return identLines(buf.toString());
     }
     
     return "";
  }

  private String genDataFormat() throws XMLCodecException {
     StringBuffer buf=new StringBuffer();
     Node node=getNode(TAG_DATA);
     
     if(node!=null) {
       String offset=eval(getAttribute(node, "offset"));
       String frame_type=getAttribute(node,  "frame_type");
       String sample_type=getAttribute(node, "sample_type");
       
       if(frame_type==null) {
          throw new XMLCodecException("no data !");
       }
       
       buf.append("public float[] getSample(long offset) throws XMLCodecException {\n");
       buf.append("   ");
       if(frame_type.equals("multiplex")) {
         if(sample_type==null || offset==null) {
            throw new XMLCodecException("sample type or offset not found !");
         }
         buf.append("return getMultiplexSample_");
         buf.append(genTypeName(sample_type.trim(), null));
         buf.append("(");
         buf.append(offset);
         buf.append(", offset, get_number_of_channels());\n");
       } else if(frame_type.equals("edf_frame")) {
         buf.append("return null;\n");
       } else if(frame_type.equals("user_frame")) {
         // TODO ???
       } else {
         throw new XMLCodecException("unknown frame type: "+frame_type);
       }
       buf.append("}");
       return identLines(buf.toString());
     } else {
       throw new XMLCodecException(TAG_DATA+" not found !");
     }
  }
  
  /*
  public static void main(String args[]) {
      try {
        System.out.println(compile(
        	  //"C:\\XMLGen\\XML-Prop\\meta_EDF.xml"
        	 "C:\\Documents and Settings\\Dobi\\Projekty\\XMLGen\\Integracja\\SignalML\\data\\sample\\meta_EASYS.xml"
        	// "C:\\XMLGen\\XML-Prop\\meta_EDF.xml"
        //"C:\\XMLGen\\XML-Prop\\meta_FORMAT_example.xml"));
      } catch(Exception e) {
        e.printStackTrace();
      }
  }
  */
  
} 

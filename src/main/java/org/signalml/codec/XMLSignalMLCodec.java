/* XMLSignalMLCodec.java created 2007-09-18
 * 
 */

package org.signalml.codec;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.codec.generator.xml.Codec;
import org.signalml.codec.generator.xml.XMLCodecException;
import org.signalml.compilation.CompilationException;
import org.signalml.compilation.CompilationRefusedException;
import org.signalml.compilation.DynamicCompilationContext;
import org.signalml.compilation.DynamicCompiler;
import org.signalml.compilation.JavaCodeProvider;
import org.signalml.util.Util;

/** XMLSignalMLCodec
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.<br>
 * 		based on code Copyright (C) 2003 Dobieslaw Ircha <dircha@eranet.pl> Artur Biesiadowski <abies@adres.pl> Piotr J. Durka     <Piotr-J.Durka@fuw.edu.pl>
 */
public class XMLSignalMLCodec extends AbstractSignalMLCodec {
		
	protected static final Logger logger = Logger.getLogger(XMLSignalMLCodec.class);
	
	private File sourceFile;
	private String cacheDirName;
	private String sourceSignature; // signature of xml file
	private Codec codec;
	
	private File repositoryDir;
	private Class<?> readerDelegateClass = null;
		
	public XMLSignalMLCodec(File sourceFile, File repositoryDir) throws IOException, XMLCodecException {
		super();
		this.sourceFile = sourceFile;
		this.sourceSignature = Util.getFileSignature(sourceFile);
		this.repositoryDir = repositoryDir;

		codec = new Codec(sourceFile.getAbsolutePath());
		setFormatName(codec.getFormatName());
	}

	public XMLSignalMLCodec(String formatName, String cacheDirName, File repositoryDir, String sourceSignature, File sourceFile) throws XMLCodecException {
		super(formatName);
		this.cacheDirName = cacheDirName;
		this.sourceSignature = sourceSignature;
		this.repositoryDir = repositoryDir;
		this.sourceFile = sourceFile;
		
		codec = new Codec(sourceFile.getAbsolutePath());
	}
	
	public File getSourceFile() {
		synchronized( this ) {
			return sourceFile;
		}
	}	
		
	public File getRepositoryDir() {
		synchronized( this ) {
			if( repositoryDir == null ) {
				return new File( System.getProperty("java.io.tmpdir") );
			}
			return repositoryDir;
		}
	}

	@Override
	public String getSourceUID() {
		synchronized( this ) {
			return sourceSignature;
		}
	}
	
	public String getCacheDirName() {
		synchronized( this ) {
			if( cacheDirName == null ) {
				cacheDirName = getCacheDirName(getFormatName());
			}
			return cacheDirName;
		}
	}

	public String getSourceSignature() {
		synchronized( this ) {
			return sourceSignature;
		}
	}

	@Override
	public Class<?> getReaderDelegateClass() throws SignalMLCodecException {
		
		synchronized( this ) {

			if( readerDelegateClass == null ) {
			
				File repositoryDir = getRepositoryDir();
		
				logger.debug("Using repository dir [" + repositoryDir.getAbsolutePath() + "]");
				
				logger.debug("Using cacheDirName [" + getCacheDirName() + "]");
				
				File srcDir = new File( getRepositoryDir(), "smlcache-" + System.getProperty("user.name") + Util.FILE_SEP + cacheDirName );
				if( !srcDir.exists() ) {
					boolean ok = srcDir.mkdirs();
					if( !ok ) {
						logger.error("Compilation failed - failed to create cache dir");
						throw new SignalMLCodecException("error.codecCompilationFailed");										
					}
				}
				
				DynamicCompiler dynamicCompiler = DynamicCompilationContext.getSharedInstance().getCompiler();
									
				try {
					readerDelegateClass = dynamicCompiler.compile(
							srcDir, 
							"org.signalml.codec." + getFormatName(),
							new JavaCodeProvider() {
								@Override
								public String getCode() throws CompilationException {
									String code;
									try {
										code = Codec.compile(codec);
									} catch( XMLCodecException ex ) {
										logger.error("Code generation failed", ex);					
										throw new CompilationException("error.codecCompilationFailed",ex);
									}
									return code;
								}
								
							}
					);
				} catch (CompilationRefusedException ex ) {
					logger.info( "Compilation refused by user" );
					throw new SignalMLCodecException(ex.getMessage());
				} catch (CompilationException ex) {
					logger.error("Compilation failed", ex);
					throw new SignalMLCodecException("error.codecCompilationFailed",ex);
				}
				
			}
			
			return readerDelegateClass;
			
		}
				
	}
	
	private String getCacheDirName(String formatName) {
		return Util.getRandomHexString(10) + "_" + formatName.replaceAll("[^a-zA-Z0-9_]+", "_");
	}
	
}

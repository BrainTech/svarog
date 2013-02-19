/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.signalml.app.util.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.rolling.RollingPolicyBase;
import org.apache.log4j.rolling.RolloverDescription;
import org.apache.log4j.rolling.RolloverDescriptionImpl;
import org.apache.log4j.rolling.TriggeringPolicy;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.rolling.helper.ActionBase;
import org.apache.log4j.rolling.helper.FileRenameAction;
import org.apache.log4j.rolling.helper.GZCompressAction;
import org.apache.log4j.rolling.helper.ZipCompressAction;
import org.apache.log4j.spi.LoggingEvent;
import org.signalml.util.FormatUtils;

/**
 * <code>DeletingFilesTimeBasedRollingPolicy</code> works the same as the
 * {@link org.apache.log4j.rolling.TimeBasedRollingPolicy}, but adds
 * a special functionality of deleting log files older than the specified
 * number of days.
 *
 * The number of days can be specified in the <code>maxDaysToKeep</code> parameter.
 */
public class DeletingFilesTimeBasedRollingPolicy extends RollingPolicyBase
		implements TriggeringPolicy {

	protected static final Logger logger = Logger.getLogger(DeletingFilesTimeBasedRollingPolicy.class);

	/*
	 * A lot of the following code was copied from the original repository,
	 * because the TimeBasedRollingPolicy is defined as final there, so
	 * inheritance was not possible. :(
	 */

	/**
	 * Time for next determination if time for rollover.
	 */
	private long nextCheck = 0;

	/**
	 * File name at last rollover.
	 */
	private String lastFileName = null;

	/**
	 * Length of any file type suffix (.gz, .zip).
	 */
	private int suffixLength = 0;

	private int maxDaysToKeep = -1;

	/**
	 * Constructs a new instance.
	 */
	public DeletingFilesTimeBasedRollingPolicy() {
	}

	/**
	 * Prepares instance of use.
	 */
	public void activateOptions() {
		super.activateOptions();

		PatternConverter dtc = getDatePatternConverter();

		if (dtc == null) {
			throw new IllegalStateException("FileNamePattern ["
					+ getFileNamePattern()
					+ "] does not contain a valid date format specifier");
		}

		long n = System.currentTimeMillis();
		StringBuffer buf = new StringBuffer();
		formatFileName(new Date(n), buf);
		lastFileName = buf.toString();

		suffixLength = 0;

		if (lastFileName.endsWith(".gz")) {
			suffixLength = 3;
		} else if (lastFileName.endsWith(".zip")) {
			suffixLength = 4;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RolloverDescription initialize(final String currentActiveFile,
			final boolean append) {
		long n = System.currentTimeMillis();
		nextCheck = ((n / 1000) + 1) * 1000;

		StringBuffer buf = new StringBuffer();
		formatFileName(new Date(n), buf);
		lastFileName = buf.toString();

		//
		// RollingPolicyBase.activeFileName duplicates RollingFileAppender.file
		// and should be removed.
		//
		if (activeFileName != null) {
			return new RolloverDescriptionImpl(activeFileName, append, null,
					null);
		} else if (currentActiveFile != null) {
			return new RolloverDescriptionImpl(currentActiveFile, append, null,
					null);
		} else {
			return new RolloverDescriptionImpl(lastFileName.substring(0,
					lastFileName.length() - suffixLength), append, null, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RolloverDescription rollover(final String currentActiveFile) {
		long n = System.currentTimeMillis();
		nextCheck = ((n / 1000) + 1) * 1000;

		StringBuffer buf = new StringBuffer();
		formatFileName(new Date(n), buf);

		String newFileName = buf.toString();

		//delete old files
		File currentDirectory = new File(currentActiveFile).getParentFile();
		try {
			new DeleteOldMyAppLogFilesInDirAction(currentDirectory, maxDaysToKeep).execute();
		} catch (IOException e) {
			logger.error("Deleting old files was unsuccessful", e);
			e.printStackTrace();
		}
		//
		// if file names haven't changed, no rollover
		//
		if (newFileName.equals(lastFileName)) {
			return null;
		}

		Action renameAction = null;
		Action compressAction = null;
		String lastBaseName = lastFileName.substring(0, lastFileName.length()
				- suffixLength);
		String nextActiveFile = newFileName.substring(0, newFileName.length()
				- suffixLength);

		//
		// if currentActiveFile is not lastBaseName then
		// active file name is not following file pattern
		// and requires a rename plus maintaining the same name
		if (!currentActiveFile.equals(lastBaseName)) {
			renameAction = new FileRenameAction(new File(currentActiveFile),
					new File(lastBaseName), true);
			nextActiveFile = currentActiveFile;
		}

		if (suffixLength == 3) {
			compressAction = new GZCompressAction(new File(lastBaseName),
					new File(lastFileName), true);
		}

		if (suffixLength == 4) {
			compressAction = new ZipCompressAction(new File(lastBaseName),
					new File(lastFileName), true);
		}

		lastFileName = newFileName;

		// defining actions
		return new RolloverDescriptionImpl(nextActiveFile, false, renameAction, compressAction);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTriggeringEvent(final Appender appender,
			final LoggingEvent event, final String filename,
			final long fileLength) {
		return System.currentTimeMillis() >= nextCheck;
	}

	public int getMaxDaysToKeep() {
		return maxDaysToKeep;
	}

	public void setMaxDaysToKeep(int maxDaysToKeep) {
		this.maxDaysToKeep = maxDaysToKeep;
	}

	/**
	 * Deletes files older than the specified number of days.
	 */
	private static class DeleteOldMyAppLogFilesInDirAction extends ActionBase {

		private File dir;
		private long maxHistoryInMiliseconds;
		private int maxDaysToKeep;

		public DeleteOldMyAppLogFilesInDirAction(File dir, int maxDaysToKeep) {
			this.dir = dir;
			this.maxDaysToKeep = maxDaysToKeep;
			this.maxHistoryInMiliseconds = maxDaysToKeep * 24 * 60 * 60 * 1000;
		}

		@Override
		public boolean execute() throws IOException {
			if (maxHistoryInMiliseconds < 0)
				return true;

			for (File f : dir.listFiles()) {
				long lastModified = f.lastModified();

				String fileName = f.getName();
				long thresholdTimeMs = System.currentTimeMillis() - this.maxHistoryInMiliseconds;

				if ((fileName.endsWith(".log") || fileName.endsWith(".zip") || fileName.endsWith(".zip"))
						&& lastModified < thresholdTimeMs) {
					f.delete();
					logger.debug("Deleting file " + f.toString() + " because it was last modified at "
							+ FormatUtils.formatTime(new Date(lastModified)) +
							" and we only want to keep files younger than " + maxDaysToKeep + " days (i.e. younger than "
							+ FormatUtils.formatTime(new Date(thresholdTimeMs)) + ").");
				}
			}
			return true;
		}

	}
}

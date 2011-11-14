/* SvarogConstants.java created 2008-01-17
 *
 */

package org.signalml.util;

import java.awt.Dimension;
import java.util.Locale;
import java.util.Scanner;

/**
 * SvarogConstants contains general project constants.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SvarogConstants {
	/**
	 * Name of the project
	 */
	public static final String NAME = "Svarog";

	/**
	 * Actual version of the project. This is generated using git-describe.
	 * The format is {lastest-tag}[-{commits-since-the-tag}-{sha1}[{+ if dirty}]].
	 */
	public static final String VERSION =
	    new Scanner(SvarogConstants.class.getResourceAsStream("/svarog/git-version")).next();

	/**
	 * Recommended minimal dimension of the screen
	 */
	public static final Dimension MIN_ASSUMED_DESKTOP_SIZE = new Dimension(1024,768);

	/**
	 * Available languages
	 */
	public static Locale[] AVAILABLE_LOCALES = new Locale[] {Locale.ENGLISH, new Locale("pl")};

	/** resource bundle base name */
	public static final String I18nCatalogId = "org.signalml.app.i18n.I18nBundle";

}

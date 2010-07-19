/* SleepTagName.java created 2008-02-21
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;
import java.util.HashMap;

import org.signalml.app.document.TagDocument;

/**
 * This is an abstract class with only static methods and attributes.
 * Associates sleep stages with their levels and colours.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SleepTagName {

	public static final String RK_WAKE = "w";
	public static final String RK_MT = "m";
	public static final String RK_1 = "1";
	public static final String RK_2 = "2";
	public static final String RK_3 = "3";
	public static final String RK_4 = "4";
	public static final String RK_REM = "r";

	public static final String AASM_WAKE = "nw";
	public static final String AASM_N1 = "n1";
	public static final String AASM_N2 = "n2";
	public static final String AASM_N3 = "n3";
	public static final String AASM_REM = "nr";

	public static int RK_LEVEL_WAKE = 1;
	public static int RK_LEVEL_MT = 0;
	public static int RK_LEVEL_1 = 2;
	public static int RK_LEVEL_2 = 4;
	public static int RK_LEVEL_3 = 5;
	public static int RK_LEVEL_4 = 6;
	public static int RK_LEVEL_REM = 3;

	public static int AASM_LEVEL_WAKE = 1;
	public static int AASM_LEVEL_N1 = 2;
	public static int AASM_LEVEL_N2 = 4;
	public static int AASM_LEVEL_N3 = 5;
	public static int AASM_LEVEL_REM = 3;

	public static Color RK_COLOR_WAKE = Color.BLUE;
	public static Color RK_COLOR_MT = null;
	public static Color RK_COLOR_1 = null;
	public static Color RK_COLOR_2 = null;
	public static Color RK_COLOR_3 = null;
	public static Color RK_COLOR_4 = null;
	public static Color RK_COLOR_REM = Color.RED;

	public static Color AASM_COLOR_WAKE = new Color(0xffff00);
	public static Color AASM_COLOR_N1 = new Color(0x00cccc);
	public static Color AASM_COLOR_N2 = new Color(0x99ff00);
	public static Color AASM_COLOR_N3 = new Color(0xff9966);
	public static Color AASM_COLOR_REM = new Color(0xcc00cc);

        /**
         * HashMap associating sleep stages with their levels
         */
	private static final HashMap<String,Integer> levelMap;

        /**
         * HashMap associating sleep stages with their colours
         */
	private static final HashMap<String,Color> colorMap;

	static {

		levelMap = new HashMap<String, Integer>();
		colorMap = new HashMap<String, Color>();

		levelMap.put(RK_WAKE, RK_LEVEL_WAKE);
		levelMap.put(RK_MT, RK_LEVEL_MT);
		levelMap.put(RK_1, RK_LEVEL_1);
		levelMap.put(RK_2, RK_LEVEL_2);
		levelMap.put(RK_3, RK_LEVEL_3);
		levelMap.put(RK_4, RK_LEVEL_4);
		levelMap.put(RK_REM, RK_LEVEL_REM);

		levelMap.put(AASM_WAKE, AASM_LEVEL_WAKE);
		levelMap.put(AASM_N1, AASM_LEVEL_N1);
		levelMap.put(AASM_N2, AASM_LEVEL_N2);
		levelMap.put(AASM_N3, AASM_LEVEL_N3);
		levelMap.put(AASM_REM, AASM_LEVEL_REM);

		colorMap.put(RK_WAKE, RK_COLOR_WAKE);
		colorMap.put(RK_MT, RK_COLOR_MT);
		colorMap.put(RK_1, RK_COLOR_1);
		colorMap.put(RK_2, RK_COLOR_2);
		colorMap.put(RK_3, RK_COLOR_3);
		colorMap.put(RK_4, RK_COLOR_4);
		colorMap.put(RK_REM, RK_COLOR_REM);

		colorMap.put(AASM_WAKE, AASM_COLOR_WAKE);
		colorMap.put(AASM_N1, AASM_COLOR_N1);
		colorMap.put(AASM_N2, AASM_COLOR_N2);
		colorMap.put(AASM_N3, AASM_COLOR_N3);
		colorMap.put(AASM_REM, AASM_COLOR_REM);

	}

        /**
         * Returns the level of a given sleep stage
         * @param name the name of the sleep stage
         * @return the level of a given sleep stage
         */
	public static int getLevel(String name) {
		Integer integer = levelMap.get(name);
		return (integer == null ? 0 : integer);
	}

        /**
         * Returns the colour for the given sleep stage
         * @param name the name of the sleep stage
         * @return the colour for the given sleep stage
         */
	public static Color getColor(String name) {
		return colorMap.get(name);
	}

        /**
         * Returns whether a given stage is a wake stage
         * @param name the name of the sleep stage
         * @return true if a given stage is a wake stage, false otherwise
         */
	public static boolean isWake(String name) {
		return (RK_WAKE.equals(name) || AASM_WAKE.equals(name));
	}

        /**
         * Returns whether a given stage is a REM stage
         * @param name the name of the sleep stage
         * @return true if a given stage is a REM stage, false otherwise
         */
	public static boolean isREM(String name) {
		return (RK_REM.equals(name) || AASM_REM.equals(name));
	}

        /**
         * Returns whether a given stage is a slow wave stage
         * @param name the name of the sleep stage
         * @return true if a given stage is a slow wave stage, false otherwise
         */
	public static boolean isSlowWave(String name) {
		return (RK_3.equals(name) || RK_4.equals(name) || AASM_N3.equals(name));
	}

        /**
         * Returns whether a given stage is a proper sleep stage
         * @param name the name of the sleep stage
         * @return true if a given stage is a proper sleep stage, false otherwise
         */
	public static boolean isPropperSleep(String name) {
		return (
		               RK_2.equals(name) || RK_3.equals(name) || RK_4.equals(name) || RK_REM.equals(name)
		               || AASM_N2.equals(name) || AASM_N3.equals(name) || AASM_REM.equals(name)
		       );
	}

        /**
         * Returns whether a given stage is a sleep stage
         * @param name the name of the sleep stage
         * @return true if a given stage is a sleep stage, false otherwise
         */
	public static boolean isAnySleep(String name) {
		return (isPropperSleep(name) || RK_1.equals(name) || AASM_N1.equals(name));
	}

        /**
         * Returns whether a given tag is a valid tag for RK sleep
         * @param tag the tag which validity is to be checked
         * @return true if a given tag is a valid tag for RK sleep,
         * false otherwise
         */
	public static boolean isValidRKSleepTag(TagDocument tag) {

		StyledTagSet tagSet = tag.getTagSet();

		TagStyle style;
		style = tagSet.getStyle(RK_WAKE);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_1);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_2);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_3);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_4);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_REM);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(RK_MT);
		if (style == null || !style.getType().isPage()) {
			return false;
		}

		return true;

	}

        /**
         * Returns whether a given tag is a valid tag for AAMS sleep
         * @param tag the tag which validity is to be checked
         * @return true if a given tag is a valid tag for AAMS sleep,
         * false otherwise
         */
	public static boolean isValidAASMSleepTag(TagDocument tag) {

		StyledTagSet tagSet = tag.getTagSet();

		TagStyle style;
		style = tagSet.getStyle(AASM_WAKE);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(AASM_N1);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(AASM_N2);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(AASM_N3);
		if (style == null || !style.getType().isPage()) {
			return false;
		}
		style = tagSet.getStyle(AASM_REM);
		if (style == null || !style.getType().isPage()) {
			return false;
		}

		return true;

	}

}

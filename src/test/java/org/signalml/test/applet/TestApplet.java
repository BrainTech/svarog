/* TestApplet.java created 2008-02-20
 *
 */

package org.signalml.test.applet;

import javax.swing.JApplet;

/** TestApplet
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TestApplet extends JApplet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		super.init();

		System.out.println("User dir: [" + System.getProperty("user.dir") + "]");
	}

}

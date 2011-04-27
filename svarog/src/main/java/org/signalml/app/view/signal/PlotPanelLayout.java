/* PlotPanelLayout.java created 2007-11-20
 *
 */

package org.signalml.app.view.signal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/** PlotPanelLayout
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PlotPanelLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// do nothing
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// do nothing
	}

	@Override
	public void layoutContainer(Container parent) {

		Component[] comps = parent.getComponents();

		if (comps.length == 0) {
			return;
		}

		Dimension parentSize = parent.getSize();

		int[] heights = new int[comps.length];
		boolean[] ok = new boolean[comps.length];
		boolean end = false;
		int heightPerPlot = 0;
		int usedHeight = 0;
		Dimension prefSize = null;
		int compCnt = comps.length;
		int i;

		do {

			heightPerPlot = (parentSize.height - usedHeight) / compCnt;

			for (i=0; i<comps.length; i++) {

				if (!ok[i]) {

					prefSize = comps[i].getPreferredSize();
					if (prefSize.height <= heightPerPlot) {
						heights[i] = prefSize.height;
						usedHeight += heights[i];
						ok[i] = true;
						compCnt--;
						if (compCnt == 0) {
							end = true;
							continue;
						}
					}

				}

			}

			end = true;

		} while (!end);

		int totalHeight = 0;

		if (compCnt > 0) {
			heightPerPlot = (parentSize.height - usedHeight) / compCnt;

			for (i=0; i<comps.length; i++) {
				if (!ok[i]) {
					heights[i] = heightPerPlot;
				}
				totalHeight += heights[i];
			}
		} else {
			for (i=0; i<comps.length; i++) {
				totalHeight += heights[i];
			}
		}

		if (totalHeight < parentSize.height) {
			for (i=0; i<comps.length; i++) {
				if (!ok[i]) {
					heights[i] += (parentSize.height - totalHeight);
					break;
				}
			}
			if (i == comps.length) {
				heights[comps.length-1] += (parentSize.height - totalHeight);
			}
		}

		comps[0].setBounds(0, 0, parentSize.width, heights[0]);
		totalHeight = heights[0];
		for (i=1; i<comps.length; i++) {
			comps[i].setBounds(0, totalHeight, parentSize.width, heights[i]);
			totalHeight += heights[i];
		}

	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {

		Component[] comps = parent.getComponents();

		Dimension size = new Dimension(0,0);
		Dimension plotSize;

		for (Component comp : comps) {
			plotSize = comp.getMinimumSize();
			if (plotSize.width > size.width) {
				size.width = plotSize.width;
			}
			size.height += plotSize.height;
		}
		return size;

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {

		Component[] comps = parent.getComponents();

		Dimension size = new Dimension(0,0);
		Dimension plotSize;

		for (Component comp : comps) {
			plotSize = comp.getPreferredSize();
			if (plotSize.width > size.width) {
				size.width = plotSize.width;
			}
			size.height += plotSize.height;
		}
		return size;

	}

}

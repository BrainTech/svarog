/* WorkspaceBookPlot.java created 2007-02-23
 *
 */

package org.signalml.app.config;

import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.GrayscaleMapPalette;
import org.signalml.app.view.book.RainbowMapPalette;
import org.signalml.app.view.book.WignerMapPalette;
import org.signalml.domain.book.WignerMapScaleType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** WorkspaceBookPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacebookplot")
public class WorkspaceBookPlot {

	private WignerMapPalette palette;
	private WignerMapScaleType scaleType;

	private boolean signalAntialiased;
	private boolean originalSignalVisible;
	private boolean fullReconstructionVisible;
	private boolean reconstructionVisible;
	private boolean legendVisible;
	private boolean scaleVisible;
	private boolean axesVisible;
	private boolean atomToolTipsVisible;

	private int mapAspectRatioUp = 1;
	private int mapAspectRatioDown = 1;

	private int reconstructionHeight;

	protected WorkspaceBookPlot() {
	}

	public WorkspaceBookPlot(BookPlot plot) {

		palette = plot.getPalette();
		scaleType = plot.getScaleType();

		signalAntialiased = plot.isSignalAntialiased();
		originalSignalVisible = plot.isOriginalSignalVisible();
		fullReconstructionVisible = plot.isFullReconstructionVisible();
		reconstructionVisible = plot.isReconstructionVisible();
		legendVisible = plot.isLegendVisible();
		scaleVisible = plot.isScaleVisible();
		axesVisible = plot.isAxesVisible();
		atomToolTipsVisible = plot.isAtomToolTipsVisible();

		mapAspectRatioUp = plot.getMapAspectRatioUp();
		mapAspectRatioDown = plot.getMapAspectRatioDown();

		reconstructionHeight = plot.getReconstructionHeight();

	}

	public void configurePlot(BookPlot plot) {

		if (palette != null) {
			if (palette instanceof RainbowMapPalette)
				palette = RainbowMapPalette.getInstance();
			else
				palette = GrayscaleMapPalette.getInstance();

			plot.setPalette(palette);
		}
		if (scaleType != null) {
			plot.setScaleType(scaleType);
		}

		plot.setSignalAntialiased(signalAntialiased);
		plot.setOriginalSignalVisible(originalSignalVisible);
		plot.setFullReconstructionVisible(fullReconstructionVisible);
		plot.setReconstructionVisible(reconstructionVisible);
		plot.setLegendVisible(legendVisible);
		plot.setScaleVisible(scaleVisible);
		plot.setAxesVisible(axesVisible);
		plot.setAtomToolTipsVisible(atomToolTipsVisible);

		plot.setMapAspectRatioUp(mapAspectRatioUp);
		plot.setMapAspectRatioDown(mapAspectRatioDown);

		plot.setReconstructionHeight(reconstructionHeight);

	}

}

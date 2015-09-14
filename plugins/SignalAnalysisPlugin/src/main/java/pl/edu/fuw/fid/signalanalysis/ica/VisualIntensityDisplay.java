package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import org.signalml.app.view.montage.visualreference.VisualReferenceBin;
import org.signalml.app.view.montage.visualreference.VisualReferenceDisplay;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.app.view.montage.visualreference.VisualReferenceSourceChannel;

/**
 * @author ptr@mimuw.edu.pl
 */
public class VisualIntensityDisplay extends VisualReferenceDisplay {

	private float[] intensities;

	public VisualIntensityDisplay(VisualReferenceModel model) {
		super(model);
	}

	public void setIntensities(float[] intensities) {
		this.intensities = intensities;
		repaint();
	}

	@Override
	protected void paintBinContents(VisualReferenceBin bin, Graphics2D g) {
		final float[] data = this.intensities;
		Iterator<VisualReferenceSourceChannel> it = bin.iterator();

		float max = 0;
		if (data != null) {
			for (float x : data) {
				max = Math.max(Math.abs(x), max);
			}
		}

		while (it.hasNext()) {
			VisualReferenceSourceChannel channel = it.next();
			Color fill = Color.WHITE;
			Color outline = Color.BLACK;
			int ch = channel.getChannel();
			if (data != null && max > 0 && ch < data.length) {
				float x = data[ch] / max;
				if (x > 0) {
					fill = new Color(1.0f, 1.0f-x, 1.0f-x);
				}
				if (x < 0) {
					fill = new Color(1.0f+x, 1.0f+x, 1.0f);
				}
			}
			paintGivenChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), fill, outline, false, g);
		}
	}

}

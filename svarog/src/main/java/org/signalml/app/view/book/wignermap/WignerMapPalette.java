package org.signalml.app.view.book.wignermap;

import javax.swing.Icon;

import org.signalml.app.view.book.palette.GrayscaleMapPalette;
import org.signalml.app.view.book.palette.IWignerMapPalette;
import org.signalml.app.view.book.palette.RainbowMapPalette;

import pl.edu.fuw.MP.Core.Book;
import pl.edu.fuw.MP.WignerMap.WignerMap;

/**
 * Enum holding all types of {@link IWignerMapPalette palettes} that
 * can be used for drawing a {@link WignerMap} for a {@link Book}.
 *
 * @author Piotr Szachewicz
 */
public enum WignerMapPalette implements IWignerMapPalette {
	RAINBOW(new RainbowMapPalette()),
	GRAYSCALE(new GrayscaleMapPalette());

	private IWignerMapPalette implementer;

	private WignerMapPalette(IWignerMapPalette palette) {
		this.implementer = palette;
	}

	@Override
	public String i18n() {
		return implementer.i18n();
	}

	@Override
	public Icon getIcon() {
		return implementer.getIcon();
	}

	@Override
	public int[] getPalette() {
		return implementer.getPalette();
	}

}

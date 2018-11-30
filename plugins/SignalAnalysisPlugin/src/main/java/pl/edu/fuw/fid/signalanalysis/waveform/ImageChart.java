package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Subclass of XYChart (from JFreeChart) adapted to display time-frequency data
 * as an image laid over an empty chart.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageChart extends XYChart<Number, Number> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageChart.class);

	private final ImageView imageView = new ImageView();
	private final ImageRenderer renderer;
	private final Runnable onResize;

	public ImageChart(Axis<Number> xAxis, Axis<Number> yAxis, ImageRenderer renderer, Runnable onResize) {
		super(xAxis, yAxis);
		this.renderer = renderer;
		setData(FXCollections.<Series<Number,Number>>emptyObservableList());
		this.imageView.setPreserveRatio(false);
		getPlotChildren().add(this.imageView);
		this.imageView.relocate(0, 0);
		this.onResize = onResize;
	}

	// must be called from JavaFX thread
	public void hideImage() {
		imageView.setOpacity(0.0);
	}

	// must be called from JavaFX thread
	public void showImage(Image image) {
		imageView.setImage(image);
		imageView.setOpacity(1.0);
	}

	public TimeFrequency getTimeFrequency(int x, int y) {
		return renderer.getTimeFrequency(x, y);
	}

	@Override
	protected void dataItemAdded(Series series, int itemIndex, Data item) {
		throw new UnsupportedOperationException(_("this operation is not supported"));
	}

	@Override
	protected void dataItemRemoved(Data item, Series series) {
		throw new UnsupportedOperationException(_("this operation is not supported"));
	}

	@Override
	protected void layoutPlotChildren() {
		imageView.setFitWidth(getXAxis().getWidth());
		imageView.setFitHeight(getYAxis().getHeight());
		onResize.run();
	}

	@Override
	protected void dataItemChanged(Data item) {
		throw new UnsupportedOperationException(_("this operation is not supported"));
	}

	@Override
	protected void seriesAdded(Series series, int seriesIndex) {
		throw new UnsupportedOperationException(_("this operation is not supported"));
	}

	@Override
	protected void seriesRemoved(Series series) {
		throw new UnsupportedOperationException(_("this operation is not supported"));
	}

	public void setOnCursorOnChart(EventHandler<? super MouseEvent> value) {
		imageView.setOnMouseMoved(value);
	}

	public void setOnCursorOffChart(EventHandler<? super MouseEvent> value) {
		imageView.setOnMouseExited(value);
	}

}

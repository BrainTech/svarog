package org.signalml.app.video.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.signalml.app.util.IconUtils;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;

/**
 * Panel consisting of a media player component (@see OnlineMediaPlayerComponent)
 * and a toolbar with PTZ buttons.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class OnlineMediaPlayerPanel extends JPanel {

	private static final double DEFAULT_PAN = 0.05;
	private static final double DEFAULT_TILT = 0.1;
	private static final double DEFAULT_ZOOM = 0.1;

	private final OnlineMediaComponent component;
	private final JToolBar toolbar;

	private final JButton[] panButtons = new JButton[2];
	private final JButton[] tiltButtons = new JButton[2];
	private final JButton[] zoomButtons = new JButton[2];
	private final JButton[] homeButtons = new JButton[1];
	private final JToggleButton[] nightModeButtons = new JToggleButton[3];

	private static interface CameraActionParams {
		public void add(CameraControlRequest request);
	}

	/**
	 * Create a new panel with an already created media player component.
	 * To enable some of the PTZ buttons, setCameraFeatures must be called.
	 *
	 * @param component  existing media player component, must not be used
	 * by more than one panel
	 */
	public OnlineMediaPlayerPanel(OnlineMediaComponent component) {
		super(new BorderLayout());
		this.component = component;
		this.toolbar = new JToolBar();
		toolbar.setFloatable(false);

		addTiltAndPanButtons();
		toolbar.addSeparator();
		addZoomButtons();
		toolbar.addSeparator();
		addHomeButton();
		toolbar.addSeparator();
		addNightModeButtons();
		setCameraFeatures(0);

		add(component, BorderLayout.CENTER);
		add(toolbar, BorderLayout.SOUTH);
	}

	/**
	 * Set camera features available for the currently displayed stream.
	 *
	 * @param features  bitmask of FEATURE_* constants from VideoStreamSpecification
	 */
	public void setCameraFeatures(int features) {
		setButtonsEnabled(panButtons,       0 != (features & VideoStreamSpecification.FEATURE_PAN));
		setButtonsEnabled(tiltButtons,      0 != (features & VideoStreamSpecification.FEATURE_TILT));
		setButtonsEnabled(zoomButtons,      0 != (features & VideoStreamSpecification.FEATURE_ZOOM));
		setButtonsEnabled(homeButtons,      0 != (features & VideoStreamSpecification.FEATURE_HOME));
		setButtonsEnabled(nightModeButtons, 0 != (features & VideoStreamSpecification.FEATURE_NIGHT_MODE));
	}

	private <T extends JComponent> void setButtonsEnabled(T[] buttons, boolean enabled) {
		for (T button : buttons) {
			button.setEnabled(enabled);
		}
	}

	private void addTiltAndPanButtons() {
		panButtons[0] = toolbar.add(createAction(
			"moveleft",
			"relative_pan",
			(CameraControlRequest request) -> {
				request.putArg("x", -DEFAULT_PAN);
			}
		));
		tiltButtons[0] = toolbar.add(createAction(
			"moveup",
			"relative_tilt",
			(CameraControlRequest request) -> {
				request.putArg("rot", DEFAULT_TILT);
			}
		));
		tiltButtons[1] = toolbar.add(createAction(
			"movedown",
			"relative_tilt",
			(CameraControlRequest request) -> {
				request.putArg("rot", -DEFAULT_TILT);
			}
		));
		panButtons[1] = toolbar.add(createAction(
			"moveright",
			"relative_pan",
			(CameraControlRequest request) -> {
				request.putArg("x", DEFAULT_PAN);
			}
		));
	}

	private void addZoomButtons() {
		zoomButtons[0] = toolbar.add(createAction(
			"zoomin",
			"relative_zoom",
			(CameraControlRequest request) -> {
				request.putArg("zoom", DEFAULT_ZOOM);
			}
		));
		zoomButtons[1] = toolbar.add(createAction(
			"zoomout",
			"relative_zoom",
			(CameraControlRequest request) -> {
				request.putArg("zoom", -DEFAULT_ZOOM);
			}
		));
	}

	private void addHomeButton() {
		homeButtons[0] = toolbar.add(createAction(
			"reset",
			"reset_ptz",
			null  // no parameters
		));
	}

	private void addNightModeButtons() {
		nightModeButtons[0] = new JToggleButton(createAction(
			"day",
			"set_night_mode",
			(CameraControlRequest request) -> {
				request.putArg("on", false);
			}
		));
		nightModeButtons[1] = new JToggleButton(createAction(
			"night",
			"set_night_mode",
			(CameraControlRequest request) -> {
				request.putArg("on", true);
			}
		));
		nightModeButtons[2] = new JToggleButton(createAction(
			"daynight",
			"set_night_mode",
			(CameraControlRequest request) -> {
				request.putArg("on", null);
			}
		));
		// we use button group so only one button may be in a "pressed" state
		ButtonGroup nightModeGroup = new ButtonGroup();
		for (JToggleButton button : nightModeButtons) {
			toolbar.add(button);
			nightModeGroup.add(button);
		}
	}

	private Action createAction(String iconName, String actionName, CameraActionParams params) {
		Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/"+iconName+".png");
		return new AbstractAction(null, icon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				CameraControlRequest request = new CameraControlRequest(actionName);
				if (params != null) {
					params.add(request);
				}
				component.sendCameraRequest(request);
			}
		};
	}

}

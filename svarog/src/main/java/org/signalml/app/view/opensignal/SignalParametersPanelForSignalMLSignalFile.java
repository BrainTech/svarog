package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.model.SignalMLCodecListModel;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.dialog.RegisterCodecDialog;
import org.signalml.app.view.element.FloatSpinner;
import org.signalml.app.view.element.IntegerSpinner;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;

/**
 * Contains {@link SignalMLOptionsPanel} and serves
 * as link between {@link SignalMLOptionsPanel} and
 * {@link FileSignalSourcePanel}.
 *
 * @author Tomasz Sawicki
 */
public class SignalParametersPanelForSignalMLSignalFile extends JPanel {

        /**
         * The element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * The original options panel.
         */
        private SignalMLOptionsPanel signalMLOptionsPanel;
        /**
         * The codec manager.
         */
        private SignalMLCodecManager codecManager;
        /**
         * The application config.
         */
        private ApplicationConfiguration applicationConfig;
        /**
         * The please wait dialog.
         */
        private PleaseWaitDialog pleaseWaitDialog;
        /**
         * The register codec dialog.
         */
        private RegisterCodecDialog registerCodecDialog;
        /**
         * The text field with the size of the page of signal in seconds.
         */
        private FloatSpinner pageSizeSpinner;
        /**
         * The text field with the number of blocks that fit into one page of
         * the signal.
         */
        private IntegerSpinner blocksPerPageSpinner;

        /**
         * Constructor initializes the panel.
         *
         * @param elementManager {@link #elementManager}
         */
        public SignalParametersPanelForSignalMLSignalFile(ViewerElementManager elementManager) {

                super();
                this.elementManager = elementManager;
                this.codecManager = elementManager.getCodecManager();
                this.applicationConfig = elementManager.getApplicationConfig();

                initialize();
        }

        /**
         * Creates interface and initializes the dialog.
         */
        private void initialize() {

                setLayout(new BorderLayout(0, 10));
                add(getSignalMLOptionsPanel(), BorderLayout.PAGE_START);

                JPanel pagingPanel = new JPanel(new BorderLayout());
                pagingPanel.setBorder(new CompoundBorder(
                        new TitledBorder(_("Paging parameters:")),
                        new EmptyBorder(3, 3, 3, 3)));

                JPanel parametersPanel = new JPanel(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.weighty = 0; constraints.gridwidth = 1;
                constraints.insets = new Insets(8, 8, 8, 8);

                JLabel pageSizeLabel = new JLabel(_("Page size: "));
                JLabel blocksPerPageLabel = new JLabel(_("Number of blocks per page: "));

                constraints.gridx = 0; constraints.gridy = 0; constraints.weightx = 0;
                parametersPanel.add(pageSizeLabel, constraints);
                constraints.gridx = 1; constraints.gridy = 0; constraints.weightx = 1;
                parametersPanel.add(getPageSizeSpinner(), constraints);

                constraints.gridx = 0; constraints.gridy = 1; constraints.weightx = 0;
                parametersPanel.add(blocksPerPageLabel, constraints);
                constraints.gridx = 1; constraints.gridy = 1; constraints.weightx = 1;
                parametersPanel.add(getBlocksPerPageSpinner(), constraints);

                pagingPanel.add(parametersPanel, BorderLayout.PAGE_START);
                add(pagingPanel, BorderLayout.CENTER);
        }

        /**
         * Returns the signalML options panel.
         * @return the signalML options panel
         */
        protected SignalMLOptionsPanel getSignalMLOptionsPanel() {

                if (signalMLOptionsPanel == null) {
                        signalMLOptionsPanel = new SignalMLOptionsPanel();
                        SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
                        codecListModel.setCodecManager(codecManager);

                        RegisterCodecAction registerCodecAction = new RegisterCodecAction();
                        registerCodecAction.setCodecManager(codecManager);
                        registerCodecAction.setRegisterCodecDialog(getRegisterCodecDialog());
                        registerCodecAction.setPleaseWaitDialog(getPleaseWaitDialog());
                        registerCodecAction.setApplicationConfig(applicationConfig);
                        registerCodecAction.initializeAll();

                        registerCodecAction.setSelector(new SignalMLCodecSelector() {

                                @Override
                                public SignalMLCodec getSelectedCodec() {
                                        return (SignalMLCodec) signalMLOptionsPanel.getSignalMLDriverComboBox().getSelectedItem();
                                }

                                @Override
                                public void setSelectedCodec(SignalMLCodec codec) {
                                        signalMLOptionsPanel.getSignalMLDriverComboBox().setSelectedItem(codec);
                                }
                        });

                        signalMLOptionsPanel.getSignalMLDriverComboBox().setModel(codecListModel);
                        signalMLOptionsPanel.getRegisterCodecButton().setAction(registerCodecAction);
                }
                return signalMLOptionsPanel;
        }

        /**
         * Returns the please wait dialog.
         * @return the please wait dialog
         */
        protected PleaseWaitDialog getPleaseWaitDialog() {
                if (pleaseWaitDialog == null) {
                        pleaseWaitDialog = new PleaseWaitDialog(null);
                        pleaseWaitDialog.initializeNow();
                }
                return pleaseWaitDialog;
        }

        /**
         * Returns the register codec dialog.
         * @return the register codec dialog
         */
        protected RegisterCodecDialog getRegisterCodecDialog() {
                if (registerCodecDialog == null) {
                        registerCodecDialog = new RegisterCodecDialog(null, true);
                        registerCodecDialog.setCodecManager(codecManager);
                }
                return registerCodecDialog;
        }

        /**
         * Returns the page size spinner.
         * @return the page size spinner
         */
        protected FloatSpinner getPageSizeSpinner() {

                if (pageSizeSpinner == null) {
                        pageSizeSpinner = new FloatSpinner(new SpinnerNumberModel(20.0F, 0.1F, 100000.0F, 0.1F));
                }
                return pageSizeSpinner;
        }

        /**
         * Returns the blocks per page field.
         * @return the blocks per page field
         */
        protected IntegerSpinner getBlocksPerPageSpinner() {

                if (blocksPerPageSpinner == null) {
                        blocksPerPageSpinner = new IntegerSpinner(new SpinnerNumberModel(4, 1, 200, 1));
                }
                return blocksPerPageSpinner;
        }

        /**
         * Returns the selected codec on codec list.
         * @return the selected codec on codec list
         */
        private SignalMLCodec getSelectedCodec() {

                return (SignalMLCodec) getSignalMLOptionsPanel().getSignalMLDriverComboBox().getModel().getSelectedItem();
        }

        /**
         * Fills this panel from a model.
         * @param model model to fill this panel from
         */
        public void fillPanelFromModel(OpenFileSignalDescriptor model) {

                getSignalMLOptionsPanel().getSignalMLDriverComboBox().getModel().setSelectedItem(model.getCodec());
                getPageSizeSpinner().setValue(model.getParameters().getPageSize());
                getBlocksPerPageSpinner().setValue(model.getParameters().getBlocksPerPage());
        }

        /**
         * Fills a model from this panel.
         * @param model model to fill from this panel
         */
        public void fillModelFromPanel(OpenFileSignalDescriptor model) {

                model.setCodec(getSelectedCodec());
		model.getParameters().setPageSize(getPageSizeSpinner().getValue());
                model.getParameters().setBlocksPerPage(getBlocksPerPageSpinner().getValue());
        }
}

package org.signalml.app.view.components;

/**
 * Shows state of some progress, to be used with {@link ProgressDialog}.
 * If currentProgress is less than 0: error.
 * If currentProgress equals maxProgress: end of work.
 *
 * @author Tomasz Sawicki
 */
public class ProgressState {

        /**
         * Progress message to be shown in {@link ProgressDialog}.
         */
        private String progressMsg;

        /**
         * Current progress to be represented on progress bar.
         */
        private int currentProgress;

        /**
         * Maximum progress.
         */
        private int maxProgress;

        public ProgressState(String progressMsg, int currentProgress, int maxProgress) {
                this.progressMsg = progressMsg;
                this.currentProgress = currentProgress;
                this.maxProgress = maxProgress;
        }

        public ProgressState() {
                this.progressMsg = "";
                this.currentProgress = 0;
                this.maxProgress = 1;
        }

        public int getCurrentProgress() {
                return currentProgress;
        }

        public int getMaxProgress() {
                return maxProgress;
        }

        public String getProgressMsg() {
                return progressMsg;
        }
}
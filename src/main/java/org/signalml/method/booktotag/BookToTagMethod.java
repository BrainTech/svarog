/* BookToTagMethod.java created 2008-03-22
 *
 */
package org.signalml.method.booktotag;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;
import org.signalml.exception.SignalMLException;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * BookToTagMethod class provides method which executes BookToTagData.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagMethod extends AbstractMethod implements TrackableMethod {

        /**
         * Logger to save history of execution at.
         */
	protected static final Logger logger = Logger.getLogger(BookToTagMethod.class);

	private static final String UID = "10984681-2eea-42aa-b637-bc4781baf692";
	private static final String NAME = "bookToTag";
	private static final int[] VERSION = new int[] {1,0};

        /**
         * Creates new instance of BookToTagMethod.
         */
	public BookToTagMethod() throws SignalMLException {
		super();
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public String getUID() {
		return UID;
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public String getName() {
		return NAME;
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public int[] getVersion() {
		return VERSION;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public Object createData() {
		return new BookToTagData();
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public boolean supportsDataClass(Class<?> clazz) {
		return BookToTagData.class.isAssignableFrom(clazz);
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public Class<?> getResultClass() {
		return BookToTagResult.class;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public void validate(Object data, Errors errors) {
		super.validate(data, errors);

		// TODO validation

	}

        /**
         * Executes this BookToTagMethod and returns BookToTagResult containing result of computation.
         *
         * @param dataObj the data object
         * @param tracker the tracker used to monitor the execution
         * @return the result object
         * @throws ComputationException when computation fails for
         * reasons other than bad input data, e.g. when book is empty
         */
	@Override
	public Object doComputation(Object dataObj, final MethodExecutionTracker tracker) throws ComputationException {

		BookToTagData data = (BookToTagData) dataObj;

		tracker.resetTickers();

		StandardBook book = data.getBook();

		LinkedHashSet<Integer> channels = data.getChannels();
		int channelCount = channels.size();
		int segmentCount = book.getSegmentCount();

		int[] channelArr = new int[channelCount];
		int i = 0;
		for (Integer integer : channels) {
			channelArr[i] = integer;
			i++;
		}

		if (channelCount <=0 || segmentCount <= 0) {
			throw new ComputationException("Empty book");
		}

		int stepCount = channelCount * segmentCount;
		tracker.setTickerLimits(new int[] {stepCount});

		StandardBookSegment firstSegment = book.getSegmentAt(0, channelArr[0]);
		float pageSize = firstSegment.getSegmentTimeLength();
		int blocksPerPage = 5; // TODO add to configuration maybe

		float blockSize = pageSize / blocksPerPage;

		StyledTagSet resultTagSet = new StyledTagSet(pageSize, blocksPerPage);

		TagStyle pageStyle = null;
		TagStyle blockStyle = null;
		TagStyle channelStyle = null;

		if (data.isMakePageTags()) {
			pageStyle = new TagStyle(SignalSelectionType.PAGE, "P", "Page", Color.YELLOW, Color.YELLOW, 1, null, null, false);
			resultTagSet.addStyle(pageStyle);
		}

		if (data.isMakeBlockTags()) {
			blockStyle = new TagStyle(SignalSelectionType.BLOCK, "B", "Block", Color.ORANGE, Color.ORANGE, 1, null, null, false);
			resultTagSet.addStyle(blockStyle);
		}

		if (data.isMakePageTags()) {
			channelStyle = new TagStyle(SignalSelectionType.CHANNEL, "C", "Channel", Color.RED, Color.RED, 1, null, null, false);
			resultTagSet.addStyle(channelStyle);
		}

		boolean pageMarked;
		boolean[] blockMarked = new boolean[blocksPerPage];
		int e;
		int j;
		int atomCount;
		StandardBookSegment segment;
		StandardBookAtom atom;
		float segmentPosition;
		int blockIndex;

		for (i=0; i<segmentCount; i++) {

			pageMarked = false;
			Arrays.fill(blockMarked, false);

			for (e=0; e<channelCount; e++) {

				segment = book.getSegmentAt(i, channelArr[e]);
				if (segment.getSegmentTimeLength() != pageSize) {
					logger.warn("Incompatible segment length [" + segment.getSegmentTimeLength() + "]");
					tracker.tick(0);
					continue;
				}

				segmentPosition = segment.getSegmentTime();

				atomCount = segment.getAtomCount();

				if (atomCount > 0 && !pageMarked && data.isMakePageTags()) {

					resultTagSet.addTag(new Tag(pageStyle, segmentPosition, pageSize));
					pageMarked = true;

				}

				for (j=0; j<atomCount; j++) {

					atom = segment.getAtomAt(j);

					if (data.isMakeBlockTags()) {

						blockIndex = (int) Math.floor(atom.getTimePosition() / blockSize);
						if (blockIndex >= blocksPerPage) {
							logger.warn("Incorrect block index [" + blockIndex + "]");
						}
						if (!blockMarked[blockIndex]) {

							resultTagSet.addTag(new Tag(blockStyle, segmentPosition + blockIndex*blockSize, blockSize));
							blockMarked[blockIndex] = true;

						}

					}

					if (data.isMakeChannelTags()) {

						resultTagSet.addTag(new Tag(channelStyle, segmentPosition + atom.getTimePosition(), atom.getTimeScale(), channelArr[e]));

					}

				}

				tracker.tick(0);

			}

		}

		BookToTagResult result = new BookToTagResult();
		result.setTagSet(resultTagSet);

		return result;

	}

        /**
         * {@inheritDoc}
         */
	@Override
	public int getTickerCount() {
		return 1;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public String getTickerLabel(MessageSourceAccessor messageSource, int ticker) {
		return messageSource.getMessage("bookToTagMethod.ticker");
	}


}

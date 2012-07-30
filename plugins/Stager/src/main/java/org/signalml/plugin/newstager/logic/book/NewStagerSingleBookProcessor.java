package org.signalml.plugin.newstager.logic.book;

import java.util.Arrays;

import org.signalml.plugin.data.PluginPair;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.book.NewStagerSingleBookProcessorData;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorResult;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomFilterData;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagCreatorData;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;
import org.signalml.plugin.newstager.data.tag.NewStagerBookProcessorData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.logic.book.tag.INewStagerTagBuilder;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerAtomTagBuilderChain;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerBookAtomSimpleTagBuilder;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerBookAtomTagCreator;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerHelperValueThresholdTagBuilderChain;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerNonEmptyHelperConditionBuilderChain;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerPrimaryTagBuilder;
import org.signalml.plugin.newstager.logic.book.tag.helper.INewStagerBookAtomPrimaryTagHelper;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerAmpBookAtomFilter;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomFilterBase;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomPrimaryTagHelper;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomSampleHelperSet;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerCountingBuilderHelper;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerKCTagBuilderHelper;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerMuscleTagBuilderHelper;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerSwaTagBuilderHelper;

public class NewStagerSingleBookProcessor {

	protected NewStagerTagBuilderSet builderSet;
	protected NewStagerBookAtomSampleHelperSet helperSet;
	private final NewStagerBookAtomTagCreator tagCreator;
	private final NewStagerBookInfo bookInfo;

	protected class Pair
			extends
			PluginPair<NewStagerTagCollectionType, INewStagerBookAtomPrimaryTagHelper> {

		public Pair(NewStagerTagCollectionType x,
				INewStagerBookAtomPrimaryTagHelper y) {
			super(x, y);
		}

	};

	public NewStagerSingleBookProcessor(NewStagerBookProcessorData data) {
		this.bookInfo = data.bookInfo;

		this.helperSet = new NewStagerBookAtomSampleHelperSet();

		NewStagerParameterThresholds thresholds = data.parameters.thresholds;
		NewStagerBookInfo bookInfo = data.bookInfo;
		this.helperSet.alphaFilter = new NewStagerBookAtomFilterBase(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.alphaThreshold, this.helperSet));
		this.helperSet.deltaFilter = new NewStagerBookAtomFilterBase(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.deltaThreshold, this.helperSet));
		this.helperSet.gaborDeltaFilter = new NewStagerAmpBookAtomFilter(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.deltaThreshold, this.helperSet));
		this.helperSet.spindleFilter = new NewStagerBookAtomFilterBase(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.spindleThreshold, this.helperSet));
		this.helperSet.kcFilter = new NewStagerAmpBookAtomFilter(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.kCThreshold, this.helperSet));
		this.helperSet.thetaGaborFilter = new NewStagerAmpBookAtomFilter(
				new NewStagerBookAtomFilterData(bookInfo,
						thresholds.thetaThreshold, this.helperSet));

		this.tagCreator = new NewStagerBookAtomTagCreator(
				new NewStagerBookAtomTagCreatorData(data.constants,
						data.bookInfo));

		this.builderSet = new NewStagerTagBuilderSet();

		this.createPrimaryHypnogramBuilders(data);

		this.createHelpers(data);
		this.createMainBuilders(data);
	}

	private void createHelpers(NewStagerBookProcessorData data) {
		NewStagerBookAtomTagHelperData helperData = new NewStagerBookAtomTagHelperData(
				data.bookInfo, this.helperSet, data.muscle,
				data.signalStatCoeffs);
		this.helperSet.swaHelper = new NewStagerSwaTagBuilderHelper(helperData,
				this.helperSet.gaborDeltaHelper, 0);
		this.helperSet.alphaHelper = new NewStagerSwaTagBuilderHelper(
				helperData, this.helperSet.alphaPrimaryHelper, 1);
		this.helperSet.spindleHelper = new NewStagerSwaTagBuilderHelper(
				helperData, this.helperSet.spindlePrimaryHelper, 0);
		this.helperSet.kcHelper = new NewStagerKCTagBuilderHelper(helperData,
				this.helperSet.kcFilter);
		this.helperSet.thetaHelper = new NewStagerCountingBuilderHelper(
				helperData, this.helperSet.thetaGaborFilter);
		this.helperSet.muscleHelper = new NewStagerMuscleTagBuilderHelper(
				helperData);
	}

	private void createMainBuilders(NewStagerBookProcessorData data) {
		NewStagerBookAtomTagBuilderData chainData = new NewStagerBookAtomTagBuilderData(
				data.channelMap, this.helperSet, this.tagCreator);

		INewStagerTagBuilder stadium1Builder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_1);
		INewStagerTagBuilder stadium2Builder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_2);
		INewStagerTagBuilder stadium3Builder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_3);
		INewStagerTagBuilder stadium4Builder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_4);
		INewStagerTagBuilder stadiumRBuilder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_R);
		INewStagerTagBuilder stadiumWBuilder = new NewStagerBookAtomSimpleTagBuilder(
				chainData, NewStagerTagCollectionType.SLEEP_STAGE_W);

		NewStagerAtomTagBuilderChain mainChain = new NewStagerAtomTagBuilderChain(
				chainData, "mainLoop");

		NewStagerAtomTagBuilderChain ampChain = new NewStagerNonEmptyHelperConditionBuilderChain(
				chainData, this.helperSet.deltaPrimaryHelper, "SWA > 0");

		mainChain.compose(ampChain);
		NewStagerAtomTagBuilderChain spindleTopChain = mainChain
				.composeChain("spindle check"); // OK

		NewStagerNonEmptyHelperConditionBuilderChain spindleChain = new NewStagerNonEmptyHelperConditionBuilderChain(
				chainData, this.helperSet.spindlePrimaryHelper);

		NewStagerNonEmptyHelperConditionBuilderChain swaChain = new NewStagerNonEmptyHelperConditionBuilderChain(
				chainData, this.helperSet.gaborDeltaHelper, "spindle > 0");
		swaChain.compose(new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.swaHelper, 0.5d)
				.compose(stadium4Builder));
		swaChain.compose(new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.swaHelper, 0.2d)
				.compose(stadium3Builder));

		swaChain.compose(spindleTopChain);

		NewStagerAtomTagBuilderChain spindleSubChain = new NewStagerAtomTagBuilderChain(
				chainData);
		NewStagerHelperValueThresholdTagBuilderChain alphaChain = new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.spindleHelper,
				data.fixedParameters.alphaPerc1);
		spindleSubChain.compose(alphaChain);
		spindleSubChain.compose(stadium2Builder);
		NewStagerAtomTagBuilderChain muscleChain = new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.muscleHelper,
				data.signalStatCoeffs.toneMThreshold).compose(stadiumWBuilder)
				.compose(stadiumRBuilder);
		alphaChain.compose(muscleChain);

		spindleChain.compose(spindleSubChain);
		NewStagerAtomTagBuilderChain kcTopChain = spindleChain.composeChain();

		NewStagerAtomTagBuilderChain thetaTopChain = new NewStagerAtomTagBuilderChain(
				chainData);
		NewStagerHelperValueThresholdTagBuilderChain thetaChain = new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.muscleHelper,
				data.signalStatCoeffs.toneMThreshold);
		NewStagerAtomTagBuilderChain thetaMuscleChain = new NewStagerAtomTagBuilderChain(
				chainData);
		thetaMuscleChain.compose(stadiumRBuilder);
		thetaChain
				.compose(
						new NewStagerHelperValueThresholdTagBuilderChain(
								chainData, this.helperSet.thetaHelper, 0)
								.compose(stadium1Builder)).compose(
						stadiumWBuilder);
		thetaTopChain.compose(thetaChain).compose(thetaMuscleChain);

		NewStagerHelperValueThresholdTagBuilderChain spindleSubChain2 = new NewStagerHelperValueThresholdTagBuilderChain(
				chainData, this.helperSet.alphaHelper,
				data.fixedParameters.alphaPerc2);
		spindleSubChain2.compose(muscleChain);

		kcTopChain
				.compose(new NewStagerHelperValueThresholdTagBuilderChain(
						chainData, this.helperSet.kcHelper, 0)
						.compose(stadium2Builder));
		kcTopChain.composeChain().compose(spindleSubChain2)
				.compose(thetaTopChain);

		ampChain.compose(swaChain);
		ampChain.compose(spindleTopChain);

		spindleTopChain.compose(spindleChain); // OK
		spindleTopChain.compose(kcTopChain); // OK

		this.builderSet.add(mainChain);
	}

	private void createPrimaryHypnogramBuilders(NewStagerBookProcessorData data) {
		NewStagerBookAtomTagHelperData helperData = new NewStagerBookAtomTagHelperData(
				data.bookInfo, this.helperSet, data.muscle,
				data.signalStatCoeffs);
		this.helperSet.alphaPrimaryHelper = new NewStagerBookAtomPrimaryTagHelper(
				helperData, this.helperSet.alphaFilter);
		this.helperSet.deltaPrimaryHelper = new NewStagerBookAtomPrimaryTagHelper(
				helperData, this.helperSet.deltaFilter);
		this.helperSet.spindlePrimaryHelper = new NewStagerBookAtomPrimaryTagHelper(
				helperData, this.helperSet.spindleFilter);
		this.helperSet.gaborDeltaHelper = new NewStagerBookAtomPrimaryTagHelper(
				helperData, this.helperSet.gaborDeltaFilter);

		if (data.parameters.primaryHypnogramFlag) {

			NewStagerBookAtomTagBuilderData builderData = new NewStagerBookAtomTagBuilderData(
					data.channelMap, this.helperSet, this.tagCreator);

			for (final Pair p : Arrays.asList(new Pair(
					NewStagerTagCollectionType.HYPNO_ALPHA,
					this.helperSet.alphaPrimaryHelper), new Pair(
					NewStagerTagCollectionType.HYPNO_DELTA,
					this.helperSet.gaborDeltaHelper), new Pair(
					NewStagerTagCollectionType.HYPNO_SPINDLE,
					this.helperSet.spindlePrimaryHelper))) {
				this.builderSet
						.add(new NewStagerPrimaryTagBuilder(builderData) {

							@Override
							protected INewStagerBookAtomPrimaryTagHelper getConverter() {
								return p.y;
							}

							@Override
							protected NewStagerTagCollectionType getTagType() {
								return p.x;
							}
						});
			}
		}
	}

	public void process(NewStagerSingleBookProcessorData data) {
		this.builderSet.process(data.atomSample);
	}

	public NewStagerBookProcessorResult getResult() {
		return new NewStagerBookProcessorResult(this.bookInfo,
				this.builderSet.getResult());
	}

}

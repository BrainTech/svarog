/* TagDifferenceDetector.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.exception.SanityCheckException;

/** TagDifferenceDetector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifferenceDetector {

	public void getDifferences(SortedSet<Tag> topTags, SortedSet<Tag> bottomTags, final SignalSelectionType targetType, final int channel, final TreeSet<TagDifference> differences) {

		TaggedFragmentProcessor processor = new TaggedFragmentProcessor() {

			Tag tag = null;
			Iterator<Tag> it;
			Iterator<Tag> it2;
			TagDifference difference;
			boolean tagsAreTheSame;
			boolean found;

			@Override
			public void process(List<Tag> activeTopTags, List<Tag> activeBottomTags, float start, float end) {

				// now we have established that in the time period from
				// timePointer to nextInterestingEvent respective active tags of the given
				// type exist for top & bottom tag. Let's compare them.

				if (activeTopTags.isEmpty() && activeBottomTags.isEmpty()) {
					// no tags - no difference
					difference = null;
				} else if (!activeTopTags.isEmpty() && activeBottomTags.isEmpty()) {
					// tag missing at the bottom
					difference = new TagDifference(targetType,start,end-start,channel,TagDifferenceType.MISSING_IN_BOTTOM);
				} else if (!activeBottomTags.isEmpty() && activeTopTags.isEmpty()) {
					// tag missing at the top
					difference = new TagDifference(targetType,start,end-start,channel,TagDifferenceType.MISSING_IN_TOP);
				} else {
					// there are some tags both top & bottom - are they the same?
					tagsAreTheSame = false;
					if (activeBottomTags.size() == activeTopTags.size()) {
						it = activeTopTags.iterator();
						found = false;
						while (it.hasNext()) {
							tag = it.next();
							it2 = activeBottomTags.iterator();
							found = false;
							while (it2.hasNext()) {
								if (it2.next().getStyle().getName().equals(tag.getStyle().getName())) {
									found = true;
									break;
								}
							}
							if (!found) {
								break;
							}
						}
						if (found) {
							tagsAreTheSame = true;
						}
					}

					if (!tagsAreTheSame) {
						difference = new TagDifference(targetType,start,end-start,channel,TagDifferenceType.DIFFERENT);
					} else {
						difference = null;
					}

				}

				if (difference != null) {
					differences.add(difference);
				}


			}

		};

		iterate(topTags, bottomTags, targetType, channel, processor, -1);

	}

	public TagComparisonResult compare(TagStyle[] topStyles, TagStyle[] bottomStyles, SortedSet<Tag> topTags, SortedSet<Tag> bottomTags, SignalSelectionType targetType, int channel, float signalLength) {

		final TagComparisonResult result = new TagComparisonResult(topStyles, bottomStyles, signalLength, signalLength);

		TaggedFragmentProcessor processor = new TaggedFragmentProcessor() {

			private Iterator<Tag> topIterator;
			private Iterator<Tag> bottomIterator;
			private TagStyle topTagStyle;
			private TagStyle bottomTagStyle;
			private float length;
			private boolean topEmpty;
			private boolean bottomEmpty;

			@Override
			public void process(List<Tag> activeTopTags, List<Tag> activeBottomTags, float start, float end) {

				length = end - start;
				if (length <= 0) {
					return;
				}

				topEmpty = activeTopTags.isEmpty();
				bottomEmpty = activeBottomTags.isEmpty();

				if (topEmpty && bottomEmpty) {
					result.addTopStyleTime(-1, length);
					result.addBottomStyleTime(-1, length);
					result.addStyleOverlay(-1, -1, length);
					return;
				}

				if (!bottomEmpty) {
					bottomIterator = activeBottomTags.iterator();
					while (bottomIterator.hasNext()) {

						bottomTagStyle = bottomIterator.next().getStyle();

						result.addBottomStyleTime(bottomTagStyle, length);

						if (topEmpty) {
							result.addTopStyleTime(-1, length);
							result.addStyleOverlay(null, bottomTagStyle, length);
						}

					}
				}

				if (!topEmpty) {
					topIterator = activeTopTags.iterator();
					while (topIterator.hasNext()) {

						topTagStyle = topIterator.next().getStyle();

						result.addTopStyleTime(topTagStyle, length);

						if (bottomEmpty) {
							result.addBottomStyleTime(-1, length);
							result.addStyleOverlay(topTagStyle, null, length);
						} else {
							bottomIterator = activeBottomTags.iterator();
							while (bottomIterator.hasNext()) {

								bottomTagStyle = bottomIterator.next().getStyle();

								result.addStyleOverlay(topTagStyle, bottomTagStyle, length);

							}
						}

					}
				}

			}

		};

		iterate(topTags, bottomTags, targetType, channel, processor, signalLength);

		return result;

	}

	private void arrangeTagStyles(LinkedHashSet<TagStyle> topStyles, LinkedHashSet<TagStyle> bottomStyles, TagStyle[] topArr, TagStyle[] bottomArr) {

		LinkedHashMap<String,TagStyle> topMap = new LinkedHashMap<String, TagStyle>();
		LinkedHashMap<String,TagStyle> bottomMap = new LinkedHashMap<String, TagStyle>();

		TagStyleNameComparator comparator = new TagStyleNameComparator();

		LinkedList<TagStyle> matching = new LinkedList<TagStyle>();
		LinkedList<TagStyle> assorted = new LinkedList<TagStyle>();
		int cnt = 0;

		for (TagStyle style : bottomStyles) {
			bottomMap.put(style.getName(), style);
		}

		for (TagStyle style : topStyles) {
			topMap.put(style.getName(), style);
			if (bottomMap.containsKey(style.getName())) {
				matching.add(style);
			} else {
				assorted.add(style);
			}
		}

		Collections.sort(matching, comparator);
		Collections.sort(assorted, comparator);

		for (TagStyle style : matching) {
			topArr[cnt] = style;
			cnt++;
		}
		for (TagStyle style : assorted) {
			topArr[cnt] = style;
			cnt++;
		}

		matching.clear();
		assorted.clear();

		for (TagStyle style : bottomStyles) {
			if (topMap.containsKey(style.getName())) {
				matching.add(style);
			} else {
				assorted.add(style);
			}
		}

		Collections.sort(matching, comparator);
		Collections.sort(assorted, comparator);

		cnt = 0;
		for (TagStyle style : matching) {
			bottomArr[cnt] = style;
			cnt++;
		}
		for (TagStyle style : assorted) {
			bottomArr[cnt] = style;
			cnt++;
		}

	}

	public TagComparisonResults compare(TagDocument topTagDocument, TagDocument bottomTagDocument) {

		SignalDocument parent = topTagDocument.getParent();
		if (parent != bottomTagDocument.getParent()) {
			throw new SanityCheckException("Cannot compare tags on different signals");
		}

		int channelCount = parent.getChannelCount();
		float signalLength = parent.getMaxSignalLength();

		StyledTagSet topTagSet = topTagDocument.getTagSet();
		StyledTagSet bottomTagSet = bottomTagDocument.getTagSet();

		TagStyle[] topStyles;
		TagStyle[] bottomStyles;

		SortedSet<Tag> topTags = topTagSet.getTags();
		SortedSet<Tag> bottomTags = bottomTagSet.getTags();

		LinkedHashSet<TagStyle> topStyleSet = topTagSet.getPageStylesNoMarkers();
		LinkedHashSet<TagStyle> bottomStyleSet = bottomTagSet.getPageStylesNoMarkers();

		topStyles = new TagStyle[topStyleSet.size()];
		bottomStyles = new TagStyle[bottomStyleSet.size()];

		arrangeTagStyles(topStyleSet, bottomStyleSet, topStyles, bottomStyles);

		TagComparisonResult pageComparisonResult = compare(topStyles, bottomStyles, topTags, bottomTags, SignalSelectionType.PAGE, SignalSelection.CHANNEL_NULL, signalLength);

		topStyleSet = topTagSet.getBlockStylesNoMarkers();
		bottomStyleSet = bottomTagSet.getBlockStylesNoMarkers();

		topStyles = new TagStyle[topStyleSet.size()];
		bottomStyles = new TagStyle[bottomStyleSet.size()];

		arrangeTagStyles(topStyleSet, bottomStyleSet, topStyles, bottomStyles);

		TagComparisonResult blockComparisonResult = compare(topStyles, bottomStyles, topTags, bottomTags, SignalSelectionType.BLOCK, SignalSelection.CHANNEL_NULL, signalLength);

		topStyleSet = topTagSet.getChannelStylesNoMarkers();
		bottomStyleSet = bottomTagSet.getChannelStylesNoMarkers();

		topStyles = new TagStyle[topStyleSet.size()];
		bottomStyles = new TagStyle[bottomStyleSet.size()];

		arrangeTagStyles(topStyleSet, bottomStyleSet, topStyles, bottomStyles);

		TagComparisonResult[] channelComparisonResults = new TagComparisonResult[channelCount];
		for (int i=0; i<channelCount; i++) {
			channelComparisonResults[i] = compare(topStyles, bottomStyles, topTags, bottomTags, SignalSelectionType.CHANNEL, i, signalLength);
		}

		return new TagComparisonResults(pageComparisonResult, blockComparisonResult, channelComparisonResults);

	}

	private void iterate(SortedSet<Tag> topTags, SortedSet<Tag> bottomTags, SignalSelectionType targetType, int channel, TaggedFragmentProcessor processor, float signalLength) {

		Iterator<Tag> topIterator = topTags.iterator();
		Iterator<Tag> bottomIterator = bottomTags.iterator();
		LinkedList<Tag> activeTopTags = new LinkedList<Tag>();
		LinkedList<Tag> activeBottomTags = new LinkedList<Tag>();
		Iterator<Tag> it;
		Tag tag = null;

		Tag endingTopTag = null;
		Tag endingBottomTag = null;
		Tag addedTopTag = null;
		Tag addedBottomTag = null;

		Tag topTag = null;
		Tag bottomTag = null;
		Tag tagCandidate = null;
		float timePointer = 0;
		float nextInterestingEvent;
		boolean finished = false;

		boolean channelMode = targetType.isChannel();
		boolean hasEvent = false;

		do {

			endingTopTag = null;
			endingBottomTag = null;
			addedTopTag = null;
			addedBottomTag = null;

			nextInterestingEvent = Float.MAX_VALUE;
			hasEvent = false;

			if (!activeTopTags.isEmpty()) {
				it = activeTopTags.iterator();
				while (it.hasNext()) {
					tag = it.next();
					if (tag.getEndPosition() <= nextInterestingEvent) {
						nextInterestingEvent = tag.getEndPosition();
						hasEvent = true;
						endingTopTag = tag;
					}
				}
			}

			if (!activeBottomTags.isEmpty()) {
				it = activeBottomTags.iterator();
				while (it.hasNext()) {
					tag = it.next();
					if (tag.getEndPosition() <= nextInterestingEvent) {
						nextInterestingEvent = tag.getEndPosition();
						hasEvent = true;
						endingBottomTag = tag;
						endingTopTag = null;
					}
				}
			}

			if (topTag == null) {
				while (topIterator.hasNext()) {
					tagCandidate = topIterator.next();
					if (tagCandidate.getType() == targetType) {
						if (!channelMode || tagCandidate.getChannel() == channel) {
							if (!tagCandidate.isMarker()) {
								topTag = tagCandidate;
								break;
							}
						}
					}
				}
			}

			if (topTag != null) {
				if (topTag.getPosition() <= nextInterestingEvent) {
					nextInterestingEvent = topTag.getPosition();
					hasEvent = true;
					addedTopTag = topTag;
					endingTopTag = null;
					endingBottomTag = null;

				}
			}

			if (bottomTag == null) {
				while (bottomIterator.hasNext()) {
					tagCandidate = bottomIterator.next();
					if (tagCandidate.getType() == targetType) {
						if (!channelMode || tagCandidate.getChannel() == channel) {
							if (!tagCandidate.isMarker()) {
								bottomTag = tagCandidate;
								break;
							}
						}
					}
				}
			}

			if (bottomTag != null) {
				if (bottomTag.getPosition() <= nextInterestingEvent) {
					nextInterestingEvent = bottomTag.getPosition();
					hasEvent = true;
					addedBottomTag = bottomTag;
					addedTopTag = null;
					endingTopTag = null;
					endingBottomTag = null;

				}
			}

			if (!hasEvent) {

				if (signalLength >= 0 && signalLength > timePointer) {

					// process end fragment
					processor.process(activeTopTags, activeBottomTags, timePointer, signalLength);

				}

				finished = true;
			} else {

				if (nextInterestingEvent > timePointer) {

					processor.process(activeTopTags, activeBottomTags, timePointer, (signalLength >= 0 && nextInterestingEvent > signalLength) ? signalLength : nextInterestingEvent);

				}

				// now consume event

				if (addedTopTag != null) {
					activeTopTags.add(addedTopTag);
					topTag = null;
				}
				if (addedBottomTag != null) {
					activeBottomTags.add(addedBottomTag);
					bottomTag = null;
				}
				if (endingTopTag != null) {
					activeTopTags.remove(endingTopTag);
				}
				if (endingBottomTag != null) {
					activeBottomTags.remove(endingBottomTag);
				}

				// move time pointer
				timePointer = nextInterestingEvent;

			}

		} while (!finished);

	}

	private interface TaggedFragmentProcessor {

		void process(List<Tag> activeTopTags, List<Tag> activeBottomTags, float start, float end);

	}

}

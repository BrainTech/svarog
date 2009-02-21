/* ArtifactComparison.java created 2008-02-27
 * 
 */

package org.signalml.app.method.artifact;

import java.util.Iterator;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;

/** ArtifactComparison
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactComparison {

	protected static final Logger logger = Logger.getLogger(ArtifactComparison.class);
	
	private TagDocument tag;
	private TagDocument expertTag;

	private int truePositive;
	private int falsePositive;
	private int trueNegative;
	private int falseNegative;

	private float segmentLength;

	private int segmentCount;
	
	public ArtifactComparison(float totalLength, TagDocument tagDocument, TagDocument expertTag) {
		this.tag = tagDocument;
		this.expertTag = expertTag;
		
		StyledTagSet tagSet = tagDocument.getTagSet();
		StyledTagSet expertTagSet = expertTag.getTagSet();

		segmentLength = tagDocument.getPageSize() / tagDocument.getBlocksPerPage();
		segmentCount = (int) Math.floor( totalLength / segmentLength );
				
		Tag tag = null;
		TagStyle style = null;
		float position = 0;
		int i;
		
		SortedSet<Tag> tags = tagSet.getTags();
		Iterator<Tag> it = tags.iterator();
		SortedSet<Tag> expertTags = expertTagSet.getTags();		
		Iterator<Tag> expertIt = expertTags.iterator();
		Tag expert = null;
		TagStyle expertStyle = null;
				
		float expectedTime;
		
		boolean tagHasArtifact;
		boolean expertHasArtifact;
				
		for( i=0; i<segmentCount; i++ ) {
			
			tagHasArtifact = false;
			expertHasArtifact = false;
			
			expectedTime = i * segmentLength;
						
			while( tag == null ) {
				
				// acquire next valid page tag
				if( !it.hasNext() ) {
					break;
				}
				tag = it.next();
				style = tag.getStyle();
				position = tag.getPosition();
				if( !style.getType().isBlock() ) {
					tag = null;
				}
				else if( position < expectedTime ) {
					logger.warn( "Extra tag [" + style.getName() + "] found at [" + position + "]" );
					tag = null; 
				}
				else if( tag.getLength() != segmentLength ) {
					logger.warn( "Bad tag length [" + tag.getLength() + "]" );
					tag = null;
				}
				
			}

			if( tag != null ) {

				if( tag.getPosition() <= expectedTime ) {
					tagHasArtifact = true;
					tag = null;
				}
				
			}
			
			while( expert == null ) {
				
				// acquire next valid page tag
				if( !expertIt.hasNext() ) {
					break;
				}
				expert = expertIt.next();
				expertStyle = expert.getStyle();
				position = expert.getPosition();
				if( !expertStyle.getType().isBlock() ) {
					expert = null;
				}
				else if( position < expectedTime ) {
					logger.warn( "Extra tag [" + expertStyle.getName() + "] found at [" + position + "]" );
					expert = null; 
				}
				else if( expert.getLength() != segmentLength ) {
					logger.warn( "Bad tag length [" + expert.getLength() + "]" );
					expert = null;
				}
				
			}
						
			if( expert != null ) {
				
				if( expert.getPosition() <= expectedTime ) {
					expertHasArtifact = true;
					expert = null;
				}
				
			}
			
			if( tagHasArtifact ) {
				if( expertHasArtifact ) {
					truePositive++;
				} else {
					falsePositive++;
				}
			} else {
				if( expertHasArtifact ) {
					falseNegative++;
				} else {
					trueNegative++;
				}
			}
			
		}
				
	}

	public TagDocument getTag() {
		return tag;
	}

	public TagDocument getExpertTag() {
		return expertTag;
	}
	
	public int getSegmentCount() {
		return segmentCount;
	}
	
	public float getSegmentLength() {
		return segmentLength;
	}

	public int getTruePositive() {
		return truePositive;
	}

	public int getFalsePositive() {
		return falsePositive;
	}

	public int getTrueNegative() {
		return trueNegative;
	}

	public int getFalseNegative() {
		return falseNegative;
	}
	
}

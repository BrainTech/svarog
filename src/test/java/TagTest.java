
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;
import org.signalml.exception.SignalMLException;


public class TagTest {

    @Test
    public void testTags() throws SignalMLException, IOException {
        StyledTagSet tagSet = new StyledTagSet();
        TagStyle style = new TagStyle( SignalSelectionType.CHANNEL, "REM", "Faza snu REM", Color.RED, Color.BLUE, 2);
        Tag tag = new Tag(style, 10.0f, 20.0f, 3);
        tagSet.addTag(tag);
        style = new TagStyle( SignalSelectionType.CHANNEL, "ABC", "Faza snu ABC", Color.GREEN, Color.BLACK, 1);
        tag = new Tag(style, 30.0f, 40.0f, 6);
        tagSet.addTag(tag);
        style = new TagStyle( SignalSelectionType.CHANNEL, "XYZ", "Faza snu XYZ", Color.BLUE, Color.YELLOW, 3);
        tag = new Tag(style, 10.0f, 20.0f, 6);
        tagSet.addTag(tag);
        TagDocument doc = new TagDocument( tagSet);
        doc.setBackingFile( new File( "tag_test_out.tag"));
        doc.saveDocument();
    }

}

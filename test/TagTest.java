import models.Post;
import models.Section;
import models.Tag;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.*;

public class TagTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    /**
     * Test for adding tags to database
     */
    @Test
    public void addTag() {
        Tag testTag = new Tag();
        testTag.name = "Test";
        testTag.description = "Test tag";
        testTag.color = "#00ff00";
        testTag.save();

        Tag test = Tag.find.query().where().eq("name", "Test").findOne();
        assertNotNull(test);
        assertEquals("Test tag", test.description);
    }

    /**
     * Test for adding empty tags to post (Removing all tags)
     */
    @Test
    public void addEmptyTagToPost() {
        Section TestSection = new Section();
        TestSection.id = 10;
        TestSection.name = "Test section";
        TestSection.alias = "Ts";
        TestSection.save();

        Tag testTag = new Tag();
        testTag.id = 10;
        testTag.name = "Test";
        testTag.description = "Test tag";
        testTag.color = "#00ff00";
        testTag.save();

        Post testPost = new Post();
        testPost.id = 10;
        testPost.section = Section.find.byId(10);
        testPost.title = "Test";
        testPost.content = "Test post";
        testPost.tags = Tag.find.query().where().eq("id", 10).findList();
        testPost.save();

        Post test = Post.find.query().where().eq("id", 10).findOne();
        assertNotNull(test);
        assertEquals("Test", test.title);

        test.tags = new ArrayList<>();
        test.update();

        Post test2 = Post.find.query().where().eq("id", 10).findOne();
        assertNotNull(test2);
        assertEquals(0, test2.tags.size());
    }

    /**
     * Test for removing tag from post
     */
    @Test
    public void removeTagFromPost() {
        Section TestSection = new Section();
        TestSection.id = 10;
        TestSection.name = "Test section";
        TestSection.alias = "Ts";
        TestSection.save();

        Tag testTag = new Tag();
        testTag.id = 10;
        testTag.name = "Test";
        testTag.description = "Test tag";
        testTag.color = "#00ff00";
        testTag.save();

        Tag testTag2 = new Tag();
        testTag2.id = 11;
        testTag2.name = "Test2";
        testTag2.description = "Test2 tag";
        testTag2.color = "#00ff00";
        testTag2.save();

        Post testPost = new Post();
        testPost.id = 10;
        testPost.section = Section.find.byId(10);
        testPost.title = "Test";
        testPost.content = "Test post";
        testPost.tags = Tag.find.query().where().or().eq("id", 10).eq("id", 11).endOr().findList();
        testPost.save();

        Post test = Post.find.query().where().eq("id", 10).findOne();
        assertNotNull(test);
        assertEquals("Test", test.title);

        test.tags = Tag.find.query().where().eq("id", 10).findList();
        test.update();

        Post test2 = Post.find.query().where().eq("id", 10).findOne();
        assertNotNull(test2);
        assertEquals(1, test2.tags.size());
    }
}

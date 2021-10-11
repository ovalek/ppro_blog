import models.Section;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.ValidationError;
import play.test.WithApplication;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.*;

public class SectionTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    /**
     * Test for adding section
     */
    @Test
    public void addSection() {
        Section TestSection = new Section();
        TestSection.name = "Test section";
        TestSection.alias = "Ts";
        TestSection.save();

        Section test = Section.find.query().where().eq("name", "Test section").findOne();
        assertNotNull(test);
        assertEquals("Ts", test.alias);
    }

    /**
     * Test for throwing error if the name is not unique.
     */
    @Test
    public void checkSectionUniqueNameError() {
        Section TestSection = new Section();
        TestSection.name = "Test section";
        TestSection.alias = "Ts";
        TestSection.save();

        Section TestSection2 = new Section();
        TestSection2.name = "Test section";
        TestSection2.alias = "Ts2";
        ValidationError error = TestSection2.saveWithValidation(0);

        assertNotNull(error);
        assertEquals("Name must be unique.", error.message());
    }
}

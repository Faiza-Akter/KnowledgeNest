package com.knowledgenest;



import com.knowledgenest.Model.CourseModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class CourseModelTest {

    @Test
    public void testDefaultConstructor() {
        CourseModel course = new CourseModel();
        assertNotNull(course);
    }

    @Test
    public void testTitleOnlyConstructor() {
        CourseModel course = new CourseModel("Java Basics");
        assertEquals("Java Basics", course.getTitle());
    }

    @Test
    public void testFullConstructorAndGetters() {
        CourseModel course = new CourseModel("Java", "3h", "4.5", "Learn Java",
                500, "thumb.png", "intro.mp4", "admin", "true");

        assertEquals("Java" , course.getTitle());
        assertEquals("3h", course.getDuration());
        assertEquals("4.5", course.getRating());
        assertEquals( "Learn Java", course.getDescription());
        assertEquals(500, course.getPrice());
        assertEquals("thumb.png", course.getThumbnail());
        assertEquals("intro.mp4", course.getIntrovideo());
        assertEquals("admin", course.getPostedBy());
        assertEquals("true", course.getEnable());
    }

    @Test
    public void testSetters() {
        CourseModel course = new CourseModel();
        course.setTitle("Python");
        course.setDuration("2h");
        course.setPrice(300);

        assertEquals("Python", course.getTitle());
        assertEquals("2h", course.getDuration());
        assertEquals(300, course.getPrice());
    }
}


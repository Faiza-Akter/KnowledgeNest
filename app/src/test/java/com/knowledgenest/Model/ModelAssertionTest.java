package com.knowledgenest.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelAssertionTest {

    // === COURSE MODEL TESTS ===

    @Test
    public void testCourseModelDefaultConstructor() {
        CourseModel course = new CourseModel();
        assertNotNull(course);
    }

    @Test
    public void testCourseModelTitleConstructor() {
        CourseModel course = new CourseModel("C++ Basics");
        assertEquals("C++ Basics", course.getTitle());
    }

    @Test
    public void testCourseModelFullConstructor() {
        CourseModel course = new CourseModel("Python", "5h", "4.7", "Learn Python",
                999, "thumb.jpg", "intro.mp4", "admin", "true");

        assertEquals("Python", course.getTitle());
        assertEquals("5h", course.getDuration());
        assertEquals("4.7", course.getRating());
        assertEquals("Learn Python", course.getDescription());
        assertEquals(999, course.getPrice());
        assertEquals("thumb.jpg", course.getThumbnail());
        assertEquals("intro.mp4", course.getIntrovideo());
        assertEquals("admin", course.getPostedBy());
        assertEquals("true", course.getEnable());
    }

    @Test
    public void testCourseModelSetters() {
        CourseModel course = new CourseModel();
        course.setTitle("DSA");
        course.setDuration("3h");
        course.setPrice(500);

        assertEquals("DSA", course.getTitle());
        assertEquals("3h", course.getDuration());
        assertEquals(500, course.getPrice());
    }

    // === PLAYLIST MODEL TESTS ===

    @Test
    public void testPlayListModelDefaultConstructor() {
        PlayListModel playlist = new PlayListModel();
        assertNull(playlist.getTitle());
        assertNull(playlist.getVideoUrl());
        assertNull(playlist.getEnabled());
        assertNull(playlist.getKey());
    }

    @Test
    public void testPlayListModelParameterizedConstructor() {
        PlayListModel playlist = new PlayListModel("Intro", "video.mp4", "true");

        assertEquals("Intro", playlist.getTitle());
        assertEquals("video.mp4", playlist.getVideoUrl());
        assertEquals("true", playlist.getEnabled());
        assertNull(playlist.getKey());
    }

    @Test
    public void testPlayListModelSetters() {
        PlayListModel playlist = new PlayListModel();
        playlist.setTitle("Lecture 1");
        playlist.setVideoUrl("url.com/video");
        playlist.setEnabled("false");
        playlist.setKey("xyz123");

        assertEquals("Lecture 1", playlist.getTitle());
        assertEquals("url.com/video", playlist.getVideoUrl());
        assertEquals("false", playlist.getEnabled());
        assertEquals("xyz123", playlist.getKey());
    }

    // === USER MODEL TESTS ===

    @Test
    public void testUserModelDefaultConstructor() {
        UserModel user = new UserModel();
        assertNotNull(user);
    }

    @Test
    public void testUserModelFullConstructor() {
        UserModel user = new UserModel("Tahnia", "tahnia@gmail.com", "pass123", "profile.png");

        assertEquals("Tahnia", user.getName());
        assertEquals("tahnia@gmail.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("profile.png", user.getProfile());
    }

    @Test
    public void testUserModelSetters() {
        UserModel user = new UserModel();
        user.setName("Tasnia");
        user.setEmail("tasnia@example.com");
        user.setPassword("123456");
        user.setProfile("avatar.png");

        assertEquals("Tasnia", user.getName());
        assertEquals("tasnia@example.com", user.getEmail());
        assertEquals("123456", user.getPassword());
        assertEquals("avatar.png", user.getProfile());
    }
}

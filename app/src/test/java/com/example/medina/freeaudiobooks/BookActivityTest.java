package com.example.medina.freeaudiobooks;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class BookActivityTest {

    @Test
    public void testIfChaptersListIsNotNull() {
        BookActivity bookActivity = new BookActivity();
        assertNotNull(bookActivity.testChapters.size());
    }
}

package com.example.medina.freeaudiobooks;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearchBooksActivityTest {

    @Test
    public void checkIfBookListIsNotNull() throws Exception{
        SearchBooksActivity searchBooksActivity = new SearchBooksActivity();
        assertNotNull( searchBooksActivity.testBooks.size());
    }
}

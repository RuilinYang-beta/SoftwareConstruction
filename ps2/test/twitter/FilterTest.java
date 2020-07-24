package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /**
     * Testing Strategy - Filter.writtenBy(...)
     * Assume `tweets` non-empty, and there is at least 1 tweet by the author `username`.
     * - tweets.size():     ==1, > 1
     * - username:          match the t.getAuthor() exactly, does not match but due to case-insensitive is the same author
     * - _                  returned list of tweets should appear in the same order as they are in the given list of tweets                    
     * 
     * Testing Strategy - Filter.inTimeSpan(...)
     * Assume in `timespan` there is at least 1 tweet in `tweets`.
     * - number of tweet in the timespan:    ==1, >1
     * - `tweets` timestamp:                 ascending order, unordered
     * - _                  returned list of tweets should appear in the same order as they are in the given list of tweets
     * 
     * Testing Strategy - Filter.containing(...)
     * - input:   none of tweet in `tweets` contains any of `words`; 
     *            some tweets in `tweets` contains some of `words` in the exact form;
     *            some tweets in `tweets` contains some of `words` in alternative case-insensitive form
     * - _        returned list of tweets should appear in the same order as they are in the given list of tweets 
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T11:03:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T19:00:00Z");
    private static final Instant d5 = Instant.parse("2016-02-18T11:00:56Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "my time at portia becomes boring", d3);
    private static final Tweet tweet4 = new Tweet(4, "ALySSa", "but I'm expecting how the baby is like", d4);
    private static final Tweet tweet5 = new Tweet(5, "epiphany", "pity that i am not fully socialized", d5);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
        
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testWrittenByMultipleTweetsMultipleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        
        assertEquals("expected a list of 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
    }
    

    @Test
    public void testWrittenByGivenAuthorNameMixedCase() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "aLySsA");
        
        assertEquals("expected a list of 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
    }
    
    @Test
    public void testWrittenByTweetAuthorNameMixedCase() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3, tweet4), "aLySsA");
        
        assertEquals("expected a list of 3", 3, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.containsAll(Arrays.asList(tweet1, tweet3, tweet4)));
    }    
    
    @Test
    public void testWrittenByOrderOfReturnedList() {
        List<Tweet> all = Arrays.asList(tweet1, tweet2, tweet3, tweet4);
        List<Tweet> writtenBy = Filter.writtenBy(all, "aLySsA");
        
        assertTrue(isSameOrder(all, writtenBy));
    }
    
    /**
     * Returns true if the `filtered` tweets appears in the same order as they do in `original`.
     */
    private boolean isSameOrder(List<Tweet> original, List<Tweet> filtered) {
        int i = 0;
        int j = 0;
        
        while (i < original.size() && j < filtered.size()) {
              if (original.get(i).getId() == filtered.get(j).getId()) {
                  i++;
                  j++;
              }
              else {
                  i++;
              }
        }
        
        if (j == filtered.size()) {
            return true;
        }
        
        return false;
    }
    
    
    @Test
    public void testInTimespanMultipleTweetsSingleResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");
        
        List<Tweet> all = Arrays.asList(tweet1, tweet2, tweet3);
        List<Tweet> inTimespan = Filter.inTimespan(all, new Timespan(testStart, testEnd));   
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.contains(tweet1));
        assertTrue("expected same order", isSameOrder(all, inTimespan));
    }
    
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResultsGivenTweetsUnordered() {
        Instant testStart = Instant.parse("2016-02-17T18:00:00Z");
        Instant testEnd = Instant.parse("2016-02-19T10:30:00Z");
        
        List<Tweet> all = Arrays.asList(tweet1, tweet5, tweet2, tweet4, tweet3);
        List<Tweet> inTimespan = Filter.inTimespan(all, new Timespan(testStart, testEnd));   
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet4, tweet5)));
        assertTrue("expected same order", isSameOrder(all, inTimespan));
    }    
    
    
    
    @Test
    public void testContainingDoesNotContain() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("winnie", "crazy"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    
    
    @Test
    public void testContainingSomeTweetsContainSomeWords() {
        List<Tweet> all = Arrays.asList(tweet1, tweet3, tweet2);
        List<Tweet> containing = Filter.containing(all, Arrays.asList("30", "much", "universe"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet2)));
        assertTrue(isSameOrder(all,  containing));
    }

    @Test
    public void testContainingSomeTweetsContainSomeWordsCaseInsensitive() {
        List<Tweet> all = Arrays.asList(tweet4, tweet3, tweet2);
        List<Tweet> containing = Filter.containing(all, Arrays.asList("PorTia", "ExPectING", "UNiverse"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet3, tweet4)));
        assertTrue(isSameOrder(all,  containing));
    }    

    
    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

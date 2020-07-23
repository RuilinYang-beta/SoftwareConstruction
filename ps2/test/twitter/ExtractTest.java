package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * @author RuilinYang
 *
 */
/**
 * @author RuilinYang
 *
 */
public class ExtractTest {

    /**
     * Testing Strategy - Extract.getTimespan(..)
     * 
     * Partitioning the input as follows: 
     * - tweets.size():               ==2, > 2
     * - order of timestamp of tweet: acending, descending, unordered
     * - draw in timestamp:           multiple starting point, multiple mid point, multiple ending point
     * 
     * 
     * Testing Strategy - Extract.getMentionedUsers(..)
     * 
     * - number of mentioned users: 0, >0
     * - duplicate of users:        no duplicates, dups with same upper/lower case, dups with diff upper/lower case
     * - precedence/succession:     user-mention with immediate preceding char that is valid in username,
     *                              user-mention with immediate preceding char that is NOT valid in username
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T11:03:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T19:00:00Z");
    private static final Instant d5 = Instant.parse("2016-02-18T11:00:56Z");
    
    private static final Tweet tweet0 = new Tweet(0, "a0", "t0", d1);
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "corona", "@alyssa how's the lockdown", d3);
    private static final Tweet tweet4 = new Tweet(4, "disorder", "t4 @AlYsSa @OtHeR-uSeR @other-user", d4);
    private static final Tweet tweet5 = new Tweet(5, "earth", "@alyssa @another @some_one", d5);
    private static final Tweet tweet6 = new Tweet(6, "a6", "Iam@hometoday, visit ME@4@uur,-@_@- @alyssa", d5);
    private static final Tweet tweet7 = new Tweet(7, "a7", "Iam@@hometoday, visit#@ME, #@#, @alyssa", d5);
//    private static final Tweet tweet7 = new Tweet(7, "a7", "@@@heyhey@@@, #@today^big event", d4);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    
    @Test
    public void testGetTimespanManyTweets() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d5, t.getEnd());
    }
    
    
    @Test
    public void testGetTimespanManyTweetsReverse() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet5, tweet4, tweet3, tweet2, tweet1));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d5, t.getEnd());
    }    

    @Test
    public void testGetTimespanManyTweetsUnordered() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet4, tweet5, tweet1));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d5, t.getEnd());
    }    
    
    
    
    @Test
    public void testGetTimespanMultipleStartPoint() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet0, tweet1, tweet2));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d2, t.getEnd());
    }
    

    @Test
    public void testGetTimespanMultipleEndPoint() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d5, t.getEnd());
    }

    
    @Test
    public void testGetTimespanMultipleMiddlePoint() {
        Timespan t = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6, tweet7));
        
        assertEquals("expected start", d1, t.getStart());
        assertEquals("expected end", d5, t.getEnd());
    }
    
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    
    @Test
    public void testGetMentionedUsersNoDup() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet0, tweet1, tweet5));
        Set<String> mentionedUsersLower = getLowercaseOfSet(mentionedUsers);
        
        System.out.println(mentionedUsersLower);
        
        assertTrue(mentionedUsers.size() == 3);
        assertTrue(mentionedUsersLower.containsAll(Arrays.asList("alyssa", "another", "some_one")));
    }
    
    @Test
    public void testGetMentionedUsersSameDup() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet0, tweet3, tweet5));
        Set<String> mentionedUsersLower = getLowercaseOfSet(mentionedUsers);
        
        assertTrue(mentionedUsers.size() == 3);
        assertTrue(mentionedUsersLower.containsAll(Arrays.asList("alyssa", "another", "some_one")));        
    }
    
    
    
    @Test
    public void testGetMentionedUsersDiffDup() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet0, tweet3, tweet4));
        Set<String> mentionedUsersLower = getLowercaseOfSet(mentionedUsers);
        
        assertTrue(mentionedUsers.size() == 2);
        assertTrue(mentionedUsersLower.containsAll(Arrays.asList("alyssa", "other-user")));         
    }
    
    @Test
    public void testGetMentionedUsersPrecWithValidChar() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet0, tweet6));
        Set<String> mentionedUsersLower = getLowercaseOfSet(mentionedUsers);
        
        assertTrue(mentionedUsers.size() == 1);
        assertTrue(mentionedUsersLower.containsAll(Arrays.asList("alyssa")));          
    }

    
    @Test
    public void testGetMentionedUsersPrecWithInvalidChar() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet0, tweet7));
        Set<String> mentionedUsersLower = getLowercaseOfSet(mentionedUsers);
        
        assertTrue(mentionedUsers.size() == 3);
        assertTrue(mentionedUsersLower.containsAll(Arrays.asList("hometoday", "me", "alyssa")));          
    }
    
    
    private Set<String> getLowercaseOfSet(Set<String> original){
        Set<String> lowered = new HashSet<>();
        Iterator<String> iter = original.iterator();
        while(iter.hasNext()) {
            lowered.add(iter.next().toLowerCase());
        }
        return lowered;
    }
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

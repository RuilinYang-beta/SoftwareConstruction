package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Test;

public class SocialNetworkTest {

    /**
     * Testing Strategy - SocialNetwork.guessFollowsGraph(...)
     * - user-mention in tweets:     no mention, 
     *                               non-overlapping mention (each user mentioned non-overlapping users in his/her tweets), 
     *                               overlapping mention, 
     *                               user mention themselves.
     *                               
     * Testing Strategy - SocialNetwork.influencers(...)
     * - input is empty
     * - input non-empty, there's no draw in number of followers
     * - input non-empty, there's draw in number of followers
     * 
     *                                
     */
    

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T11:03:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T19:00:00Z");
    private static final Instant d5 = Instant.parse("2016-02-18T11:00:56Z");
    
    private static final Tweet tweet0 = new Tweet(0, "aLYssA", "is it reasonable to talk about rivest so much?@bitch", d1);
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?@BITCH", d1);
    private static final Tweet tweet2 = new Tweet(2, "bitch", "email@cumin, @doggie @otherone  rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "cumin", "@alyssa @biTCh @cumin my time at portia becomes boring", d3);
    private static final Tweet tweet4 = new Tweet(4, "DoGGIE", "@doggie @CUMIN @DOGGIE but I'm expecting how the baby is like", d4);
    private static final Tweet tweet5 = new Tweet(5, "ESCA-late", "pity that i am not fully socialized", d5);
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    
    @Test
    public void testGuessFollowsGraphTweetsWithoutMention() {
        List<Tweet> all = Arrays.asList(tweet5);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(all);
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    
    @Test
    public void testGuessFollowsGraphTweetsWithNonOverlappingMention() {
        List<Tweet> all = Arrays.asList(tweet1, tweet2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(all);
        Map<String, Set<String>> fLowered = toLowercase(followsGraph);
        
        assertFalse(followsGraph.isEmpty());
        assertEquals(2, followsGraph.size());
        assertTrue(fLowered.get("alyssa").containsAll(Arrays.asList("bitch")));
        assertTrue(fLowered.get("bitch").containsAll(Arrays.asList("doggie", "otherone")));
    }

    @Test
    public void testGuessFollowsGraphTweetsWithOverlappingMention() {
        List<Tweet> all = Arrays.asList(tweet0, tweet1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(all);
        Map<String, Set<String>> fLowered = toLowercase(followsGraph);
        
        assertFalse(followsGraph.isEmpty());
        assertEquals(1, followsGraph.size());
        assertTrue(fLowered.get("alyssa").contains("bitch"));
    }    
    
    @Test
    public void testGuessFollowsGraphUsersMentionThemselves() {
        List<Tweet> all = Arrays.asList(tweet3, tweet4);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(all);
        Map<String, Set<String>> fLowered = toLowercase(followsGraph);
        
        assertFalse(followsGraph.isEmpty());
        assertEquals(2, followsGraph.size());
        assertEquals(2, fLowered.get("cumin").size());
        assertTrue(fLowered.get("cumin").containsAll(Arrays.asList("bitch", "alyssa")));
        assertEquals(1, fLowered.get("doggie").size());
        assertTrue(fLowered.get("doggie").containsAll(Arrays.asList("cumin")));
    }    
    
    
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    
    
    @Test
    public void testInfluencersNonEmptyWithoutDraw() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("a", new HashSet<String>(Arrays.asList("b")));
        followsGraph.put("b", new HashSet<String>(Arrays.asList("a")));
        followsGraph.put("c", new HashSet<String>(Arrays.asList("b", "a")));
        followsGraph.put("d", new HashSet<String>(Arrays.asList("a")));
        followsGraph.put("q", new HashSet<String>(Arrays.asList("c")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        List<String> influencersLower = influencers.stream()
                                                   .map(s -> s.toLowerCase())
                                                   .collect(Collectors.toList());

        assertEquals(3, influencersLower.size());
        assertTrue(influencersLower.get(0).equals("a"));
        assertTrue(influencersLower.get(1).equals("b"));
        assertEquals("c", influencersLower.get(2));
    }   
    
    @Test
    public void testInfluencersNonEmptyWithDraw() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("a", new HashSet<String>(Arrays.asList("b")));
        followsGraph.put("b", new HashSet<String>(Arrays.asList("a")));
        followsGraph.put("c", new HashSet<String>(Arrays.asList("b", "a")));
        followsGraph.put("d", new HashSet<String>(Arrays.asList("a")));
        followsGraph.put("q", new HashSet<String>(Arrays.asList("b", "c")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        List<String> influencersLower = influencers.stream()
                                                   .map(s -> s.toLowerCase())
                                                   .collect(Collectors.toList());

        assertEquals(3, influencersLower.size());
        assertTrue(influencersLower.get(0).equals("a") || influencersLower.get(1).equals("a"));
        assertTrue(influencersLower.get(0).equals("b") || influencersLower.get(1).equals("b"));
        assertEquals("c", influencersLower.get(2));
    }   
    
    
    private boolean isDescOrderNumOfInfluencer(List<String> influencers, Map<String, Set<String>> graph) {
        
        for (int i = 0; i < influencers.size() - 1; i++) {
            String inf1 = influencers.get(i);
            String inf2 = influencers.get(i+1);
            if (  ! (graph.get(inf1).size() <= graph.get(inf2).size()) ) {
                return false;
            }
        }
        
        return true;
        
    }
    
    private Map<String, Set<String>> toLowercase(Map<String, Set<String>> original){
        Map<String, Set<String>> ans = new HashMap<String, Set<String>>();
        
        for (Map.Entry<String, Set<String>> e : original.entrySet()) {
            String key = e.getKey();
            Set<String> val = e.getValue();
            
            Set<String> valLower = val.stream()
                                      .map(s -> s.toLowerCase())
                                      .collect(Collectors.toSet());
            
            ans.put(key.toLowerCase(), valLower);
        }
        return ans;
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

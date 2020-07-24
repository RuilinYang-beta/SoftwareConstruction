package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        // not specified how to store username, choose store in lowercase
        Map<String, Set<String>> ans = new HashMap<String, Set<String>>();
        
        for (Tweet t: tweets) {
            String author = t.getAuthor().toLowerCase();
            Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t)).stream()
                                                                               .map(s -> s.toLowerCase())
                                                                               .filter(s -> !s.equals(author))
                                                                               .collect(Collectors.toSet());
            if (mentioned.size() > 0) {
                if (ans.containsKey(author)) {
                    ans.get(author).addAll(mentioned);
                } else {
                    ans.put(author, mentioned);
                }
            }
        }
        return ans;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        // idea: 
        // from followsGraph compute ansMap: username: num_of_ppl_follow_this_user
        // then sort ansMap by value
        // then extract the keyset of ansMap
        
        // idea2:
        // first make followsGraph all lowercase
        // then merge all the value set into a list
        // then sort the elements in the list by their frequency
        
        //
        // not specified when number of followers has draw
        Map<String, Set<String>> followsGraphLowered = new HashMap<String, Set<String>>();
        for (String key : followsGraph.keySet()) {
            followsGraphLowered.put(key.toLowerCase(), 
                                    followsGraph.get(key).stream()
                                                         .map(s -> s.toLowerCase())
                                                         .collect(Collectors.toSet()));
        }

        Map<String, Integer> freqMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Set<String>> e: followsGraphLowered.entrySet()) {
            Set<String> vals = e.getValue();
            
            for (String v : vals) {
                if (freqMap.containsKey(v)) {
                    freqMap.put(v, freqMap.get(v)+1);
                } else { 
                    freqMap.put(v, 1);
                }
            }   
        }
        
        
        Map<String, Integer> sortedFreqMap = freqMap.entrySet()
                                                    .stream()
                                                    .sorted(Map.Entry.comparingByKey())
                                                    .collect(Collectors.toMap(Map.Entry::getKey, 
                                                                             Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        
        
        return new ArrayList<String>(sortedFreqMap.keySet());
        
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.size() == 0) {
            return null;  // behavior not specified, do whatever
        }
        
        Instant early = tweets.get(0).getTimestamp();
        Instant late = tweets.get(0).getTimestamp();
        
        for (Tweet t: tweets) {
            Instant ts = t.getTimestamp();
            if (ts.isBefore(early)) {
                early = ts;
            }
            if (ts.isAfter(late)) {
                late = ts;
            }
        }
        
        return new Timespan(early, late);

    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        // it's not specified the returned set contain uppercase/lowercase username
        Set<String> mentioned = new HashSet<String>();
        
        for (Tweet tt: tweets) {
            String txt = tt.getText();
            char[] chars = txt.toCharArray();
            
            int i = 0;
            while (i < chars.length - 1) {
                if (isStartOfUsername(chars, i)) {
                    int j = i + 1;
                    StringBuilder sb = new StringBuilder();    
                    while (j < chars.length && isValidCharInUsername(chars[j])){
                        sb.append(chars[j]);
                        j++;  
                    }
                    String name = sb.toString();
                    mentioned.add(name.toUpperCase());
                    i = j;
                }
                i += 1;     
            }
        }
        return mentioned;
    }
    
    private static boolean isStartOfUsername(char[] chars, int i) {
        
        if (i == 0) {
            if (chars[i] == '@' && isValidCharInUsername(chars[i+1])) {
                return true;
            }     
        } else if (!isValidCharInUsername(chars[i-1]) && chars[i] == '@' && isValidCharInUsername(chars[i+1])) {
            return true;
        }

        return false;
    }
    
    private static boolean isValidCharInUsername(char c) {
        if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
            return true;
        }
        
        return false;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

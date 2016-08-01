package masterwb.design.arkcongress.entities;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Created by Master on 25/07/2016.
 */
public class TwitterClient {
    private TwitterSession session;

    public void setTwitterSession(TwitterSession sessionCreated) {
        session = sessionCreated;
    }

    // Get the email once Twitter login was successful
    public String getTwitterEmail() {
        final String[] email = new String[1];
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                email[0] = result.toString();
            }

            @Override
            public void failure(TwitterException exception) {
                email[0] = "";
            }
        });
        return email[0];
    }

    public static TwitterClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final TwitterClient INSTANCE = new TwitterClient();
    }
}

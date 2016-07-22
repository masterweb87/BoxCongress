package masterwb.design.arkcongress;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Master on 08/07/2016.
 */
public class ArkCongressApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeTwitterLogin();
        initializeFacebookLogin();
    }

    public void initializeTwitterLogin() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }

    public void initializeFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}

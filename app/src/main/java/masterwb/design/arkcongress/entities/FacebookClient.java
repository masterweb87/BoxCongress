package masterwb.design.arkcongress.entities;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Master on 25/07/2016.
 */
public class FacebookClient {
    private CallbackManager fb_manager;
    private List<String> fb_permissions = Arrays.asList("email", "public_profile");
    private AccessToken currentToken;
    private AccessTokenTracker accessTokenTracker;

    public List<String> getFb_permissions() {
        return fb_permissions;
    }

    public void setFb_permissions(List<String> fb_permissions) {
        this.fb_permissions = fb_permissions;
    }

    public CallbackManager getFacebookManager() {
        return fb_manager;
    }

    public void setFacebookManager() {
        this.fb_manager = CallbackManager.Factory.create();
    }

    public boolean isConnected() {
        currentToken = AccessToken.getCurrentAccessToken();
        if(currentToken != null) return true;
        else return false;
    }

    public AccessToken getFacebookToken() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                currentToken = currentAccessToken;
            }
        };
        accessTokenTracker.startTracking();
        currentToken = AccessToken.getCurrentAccessToken();
        return currentToken;
    }

    public void signOut() {
        LoginManager.getInstance().logOut();
    }

    public static FacebookClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FacebookClient INSTANCE = new FacebookClient();
    }
}

package masterwb.design.arkcongress.db;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.LoginEvent;
import masterwb.design.arkcongress.ui.LoginActivity;

/**
 * Created by Master on 14/07/2016.
 */
public class LoginRepository {
    private AuthCredential firebaseCredential;

    // Authenticate with Firebase once Facebook has been successful
    public AuthCredential firebaseAuthWithFacebook(AccessToken token) {
        firebaseCredential = FacebookAuthProvider.getCredential(token.getToken());
        return firebaseCredential;
    }

    // Authenticate with Firebase once Google has been successful
    public AuthCredential firebaseAuthWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount account = result.getSignInAccount();
            if(account != null) {
                firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                return firebaseCredential;
            }
        }
        return null;
    }

    // Authenticate with Firebase once Twitter has been successful
    public AuthCredential firebaseAuthWithTwitter(String token, String secret) {
        firebaseCredential = TwitterAuthProvider.getCredential(token, secret);
        return firebaseCredential;
    }

    // Register and send the event to EventBus
    private void postEvent(int type) {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setEventType(type);

        CustomEventBus eventBus = CustomEventBus.getInstance();
        eventBus.post(loginEvent);
    }
}

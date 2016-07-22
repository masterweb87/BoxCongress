package masterwb.design.arkcongress.db;

import android.support.annotation.NonNull;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;

import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.LoginEvent;

/**
 * Created by Master on 14/07/2016.
 */
public class LoginRepository {
    private FirebaseManager firebaseManager;
    private FirebaseAuth authUser;
    private AuthCredential firebaseCredential;
    // Facebook variables
    private AccessToken currentToken;
    private AccessTokenTracker accessTokenTracker;
    // Google variables
    private GoogleSignInOptions googleOptions;
    private String googleLoginType = Scopes.PLUS_LOGIN;
    private Scope googleScope;

    public LoginRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    // Set the access token using CurrentAccessToken and set the actual session if exists
    public AccessToken getFacebookSession() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                currentToken = currentAccessToken;
            }
        };
        currentToken = AccessToken.getCurrentAccessToken();
        return currentToken;
    }

    // Set Google Options
    public GoogleSignInOptions setGoogleOptions() {
        googleScope = new Scope(googleLoginType);
        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(masterwb.design.arkcongress.BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail().requestScopes(googleScope).build();
        return googleOptions;
    }

    // Handle Google authentication
    public void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Signed out
            postEvent(LoginEvent.loginError);
        }
    }

    // Check if a Twitter session exists
    public boolean getTwitterSession() {
        if(Twitter.getSessionManager().getActiveSession() != null) {
            return true;
        }
        else {
            return false;
        }
    }

    // Authenticate with Firebase once Google has been successful
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInWithCredential();
    }

    // Authenticate with Firebase once Facebook has been successful
    public void firebaseAuthWithFacebook(AccessToken token) {
        firebaseCredential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential();
    }

    // Authenticate with Firebase once Twitter has been successful
    public void firebaseAuthWithTwitter(String token, String secret) {
        firebaseCredential = TwitterAuthProvider.getCredential(token, secret);
        signInWithCredential();
    }

    // Sign in Firebase with the created Credential
    public void signInWithCredential() {
        authUser = firebaseManager.getAuthenticateUser();
        authUser.signInWithCredential(firebaseCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    postEvent(LoginEvent.loginSuccess);
                }
                else {
                    postEvent(LoginEvent.loginError);
                }
            }
        });
    }

    // Adds an AuthListener when needed
    public void addAuthListener() {
        firebaseManager.addAuthListener();
    }

    // Removes an AuthListener when needed
    public void removeAuthListener() {
        firebaseManager.removeAuthListener();
    }

    // Register and send the event to EventBus
    private void postEvent(int type) {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setEventType(type);

        CustomEventBus eventBus = CustomEventBus.getInstance();
        eventBus.post(loginEvent);
    }
}

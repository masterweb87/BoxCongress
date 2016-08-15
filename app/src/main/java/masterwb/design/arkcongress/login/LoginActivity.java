package masterwb.design.arkcongress.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.FacebookClient;
import masterwb.design.arkcongress.entities.GoogleClient;
import masterwb.design.arkcongress.entities.TwitterClient;
import masterwb.design.arkcongress.events.LoginEvent;
import masterwb.design.arkcongress.main.MainActivity;

/**
 * Created by Master on 08/07/2016.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, LoginView {
    @BindView(R.id.facebook_login_button) LoginButton facebookLoginButton;
    @BindView(R.id.google_login_button) SignInButton googleLoginButton;
    @BindView(R.id.twitter_login_button) TwitterLoginButton twitterLoginButton;
    @BindView(R.id.loginContainer) RelativeLayout loginContainer;
    @BindView(R.id.loginProgressBar) ProgressBar progressBar;

    private boolean activeSession;
    // Presenter
    private LoginPresenter presenter;
    // Firebase listener
    private FirebaseAuth authUser;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    // Facebook, Google and Twitter instances
    private FacebookClient fbClient = FacebookClient.getInstance();
    private GoogleClient googleClient = GoogleClient.getInstance();
    private TwitterClient twitterClient = TwitterClient.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Set Firebase auth instance
        authUser = firebaseManager.getFirebaseAuth();
        activeSession = firebaseManager.getFirebaseSession();
        // Set the Presenter
        presenter = new LoginPresenter(this);
        presenter.onCreate();

        if(activeSession) {
            goToMainScreen();
        }

        // Set Google
        setGoogleOptions();
        // Login with Facebook
        loginWithFacebook();
        // Login with Twitter
        loginWithTwitter();
    }

    // Set the read permissions and login using FB credentials
    private void loginWithFacebook() {
        // Set FB callback Manager
        fbClient.setFacebookManager();

        facebookLoginButton.setReadPermissions(fbClient.getFb_permissions());
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
            }
        });
        facebookLoginButton.registerCallback(fbClient.getFacebookManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken newCurrentToken = loginResult.getAccessToken();
                if(fbClient.isConnected()) fbClient.signOut();
                AuthCredential authCredential = presenter.loginFirebaseWithFacebook(newCurrentToken);
                if(authCredential != null) {
                    signInWithCredential(authCredential);
                }
                else {
                    loginError(LoginEvent.loginFacebookError);
                }
            }

            @Override
            public void onCancel() {
                loginError(LoginEvent.loginFacebookCancel);
            }

            @Override
            public void onError(FacebookException error) {
                loginError(LoginEvent.loginFacebookError);
            }
        });
    }

    // Login using Google credentials
    public void loginWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleClient.getGoogleApiClient());
        startActivityForResult(intent, googleClient.getSuccessCode());
    }

    // Login using Twitter credentials
    public void loginWithTwitter() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterClient.setTwitterSession(result.data);
                String token = result.data.getAuthToken().token;
                String secret = result.data.getAuthToken().secret;
                AuthCredential authCredential = presenter.loginFirebaseWithTwitter(token, secret);
                //String twEmail = twitterClient.getTwitterEmail();
                if(authCredential != null) {
                    signInWithCredential(authCredential);
                }
                else {
                    loginError(LoginEvent.loginTwitterError);
                }
            }
            @Override
            public void failure(TwitterException exception) {
                loginError(LoginEvent.loginTwitterError);
            }
        });
    }

    // Set Google Options
    public void setGoogleOptions() {
        GoogleSignInOptions googleOptions = googleClient.setGoogleOptions();
        GoogleApiClient newGoogleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleOptions).build();
        googleClient.setGoogleApiClient(newGoogleClient);

        googleLoginButton.setOnClickListener(this);
        googleLoginButton.setScopes(googleOptions.getScopeArray());
        googleLoginButton.setSize(SignInButton.SIZE_WIDE);
    }

    // Sign in Firebase with the credential provided
    public void signInWithCredential(final AuthCredential authCredential) {
        authUser.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d("FIREBASE", "TASK RESULT -> "+task.isSuccessful());
                if(!task.isSuccessful()) {
                    enableButtons();
                    authUser = firebaseManager.getFirebaseAuth();
                    loginError(LoginEvent.loginAlreadyExists, authCredential.getProvider());
                }
                else {
                    goToMainScreen();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_button:
                disableButtons();
                loginWithGoogle();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseManager.addAuthListener();
    }

    @Override
    protected void onStop() {
        firebaseManager.removeAuthListener();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // FB
        fbClient.getFacebookManager().onActivityResult(requestCode, resultCode, data);
        // Google
        if(requestCode == googleClient.getSuccessCode()) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            AuthCredential authCredential = presenter.loginFirebaseWithGoogle(result);
            if(authCredential != null) {
                signInWithCredential(authCredential);
            }
            else {
                disableButtons();
                loginError(LoginEvent.loginGoogleError);
            }
        }
        // Twitter
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    // Display the error message if the login is not successful
    @Override
    public void loginError(int errorType) {
        String msgError = "";
        switch (errorType) {
            case LoginEvent.loginGoogleError:
                msgError = getString(R.string.google_login_error);
                break;
            case LoginEvent.loginFacebookError:
                msgError = getString(R.string.facebook_login_error);
                break;
            case LoginEvent.loginTwitterError:
                msgError = getString(R.string.twitter_login_error);
                break;
            case LoginEvent.loginFacebookCancel:
                msgError = getString(R.string.facebook_login_cancel);
                break;
        }
        Snackbar.make(loginContainer, msgError, Snackbar.LENGTH_SHORT).show();
    }

    // Display the error message if the login is not successful
    @Override
    public void loginError(int errorType, String provider) {
        String msgError = "";
        String providerSplit[] = provider.split(Pattern.quote("."));
        String providerName = providerSplit[0].substring(0,1).toUpperCase() + providerSplit[0].substring(1);
        switch (errorType) {
            case LoginEvent.loginAlreadyExists:
                msgError = String.format(getString(R.string.account_already_exists), providerName);
                break;
        }
        Snackbar.make(loginContainer, msgError, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.getErrorMessage() != null) {
            Snackbar.make(loginContainer, connectionResult.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    // Redirects the application to the Main screen
    @Override
    public void goToMainScreen() {
        enableButtons();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void enableButtons() {
        facebookLoginButton.setEnabled(true);
        googleLoginButton.setEnabled(true);
        twitterLoginButton.setEnabled(true);
    }

    @Override
    public void disableButtons() {
        facebookLoginButton.setEnabled(false);
        googleLoginButton.setEnabled(false);
        twitterLoginButton.setEnabled(false);
    }
}

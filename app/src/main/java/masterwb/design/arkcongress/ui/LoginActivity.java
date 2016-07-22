package masterwb.design.arkcongress.ui;

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
import com.facebook.CallbackManager;
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
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.events.LoginEvent;

/**
 * Created by Master on 08/07/2016.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, LoginView {
    @BindView(R.id.facebook_login_button) LoginButton facebookLoginButton;
    @BindView(R.id.google_login_button) SignInButton googleLoginButton;
    @BindView(R.id.twitter_login_button) TwitterLoginButton twitterLoginButton;
    @BindView(R.id.loginContainer) RelativeLayout loginContainer;
    @BindView(R.id.loginProgressBar) ProgressBar progressBar;

    // Presenter
    private LoginPresenter presenter;
    // Facebook variables
    private static List<String> fb_permissions = Arrays.asList("public_profile");
    private CallbackManager fb_manager;
    private AccessToken currentToken;
    // Google variables
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Set the Presenter
        presenter = new LoginPresenter(this);
        presenter.onCreate();

        // Login with Facebook
        loginWithFacebook();

        // Login with Twitter
        loginWithTwitter();
    }

    // Set the read permissions and login using FB credentials
    private void loginWithFacebook() {
        // Set FB callback Manager
        fb_manager = CallbackManager.Factory.create();
        currentToken = presenter.getFacebookSession();

        if(currentToken != null) {
            goToMainScreen();
        }
        else {
            facebookLoginButton.setReadPermissions(fb_permissions);
            facebookLoginButton.registerCallback(fb_manager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    currentToken = loginResult.getAccessToken();
                    presenter.loginFirebaseWithFacebook(currentToken);
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
    }

    // Set Google Options
    public void setGoogleOptions() {
        GoogleSignInOptions googleOptions = presenter.setGoogleOptions();
        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleOptions).build();
        googleLoginButton.setOnClickListener(this);
        googleLoginButton.setScopes(googleOptions.getScopeArray());
        googleLoginButton.setSize(SignInButton.SIZE_WIDE);
    }

    // Login using Google credentials
    public void loginWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    // Login using Twitter credentials
    public void loginWithTwitter() {
        if(presenter.getTwitterSession()) {
            goToMainScreen();
        }
        else {
            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    String token = result.data.getAuthToken().token;
                    String secret = result.data.getAuthToken().secret;
                    presenter.loginFirebaseWithTwitter(token, secret);
                }
                @Override
                public void failure(TwitterException exception) {
                    loginError(LoginEvent.loginTwitterError);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_button:
                setGoogleOptions();
                loginWithGoogle();
                break;
        }
    }

    @Override
    protected void onStart() {
        presenter.addAuthListener();
        super.onStart();
    }

    @Override
    protected void onStop() {
        presenter.removeAuthListener();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        fb_manager.onActivityResult(requestCode, resultCode, data);
        // Google
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            presenter.handleGoogleSignInResult(result);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Redirects the application to the Main screen
    @Override
    public void goToMainScreen() {
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
    public void newUserSuccess() {

    }

    @Override
    public void newUserError(String error) {

    }
}

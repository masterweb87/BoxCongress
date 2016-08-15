package masterwb.design.arkcongress.login;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;

import org.greenrobot.eventbus.Subscribe;

import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.LoginEvent;

/**
 * Created by Master on 14/07/2016.
 */
public class LoginPresenter {
    private CustomEventBus eventBus;
    private LoginRepository repository;
    private LoginView view;

    public LoginPresenter(LoginView view) {
        this.repository = new LoginRepository();
        this.view = view;
        this.eventBus = CustomEventBus.getInstance();
    }

    public void onCreate() {
        eventBus.register(this);
    }

    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    public AuthCredential loginFirebaseWithFacebook(AccessToken token) {
        return repository.firebaseAuthWithFacebook(token);
    }

    public AuthCredential loginFirebaseWithGoogle(GoogleSignInResult result) {
        return repository.firebaseAuthWithGoogle(result);
    }

    public AuthCredential loginFirebaseWithTwitter(String token, String secret) {
        return repository.firebaseAuthWithTwitter(token, secret);
    }

    @Subscribe
    public void onEventMainThread(LoginEvent event) {
        switch (event.getEventType()) {
            case LoginEvent.loginSuccess:
                onLoginSuccess();
                break;
            case LoginEvent.loginError:
                onLoginError(LoginEvent.loginError);
                break;
        }
    }

    public void onLoginSuccess() {
        if(view != null) {
            view.hideProgress();
            view.goToMainScreen();
        }
    }

    public void onLoginError(int errorType) {
        if(view != null) {
            view.hideProgress();
            view.loginError(errorType);
        }
    }
}

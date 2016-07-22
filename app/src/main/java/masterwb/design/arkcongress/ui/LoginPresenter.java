package masterwb.design.arkcongress.ui;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.greenrobot.eventbus.Subscribe;

import masterwb.design.arkcongress.db.LoginRepository;
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

    public GoogleSignInOptions setGoogleOptions() {
        return repository.setGoogleOptions();
    }

    public void handleGoogleSignInResult(GoogleSignInResult result) {
        repository.handleGoogleSignInResult(result);
    }

    public AccessToken getFacebookSession() {
        return repository.getFacebookSession();
    }

    public void loginFirebaseWithFacebook(AccessToken token) {
        repository.firebaseAuthWithFacebook(token);
    }

    public boolean getTwitterSession() {
        return repository.getTwitterSession();
    }

    public void loginFirebaseWithTwitter(String token, String secret) {
        repository.firebaseAuthWithTwitter(token, secret);
    }

    public void addAuthListener() {
        repository.addAuthListener();
    }

    public void removeAuthListener() {
        repository.removeAuthListener();
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

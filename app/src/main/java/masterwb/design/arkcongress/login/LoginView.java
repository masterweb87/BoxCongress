package masterwb.design.arkcongress.login;

/**
 * Created by Master on 16/07/2016.
 */
public interface LoginView {
    void goToMainScreen();
    void loginError(int errorType);
    void loginError(int errorType, String provider);

    void showProgress();
    void hideProgress();

    void enableButtons();
    void disableButtons();
}

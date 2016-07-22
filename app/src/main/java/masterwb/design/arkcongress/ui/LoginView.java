package masterwb.design.arkcongress.ui;

/**
 * Created by Master on 16/07/2016.
 */
public interface LoginView {
    void goToMainScreen();
    void loginError(int errorType);

    void showProgress();
    void hideProgress();

    void newUserSuccess();
    void newUserError(String error);
}

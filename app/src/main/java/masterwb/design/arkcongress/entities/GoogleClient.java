package masterwb.design.arkcongress.entities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Created by Master on 25/07/2016.
 */
public class GoogleClient {
    private GoogleApiClient googleClient;
    private int RC_SIGN_IN = 9001;

    public GoogleApiClient getGoogleApiClient() {
        return googleClient;
    }

    public GoogleSignInOptions setGoogleOptions() {
        String googleLoginType = Scopes.PLUS_LOGIN;
        Scope googleScope = new Scope(googleLoginType);
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(masterwb.design.arkcongress.BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail().requestScopes(googleScope).build();
    }

    public void setGoogleApiClient(GoogleApiClient setGoogleClient) {
        googleClient = setGoogleClient;
    }

    public int getSuccessCode() {
        return RC_SIGN_IN;
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(googleClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d("GOOGLE", "Sign out -> " + status.isSuccess() + " -> " + status.getStatusMessage());
            }
        });
    }

    public static GoogleClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final GoogleClient INSTANCE = new GoogleClient();
    }
}

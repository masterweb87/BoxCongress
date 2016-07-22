package masterwb.design.arkcongress.db;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import masterwb.design.arkcongress.entities.User;

/**
 * Created by Master on 14/07/2016.
 */
public class FirebaseManager {
    private final static String FIREBASE_URL = "https://fir-arkcongress.firebaseio.com";
    private final static String EVENTS_PATH = "events";
    private final static String USERS_PATH = "users";

    private FirebaseUser userDataReference;
    private FirebaseAuth authenticateUser;
    private FirebaseAuth.AuthStateListener authUserListener;

    public FirebaseManager() {
        authenticateUser = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuthenticateUser() {
        return authenticateUser;
    }

    public static FirebaseManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void getUserDataReference(FirebaseAuth firebaseAuth) {
        if(firebaseAuth == null)
            userDataReference = authenticateUser.getCurrentUser();
        else
            userDataReference = firebaseAuth.getCurrentUser();
    }

    public String getUserEmail() {
        String email = null;
        getUserDataReference(null);
        if(userDataReference != null) {
            for(UserInfo profile : userDataReference.getProviderData()) {
                email = profile.getEmail();
            }
        }
        return email;
    }

    public FirebaseAuth.AuthStateListener getAuthUserListener() {
        authUserListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                getUserDataReference(firebaseAuth);
            }
        };
        return authUserListener;
    }

    public void addAuthListener() {
        authenticateUser.addAuthStateListener(getAuthUserListener());
    }

    public void removeAuthListener() {
        authenticateUser.removeAuthStateListener(getAuthUserListener());
    }

    private static class SingletonHolder {
        private static final FirebaseManager INSTANCE = new FirebaseManager();
    }
}

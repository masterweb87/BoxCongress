package masterwb.design.arkcongress.db;

import android.support.annotation.NonNull;
import android.util.Log;

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

    private FirebaseUser userData;
    private FirebaseAuth authUser;
    private FirebaseAuth.AuthStateListener authListener;

    public FirebaseManager() {
        this.authUser = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getFirebaseAuth() {
        return authUser;
    }

    public boolean getFirebaseSession() {
        setFirebaseListener();
        userData = authUser.getCurrentUser();
        if(userData != null) {
            Log.d("FIREBASE", "Auth User Listener -> " + userData.getEmail() + " -> " + userData.getProviderId());
            return true;
        }
        else {
            Log.d("FIREBASE", "Auth User Listener null");
            return false;
        }
    }

    // Set Firebase Listener once authentication is successful
    public void setFirebaseListener() {
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                authUser = firebaseAuth;
            }
        };
    }

    // Add the Authenticate Listener
    public void addAuthListener() {
        authUser.addAuthStateListener(authListener);
    }

    // Remove the Authenticate Listener
    public void removeAuthListener() {
        if(authListener != null) {
            authUser.removeAuthStateListener(authListener);
        }
    }

    public void getUserDataReference(FirebaseAuth firebaseAuth) {
        userData = firebaseAuth.getCurrentUser();
        if(userData != null)
            Log.d("FIREBASE", "Successful on Auth Firebase");
        else
            Log.d("FIREBASE", "Error on Auth Firebase");
    }

    public String getUserEmail() {
        String email = null;
        getUserDataReference(null);
        if(userData != null) {
            for(UserInfo profile : userData.getProviderData()) {
                email = profile.getEmail();
            }
        }
        return email;
    }

    public void signOut() {
        authUser.signOut();
    }

    public static FirebaseManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FirebaseManager INSTANCE = new FirebaseManager();
    }
}

package masterwb.design.arkcongress.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.entities.User;
import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.EventList;
import masterwb.design.arkcongress.events.LoginEvent;

/**
 * Created by Master on 14/07/2016.
 */
public class FirebaseManager {
    private final static String FIREBASE_URL = "https://fir-arkcongress.firebaseio.com";
    private final static String EVENTS_PATH = "events";
    private final static String USERS_PATH = "users";

    // Database
    private FirebaseDatabase database;
    private DatabaseReference dataReference;
    private FirebaseUser userData;
    // Authentication
    private FirebaseAuth authUser;
    private FirebaseAuth.AuthStateListener authListener;
    // EventBus
    private CustomEventBus eventBus;

    public FirebaseManager() {
        this.authUser = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.eventBus = CustomEventBus.getInstance();
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

    public void saveNewEvent(Event newEvent) {
        User user = new User();
        String userId = userData.getUid();
        String userEmail = getUserEmail();

        newEvent.setOwner(userId);
        user.setEmail(userEmail);

        // Set the user information
        dataReference = database.getReference(USERS_PATH).child(userId);
        addEventDataListener();
        dataReference.setValue(user);

        // Set the event information
        dataReference = database.getReference(EVENTS_PATH).child(userId);
        String eventKey = dataReference.push().getKey();
        dataReference = database.getReference(EVENTS_PATH).child(userId).child(eventKey);
        addUserDataListener();
        dataReference.setValue(newEvent);
    }

    public void addEventDataListener() {
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void addUserDataListener() {
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void getAllEvents() {
        dataReference = database.getReference(EVENTS_PATH);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> allEvents = new ArrayList<>();
                for(DataSnapshot oneUserNode : dataSnapshot.getChildren()) {
                    for(DataSnapshot oneChild : oneUserNode.getChildren()) {
                        Event oneEvent = oneChild.getValue(Event.class);
                        oneEvent.setId(oneChild.getKey());
                        allEvents.add(oneEvent);
                    }
                }
                EventList eventList = new EventList();
                eventList.setListEvents(allEvents);
                eventList.setEventType(EventList.getAllEvents);
                eventBus.post(eventList);
                //Event event = dataSnapshot.getValue(Event.class);
                Log.d("Events in total: ", String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void getAllMyEvents() {
        String userId = userData.getUid();
        dataReference = database.getReference(EVENTS_PATH).child(userId);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> myEvents = new ArrayList<>();
                for(DataSnapshot oneChild : dataSnapshot.getChildren()) {
                    Event oneEvent = oneChild.getValue(Event.class);
                    myEvents.add(oneEvent);
                }
                EventList eventList = new EventList();
                eventList.setListEvents(myEvents);
                eventList.setEventType(EventList.getMyEvents);
                eventBus.post(eventList);
                //Event event = dataSnapshot.getValue(Event.class);
                Log.d("Events already got! ", dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void getSingleEvent(String eventKey) {
        String userId = userData.getUid();
        dataReference = database.getReference(EVENTS_PATH).child(userId).child(eventKey);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> myEvents = new ArrayList<>();
                EventList eventList = new EventList();
                Event selected = dataSnapshot.getValue(Event.class);
                myEvents.add(selected);
                eventList.setListEvents(myEvents);
                eventList.setEventType(EventList.getSingleEvent);
                eventBus.post(eventList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void removeDataListener() {
        if(dataReference != null) {
            dataReference.removeEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Cancelled: ", databaseError.getMessage());
                }
            });
        }
    }

    public String getUserEmail() {
        String email = null;
        userData = authUser.getCurrentUser();
        if(userData != null) {
            email = userData.getEmail();
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

package masterwb.design.arkcongress.events;

import java.util.List;

import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.entities.Event;

/**
 * Created by Master on 16/07/2016.
 */
public class LoginEvent {
    public static final int loginError = 0;
    public static final int loginSuccess = 1;
    public static final int loginAlreadyExists = 2;

    public static final int loginGoogleError = 10;
    public static final int loginFacebookError = 11;
    public static final int loginTwitterError = 12;
    public static final int loginFacebookCancel = 13;

    private int eventType;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}

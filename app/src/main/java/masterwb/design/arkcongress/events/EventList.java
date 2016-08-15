package masterwb.design.arkcongress.events;

import java.util.List;

import masterwb.design.arkcongress.entities.Event;

/**
 * Created by Master on 07/08/2016.
 */
public class EventList {
    public static final int getMyEvents = 1;
    public static final int getAllEvents = 2;
    public static final int getSingleEvent = 3;

    private int eventType;
    private List<Event> listEvents;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public List<Event> getListEvents() {
        return listEvents;
    }

    public void setListEvents(List<Event> listEvents) {
        this.listEvents = listEvents;
    }
}

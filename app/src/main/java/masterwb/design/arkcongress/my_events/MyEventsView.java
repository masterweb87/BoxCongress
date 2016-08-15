package masterwb.design.arkcongress.my_events;

import java.util.List;

import masterwb.design.arkcongress.entities.Event;

/**
 * Created by Master on 07/08/2016.
 */
public interface MyEventsView {
    void onMyEventListUpdate(List<Event> myEvents);
}

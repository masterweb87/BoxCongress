package masterwb.design.arkcongress.main;

import java.util.List;

import masterwb.design.arkcongress.entities.Event;

/**
 * Created by Master on 10/08/2016.
 */
public interface MainView {
    void onEventListUpdate(List<Event> myEvents);
}

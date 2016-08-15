package masterwb.design.arkcongress.event_info;

import java.util.List;

import masterwb.design.arkcongress.entities.Event;

/**
 * Created by Master on 14/08/2016.
 */
public interface EventInfoView {
    void onEventInfoUpdate(List<Event> event);
}

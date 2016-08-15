package masterwb.design.arkcongress.event_info;

import org.greenrobot.eventbus.Subscribe;
import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.EventList;

/**
 * Created by Master on 14/08/2016.
 */
public class EventInfoPresenter {
    private CustomEventBus eventBus;
    private EventInfoView view;

    public EventInfoPresenter(EventInfoView view) {
        this.view = view;
        this.eventBus = CustomEventBus.getInstance();
    }

    public void onCreate() {
        eventBus.register(this);
    }

    @Subscribe
    public void onSingleEventMainThread(EventList eventList) {
        if(view != null) {
            switch (eventList.getEventType()) {
                case EventList.getSingleEvent:
                    view.onEventInfoUpdate(eventList.getListEvents());
                    break;
            }
        }
    }
}

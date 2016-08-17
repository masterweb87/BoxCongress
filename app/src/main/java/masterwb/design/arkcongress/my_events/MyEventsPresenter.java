package masterwb.design.arkcongress.my_events;

import org.greenrobot.eventbus.Subscribe;

import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.EventList;

/**
 * Created by Master on 07/08/2016.
 */
public class MyEventsPresenter {
    private CustomEventBus eventBus;
    private MyEventsView view;

    public MyEventsPresenter(MyEventsView view) {
        this.view = view;
        this.eventBus = CustomEventBus.getInstance();
    }

    public void onCreate() {
        eventBus.register(this);
    }

    public void onDestroy() { eventBus.unregister(this); }

    @Subscribe
    public void onMyEventMainThread(EventList eventList) {
        if(view != null) {
            switch (eventList.getEventType()) {
                case EventList.getMyEvents:
                    view.onMyEventListUpdate(eventList.getListEvents());
                    break;
            }
        }
    }
}

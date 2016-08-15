package masterwb.design.arkcongress.main;

import org.greenrobot.eventbus.Subscribe;

import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.events.CustomEventBus;
import masterwb.design.arkcongress.events.EventList;
import masterwb.design.arkcongress.my_events.MyEventsView;

/**
 * Created by Master on 10/08/2016.
 */
public class MainPresenter {
    private CustomEventBus eventBus;
    private MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
        this.eventBus = CustomEventBus.getInstance();
    }

    public void onCreate() {
        eventBus.register(this);
    }

    @Subscribe
    public void onEventListMainThread(EventList eventList) {
        if(view != null) {
            switch (eventList.getEventType()) {
                case EventList.getAllEvents:
                    view.onEventListUpdate(eventList.getListEvents());
                    break;
            }
        }
    }
}

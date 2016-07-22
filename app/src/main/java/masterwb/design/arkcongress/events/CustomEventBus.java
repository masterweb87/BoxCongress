package masterwb.design.arkcongress.events;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Master on 16/07/2016.
 */
public class CustomEventBus {
    public EventBus eventBus;

    public CustomEventBus() {
        eventBus = org.greenrobot.eventbus.EventBus.getDefault();
    }

    public void register(Object suscriber) {
        eventBus.register(suscriber);
    }

    public void unregister(Object suscriber) {
        eventBus.unregister(suscriber);
    }

    public void post(Object event) {
        eventBus.post(event);
    }

    public static CustomEventBus getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        private static final CustomEventBus INSTANCE = new CustomEventBus();
    }
}

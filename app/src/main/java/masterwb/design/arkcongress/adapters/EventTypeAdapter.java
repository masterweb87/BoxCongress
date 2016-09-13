package masterwb.design.arkcongress.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Administrator on 12-09-2016.
 */
public class EventTypeAdapter extends ArrayAdapter<CharSequence> {
    private Context context;
    private boolean firstShow = true;

    public EventTypeAdapter(Context context, int resource, CharSequence[] objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}

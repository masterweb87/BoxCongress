package masterwb.design.arkcongress.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.event_info.EventInfoActivity;

/**
 * Created by Master on 11/07/2016.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements View.OnClickListener {
    private Context context;
    private List<Event> eventsList;

    public EventsAdapter() {}

    public EventsAdapter(Context context, List<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageResource(R.mipmap.ark_congress);
        // Set the title and the click listener
        holder.titleEvent.setText(eventsList.get(position).getName());
        holder.titleEvent.setTag(eventsList.get(position));
        holder.titleEvent.setOnClickListener(this);
        // Type, Location, etc...
        holder.typeEvent.setText(eventsList.get(position).getType());
        holder.locationEvent.setText(eventsList.get(position).getLocation());
        holder.descriptionEvent.setText(eventsList.get(position).getDescription());
        holder.startDateEvent.setText(eventsList.get(position).getStartDate());
        holder.endDateEvent.setText(eventsList.get(position).getEndDate());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void setItems(List<Event> newEvents) {
        eventsList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public void goToEventInfo(String eventId) {
        Intent intent = new Intent(this.context, EventInfoActivity.class);
        intent.putExtra("eventId", eventId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Event eventClicked = (Event) v.getTag();
        Log.d("CLICKED!:", "Title clicked:"+eventClicked.getId());
        goToEventInfo(eventClicked.getId());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageEvent) ImageView imageView;
        @BindView(R.id.titleEvent) TextView titleEvent;
        @BindView(R.id.typeEvent) TextView typeEvent;
        @BindView(R.id.locationEvent) TextView locationEvent;
        @BindView(R.id.descriptionEvent) TextView descriptionEvent;
        @BindView(R.id.startDateEvent) TextView startDateEvent;
        @BindView(R.id.endDateEvent) TextView endDateEvent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

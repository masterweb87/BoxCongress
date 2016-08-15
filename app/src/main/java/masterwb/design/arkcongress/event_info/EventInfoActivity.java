package masterwb.design.arkcongress.event_info;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.EventsAdapter;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.Event;

public class EventInfoActivity extends AppCompatActivity implements EventInfoView {
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.editEventName) TextView eventName;
    @BindView(R.id.listEventType) TextView eventType;
    @BindView(R.id.inputStartDate) TextView startDate;
    @BindView(R.id.inputEndDate) TextView endDate;
    @BindView(R.id.inputLocation) TextView location;
    @BindView(R.id.inputDescription) TextView description;
    // Events data
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    // Presenter
    private EventInfoPresenter presenter;
    // Session
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    // Progress dialog
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        ButterKnife.bind(this);

        String eventId = getIntent().getStringExtra("eventId");
        presenter = new EventInfoPresenter(this);
        presenter.onCreate();

        firebaseManager.setFirebaseListener();
        setSupportActionBar(mainToolbar);

        if(eventId != null) {
            enableProgress();
            firebaseManager.getSingleEvent(eventId);
        }
    }

    private void enableProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.my_event_loading_title));
        progress.setMessage(getString(R.string.my_event_loading_msg));
        progress.setIndeterminate(true);
        progress.show();
    }

    private void disableProgress() {
        progress.dismiss();
    }

    @Override
    public void onEventInfoUpdate(List<Event> event) {
        Event eventInfo = event.get(0);
        eventName.setText(eventInfo.getName());
        eventType.setText(eventInfo.getType());
        startDate.setText(eventInfo.getStartDate());
        endDate.setText(eventInfo.getEndDate());
        location.setText(eventInfo.getLocation());
        description.setText(eventInfo.getDescription());
        disableProgress();
    }
}

package masterwb.design.arkcongress.event_info;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.EventsAdapter;
import masterwb.design.arkcongress.create_event.CreateEventActivity;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.login.LoginActivity;
import masterwb.design.arkcongress.main.MainActivity;
import masterwb.design.arkcongress.my_events.MyEventsActivity;

public class EventInfoActivity extends AppCompatActivity implements EventInfoView {
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.eventName) TextView eventName;
    @BindView(R.id.eventType) TextView eventType;
    @BindView(R.id.startDate) TextView startDate;
    @BindView(R.id.endDate) TextView endDate;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.description) TextView description;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();

        enableProgress();
        firebaseManager.getSingleEvent(eventId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu.findItem(R.id.actionMainScreen) != null) {
            menu.findItem(R.id.actionMainScreen).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                logout();
                break;
            case R.id.actionCreateEvent:
                goToCreateEvent();
                break;
            case R.id.actionMyEvents:
                goToMyEvents();
                break;
            case android.R.id.home:
                goBackToMainScreen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void goBackToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToCreateEvent() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    private void goToMyEvents() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        startActivity(intent);
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

    public void logout() {
        firebaseManager.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onEventInfoUpdate(List<Event> event) {
        Event eventInfo = event.get(0);
        eventName.setText(eventInfo.getName());
        eventType.setText(eventInfo.getType());
        startDate.setText(eventInfo.getFormattedStartDate());
        endDate.setText(eventInfo.getFormattedEndDate());
        location.setText(eventInfo.getLocation());
        description.setText(eventInfo.getDescription());
        disableProgress();
    }
}

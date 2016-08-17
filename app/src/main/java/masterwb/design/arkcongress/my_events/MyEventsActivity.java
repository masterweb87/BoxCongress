package masterwb.design.arkcongress.my_events;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.EventsAdapter;
import masterwb.design.arkcongress.create_event.CreateEventActivity;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.event_info.EventInfoActivity;
import masterwb.design.arkcongress.login.LoginActivity;
import masterwb.design.arkcongress.main.MainActivity;

public class MyEventsActivity extends AppCompatActivity implements MyEventsView {
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.myEventsRecyclerView) RecyclerView myEventsRecyclerView;

    // Events adapter
    private EventsAdapter adapter;
    // Session
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private MyEventsPresenter presenter;
    // Progress dialog
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        ButterKnife.bind(this);

        presenter = new MyEventsPresenter(this);
        presenter.onCreate();

        firebaseManager.setFirebaseListener();
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();

        enableProgress();
        firebaseManager.getAllMyEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.actionMyEvents) != null) {
            menu.findItem(R.id.actionMyEvents).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                logout();
                break;
            case R.id.actionMainScreen:
                goBackToMainScreen();
                break;
            case R.id.actionCreateEvent:
                goToCreateEvent();
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

    public void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myEventsRecyclerView.setLayoutManager(layoutManager);
        myEventsRecyclerView.setAdapter(adapter);
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

    private void goBackToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToCreateEvent() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void logout() {
        firebaseManager.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onMyEventListUpdate(List<Event> myEvents) {
        adapter = new EventsAdapter(this, myEvents);
        setRecyclerView();
        adapter.notifyDataSetChanged();
        disableProgress();
    }
}

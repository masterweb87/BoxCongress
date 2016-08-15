package masterwb.design.arkcongress.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.EventsAdapter;
import masterwb.design.arkcongress.create_event.CreateEventActivity;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.login.LoginActivity;
import masterwb.design.arkcongress.my_events.MyEventsActivity;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.mainRecyclerView) RecyclerView mainRecyclerView;

    // Events data
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    // Session variable
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private MainPresenter presenter;
    // Progress dialog
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this);
        presenter.onCreate();

        firebaseManager.setFirebaseListener();
        setSupportActionBar(mainToolbar);
        invalidateOptionsMenu();

        enableProgress();
        firebaseManager.getAllEvents();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCreateEvent() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    private void goToMyEvents() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        startActivity(intent);
    }

    public List<Event> setItems() {
        List<Event> items = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Event item = new Event();
            item.setName("Evento #"+i);
            if(i%2 == 0) item.setType("Bazar");
            else item.setType("Congreso");
            String newStartDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
            item.setStartDate(newStartDate);
            String newEndDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
            item.setEndDate(newEndDate);
            item.setLocation("Guadalajara");
            item.setDescription("Prueba de descripción del evento número "+i+". Más texto aquí acerca del evento");
            items.add(item);
        }
        return items;
    }

    public void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(layoutManager);
        mainRecyclerView.setAdapter(adapter);
    }

    private void enableProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.main_loading_title));
        progress.setMessage(getString(R.string.main_loading_msg));
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
    protected void onStart() {
        super.onStart();
        firebaseManager.addAuthListener();
    }

    @Override
    protected void onStop() {
        firebaseManager.removeAuthListener();
        super.onStop();
    }

    @Override
    public void onEventListUpdate(List<Event> allEvents) {
        adapter = new EventsAdapter(this, allEvents);
        setRecyclerView();
        adapter.notifyDataSetChanged();
        disableProgress();
    }
}

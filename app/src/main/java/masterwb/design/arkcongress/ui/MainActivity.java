package masterwb.design.arkcongress.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.provider.FirebaseInitProvider;
import com.twitter.sdk.android.Twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.EventsAdapter;
import masterwb.design.arkcongress.entities.Event;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.mainRecyclerView) RecyclerView mainRecyclerView;

    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);
        setRecyclerView();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public List setItems() {
        List items = new ArrayList();
        for(int i=0; i<10; i++) {
            Event item = new Event("Evento #"+i);
            if(i%2 == 0) item.setType("Bazar");
            else item.setType("Congreso");
            item.setStartDate(new Date());
            item.setEndDate(new Date());
            item.setLocation("Guadalajara");
            item.setDescription("Prueba de descripción del evento número "+i+". Más texto aquí acerca del evento");
            items.add(item);
        }
        return items;
    }

    public void setRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter() {
        adapter = new EventsAdapter(setItems());
        mainRecyclerView.setAdapter(adapter);
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        Twitter.logOut();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

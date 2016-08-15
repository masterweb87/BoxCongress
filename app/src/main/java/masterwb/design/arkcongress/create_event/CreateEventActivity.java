package masterwb.design.arkcongress.create_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.login.LoginActivity;
import masterwb.design.arkcongress.main.MainActivity;
import masterwb.design.arkcongress.my_events.MyEventsActivity;

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    @BindView(R.id.createFormLayout) LinearLayout formLayout;
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.editEventName) EditText eventName;
    @BindView(R.id.listEventType) Spinner listEventType;
    @BindView(R.id.inputStartDate) TextView startDate;
    @BindView(R.id.inputStartTime) TextView startTime;
    @BindView(R.id.inputEndDate) TextView endDate;
    @BindView(R.id.inputEndTime) TextView endTime;
    @BindView(R.id.inputLocation) EditText location;
    @BindView(R.id.inputDescription) EditText description;
    @BindView(R.id.submitCreateEvent) Button submitButton;

    // Date and Time
    private DatePickerDialog startDateDialog;
    private TimePickerDialog startTimeDialog;
    private DatePickerDialog endDateDialog;
    private TimePickerDialog endTimeDialog;
    private boolean startTimeClicked;
    // Type
    private String typeSelected;
    // Session
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);

        firebaseManager.setFirebaseListener();
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();
        setEventTypeList();

        // Date and Time setting
        setDateClickListeners(startDate, "StartDate");
        setDateClickListeners(endDate, "EndDate");
        setTimeClickListeners(startTime, "StartTime");
        setTimeClickListeners(endTime, "EndTime");

        // Create the event and redirects
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyRequiredNotEmpty()) {
                    createNewEvent();
                    goToMyEvents();
                }
                else {
                    Snackbar.make(formLayout, R.string.create_event_required_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.actionCreateEvent) != null) {
            menu.findItem(R.id.actionCreateEvent).setVisible(false);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        typeSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        switch (view.getTag()) {
            case "StartDate":
                startDate.setText(date);
                break;
            case "EndDate":
                endDate.setText(date);
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String newMinute = String.format(Locale.US, "%02d", minute);
        String time = hourOfDay + ":" + newMinute;
        if(startTimeClicked) startTime.setText(time);
        else endTime.setText(time);
    }

    private void setDateClickListeners(TextView input, final String tagIdentity) {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if(tagIdentity.contains("Start")) {
                    startDateDialog = DatePickerDialog.newInstance(
                            CreateEventActivity.this,
                            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    startDateDialog.vibrate(false);
                    startDateDialog.show(getFragmentManager(), tagIdentity);
                }
                else {
                    endDateDialog = DatePickerDialog.newInstance(
                            CreateEventActivity.this,
                            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    endDateDialog.vibrate(false);
                    endDateDialog.show(getFragmentManager(), tagIdentity);
                }
            }
        });
    }

    private void setTimeClickListeners(TextView input, final String tagIdentity) {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                Calendar fixed = new GregorianCalendar(2016, 1, 1);
                if(tagIdentity.contains("Start")) {
                    startTimeClicked = true;
                    startTimeDialog = TimePickerDialog.newInstance(
                            CreateEventActivity.this, now.get(Calendar.HOUR_OF_DAY), fixed.get(Calendar.MINUTE), true);
                    startTimeDialog.vibrate(false);
                    startTimeDialog.show(getFragmentManager(), tagIdentity);
                }
                else {
                    startTimeClicked = false;
                    endTimeDialog = TimePickerDialog.newInstance(
                            CreateEventActivity.this, now.get(Calendar.HOUR_OF_DAY), fixed.get(Calendar.MINUTE), true);
                    endTimeDialog.vibrate(false);
                    endTimeDialog.show(getFragmentManager(), tagIdentity);
                }
            }
        });
    }

    private void setEventTypeList() {
        ArrayAdapter<CharSequence> typeList = ArrayAdapter.createFromResource(this,
                R.array.event_type_list, R.layout.spinner_item);
        typeList.setDropDownViewResource(R.layout.spinner_list_item);
        listEventType.setAdapter(typeList);
        listEventType.setOnItemSelectedListener(this);
    }

    private boolean verifyRequiredNotEmpty() {
        return eventName.getText().toString().isEmpty()
                || typeSelected.isEmpty()
                || description.getText().toString().isEmpty();
    }

    private void createNewEvent() {
        Event newEvent = new Event();
        // Set name and type
        newEvent.setName(eventName.getText().toString());
        newEvent.setType(typeSelected);
        // Set start date and end date
        newEvent.setStartDate(startDate.getText().toString());
        newEvent.setEndDate(endDate.getText().toString());
        // Set location and description
        newEvent.setLocation(location.getText().toString());
        newEvent.setDescription(description.getText().toString());
        registerEventOnDatabase(newEvent);
    }

    private void registerEventOnDatabase(Event newEvent) {
        firebaseManager.saveNewEvent(newEvent);
    }

    private void goBackToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToMyEvents() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void logout() {
        firebaseManager.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

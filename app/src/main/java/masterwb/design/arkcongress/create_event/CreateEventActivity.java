package masterwb.design.arkcongress.create_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.adapters.AutoLocationAdapter;
import masterwb.design.arkcongress.db.FirebaseManager;
import masterwb.design.arkcongress.entities.AutoLocation;
import masterwb.design.arkcongress.entities.Event;
import masterwb.design.arkcongress.login.LoginActivity;
import masterwb.design.arkcongress.main.MainActivity;
import masterwb.design.arkcongress.my_events.MyEventsActivity;

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    @BindView(R.id.createFormLayout) LinearLayout formLayout;
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.editEventName) EditText eventName;
    @BindView(R.id.listEventType) Spinner listEventType;
    @BindView(R.id.inputStartDate) TextView startDate;
    @BindView(R.id.inputStartTime) TextView startTime;
    @BindView(R.id.inputEndDate) TextView endDate;
    @BindView(R.id.inputEndTime) TextView endTime;
    @BindView(R.id.autoCompleteLocation) AutoCompleteTextView autoLocation;
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
    // Session and adapters
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private GoogleApiClient googleClient;
    private AutoLocationAdapter adapter;
    // Map objects
    private MapFragment locationMap;
    private CameraPosition cameraMap;
    private MarkerOptions markerLocation;
    private Float mapZoom = 14.0f;

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

        // Set Google Places API
        googleClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).build();

        // Set Location adapter
        adapter = new AutoLocationAdapter(this, googleClient);
        autoLocation.setAdapter(adapter);
        autoLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoLocation location = (AutoLocation) parent.getItemAtPosition(position);
                getLocation(location.getId());
            }
        });

        // Set the map in the layout
        markerLocation = new MarkerOptions();
        LatLng defaultLatLng = new LatLng(22.50,-101.45);
        markerLocation.position(defaultLatLng).title("");
        cameraMap = new CameraPosition.Builder().target(defaultLatLng).zoom(mapZoom).build();
        locationMap = (MapFragment) getFragmentManager().findFragmentById(R.id.locationMap);
        locationMap.getMapAsync(this);

        // Create the event and redirects
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verifyRequiredNotEmpty()) {
                    createNewEvent();
                    goToMyEvents();
                } else {
                    Snackbar.make(formLayout, R.string.create_event_required_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleClient != null && googleClient.isConnected()) {
            googleClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleClient != null) {
            googleClient.connect();
        }
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
        if (startTimeClicked) startTime.setText(time);
        else endTime.setText(time);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(markerLocation);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraMap));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorMessage() != null) {
            Snackbar.make(formLayout, connectionResult.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setDateClickListeners(TextView input, final String tagIdentity) {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (tagIdentity.contains("Start")) {
                    startDateDialog = DatePickerDialog.newInstance(
                            CreateEventActivity.this,
                            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    startDateDialog.vibrate(false);
                    startDateDialog.show(getFragmentManager(), tagIdentity);
                } else {
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
                if (tagIdentity.contains("Start")) {
                    startTimeClicked = true;
                    startTimeDialog = TimePickerDialog.newInstance(
                            CreateEventActivity.this, now.get(Calendar.HOUR_OF_DAY), fixed.get(Calendar.MINUTE), true);
                    startTimeDialog.vibrate(false);
                    startTimeDialog.show(getFragmentManager(), tagIdentity);
                } else {
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
        newEvent.setLocation(autoLocation.getText().toString());
        newEvent.setDescription(description.getText().toString());
        registerEventOnDatabase(newEvent);
    }

    private void getLocation(String id) {
        if (googleClient != null) {
            Places.GeoDataApi.getPlaceById(googleClient, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if (places.getStatus().isSuccess()) {
                        Place location = places.get(0);
                        displayLocation(location);
                        createMarkerInMap(location);
                        adapter.clear();
                        locationMap.getMapAsync(CreateEventActivity.this);
                    }
                    places.release();
                }
            });
        }
    }

    private void displayLocation(Place location) {
        String infoLocation = "";
        if (!TextUtils.isEmpty(location.getAddress()))
            infoLocation += location.getAddress();

        autoLocation.setText(infoLocation);
    }

    private void createMarkerInMap(Place location) {
        LatLng whereIs = location.getLatLng();
        String locationTitle = location.getName().toString();
        markerLocation.position(whereIs).title(locationTitle);
        // Actualizar la cámara del mapa
        cameraMap = new CameraPosition.Builder().target(location.getLatLng()).zoom(mapZoom).build();
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

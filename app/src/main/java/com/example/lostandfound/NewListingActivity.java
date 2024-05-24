package com.example.lostandfound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class NewListingActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextDescription;
    private EditText editTextDate;
    private Button btnGetCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String selectedLocation;

    private ListingDAO listingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        // Initialize views
        editTextName = findViewById(R.id.edit_text_name);
        editTextPhoneNumber = findViewById(R.id.edit_text_phone_number);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDate = findViewById(R.id.edit_text_date);
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location);

        // Initialize Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize ListingDAO
        listingDAO = new ListingDAO(this);

        // Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedLocation = place.getAddress();
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                Toast.makeText(NewListingActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Get Current Location button
        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        // Set click listener for Save button
        findViewById(R.id.btn_save_listing).setOnClickListener(v -> saveListing());
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    selectedLocation = location.getLatitude() + ", " + location.getLongitude();
                    AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                            getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
                    autocompleteFragment.setText(selectedLocation);
                } else {
                    Toast.makeText(NewListingActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveListing() {
        String type = ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.radio_group)).getCheckedRadioButtonId())).getText().toString();
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String location = selectedLocation;

        // Validate input fields
        if (name.isEmpty() || phoneNumber.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save listing to database
        long result = listingDAO.addListing(type, name, phoneNumber, description, date, location);
        if (result != -1) {
            Toast.makeText(this, "Listing saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after saving listing
        } else {
            Toast.makeText(this, "Error saving listing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}
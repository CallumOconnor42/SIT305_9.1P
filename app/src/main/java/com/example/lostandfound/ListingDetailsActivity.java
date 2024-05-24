package com.example.lostandfound;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ListingDetailsActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewPhoneNumber;
    private TextView textViewDescription;
    private TextView textViewDate;
    private TextView textViewLocation;
    private Button btnRemoveListing;

    private ListingDAO listingDAO;
    private Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        // Initialize views
        textViewName = findViewById(R.id.text_view_name);
        textViewPhoneNumber = findViewById(R.id.text_view_phone_number);
        textViewDescription = findViewById(R.id.text_view_description);
        textViewDate = findViewById(R.id.text_view_date);
        textViewLocation = findViewById(R.id.text_view_location);
        btnRemoveListing = findViewById(R.id.btn_remove_listing);

        // Initialize ListingDAO
        listingDAO = new ListingDAO(this);

        // Retrieve listing details from intent
        int listingId = getIntent().getIntExtra("listing_id", -1);
        if (listingId == -1) {
            // If listing ID is not provided, display an error message and finish the activity
            Toast.makeText(this, "Error: Listing ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Retrieve listing from database using its ID
        listing = listingDAO.getListingById(listingId);
        if (listing == null) {
            // If listing is not found, display an error message and finish the activity
            Toast.makeText(this, "Error: Listing not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Display listing details
        textViewName.setText("Name: " + listing.getName());
        textViewPhoneNumber.setText("Phone Number: " + listing.getPhoneNumber());
        textViewDescription.setText("Description: " + listing.getDescription());
        textViewDate.setText("Date: " + listing.getDate());
        textViewLocation.setText("Location: " + listing.getLocation());

        // Set click listener for the "Remove" button
        btnRemoveListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove listing from database
                if (listingDAO.removeListing(listing.getId())) {
                    // If removal is successful, display a success message and finish the activity
                    Toast.makeText(ListingDetailsActivity.this, "Listing removed", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // If removal fails, display an error message
                    Toast.makeText(ListingDetailsActivity.this, "Error: Unable to remove listing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

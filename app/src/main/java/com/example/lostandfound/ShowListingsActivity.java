package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShowListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListingAdapter listingAdapter;
    private ListingDAO listingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_listings);

        recyclerView = findViewById(R.id.recycler_view_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listingAdapter = new ListingAdapter();
        recyclerView.setAdapter(listingAdapter);

        // Initialize ListingDAO
        listingDAO = new ListingDAO(this);

        listingAdapter.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listing listing) {
                // Launch ListingDetailsActivity and pass listing ID
                Intent intent = new Intent(ShowListingsActivity.this, ListingDetailsActivity.class);
                intent.putExtra("listing_id", listing.getId());
                startActivity(intent);
            }
        });

        loadListings();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reload listings from the database
        loadListings();
    }
    private void loadListings() {
        // Retrieve all listings from the database
        List<Listing> listings = listingDAO.getAllListings();

        // Update the RecyclerView with the retrieved listings
        listingAdapter.setListings(listings);
        listingAdapter.notifyDataSetChanged();
    }
}
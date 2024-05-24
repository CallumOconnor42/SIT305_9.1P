package com.example.lostandfound;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.lostandfound.Listing;
import com.example.lostandfound.ListingDAO;
import com.example.lostandfound.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListingDAO listingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        listingDAO = new ListingDAO(this);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers for lost and found items
        List<Listing> listings = listingDAO.getAllListings();
        for (Listing listing : listings) {
            String location = listing.getLocation();
            LatLng latLng = getLatLngFromString(location);
            if (latLng != null) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(listing.getName()));
            }
        }

        // Move the camera to the first item, if there is any
        if (!listings.isEmpty()) {
            String location = listings.get(0).getLocation();
            LatLng latLng = getLatLngFromString(location);
            if (latLng != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
    }

    private LatLng getLatLngFromString(String location) {
        try {
            String[] latLng = location.split(", ");
            double latitude = Double.parseDouble(latLng[0]);
            double longitude = Double.parseDouble(latLng[1]);
            return new LatLng(latitude, longitude);
        } catch (NumberFormatException e) {
            // If location is not in lat,lng format, use Geocoder to get coordinates
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(location, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    return new LatLng(address.getLatitude(), address.getLongitude());
                }
            } catch (IOException ioException) {
                Log.e("MapActivity", "Geocoder exception: " + ioException.getMessage());
            }
        }
        return null;
    }
}
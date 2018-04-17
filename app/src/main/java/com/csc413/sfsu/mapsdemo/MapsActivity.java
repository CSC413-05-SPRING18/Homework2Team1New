package com.csc413.sfsu.mapsdemo;

import android.os.AsyncTask;
import android.os.Debug;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    private class RestaurantFinder extends AsyncTask<URL, Integer, String> {
        OkHttpClient client = new OkHttpClient();

        protected String doInBackground(URL... urls) {
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            if (result != null){


                /*
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson( result, JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");
                JsonObject addressObject = results.get(0).getAsJsonObject();
                String formattedAddress = addressObject.get("formatted_address").getAsString();
                JsonObject geometery = addressObject.getAsJsonObject("geometry");
                JsonObject location = geometery.getAsJsonObject("location");
                LatLng actualCoordinate = new LatLng(location.get("lat").getAsDouble() , location.get("lng").getAsDouble());
                mMap.addMarker(new MarkerOptions().position(actualCoordinate).title(formattedAddress));
                */

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson( result, JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");
                JsonObject addressObject = results.get(0).getAsJsonObject();
                String restaurantName = addressObject.get("name").getAsString();
                System.out.println("Restaurant name:" + restaurantName);
                JsonObject geometry = addressObject.getAsJsonObject("geometry");
                JsonObject location = geometry.getAsJsonObject("location");
                JsonObject opening_hours = addressObject.getAsJsonObject("opening_hours");
                String storeOpen = opening_hours.get("open_now").getAsString();
                System.out.println("Store open? : " + storeOpen);
                String rating = addressObject.get("rating").getAsString();
                System.out.println("This restaurant has " + rating + " stars");
                LatLng actualCoordinate = new LatLng(location.get("lat").getAsDouble() , location.get("lng").getAsDouble());
                mMap.addMarker(new MarkerOptions().position(actualCoordinate).title(restaurantName));


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        LatLng sfsu = new LatLng(37.7219, -122.4782);
        mMap.addMarker(new MarkerOptions().position(sfsu).title("SFSU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sfsu));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
    }

    @Override
    public void onMapLongClick(LatLng coordinate){
        String key = getResources().getString(R.string.google_maps_key);
        try {
            String endpoint = MessageFormat.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng={0},{1}&key={2}",
                    coordinate.latitude,
                    coordinate.longitude,
                    key);

            String request = MessageFormat.format(
                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={0},{1}&key=AIzaSyCBmXBVHv4lLz-8C1aDChmWbrr1z0J5SjQ&rankby=distance&type=restaurant",
                    coordinate.latitude,
                    coordinate.longitude,
                    key
            );

            //URL url = new URL(endpoint);
            URL url = new URL(request);

            System.out.println(endpoint);
            System.out.println(request);

            new RestaurantFinder().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

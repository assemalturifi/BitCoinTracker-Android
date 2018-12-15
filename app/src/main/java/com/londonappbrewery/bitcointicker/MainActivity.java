package com.londonappbrewery.bitcointicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    LocationManager locationManager;
    LocationListener locationListener;
    final long MIN_TIME = 5000;
    // Distance between location updates 1km
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 123;// for the permision code

    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";
    String FORLOG = "Bitcoin";


    private TextView priceTextView;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upViews();




        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d(FORLOG, "" + adapterView.getItemAtPosition(i));

                String finalURL = BASE_URL+adapterView.getItemAtPosition(i);

                Log.d(FORLOG, "Final url is: " + finalURL);

                letsDoSomeNetworking(finalURL);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(FORLOG, "Nothing is selected");

            }
        });

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(FORLOG, "Getting location for current location");

        getLocation();

    }

    private void letsDoSomeNetworking(String url) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(FORLOG, "JSON: " + response.toString());

                try {
                    String price = response.getString("last");


                    priceTextView.setText(price);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.d(FORLOG, "Request fail! Status code: "+statusCode);
                Log.d(FORLOG,  "Fail response "+response);
                Log.d(FORLOG, e.toString());
            }

        });


    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(FORLOG, "onLocationChanged callback is received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d(FORLOG, "longitude is: " + longitude);
                Log.d(FORLOG, "latitude is: " + latitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(FORLOG, "onProviderDisabled( ) callBack received");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //listening if the the permission had been given
            //requesting the location permission from the user
            Log.d(FORLOG, "requesting the permissin");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(FORLOG, "onRequestPermissionsResult(): Permission granted");
                getLocation();
            }
            else{
                Log.d(FORLOG, "Permission denied =(");

            }

        }
    }


    public void upViews(){
        priceTextView = findViewById(R.id.priceLabel);
        spinner = findViewById(R.id.currency_spinner);
        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }


}

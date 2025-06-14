package edu.uiuc.cs427app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Map;
import java.util.Set;

/**
 * MapActivity is responsible for showing an interactive map to the user.
 * It centers the map based on a city name passed via an intent.
 */
public class MapActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String userId = null;
    private MapView mapView;
    private final Handler handler = new Handler();

    /**
     * This callback is invoked when the activity is created.
     * It initializes the user interface, sets up the map view, and centers the map on the city passed in.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get theme preference from saved data
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String userTheme = sharedpreferences.getString("userTheme", "defaultTheme");

        // Change theme according to user choice
        if ("Dark".equals(userTheme)) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Light);
        }
        setContentView(R.layout.activity_map);

        // Set user agent for osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        String selectedCity;
        selectedCity = getIntent().getStringExtra("selectedCity");
        if (selectedCity == null) {
            Log.e("MapActivity", "selectedCity is null");
        } else {
            Log.d("MapActivity", "Received selectedCity: " + selectedCity);
            fetchGeoLocation(selectedCity);
        }
        fetchGeoLocation(selectedCity);

        // Logging and UI Initialization
        userId = sharedpreferences.getString("LOGIN-ID", null);
        Log.d("MainActivity onCreate", "Log In User ID : " + userId);
        Log.d("selectedCity", selectedCity);
        this.setTitle("Weather | User: " + userId + "| City: " + getIntent().getStringExtra("selectedCity"));
    }

    /**
     * Fetches geolocation information for the specified city.
     *
     * @param cityName The name of the city for which to fetch geolocation.
     */
    private void fetchGeoLocation(String cityName) {
        String key = getResources().getString(R.string.api_entry);
        String api_name = getResources().getString(R.string.api_name);
        String url = api_name + "/current?access_key=" + key + "&query=" + cityName;
        Log.d("urlAPI", url);

        // Start HTTP connection in a new Thread
        new Thread(() -> {
            String jsonString = HttpUtils.getJsonContent(url);
            if (jsonString.isEmpty()) {
                Log.e("fetchGeoLocation", "Error fetching geo location: jsonString is null or empty.");
                return;
            }

            Map<String, String> cityInfo = JsonParser.parseLocation(jsonString);

            String lat = cityInfo.get("lat");
            String lon = cityInfo.get("lon");
            if (lat != null && lon != null) {
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                // Post the results back to the main thread to update view
                handler.post(() -> {
                    TextView geoText = findViewById(R.id.geo_info_text);
                    geoText.setText(String.format("Latitude = %s,  Longitude = %s", lat, lon));
                    TextView cityText = findViewById(R.id.city_info_text);
                    cityText.setText(String.format("%s", cityName));

                    centerMapOnCity(cityName, latitude, longitude);
                });
            } else {
                Log.e("fetchGeoLocation", "Error: Latitude and/or longitude values are null.");
            }
        }).start();
    }



    /**
     * This method centers the map based on the provided latitude and longitude.
     *
     * @param cityName The city name to be centered on.
     * @param latitude The latitude of the city to be centered on.
     * @param longitude The longitude of the city to be centered on.
     */
    private void centerMapOnCity(String cityName, double latitude, double longitude) {
        GeoPoint cityGeoPoint = new GeoPoint(latitude, longitude);

        mapView.getController().setZoom(10.0);
        mapView.getController().setCenter(cityGeoPoint);

        Marker cityMarker = new Marker(mapView);
        cityMarker.setPosition(cityGeoPoint);
        cityMarker.setTitle(cityName);
        mapView.getOverlays().add(cityMarker);
    }

}

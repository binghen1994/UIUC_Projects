package edu.uiuc.cs427app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;




/**
 * WeatherActivity - An Android activity for displaying weather information for a specified city.
 *
 * This activity fetches geolocation information for a given city using the WeatherStack API.
 * It then displays relevant weather details such as temperature, weather description, humidity,
 * wind speed, wind degree, wind direction, and pressure in a user interface.
 *
 * The activity also supports theming based on user preferences, allowing the user to choose between
 * a light and dark theme.
 *
 * Usage:
 * 1. Create an instance of WeatherActivity.
 * 2. Call the fetchGeoLocation method with the desired city name to retrieve weather information.
 *
 * Dependencies:
 * - HttpUtils: A utility class for making HTTP requests and retrieving JSON content.
 * - JsonParser: A class for parsing JSON content and extracting relevant weather information.
 *
 * Layout:
 * - Uses a layout defined in the 'activity_weather.xml' file, including TextViews for displaying
 *   city name, date and time, temperature, weather description, humidity, wind details, etc.
 *
 * Theme Preferences:
 * - Retrieves user theme preferences from SharedPreferences and dynamically sets the theme
 *   (light or dark) based on the user's choice.
 *
 * Session Management:
 * - Retrieves the logged-in user ID from session data stored in SharedPreferences.
 *
 * Note: This documentation provides a high-level overview of the class and its functionality.
 * For detailed method descriptions, please refer to the individual method comments.
 *
 * @author Team5
 * @since 2023-11
 */

public class WeatherActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String userId = null;

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
            Log.d("apiOutput", cityInfo.toString());
            String city = cityInfo.get("name");
            String date_time = cityInfo.get("localtime");
            String temperature = cityInfo.get("temperature");
            String weather_description = cityInfo.get("weather_description");
            String humidity = cityInfo.get("humidity");
            String wind_speed = cityInfo.get("wind_speed");
            String wind_degree = cityInfo.get("wind_degree");
            String wind_dir = cityInfo.get("wind_dir");
            String pressure = cityInfo.get("pressure");

            if (temperature != null && weather_description != null && humidity != null && wind_speed != null
                   && wind_degree != null && wind_dir != null && pressure != null) {
                int temp_val = (int) (Integer.parseInt(temperature) * 1.8 + 32.0);
                int humidity_val = Integer.parseInt(humidity);
                int wind_speed_val = (int)(Integer.parseInt(wind_speed)/1.609);
                String windCondition = "Wind Speed : " + String.format("%d", wind_speed_val) + " MPH , \nWind Degree : " + wind_degree + " \nWind Direction : " + wind_dir + "\nWind Pressure : " + pressure ;

                // Post the results back to the main thread to update view
                handler.post(() -> {
                    TextView cityText = findViewById(R.id.cityDescription);
                    cityText.setText(city);
                    TextView date_timeText = findViewById(R.id.date_timeDescription);
                    date_timeText.setText(date_time);
                    TextView tempText = findViewById(R.id.temperatureDescription);
                    tempText.setText(String.format("%d", temp_val) + " Degree F");
                    TextView weatherText = findViewById(R.id.weatherDescription);
                    weatherText.setText(weather_description);
                    TextView humidityText = findViewById(R.id.humidityDescription);
                    humidityText.setText(String.format("%d", humidity_val) + "%");
                    TextView windText = findViewById(R.id.windDescription);
                    windText.setText(windCondition);

                });
            } else {
                Log.e("fetchGeoLocation", "Error: Latitude and/or longitude values are null.");
            }
        }).start();
    }



    /**
     * Overrides the onCreate method to initialize the activity.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
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
        setContentView(R.layout.activity_weather);

        // Get Logged In User ID from session.
        userId = sharedpreferences.getString("LOGIN-ID", null);
        Log.d("MainActivity onCreate", "Log In User ID : " + userId);
        Log.d("selectedCity",getIntent().getStringExtra("selectedCity"));

        fetchGeoLocation(getIntent().getStringExtra("selectedCity"));

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout
        userId = sharedpreferences.getString("LOGIN-ID", null);
        this.setTitle("Weather | User: " + userId + "| City: " + getIntent().getStringExtra("selectedCity"));
    }
}

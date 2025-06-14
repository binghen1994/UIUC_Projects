package edu.uiuc.cs427app;

import android.util.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {
    //take a Json string and parse geolocation,weather information, save them in a map
    /**
     * Parses a JSON string containing geolocation and weather information and stores the
     * parsed information in a Map.
     *
     * @param jsonString The JSON string to be parsed.
     * @return A Map containing geolocation and weather information.
     */
    public static Map<String, String> parseLocation(String jsonString){

        Map<String,String> cityInfo = new HashMap<>();
        if(jsonString == null){
            Log.e("Json Parsing Error","JsonString is Null");
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //parse geolocation information
            JSONObject location = jsonObject.getJSONObject("location");
            cityInfo.put("name", location.getString("name"));
            cityInfo.put("lat",location.getString("lat"));
            cityInfo.put("lon",location.getString("lon"));
            cityInfo.put("localtime",location.getString("localtime"));

            //parse weather related information
            JSONObject currentWeather = jsonObject.getJSONObject("current");
            Log.d("currentWeather", currentWeather.toString());

            cityInfo.put("observation_time",currentWeather.getString("observation_time"));
            cityInfo.put("temperature",currentWeather.getString("temperature"));
            cityInfo.put("weather_description",currentWeather.getJSONArray("weather_descriptions").getString(0));

            cityInfo.put("humidity", currentWeather.getString("humidity"));
            cityInfo.put("wind_speed", currentWeather.getString("wind_speed"));
            cityInfo.put("wind_degree", currentWeather.getString("wind_degree"));
            cityInfo.put("wind_dir", currentWeather.getString("wind_dir"));
            cityInfo.put("pressure", currentWeather.getString("pressure"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cityInfo;
    }
}

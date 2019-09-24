package com.google.firebase.project.blooddonor.Places;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Place} objects.
     */
    public static List<Place> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            Log.i(LOG_TAG, "fetchEarthquakeData.");
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Place> places = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return places;
    }
    /**
     * Query the USGS dataset and return a list of {@link Place} objects.
     */
    public static Distance fetchPlaceDistanceData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            Log.i(LOG_TAG, "fetchEarthquakeData.");
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        Distance distance = extractDistanceFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return distance;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Place} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Place> extractFeatureFromJson(String placeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(placeJSON)) {
            return null;
        }


        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Place> places = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(placeJSON);
            if(baseJsonResponse.getString("status").equals("OK")) {
                JSONArray placesArray = baseJsonResponse.getJSONArray("results");
                for (int i = 0; i < placesArray.length(); i++) {
                    JSONObject currentPlace = placesArray.getJSONObject(i);
                    JSONObject geometry = currentPlace.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double latitude = location.getDouble("lat");
                    double longitude = location.getDouble("lng");
                    String name = currentPlace.getString("name");
                    String vicinity = currentPlace.getString("vicinity");
                    String placeId = currentPlace.getString("place_id");
                    double rating = currentPlace.optDouble("rating");
                    //final String DISTANCE_REQUEST_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=-33.8670522,151.1957362&destinations=place_id:"+placeId+"&key=AIzaSyBwkd-s-vuQiNoPN3dgWNegOrydB-Od54M";
                    //Distance distance = fetchPlaceDistanceData(DISTANCE_REQUEST_URL);
                    //Place place = new Place(latitude, longitude, name, distance, vicinity, rating);
                    Place place = new Place(latitude, longitude, name,placeId, vicinity, rating);
                    places.add(place);

                }
            }else if(baseJsonResponse.getString("status").equals("ZERO_RESULTS")){
                Log.e("QueryUtils", "The search was successful but returned no results.");
            }else if(baseJsonResponse.getString("status").equals("OVER_QUERY_LIMIT")){
                Log.e("QueryUtils", "you are over your quota.");
            }else if(baseJsonResponse.getString("status").equals("REQUEST_DENIED")){
                Log.e("QueryUtils", "Our request was denied, generally because of lack of an invalid key parameter.");
            }else if(baseJsonResponse.getString("status").equals("INVALID_REQUEST")){
                Log.e("QueryUtils", "A required query parameter (location or radius) is missing.");
            }else if(baseJsonResponse.getString("status").equals("UNKNOWN_ERROR")){
                Log.e("QueryUtils", "Server-side error.");
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the place JSON results", e);
        }

        // Return the list of earthquakes
        return places;
    }
    private static Distance extractDistanceFromJson(String placeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(placeJSON)) {
            return null;
        }
        Distance distance= new Distance();

        // Create an empty ArrayList that we can start adding earthquakes to


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(placeJSON);
            if(baseJsonResponse.getString("status").equals("OK")) {
                JSONArray rowsArray = baseJsonResponse.getJSONArray("rows");
                JSONObject currentRow = rowsArray.getJSONObject(0);
                JSONArray elementsArray = currentRow.getJSONArray("elements");
                JSONObject currentElement = elementsArray.getJSONObject(0);
                if(currentElement.getString("status").equals("OK"))
                {
                    JSONObject distanceObject = currentElement.getJSONObject("distance");
                    distance =new Distance(distanceObject.getString("text"),distanceObject.getInt("value"));
                    Log.i("QueryUtils", "The search was successful but returned results."+distance.getmValue());
                }
            }else if(baseJsonResponse.getString("status").equals("ZERO_RESULTS")){
                Log.e("QueryUtils", "The search was successful but returned no results.");
            }else if(baseJsonResponse.getString("status").equals("OVER_QUERY_LIMIT")){
                Log.e("QueryUtils", "you are over your quota.");
            }else if(baseJsonResponse.getString("status").equals("REQUEST_DENIED")){
                Log.e("QueryUtils", "Our request was denied, generally because of lack of an invalid key parameter.");
            }else if(baseJsonResponse.getString("status").equals("INVALID_REQUEST")){
                Log.e("QueryUtils", "A required query parameter (location or radius) is missing.");
            }else if(baseJsonResponse.getString("status").equals("UNKNOWN_ERROR")){
                Log.e("QueryUtils", "Server-side error.");
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the place JSON results", e);
        }

        // Return the list of earthquakes
        return distance;
    }

}
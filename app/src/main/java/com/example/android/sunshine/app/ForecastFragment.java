package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> mForecastAdapter;
    private RetrieveFeedTask retriever;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        String[] forecastArray = {
//                "Today - Sunny 88/63",
//                "5 - Sunny 88/63",
//                "6 - Sunny 88/63",
//                "7 - Sunny 88/63",
//                "8 - Sunny 88/63",
//                "9 - Sunny 88/63",
//                "10 - Sunny 88/63"
//        };
//        //getWeatherJson();
//
//
//        List<String> weekForecast = new ArrayList(Arrays.asList(forecastArray));

        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList()
        );



        ListView myListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        myListView.setAdapter(mForecastAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Context context = getActivity();
                String text = mForecastAdapter.getItem(position);

                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, text);
                context.startActivity(detailIntent);
            }
        }
        );
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshLocation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        refreshLocation();
        super.onStart();
    }

    private void refreshLocation() {
        String location = preferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );
        retriever = new RetrieveFeedTask();
        retriever.execute(location);
    }

    private class RetrieveFeedTask extends AsyncTask<String, String, String[]> {
        @Override
        protected String[] doInBackground(String... params) {

            String format = "json";
            String units = preferences.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_default)
            );

            int numDays = 7;
            try {
                String zipCode = params[0];
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", zipCode)
                        .appendQueryParameter("mode", format)
                        .appendQueryParameter("units", units)
                        .appendQueryParameter("cnt", Integer.toString(numDays));

                URL url = new URL(builder.toString());
                String weatherJson = new WeatherReceiver().getWeatherJson(url);

                WeatherDataParser parser = new WeatherDataParser();
                String[] weatherDataFromJson = parser.getWeatherDataFromJson(weatherJson, numDays);
                return weatherDataFromJson;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mForecastAdapter.clear();
            if (result != null) {
                mForecastAdapter.addAll(result);
            }
            mForecastAdapter.notifyDataSetChanged();
        }
    }
}
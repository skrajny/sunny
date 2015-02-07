package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    protected ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {
                "Today - Sunny 88/63",
                "5 - Sunny 88/63",
                "6 - Sunny 88/63",
                "7 - Sunny 88/63",
                "8 - Sunny 88/63",
                "9 - Sunny 88/63",
                "10 - Sunny 88/63"
        };
        //getWeatherJson();


        List<String> weekForecast = new ArrayList(Arrays.asList(forecastArray));


        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast
        );

        ListView myListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        myListView.setAdapter(mForecastAdapter);
        return rootView;
    }
}
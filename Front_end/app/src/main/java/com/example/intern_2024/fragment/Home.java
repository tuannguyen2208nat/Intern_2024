package com.example.intern_2024.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends Fragment {

    private DatabaseReference myRef;
    private FirebaseUser user;
    private static final String API_KEY = "37294f583d2e566162db243302715283";
    private RecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private SQLiteHelper db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    TextView txtCountry, txtTemp, txtDate, txtHumidity, txtSpeed, txtTDS;
    ImageView imgWeatherIcon;
    Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtTemp = view.findViewById(R.id.txtTemp);
        txtDate = view.findViewById(R.id.txtDate);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtSpeed = view.findViewById(R.id.txtSpeed);
        txtTDS = view.findViewById(R.id.txtTDS);
        imgWeatherIcon = view.findViewById(R.id.imgWeatherIcon);
        handler = new Handler(Looper.getMainLooper());

        start();

        return view;
    }

    private void start(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new RecycleViewAdapter();
        recyclerView.setAdapter(adapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("user_inform").child(uid).child("file");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String databaseName = dataSnapshot.getValue(String.class);
                    if (databaseName != null && !databaseName.isEmpty()) {
                        db = new SQLiteHelper(getContext(), databaseName);
                        loadData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String latitude = String.valueOf(addresses.get(0).getLatitude());
                            String longitude = String.valueOf(addresses.get(0).getLongitude());
                            getJsonWeather(latitude, longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getJsonWeather(String latitude, String longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String icon = weatherObject.getString("icon");
                        if (icon.contains("n")) {
                            icon = icon.replace("n", "d");
                        }
                        String urlIcon = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                        Picasso.get().load(urlIcon).into(imgWeatherIcon);

                        double temp = response.getJSONObject("main").getDouble("temp") - 273.15;
                        String tempTxt = Math.round(temp) + "°C";
                        txtTemp.setText(tempTxt);

                        txtCountry.setText(response.getJSONObject("sys").getString("country"));
                        txtHumidity.setText(response.getJSONObject("main").getString("humidity") + "%");
                        txtSpeed.setText(response.getJSONObject("wind").getString("speed") + "m/s");
                        txtTDS.setText(response.getJSONObject("main").getString("temp_max") + "°C");

                        long timestamp = response.getLong("dt") * 1000;
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(new Date(timestamp));
                        txtDate.setText(formattedDate);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Error fetching weather data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void loadData() {
        if (db != null) {
            List<Item> itemList = db.getAll();
            adapter.setList(itemList);
            adapter.notifyDataSetChanged();
        }
    }
    private void refresh_auto() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResume();
                handler.postDelayed(this, 100);
            }
        }, 0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            start();
        }
    }
}

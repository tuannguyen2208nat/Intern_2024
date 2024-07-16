package com.example.intern_2024.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Home extends Fragment {
    private DatabaseReference myRef;
    private FirebaseUser user;
    private static final String API_KEY = "37294f583d2e566162db243302715283";
    private RecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private SQLiteHelper db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    TextView txtCountry,txtTemp, txtDate,txtHumidity, txtSpeed,txtTDS;
    ImageView imgWeatherIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new RecycleViewAdapter();
        recyclerView.setAdapter(adapter);
        txtCountry=view.findViewById(R.id.txtCountry);
        txtTemp = view.findViewById(R.id.txtTemp);
        txtDate = view.findViewById(R.id.txtDate);
        txtHumidity=view.findViewById(R.id.txtHumidity);
        txtSpeed=view.findViewById(R.id.txtSpeed);
        txtTDS=view.findViewById(R.id.txtTDS);
        imgWeatherIcon=view.findViewById(R.id.imgWeatherIcon);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

                }
            });
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, get the last known location
            getLastLocation();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Use the location data
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Now you can fetch weather data using latitude and longitude
                            getJsonWeather(latitude, longitude);
                        } else {
                            // Handle null location scenario
                            Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void getJsonWeather(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=" + API_KEY;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String icon = weatherObject.getString("icon");
                        if(icon.contains("n"))
                        {
                            icon = icon.replace("n", "d");
                        }
                        String urlIcon = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                        Picasso.get().load(urlIcon).into(imgWeatherIcon);
                        String temp = response.getJSONObject("main").getString("temp") + "°C";
                        txtCountry.setText(response.getJSONObject("sys").getString("country"));
                        txtTemp.setText(temp);
                        txtHumidity.setText(response.getJSONObject("main").getString("humidity")+"%");
                        txtSpeed.setText(response.getJSONObject("wind").getString("speed")+"m/s");
                        txtTDS.setText(response.getJSONObject("main").getString("temp_max")+"°C");

                        String sNgay = response.getString("dt");
                        long lNgay = Long.parseLong(sNgay) * 1000L;
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE , dd/MM/yyyy");
                        Date date = new Date(lNgay);
                        String currentTime = sdf.format(date);
                        txtDate.setText(currentTime);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    private void loadData() {
        if (db != null) {
            List<Item> list = db.getAll();
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

}

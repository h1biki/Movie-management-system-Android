package com.example.mymoviememoir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class Maps extends Fragment implements OnMapReadyCallback {
    private View mapV;
    private GoogleMap googleMap;
    private MapView mapView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mapV = inflater.inflate(R.layout.map, container, false);
        return mapV;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = mapV.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng loc = new LatLng(Double.parseDouble(LoginActivity.lat), Double.parseDouble(LoginActivity.lng));
        googleMap.addMarker(new MarkerOptions().position(loc).title("Home"));
        List<HashMap<String, String >> cinemaData = LoginActivity.cinemaData;
        /*for(int i = 0; i < cinemaData.size(); i++) {
            LatLng cinema = new LatLng(Double.parseDouble(cinemaData.get(i).get("lat")), Double.parseDouble(cinemaData.get(i).get("lng")));
            googleMap.addMarker(new MarkerOptions().position(cinema).title(LoginActivity.cinemaNamesList.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).alpha(0.7f));
        }*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
    }
}
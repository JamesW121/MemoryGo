package CornellTechStudio.cornell.cm.JamesW.memorygo;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Cornell = new LatLng(40.758259, -73.953853);
        mMap.addMarker(new MarkerOptions().position(Cornell).title("Cornell Tech"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(Cornell));

        LatLng a = new LatLng(40.758259, -73.952853);
        mMap.addMarker(new MarkerOptions().position(a).title("The Cafe"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(a));

        LatLng b = new LatLng(40.757259, -73.953853);
        mMap.addMarker(new MarkerOptions().position(b).title("Starbucks"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(b));

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Cornell, zoomLevel));
    }
}
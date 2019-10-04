package waakan.daminitest;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String URL = "http://103.251.184.43/json/generate_file.php?action=lightning";
    RequestQueue rq;
    StringRequest srData;
    double lat, lon;

    JSONArray five_min,ten_min,fifteen_min;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission granted !", Toast.LENGTH_SHORT).show();
            getLocation();
        }
    }

    private void getLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lat=location.getLatitude();
                            lon=location.getLongitude();
                            Log.d("/*signup",location.getLatitude()+"\n"+location.getLongitude()+"\n");
                            loadMap();
                        }
                    }
                });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rq= Volley.newRequestQueue(this);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            getLocation();
        }

        srData=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      //  Log.d("/*maps response",response);
                        try {
                            JSONObject data=new JSONObject(response);
                            JSONObject lighteningData=data.getJSONObject("lightning_data");
//                            Log.d("/*maps response",lighteningData.toString());
                            five_min=lighteningData.getJSONArray("5min_record");
                            ten_min=lighteningData.getJSONArray("10min_record");
                            fifteen_min=lighteningData.getJSONArray("15min_record");

                            Log.d("/*maps response",five_min.toString());

                            loadMap();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("/*maps error",error.toString());
                    }
                });

        srData.setShouldCache(false);
        rq.add(srData);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

//     new Timer().scheduleAtFixedRate(new TimerTask() {
//        @Override
//        public void run() {
//            Log.d("/*mapactivity","being repeated"+i++);
//            Server.pull(MapActivity.this);
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, getFragment()).commit();
//        }
//    },0,60000);


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
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }

    private void loadMap(){
        LatLng ll;
        LatLng userLatLng=new LatLng(lat,lon);

        if(five_min!=null)
        for(int i=0;i<five_min.length();i++){
            Log.d("/*loadmap",i+"");
            try {
                JSONObject object=five_min.getJSONObject(i);
                ll=new LatLng(object.getDouble("latitude"),object.getDouble("longitude"));
                mMap.addMarker(new MarkerOptions().position(ll)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.five_mins));

//                if(i==(five_min.length()-1))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(ten_min!=null)
        for(int i=0;i<ten_min.length();i++){
            Log.d("/*loadmap",i+"");
            try {
                JSONObject object=ten_min.getJSONObject(i);
                ll=new LatLng(object.getDouble("latitude"),object.getDouble("longitude"));
                mMap.addMarker(new MarkerOptions().position(ll)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ten_min));

//                if(i==(ten_min.length()-1))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(fifteen_min!=null)
        for(int i=0;i<fifteen_min.length();i++){
            Log.d("/*loadmap",i+"");
            try {
                JSONObject object=fifteen_min.getJSONObject(i);
                ll=new LatLng(object.getDouble("latitude"),object.getDouble("longitude"));
                mMap.addMarker(new MarkerOptions().position(ll)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.fifteen_min));

//                if(i==(fifteen_min.length()-1))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        mMap.addMarker(new MarkerOptions().position(userLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),8));
        mMap.addCircle(new CircleOptions().center(userLatLng).strokeColor(Color.RED).fillColor(Color.argb(50,200,0,0)).radius(20000).strokeWidth(1));
        mMap.addCircle(new CircleOptions().center(userLatLng).strokeColor(Color.RED).fillColor(Color.argb(50,200,200,0)).radius(40000).strokeWidth(1));

        LatLng concurrent20 = calculateParallelPoints(userLatLng, 20000);
        mMap.addMarker(new MarkerOptions().position(concurrent20).icon(BitmapDescriptorFactory.fromBitmap(getBubbleIcon("20\nKM"))));

        LatLng concurrent40 = calculateParallelPoints(userLatLng, 40000);
        mMap.addMarker(new MarkerOptions().position(concurrent40).icon(BitmapDescriptorFactory.fromBitmap(getBubbleIcon("40\nKM"))));
    }

    private Bitmap getBubbleIcon(String message) {
        TextView text = new TextView(this);
        text.setGravity(Gravity.CENTER);
        text.setText(message);
        IconGenerator generator = new IconGenerator(this);
        generator.setContentView(text);
        Bitmap icon = generator.makeIcon();
        return icon;
    }

    private LatLng calculateParallelPoints(LatLng point, int distance) {
        int R = 6378137;

        double dLat = distance / R;
        double dLon = distance / (R * Math.cos(Math.PI * point.latitude / 180));

        double lat1 = point.latitude + dLat * 180 / Math.PI;
        double lon1 = point.longitude + dLon * 180 / Math.PI;

        return new LatLng(lat1, lon1);
    }
}

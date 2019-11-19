package fit5120.lookout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonMultiLineString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Common.Common;
import Remote.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapMain extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float defaultZoom = 15;
    private boolean permissionGranted = false;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private Typeface typeface;
    private List<LatLng> polyLineList;
    private ArrayList<LatLng> points;
    private LatLng currentPosition;
    private Location currentLocation;
    private PolylineOptions polylineOptions, blackpolylineOptions, greenpolylineOptions;
    private Polyline blackPolyline, greyPolyline, greenPolyline;
    private IGoogleAPI mService;
    private GeoJsonLayer layer = null;
    String dest;
    private LocationManager locationManager;
    private double posLat;
    private double posLng;
    private LatLng position;
    private Marker mPosition;
    private List<LatLng> accspotonpath;
    private List<LatLng> processedPath;
    private List<List<LatLng>> processedlst;
    List<List<LatLng>> allPoints;


    double[] latArrayLD = {-37.80666, -37.79226, -37.80729, -37.81032, -37.80379, -37.8003, -37.80414, -37.81228, -37.80816, -37.81218, -37.81247, -37.79748, -37.80914,
            -37.79522, -37.79795, -37.81622, -37.81156, -37.82101, -37.81179, -37.80423, -37.81549, -37.81317, -37.80493, -37.81649, -37.80322, -37.79463, -37.7894, -37.794, -37.78857, -37.80818, -37.81961, -37.80314, -37.80847, -37.78453, -37.77804, -37.81425, -37.82933, -37.81442, -37.77995};
    double[] lonArrayLD = {144.9629, 144.96839, 144.96885, 144.96136, 144.95625, 144.94398, 144.95949, 144.96226, 144.96874, 144.95495, 144.95392, 144.97049, 144.9692,
            144.97332, 144.97488, 144.96407, 144.96473, 144.94744, 144.97483, 144.9603, 144.96654, 144.95144, 144.95607, 144.96699, 144.9511, 144.93184, 144.94217, 144.97111, 144.93436, 144.96038, 144.96002, 144.96955, 144.95532, 144.95696, 144.96023, 144.96316, 144.9689, 144.98818, 144.96284};
    ArrayList<Circle> accSpots;
    TextView getdirtext;
    private MyClusterManager myClusterManager;
    private ClusterManager clusterManager;
    LinearLayout notLayout;
    ImageView volButn;
    boolean btnsts;
    private TextView notText;
    private int distTrav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        try {
            getLocationPermission();
        } catch(Exception e){
            noLocation();
        }
        polyLineList = new ArrayList<>();
        points = new ArrayList<LatLng>();
        accSpots = new ArrayList<Circle>();
        getdirtext = (TextView) findViewById(R.id.gdirtext);
        accspotonpath = new ArrayList<>();
        processedPath = new ArrayList<>();
        processedlst = new ArrayList<>();
        notLayout= (LinearLayout) findViewById(R.id.not_layout);
        notLayout.setVisibility(View.INVISIBLE);
        volButn = (ImageView)findViewById(R.id.volumebutton);
        btnsts = true;
        notText = (TextView)findViewById(R.id.not_text);
        allPoints = new ArrayList<>();
        distTrav = 0;
    }
    public void toggleVol(View view){
        if (btnsts) {
            volButn.setImageResource(R.drawable.ic_volume_off);
            btnsts = false;
        }
        else{
            volButn.setImageResource(R.drawable.ic_volume_on);
            btnsts = true;
        }
    }

    public void trackChange() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                posLat = location.getLatitude();
                posLng = location.getLongitude();
                position = new LatLng(posLat, posLng);

                if (mPosition != null) {
                    mPosition.remove();
                }
                updateCameraBearing(gMap, location.getBearing(),position);
                checkifDangerClose(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }


    private void checkifDangerClose(Location location) {
        MediaPlayer mp;
        final LinearLayout notLayout = (LinearLayout) findViewById(R.id.not_layout);

        if (accspotonpath.size() != 0) {
            for (LatLng eachspot : accspotonpath) {
                float[] results = new float[1];
                if (!processedPath.contains(eachspot)) {
                    Location.distanceBetween(eachspot.latitude,
                            eachspot.longitude,
                            location.getLatitude(),
                            location.getLongitude(), results);

                    if (results[0] < 150 && results[0] > 100) {

                        notLayout.animate().translationY(120);
                        notLayout.setVisibility(View.VISIBLE);
                        processedPath.add(eachspot);
                        notText.setText("Acciden prone Area in 100 Meters. Slow Down!");
                        if (btnsts == true) {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.accident_area_warning);
                            mp.start();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notLayout.animate().translationY(-120);
                                notLayout.setVisibility(View.INVISIBLE);
                            }
                        }, 5000);
                    }
                }
            }
        }

        for (List<LatLng> lst : allPoints) {
            if (!processedlst.contains(lst)) {
                float[] results = new float[1];
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(),
                        lst.get(0).latitude,
                        lst.get(0).longitude, results);
                Log.d("locall", results[0] + " " + lst.get(0));
                if (results[0] > 130)
                    return;

                if (results[0] > 100) {
                    if (lst.size() < 3) {
                        notLayout.animate().translationY(120);
                        notLayout.setVisibility(View.VISIBLE);
                        notText.setText("Intersection Ahead. Look for Turning Vehicles !");
                        if (btnsts == true) {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.tc_aud);
                            mp.start();
                        }
                    } else {
                        notLayout.animate().translationY(120);
                        notLayout.setVisibility(View.VISIBLE);
                        notText.setText("Intersection Ahead. Look for Turning Vehicles !");
                        if (btnsts == true) {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.tc_2_aud);
                            mp.start();
                        }
                    }
                    processedlst.add(lst);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notLayout.animate().translationY(-120);
                            notLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
                }

                if (results[0] < 100) {
                    if (lst.size() < 3) {
                        notLayout.animate().translationY(120);
                        notLayout.setVisibility(View.VISIBLE);
                        notText.setText("Intersection Ahead. Look for Turning Vehicles !");
                        if (btnsts == true) {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.tc_aud);
                            mp.start();
                        }
                    } else {
                        notLayout.animate().translationY(120);
                        notLayout.setVisibility(View.VISIBLE);
                        notText.setText("Intersection Ahead. Look for Turning Vehicles !");
                        if (btnsts == true) {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.tc_2_aud);
                            mp.start();
                        }
                    }
                    processedlst.add(lst);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notLayout.animate().translationY(-120);
                            notLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
                }
            }
        }
    }
    private void updateCameraBearing(GoogleMap googleMap, float bearing,LatLng pos) {
        if ( googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .target(pos)
                .zoom(18)
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        gMap = googleMap;
        if (permissionGranted) {
            getDeviceLocation();
            gMap.setMyLocationEnabled(true);
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            gMap.setIndoorEnabled(true);
            gMap.setBuildingsEnabled(false);
            gMap.getUiSettings().setCompassEnabled(true);

            try {
                GeoJsonLayer layer = new GeoJsonLayer(gMap, R.raw.bicycle_ntwrk,
                        getApplicationContext());
                //layer.addLayerToMap();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
            rlp.setMargins(0, 300, 0, 0);
            rlp.setMarginEnd(80);
            init();
        }
    }

    public void init() {
        typeface = ResourcesCompat.getFont(this, R.font.montserrat);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyC2g6WItBVcXjjnt4ggz8eJ8RgJd9Zo2lg");
        }
        placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        EditText searchBox = (EditText) ((LinearLayout) autocompleteSupportFragment.getView()).getChildAt(1);
        searchBox.setTypeface(typeface);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setCountry("AU");
        autocompleteSupportFragment.getView().animate();
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                processedPath.clear();
                processedlst.clear();
                notLayout.setVisibility(View.INVISIBLE);
                getdirtext.setText(" Get Directions ");
                final LatLng latLng = place.getLatLng();
                moveCamera(new LatLng(latLng.latitude, latLng.longitude), 15, place.getAddress());

                dest = latLng.latitude + "," + latLng.longitude;
                displayInformation(dest, place.getAddress());
                try {
                    layer = new GeoJsonLayer(gMap, R.raw.bicycle_ntwrk,
                            getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        mService = Common.getGoogleAPI();
        hideSoftKeyboard();
    }

    public void getDir(View view) {

        if (getdirtext.getText().equals(" Get Directions ")) {
            getdirtext.setText(" Navigate ");
            getDirection(dest, 1);
        }
        else {
            updateCameraBearing(gMap, currentLocation.getBearing(),currentPosition);
            LinearLayout placeInfo = (LinearLayout) findViewById(R.id.place_info);
            placeInfo.setVisibility(View.INVISIBLE);
            getdirtext.setText(" Get Directions ");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            trackChange();
        }

    }
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkifDanger(List<LatLng> polyLineList){
        accspotonpath.clear();
        processedPath.clear();
        processedlst.clear();
        float[] results = new float[1];
        int dscount = 0;
        int prevj = -1;
        for (int i = 0; i < polyLineList.size(); i++){
            for (int j = 0; j < latArrayLD.length; j++){
                Location.distanceBetween(latArrayLD[j],
                        lonArrayLD[j],
                        polyLineList.get(i).latitude,
                        polyLineList.get(i).longitude,
                        results);

                if (results[0] <= 100) {
                    if (prevj != j){
                        accspotonpath.add(new LatLng(latArrayLD[j], lonArrayLD[j]));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(latArrayLD[j], lonArrayLD[j]))
                                .radius(100)
                                .fillColor(Color.argb(70, 250, 50, 30))
                                .strokeColor(Color.RED)
                                .strokeWidth(3f);
                        gMap.addCircle(circleOptions);

                        if (clusterManager == null)
                            clusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(),gMap);

                        if (myClusterManager == null)
                            myClusterManager = new MyClusterManager(getApplicationContext(),gMap,clusterManager);

                        clusterManager.setRenderer(myClusterManager);
                        int ic = R.mipmap.caution;
                        ClusterMarker cm = new ClusterMarker(
                                new LatLng(latArrayLD[j], lonArrayLD[j]),
                                "","",ic);
                        clusterManager.addItem(cm);
                    }
                    prevj = j;
                    break;
                }
            }
        }
    }

    private void displayInformation(String dest, String address) {
        LinearLayout placeInfo = (LinearLayout) findViewById(R.id.place_info);
        placeInfo.setVisibility(View.VISIBLE);
        TextView addLineOne = (TextView) findViewById(R.id.address_line_1);
        TextView addLineTwo = (TextView) findViewById(R.id.address_line_2);

        addLineOne.setText(address.substring(0, address.indexOf(",")));
        addLineTwo.setText(address.substring(address.indexOf(",") + 1).trim());
        getDirection(dest, 0);
    }

    private void getDirection(String destination, final int dcode) {
        currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        String requestAPI = null;
        try {
            requestAPI = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=bicycling&" +
                    "transit_routing_preference=less_driving&" +
                    "avoid=highways&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.Map_key);
            Log.d("reeq",requestAPI);
            mService.getPath(requestAPI)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    if (dcode == 0) {
                                        TextView duration = (TextView) findViewById(R.id.duration);
                                        TextView distance = (TextView) findViewById(R.id.distance);
                                        JSONArray leg = route.getJSONArray("legs");
                                        String jtime = leg.getJSONObject(0).getJSONObject("duration").getString("text");
                                        String jdist = leg.getJSONObject(0).getJSONObject("distance").getString("text");
                                        duration.setText(jtime);
                                        distance.setText(jdist);
                                        return;
                                    } else {
                                        JSONObject poly = route.getJSONObject("overview_polyline");
                                        String polyline = poly.getString("points");
                                        polyLineList = decodePoly(polyline);
                                        float td = 0;
                                        List<LatLng> temp = new ArrayList<>();
                                        for (int j = 0;j < polyLineList.size() - 1;j++){
                                            if (j == 0)
                                                temp.add(polyLineList.get(0));
                                            if (j == polyLineList.size() - 2) {
                                                temp.add(polyLineList.get(polyLineList.size() - 1));
                                            }
                                            float[] result = new float[1];
                                            Location.distanceBetween(polyLineList.get(j).latitude,
                                                    polyLineList.get(j).longitude,
                                                    polyLineList.get(j + 1).latitude,
                                                    polyLineList.get(j + 1).longitude,result);
                                            td = td + result[0];
                                            if (td < 150){
                                                temp.add(polyLineList.get(j));
                                            }
                                            else{
                                                td = 0;
                                                allPoints.add(new ArrayList<LatLng>(temp));
                                                temp.clear();
                                            }
                                        }
                                        if(temp != null)
                                            allPoints.add(new ArrayList<LatLng>(temp));
                                        Log.d("100ml",allPoints.toString());
                                        Log.d("100ml2",polyLineList.toString());
                                    }
                                }
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                boolean reds = false;
                                boolean isCyclingPoint;
                                List<LatLng> temp = new ArrayList<>();
                                List<List<LatLng>> lol = new ArrayList<>();

                                for (int i = 0; i < polyLineList.size(); i++) {
                                    builder.include(polyLineList.get(i));
                                    Log.d("polyline",polyLineList.get(i)+"");
                                    isCyclingPoint = checkifOnTrack(polyLineList.get(i), layer);
                                    if (!isCyclingPoint) {
                                        reds = true;
                                        if(i != 0 )
                                            temp.add(polyLineList.get(i - 1));
                                        if (i == polyLineList.size() - 1) {
                                            temp.add(polyLineList.get(i));
                                            lol.add(new ArrayList<LatLng>(temp));
                                            temp.clear();
                                        }
                                    }

                                    if ((reds == true) && (isCyclingPoint)) {
                                        reds = false;
                                        temp.add(polyLineList.get(i - 1));
                                        temp.add(polyLineList.get(i));
                                        lol.add(new ArrayList<LatLng>(temp));
                                        temp.clear();
                                    }
                                }

                                LatLngBounds bounds = builder.build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 1);
                                final Location loc = new Location("");
                                LatLng start = polyLineList.get(0);
                                loc.setLatitude(start.latitude);
                                loc.setLongitude(start.longitude);
                                gMap.animateCamera(cameraUpdate);
                                if (dcode != 0) {
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50),
                                            new GoogleMap.CancelableCallback() {
                                                @Override
                                                public void onFinish() {
                                                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(gMap.getCameraPosition())
                                                            .bearing(loc.getBearing())
                                                            .target(polyLineList.get(0))
                                                            .zoom(15)
                                                            .tilt(30)
                                                            .build()));
                                                }

                                                @Override
                                                public void onCancel() {
                                                }
                                            });
                                }
                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline = gMap.addPolyline(polylineOptions);

                                blackpolylineOptions = new PolylineOptions();
                                blackpolylineOptions.color(Color.GREEN);
                                blackpolylineOptions.width(15);
                                blackpolylineOptions.startCap(new SquareCap());
                                blackpolylineOptions.endCap(new SquareCap());
                                blackpolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = gMap.addPolyline(blackpolylineOptions);
                                blackPolyline.setPoints(greyPolyline.getPoints());

                                for (List<LatLng> each : lol) {
                                    greenpolylineOptions = new PolylineOptions();
                                    greenpolylineOptions.color(Color.RED);
                                    greenpolylineOptions.width(15);
                                    greenpolylineOptions.startCap(new RoundCap());
                                    greenpolylineOptions.endCap(new RoundCap());
                                    greenpolylineOptions.jointType(JointType.ROUND);
                                    greenpolylineOptions.addAll(each);
                                    greenPolyline = gMap.addPolyline(greenpolylineOptions);
                                    greenPolyline.setPoints(each);
                                }

                                checkifDanger(polyLineList);
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MapMain.this,"Unable to Load Maps",Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapMain.this);
    }

    public boolean checkifOnTrack(LatLng latLng, GeoJsonLayer layer) {
        List<LatLng> temp = new ArrayList<>();
        GeoJsonMultiLineString ord;
        GeoJsonLineString coord;
        for (GeoJsonFeature feature : layer.getFeatures()) {
            ord = (GeoJsonMultiLineString) feature.getGeometry();
            coord = ord.getLineStrings().get(0);
            if (PolyUtil.isLocationOnPath(latLng, coord.getCoordinates(), true, 10)) {
                return true;
            }

            if (PolyUtil.isLocationOnEdge(latLng, coord.getCoordinates(), true, 10)) {
                return true;
            }
        }
        return false;
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            try {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), defaultZoom, "M");
                            } catch(Exception e){
                                noLocation();
                            }
                        } else
                            Toast.makeText(MapMain.this, "Cannot Access Location", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(latLng).
                tilt(0).
                zoom(zoom).
                bearing(0).
                build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (title != "M") {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            gMap.clear();
            gMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
                && (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)) {
            permissionGranted = true;
            initMap();
        } else
            ActivityCompat.requestPermissions(this, permissions, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = false;

        switch (requestCode) {
            case 0:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();
                }
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void noLocation(){
        typeface = ResourcesCompat.getFont(this, R.font.fredoka_one);

        SweetAlertDialog sDialog = new SweetAlertDialog(MapMain.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Please find a open space, and try again.Can not locate you now.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                });
        sDialog.show();
    }
}

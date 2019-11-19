package fit5120.lookout;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static int[] recSources = new int[]{
            R.drawable.guide_icon_main2,
            R.drawable.video_icon_main,
            R.drawable.rule_icon_main,
            R.drawable.quiz_main,
            R.drawable.navigation_main,
            R.drawable.keepsafe
    };

    private static String[] recTextSources = new String[]{
            "Cyclist Safety Guide",
            "Watch Safety Videos",
            "Bicycle Road Rules",
            "Test Your Knowledge",
            "Navigation\nAlerts",
            "Keep Me\nSafe"
    };

    private int[] textResources = new int[]{
            R.string.slider1,
            R.string.slider2,
            R.string.slider3,
            R.string.slider4
    };

    ViewPager viewPager;
    sliderAdapter adapter;
    private RecyclerView recyclerView;
    private Timer timer;
    private int currentPosition = 0;
    private LinearLayout dotsLayout;
    private LinearLayout sliderText;
    private int dotPos = 0;
    protected DrawerLayout myDrawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle myToggle;
    private RecyclerViewAdapter recyclerViewAdapter;
    Typeface typeface;
    private boolean isCanScroll = false;
    private float beforeX;
    Intent intent;
    static final int request_permission = 1;
    LocationManager locationManager;
    RequestQueue requestQueue;
    ProgressDialog progress;
    protected double latitude,longitude;
    JSONObject data;
    TextView weatherTile,wDesc,wLoc,curr_temp_main,rain_main;
    ImageView wIcon;
    private static final int ERROR_DIALOG_REQ = 9001;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("brcv","here");
        setContentView(R.layout.activity_main);
        setNavigationViewListener();
        dotsLayout = (LinearLayout) findViewById(R.id.dots);
        sliderText = (LinearLayout) findViewById(R.id.slider_text);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new sliderAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        typeface = ResourcesCompat.getFont(this, R.font.montserrat);
        viewPager.setOnTouchListener(new View.OnTouchListener() {//can not slide by left slide
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if(isCanScroll){
                    return MainActivity.this.onTouchEvent(ev);
                }else  {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            beforeX = ev.getX();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float motionValue = ev.getX() - beforeX;
                            if (motionValue > 0) {
                                return true;
                            }
                            return true;

                        default:
                            break;
                    }
                    return MainActivity.this.onTouchEvent(ev);
                }
            }
        });
        createDots(dotPos++);
        createSlideShow();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (dotPos > (adapter.actCount() - 1))
                    dotPos = 0;
                createDots(dotPos++);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rec_main);
        //LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this,R.anim.fade_animation);
        //recyclerView.setLayoutAnimation(animationController);
        recyclerViewAdapter = new RecyclerViewAdapter(recSources, recTextSources,'H',null,null,null,null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Toast.makeText(MainActivity.this,position + "Clicked",Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        Intent intent_0 = new Intent(MainActivity.this, GuideMain.class);
                        startActivity(intent_0);
                        break;
                    case 1:
                        Intent intent_1 = new Intent(MainActivity.this, VideoPage.class);
                        startActivity(intent_1);
                        break;
                    case 2:
                        Intent intent_2 = new Intent(MainActivity.this, RuleActivity.class);
                        startActivity(intent_2);
                        break;
                    case 3:
                        Intent intent_3 = new Intent(MainActivity.this, RuleQuiz.class);
                        startActivity(intent_3);
                        break;

                    case 4:
                        if (isServiceOk()) {
                            Intent intent_4 = new Intent(MainActivity.this, MapMain.class);
                            startActivity(intent_4);
                        }
                        break;

                    case 5:
                        Intent intent_5 = new Intent(MainActivity.this,EmergencyContactPage.class);
                        startActivity(intent_5);
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        //CardView weather = (CardView) MainActivity.this.findViewById(R.id.weather_btn);
        LinearLayout weather = (LinearLayout) MainActivity.this.findViewById(R.id.weather_btn);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeatherMain.class);
                startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
    }

    public boolean isServiceOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS)
            return true;
        else
        if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,available,ERROR_DIALOG_REQ);
            dialog.show();
        }
        else
            Toast.makeText(this,"Some Error Occurred", Toast.LENGTH_SHORT).show();

        return false;
    }
    public void toggleNavBar(View view) {
        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        if (!myDrawer.isDrawerOpen(GravityCompat.START))
            myDrawer.openDrawer(GravityCompat.START);
        else
            myDrawer.closeDrawer(GravityCompat.START);
    }

    public void onBackPressed() {
        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        if (myDrawer.isDrawerOpen(GravityCompat.START)) {
            myDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (myToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void reRunAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void createSlideShow() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition == Integer.MAX_VALUE)
                    currentPosition = 0;

                viewPager.setCurrentItem(currentPosition++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);

            }
        }, 350, 3000);
    }

    private void createDots(int currentPosition) {
        if (dotsLayout.getChildCount() > 0)
            dotsLayout.removeAllViews();

        if (sliderText.getChildCount() > 0)
            sliderText.removeAllViews();

        curr_temp_main = (TextView) findViewById(R.id.curr_temp_main);
        wDesc = (TextView) findViewById(R.id.wdesc);
        wIcon = (ImageView) findViewById(R.id.w_icon);
        wLoc = (TextView) findViewById(R.id.loc_id);
        rain_main = (TextView) findViewById(R.id.rain_main);
        TextView textView = new TextView(this);
        textView.setText(textResources[currentPosition]);
        ImageView dotArr[] = new ImageView[adapter.actCount()];

        for (int i = 0; i < adapter.actCount(); i++) {
            dotArr[i] = new ImageView(this);
            if (i == currentPosition) {
                dotArr[i].setPadding(10, 0, 10, 0);
                dotArr[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.slide_dot_active));
            } else {
                dotArr[i].setPadding(10, 10, 10, 0);
                dotArr[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.slide_dot_inactive));
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dotArr[i], layoutParams);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(getResources().getColor(R.color.fortext));
        textView.setTextSize(30);
        textView.setTypeface(typeface, Typeface.BOLD);
        sliderText.addView(textView, layoutParams);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        if (myDrawer.isDrawerOpen(GravityCompat.START))

            myDrawer.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {

            case R.id.home: {
                if (this.getClass() == MainActivity.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.guide:{
                if (this.getClass() == GuideMain.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, GuideMain.class);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.vid:{
                if (this.getClass() == VideoPage.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, VideoPage.class);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.rules:{
                if (this.getClass() == RuleActivity.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, RuleActivity.class);
                    startActivity(intent);
                }
                return true;
            }

            case R.id.nav:{
                if (this.getClass() == RuleActivity.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, MapMain.class);
                    startActivity(intent);
                }
                return true;
            }

            case R.id.kms:{
                if (this.getClass() == RuleActivity.class) {
                    myDrawer.closeDrawer(GravityCompat.START);
                }
                else{
                    intent = new Intent(MainActivity.this, EmergencyContactPage.class);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.versionno:{
                //do nothing
            }

            default:
                Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    protected void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void getLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},request_permission);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        else{
            latitude = -37.876366;
            longitude = 145.044267;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case (request_permission):{
                getLocation();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeather();
    }

    protected void getWeather(){

        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = "https://api.weatherbit.io/v2.0/current?&lat="+ latitude + "&lon=" + longitude + "&key=fe233f6bd49e46929dbfbad5ddf3f27b";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    data = (JSONObject) response.getJSONArray("data").get(0);
                    wIcon.setImageResource(getResources().getIdentifier(data.optJSONObject("weather").optString("icon"), "mipmap", getPackageName()));
                    wDesc.setText(data.optJSONObject("weather").optString("description"));
                    wLoc.setText(data.optString("city_name").toUpperCase());
                    curr_temp_main.setText(data.optString("temp") + "°C");
                    rain_main.setText(data.optString("precip") + "%");

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}

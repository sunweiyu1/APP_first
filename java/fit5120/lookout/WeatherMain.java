package fit5120.lookout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loc.am;
import com.tooltip.Tooltip;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class WeatherMain extends MainActivity {

    static final int request_permission = 1;
    LocationManager locationManager;

    RequestQueue requestQueue;
    TextView currTemp,currLocation,wdesc,windSpeed,winddirtext,aqi,humidity,visib,uvIndex,min_temp,max_temp,sunrise,sunset;
    ImageView windDir;
    ProgressDialog progress;
    Typeface typeface;
    double latitude,longitude;
    SweetAlertDialog pDialog;
    private RecyclerView recyclerViewInw;
    private RecyclerViewAdapter recyclerViewAdapterInw;
    private Context context;
    private ConstraintLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        setNavigationViewListener();

        typeface = ResourcesCompat.getFont(this, R.font.montserrat);
        currTemp = (TextView)findViewById(R.id.curr_temp);
        currLocation = (TextView)findViewById(R.id.location);
        wdesc = (TextView) findViewById(R.id.winfo);
        windSpeed = (TextView) findViewById(R.id.wind_speed);
        windDir = (ImageView) findViewById(R.id.windr);
        winddirtext = (TextView)findViewById(R.id.windrtext);
        aqi = (TextView) findViewById(R.id.aqi);
        humidity = (TextView) findViewById(R.id.humidity_text);
        visib = (TextView) findViewById(R.id.vis_text);
        uvIndex = (TextView) findViewById(R.id.uv_text);
        mainLayout = (ConstraintLayout) findViewById(R.id.main_back);
        /*swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(120);*/
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        min_temp = (TextView) findViewById(R.id.min_temp);
        max_temp = (TextView) findViewById(R.id.max_temp);
        sunrise = (TextView) findViewById(R.id.sr_text);
        sunset = (TextView) findViewById(R.id.ss_text);

        ImageView ws = (ImageView)findViewById(R.id.ws_image);

        getLocation();
        /*
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(getIntent());
                        swipeRefreshLayout.setRefreshing(false);
                        pDialog.dismiss();
                    }
                },1500);
            }
        });*/
    }
    public void refreshLayout(View view) {
        onRestart();
    }

    public void displayTT(View view){
        Tooltip tooltip;
        switch (view.getId()){

            case (R.id.ws_image):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Wind Speed")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.windr):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Wind Direction")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.aqi_image):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Air Quality Index")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.hum_image):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Humidity")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.vis_image):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Visibility")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.uv_image):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("UV Index")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.sunrise):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Sunrise Time")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;

            case (R.id.sunset):
                tooltip = new Tooltip.Builder(view)
                        .setTypeface(typeface)
                        .setText("Sunset Time")
                        .setBackgroundColor(this.getColor(R.color.rec))
                        .setTextSize(15.0f)
                        .setCancelable(true)
                        .setPadding(30)
                        .setDrawablePadding(40)
                        .setCornerRadius(getResources().getDimension(R.dimen.tooltip_size))
                        .show();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
                pDialog.dismiss();
            }
        },1500);

    }

    public void getLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},request_permission);
            return;
        }
        locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        String np = LocationManager.NETWORK_PROVIDER;
        final Location myLocation = locationManager.getLastKnownLocation(np);
        if (myLocation != null){
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
        }
        else{
            locationManager.requestLocationUpdates(np, 300, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = myLocation.getLatitude();
                    longitude = myLocation.getLongitude();
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
        getFullDayWeather();
    }

    public void getWeather(){
        //progress = ProgressDialog.show(this, "Loading..", "Please wait", true);
        final String url = "https://api.weatherbit.io/v2.0/current?&lat="+ latitude + "&lon=" + longitude + "&key=fe233f6bd49e46929dbfbad5ddf3f27b";

        final DateFormat utcFormat = new SimpleDateFormat("HH:MM");
        final DateFormat actFormat = new SimpleDateFormat("HH:MM");
        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");

        actFormat.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                JSONObject data;
                try {
                    data = (JSONObject) response.getJSONArray("data").get(0);
                    String city_name = data.optString("city_name");
                    String desc = data.optJSONObject("weather").optString("description");
                    String backCode = data.optJSONObject("weather").optString("icon");

                    double wind_speed = Float.parseFloat(data.optString("wind_spd")) * 3.6;
                    float wind_dir = Float.parseFloat(data.optString("wind_dir"));
                    String pressure = data.optString("pres");
                    String tempnow = data.optString("temp");
                    Date sunr = utcFormat.parse(data.optString("sunrise"));
                    Date suns = utcFormat.parse(data.optString("sunset"));
                    Date dateObj = sdf.parse(actFormat.format(suns));

                    currLocation.setText(city_name.toUpperCase());
                    currTemp.setText(tempnow+"°");
                    wdesc.setText(desc);
                    windSpeed.setText(Math.round(wind_speed) + " Km/h");
                    windDir.setRotation((float)wind_dir + 180);
                    winddirtext.setText(data.optString("wind_cdir_full").substring(0,1).toUpperCase()
                            + data.optString("wind_cdir_full").substring(1).toLowerCase());
                    aqi.setText(data.optString("aqi") + " AQI");
                    humidity.setText(data.optString("rh")+" %");
                    double vis = Math.round(Float.parseFloat(data.optString("vis")) * 1.61);
                    visib.setText(vis + " Km");
                    uvIndex.setText(""+Math.round(Float.parseFloat(data.optString("uv"))));
                    sunrise.setText((actFormat.format(sunr)) + " AM");
                    sunset.setText(new SimpleDateFormat("K:mm").format(dateObj) + " PM");
                } catch(JSONException e){
                    e.printStackTrace();
                } catch (ParseException pe){
                    pe.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        //progress.dismiss();
        requestQueue.add(jsonObjectRequest);
    }


    public void getFullDayWeather(){
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = "https://api.weatherbit.io/v2.0/forecast/hourly?&lat="+ latitude + "&lon=" + longitude + "&key=fe233f6bd49e46929dbfbad5ddf3f27b";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject data;
                String[] hrArray = new String[24];
                double[] aptTemp = new double[24];
                double[] wcFactor = new double[24];
                String[] rainPer = new String[24];
                int[] backgroundArr = new int[24];
                double[] tempArr = new double[24];
                double[] uvArray = new double[24];
                String iconCode,ampmInd;
                int hr;
                int[] imageArr = new int[24];
                try {
                    pDialog.dismiss();
                    for (int i = 0 ; i < 24 ; i++){
                        data = (JSONObject) response.getJSONArray("data").get(i);
                        hr = Integer.parseInt(data.optString("timestamp_local").substring(11,13));
                        iconCode = data.optJSONObject("weather").optString("icon");

                        ampmInd = " AM";
                        if (hr > 12){
                            hr = hr - 12;
                            ampmInd = " PM";
                        }

                        if (hr == 0) {
                            hr = 12;
                            ampmInd = " AM";
                        }
                        else {
                            if (hr == 12)
                                ampmInd = " PM";
                        }
                        imageArr[i] = getResources().getIdentifier(iconCode, "mipmap", getPackageName());
                        aptTemp[i] = Double.parseDouble(data.optString("app_temp"));
                        double tempF = 1.8 * Float.parseFloat(data.optString("temp")) + 32;
                        double windsM = Float.parseFloat(data.optString("wind_spd")) * 2.23694;
                        wcFactor[i] = Math.round(((35.74 +
                                (0.6215 * (tempF)) -
                                (35.75 * Math.pow(windsM,0.16)) +
                                (0.4275 * tempF * Math.pow(windsM,0.16))) - 32) *  0.55556);
                        hrArray[i] = hr  + ampmInd;
                        rainPer[i] = data.optString("pop");
                        tempArr[i] = Math.round(Float.parseFloat(data.optString("temp")));
                        uvArray[i] = Double.parseDouble(data.optString("uv"));
                        backgroundArr[i] = getBackground(
                                Integer.parseInt(data.optString("pop")),
                                aptTemp[i],
                                wcFactor[i],
                                uvArray[i]
                        );
                    }
                    Arrays.sort(tempArr);
                    min_temp.setText(tempArr[0] + "°");
                    max_temp.setText(tempArr[23] + "°");
                    CreateRecView(imageArr,hrArray,wcFactor,aptTemp,rainPer,backgroundArr,uvArray);
                    pDialog.dismiss();
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

    public void CreateRecView(final int[] imageArr, String[] hrArray, final double[] wcFactor, final double[] apTemp, final String[] rainPer, final int[] backArr, final double[] uvArray){
        String titleText,contentText;
        recyclerViewInw = (RecyclerView) findViewById(R.id.rec_main_Inw);
        recyclerViewAdapterInw = new RecyclerViewAdapter(imageArr, hrArray,'W',wcFactor,apTemp,rainPer,backArr);
        recyclerViewInw.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewInw.setAdapter(recyclerViewAdapterInw);

        recyclerViewAdapterInw.setOnItemClickListener(new RecyclerViewAdapter.ClickListener() {

            @Override
            public void onItemClick(int position, View v) {
                SweetAlertDialog infoD = new SweetAlertDialog(WeatherMain.this,SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                infoD.setCustomImage(imageArr[position]);
                infoD.setTitle(getTitleText(backArr[position]));

                final String prec = getPrec(apTemp[position],wcFactor[position],uvArray[position],Integer.parseInt(rainPer[position]));
                infoD.setContentText("");
                infoD.setConfirmText("Got It");
                infoD.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                        TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                        TextView subText = (TextView) alertDialog.findViewById(R.id.content_text);
                        Button con_button = (Button) alertDialog.findViewById(R.id.confirm_button);
                        text.setTypeface(typeface,Typeface.BOLD);
                        text.setTextSize(25f);
                        subText.setTypeface(typeface);
                        subText.setText(Html.fromHtml(prec));
                        subText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        con_button.setTypeface(typeface,Typeface.BOLD);
                    }
                });
                infoD.show();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }

    public void setBackground(String background){
        if (background.equals("c"))
            mainLayout.setBackground(getDrawable(R.mipmap.sun1));
        else
            mainLayout.setBackground(getDrawable(R.drawable.clear_sky));
    }

    public int getBackground(int rainpr,double heatIndex,double wcFactor,double uvindex){
        int heatStatus = getHeatStatus(heatIndex);
        int wcStatus = getWCFactor(wcFactor);
        int rainStatus = getRainStatus(rainpr);
        int uvStatus= getUVStatus(uvindex);

        if ((rainStatus == 0) && (heatStatus == 0) && (wcStatus == 0) && (uvStatus == 0))
            return R.drawable.weather_back_1;

        if ((heatStatus == 3) || (wcStatus == 3))
            return R.drawable.weather_back_4;

        if ((rainStatus >= 2) || (uvStatus >= 2))
            return  R.drawable.weather_back_3;

        return R.drawable.weather_back_2;
    }

    public int getHeatStatus(double heatIndex){
        int heatStatus;
        if (heatIndex < 25)
            heatStatus = 0;
        else
            if (heatIndex < 39)
                heatStatus = 1;
            else
                if (heatIndex < 45)
                    heatStatus = 2;
                else
                    heatStatus = 3;
        return heatStatus;
    }

    public int getWCFactor(double wcFactor){
        int wcStatus = 0;

        if (wcFactor > 16)
            wcStatus = 0;
        else
            if (wcFactor > -27)
                wcStatus = 1;
            else
                if (wcFactor > -39)
                    wcStatus = 2;
                else
                    wcStatus = 3;
        return wcStatus;
    }

    public int getRainStatus(int rainpr){
        int rainStatus = 0;
        if (rainpr < 25)
            rainStatus = 0;
        else
            if(rainpr < 50)
                rainStatus = 1;
            else
                if (rainpr < 75)
                    rainStatus = 2;
                else
                    rainStatus = 3;

        return rainStatus;
    }

    public int getUVStatus(double uvindex){
        int uvStatus = 0;
        Log.d("uv",uvindex + "");
        if (uvindex < 3)
            uvStatus = 0;
        else
            if (uvindex < 7)
                uvStatus = 1;
            else
                if (uvindex < 10)
                    uvStatus = 2;
                else
                    uvStatus = 3;
        return uvStatus;
    }

    public String getTitleText(int backid){

        if (backid == R.drawable.weather_back_1)
            return ("Go For It !");
        else
            if (backid == R.drawable.weather_back_2)
                return ("Take It Easy !");
            else
                if (backid == R.drawable.weather_back_3)
                    return ("Take Extreme Care !");
                else
                    return ("Stay Inside !!");
    }

    public String getPrec(double apTemp,double wcFactor,double uv,int rainPer) {
        String prec = "";
        int heatStatus = getHeatStatus(apTemp);
        int wcStatus = getWCFactor(wcFactor);
        int rainStatus = getRainStatus(rainPer);
        int uvStatus = getUVStatus(uv);

        if (heatStatus >= 3)
            prec = prec + "<b><font color = '#f50707'> Extreme Heat </font></b>\n\nIt is not recommended to go out at this hour. \n\n";

        if (wcStatus >= 3)
            prec = prec + "<b><font color = '#f50707'> Extreme Wind-chill </font></b>\n\nIt is not recommended to go out at this hour. \n\n";

        if (uvStatus >= 3)
            prec = prec + "<b><font color = '#f50707'> High UV Index </font></b>\n\nIt is not recommended to go out at this hour. \n\n";

        if (heatStatus == 2)
            prec = prec + "<b><font color = '#f58a07'> Moderate Heat </font></b> \n\nStay Hydrated. \nDrink water even if not thirsty \n"
                    + "Wear sunscreen with good SPF \n"
                    + "Use wet towel/napkins under your helmet \n"
                    + "Carry some snacks to stay energized \n"
                    + "Watch out for signs of strokes \n\n";

        if (wcStatus == 2)
            prec = prec + "<b><font color = '#f58a07'> Moderate Wind-chill </font></b> \n\nDress in layers. \nWind resistent outer layer \n"
                    + "Wear insulated gloves while riding \n"
                    + "Cover as much exposed skin as possible \n"
                    + "More you pedal more you generate the heat \n"
                    + "Carry warm water in insulated bottle \n\n";

        if (heatStatus == 1)
            prec = prec + "<b><font color = '#f5c107'> Minimal Heat </font></b> \n\n\u2022 Take plenty of water with you. \n"
                    + "\u2022 Wear sunscreen with good SPF \n"
                    + "\u2022 Do not wear dark cloths \n"
                    + "\u2022 Use wet towel/napkins under your helmet\n"
                    + "\u2022 Take frequent breaks \n\n";

        if (wcStatus == 1)
            prec = prec + "<b><font color = '#f5c107'> Minimal Wind-chill</font></b>\n\n\u2022 Dress in layers and Wear body warmers. \n"
                    + "\u2022 Cover your skin \n"
                    + "\u2022 Moisturize before you go out \n"
                    + "\u2022 Slow down your bicycle\n"
                    + "\u2022 Carry pocket heater\n\n";

        if (rainStatus > 0)
            prec = prec + "<b>" + rainPer + "% Chances of Rain </b> \n\n" +
                    "\u2022 Carry windcheater or jacket \n";

            if (rainStatus >= 2)
                prec = prec + "\u2022 Roads can be slippery, apply breaks slowely \n" +
                        "\u2022 Wear overshoes and waterproof gloves\n" +
                        "\u2022 Try to keep your head dry\n";

            if (rainStatus >= 3)
                prec = prec + "\u2022 Lower pressure in tires\n" +
                        "\u2022 Stay seated while riding\n\n";

        if (uvStatus == 1)
            prec = prec + "<b><font color = '#f5c107'> Minimal UV exposure</font></b>\n\n" +
                    "\u2022 Cover your skin\n" +
                    "\u2022 Wear good sunscreen with SPF 50+ \n\n";

        if (uvStatus == 2)
            prec = prec + "<b><font color = '#f5c107'> UV exposure</font></b>\n\n" +
                    "\u2022 Cover all your skin\n" +
                    "\u2022 Wear UPF labelled clothing\n" +
                    "\u2022 Use wrap on sunglasses\n\n";

        if (prec == "")
            prec = "<b><font color = '#3bf507'> Everything Looks Good ! </font></b>\n\n" +
                    "<b>Happy Cycling</b>";

        prec = prec.replace("\n","<br>");
        return prec;
    }
}

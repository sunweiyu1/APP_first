package fit5120.lookout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Common.Constant;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class EmergencyContactPage extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView tv_show_location, tv_show;
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    private Handler handler;
    private ArrayList<Double> twolocation = new ArrayList<Double>();
    private Notification notification;
    private NotificationCompat.Builder builder, builder2;
    private NotificationManager manager;
    private EditText username, friendname, phone, message;
    private ProgressBar progressBar1;
    private SharedPreferences sp;
    private LocationManager locationManager;
    private Location location, reallocation;
    private int count,i;
    private SparkButton starbutton;
    boolean startonce = false;
    private Location currentLocation;
    private TelephonyManager telephonyManager;
    private ImageView cont;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_fragment);
        showGPSContacts();
        //       tv_show_location = (TextView) findViewById(R.id.tv_show_location);
        username = (EditText) findViewById(R.id.ed_username);
        friendname = (EditText) findViewById(R.id.ed_name);
        phone = (EditText) findViewById(R.id.ed_phone);
        message = (EditText) findViewById(R.id.ed_help);
        Button begin = (Button) findViewById(R.id.b_begin);
        Button end = (Button) findViewById(R.id.b_end);
        Button submit = (Button) findViewById(R.id.b_submit);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        tv_show = (TextView) findViewById(R.id.tv_describtion);
        cont = (ImageView) findViewById(R.id.cont_btn);
        final AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactPage.this);
        builder.setTitle("User Guideline");
        builder.setMessage("This function starts timing when it is turned on, and automatically sends text messages to your emergency contacts after six prompts when the displacement is less than 8 meters per 10 minutes. Click START Timing, click CLOSE will close the function. (Your can type in your own phone number to test.)\n");
        hideSoftKeyboard();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "message";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
        //获取到位置管理器实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取到GPS_PROVIDER
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                reallocation = location;
 //               updata(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @SuppressLint("MissingPermission")
            @Override
            public void onProviderEnabled(String provider) {
  //              updata(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


        starbutton = (SparkButton) findViewById(R.id.star_button);
        starbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (starbutton.isChecked() == false) {
                    starbutton.setChecked(true);
                    builder.show();
                } else if (starbutton.isChecked() == true) {
                    starbutton.setChecked(false);
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                starbutton.setChecked(false);
            }
        });

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });


        sp = getSharedPreferences("User", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        username.setText(sp.getString("username", ""));
        friendname.setText(sp.getString("friendname", ""));
        phone.setText(sp.getString("phone", ""));
        message.setText(sp.getString("message", ""));


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean allfilled = true;
                if (TextUtils.isEmpty(username.getText())) {
                    username.setError("Please enter your name");
                    allfilled = false;
                }
                if (TextUtils.isEmpty(friendname.getText())){
                    friendname.setError("Please enter contact person name");
                    allfilled = false;
                }
                if (TextUtils.isEmpty(phone.getText())){
                    phone.setError("Please enter Contact phone number");
                    allfilled = false;
                }
                if (allfilled) {
                    editor.putString("username", String.valueOf(username.getText()));
                    editor.putString("friendname", String.valueOf(friendname.getText()));
                    editor.putString("phone", String.valueOf(phone.getText()));
                    editor.putString("message", String.valueOf(message.getText()));
                    editor.commit();
                    if (ContextCompat.checkSelfPermission(EmergencyContactPage.this,
                            Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EmergencyContactPage.this,
                                new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        sendMessage();
                        displayD();
                    }
                }
            }
        });

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.setVisibility(View.VISIBLE);
                Toast.makeText(EmergencyContactPage.this, "Wait for get your location", Toast.LENGTH_SHORT).show();
                startonce = true;
                twolocation.clear();
                count = 6;
                i=0;
                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EmergencyContactPage.this, "Timing begins", Toast.LENGTH_SHORT).show();
                        progressBar1.setVisibility(View.INVISIBLE);
                    }
                },4000);

                getLocation(reallocation);
                handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void run() {
                        getLocation(reallocation);
                        // handler自带方法实现定时器
                        double result = 10;
                        try {
                            result = getDistance(twolocation.get(i), twolocation.get(i+1), twolocation.get(i+2), twolocation.get(i+3));
                        } catch(Exception e){
                            noLocation();
                        }
                        finally{
                        }
//                        double result = getDistance(twolocation.get(i), twolocation.get(i+1), twolocation.get(i+2), twolocation.get(i+3));
                        if (result < 8){
                            wakeUpAndUnlock(EmergencyContactPage.this);
                            if (count > 0){
                                setNotification(2);
                                Toast.makeText(EmergencyContactPage.this, "It will send message after "+(count-1)+" times", Toast.LENGTH_SHORT).show();
                            }
                            else {setNotification(1);}
                            count --;
                            if (count < 0){
                                handler.removeCallbacksAndMessages(null);
                            }
                            else if (count == 0){
                                String content = null;
                                content = sp.getString("message","")+" Im in "+"https://www.google.com/maps/place/"+twolocation.get(i+2)+","+twolocation.get(i+3)+"/";
                                SmsManager manage=SmsManager.getDefault();  //取得默认的SmsManager用于短信的发送
                                List<String> all=manage.divideMessage(content);  //短信的内容是有限的，要根据短信长度截取。逐条发送
                                Iterator<String> it=all.iterator();
                                while(it.hasNext()){
                                    manage.sendTextMessage(sp.getString("phone",""), null, it.next(), null, null);  //逐条发送短息
                                    handler.removeCallbacksAndMessages(null);
                                }
                            }
                        }
                        i = i+2;
                        handler.postDelayed(this,4*1000);
                    }},5000);
//                Toast.makeText(EmergencyContactPage.this, ""+distance.get(0), Toast.LENGTH_SHORT).show();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startonce){
                    handler.removeCallbacksAndMessages(null);
                    Toast.makeText(EmergencyContactPage.this, "Timing End", Toast.LENGTH_SHORT).show();}
                else {}
            }
        });
/*        IntentFilter filter = new IntentFilter("action_send");
        registerReceiver(broadcastReceiver, filter);*/
    }
  /*  private void updata(Location location){
        if(location != null){
            StringBuilder sb = new StringBuilder();
            sb.append("Location:\n");
            sb.append("Latitude:");
            sb.append(location.getLongitude());
            sb.append("\nLongitude:");
            sb.append(location.getLatitude());
            sb.append("\bAltitude:");
            sb.append(location.getAltitude());
            sb.append("\nSpeed：");
            sb.append(location.getSpeed());
            sb.append("\nBearing：");
            sb.append(location.getBearing());
            //         tv_show_location.setText(sb.toString());
        }
        else{
            StringBuilder sb = new StringBuilder();
            sb.append("No location");
            //          tv_show_location.setText(sb.toString());
        }
    }*/

    public void showGPSContacts() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PERMISSION_GRANTED) {// 没有权限，申请权限。
                    ActivityCompat.requestPermissions(this, LOCATIONGPS,
                            BAIDU_READ_PHONE_STATE);
                } else {
                    //                   onMyLocationButtonClick();//getLocation为定位方法
                }
            } else {
//                onMyLocationButtonClick();//getLocation为定位方法
            }
        } else {
            Toast.makeText(this, "Please open the GPS", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, PRIVATE_CODE);
        }
    }
    private void getLocation(Location location) {
        if (location != null)
        {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            twolocation.add(lat);
            twolocation.add(lng);
            Log.d("twoloca",lat+" "+lng);
        }
        else
        {
            double lat = -37.876364;
            double lng = 145.044192;
            twolocation.add(lat);
            twolocation.add(lng);
        }
    }


    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    //设置通知栏消息样式
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotification(int type) {
        //点击通知栏消息跳转页
        Intent intent = new Intent(this, EmergencyContactPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //创建通知消息管理类
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this,"chat")//创建通知消息实例
                .setContentTitle("Do you want to stop it?")
                .setContentText("Touch me. (Send message in " + (count-1) +" times)")
                .setWhen(System.currentTimeMillis())//通知栏显示时间
                .setSmallIcon(R.mipmap.ic_launcher)//通知栏小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//通知栏下拉是图标
                .setContentIntent(pendingIntent)//关联点击通知栏跳转页面
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                .setAutoCancel(true)//设置点击通知栏消息后，通知消息自动消失
//                .setSound(Uri.fromFile(new File("/system/MP3/music.mp3"))) //通知栏消息提示音
                .setVibrate(new long[]{0, 1000, 1000, 1000}) //通知栏消息震动
                .setLights(Color.GREEN, 1000, 2000) //通知栏消息闪灯(亮一秒间隔两秒再亮)
                .setDefaults(NotificationCompat.DEFAULT_ALL); //通知栏提示音、震动、闪灯等都设置为默认

        builder2 = new NotificationCompat.Builder(this)//创建通知消息实例
                .setContentTitle("Please call this phone to help me!")
                .setContentText("My name: "+sp.getString("username","")+"\nMy friend phone: "+sp.getString("phone","" +
                        ""))
                .setWhen(System.currentTimeMillis())//通知栏显示时间
                .setSmallIcon(R.mipmap.ic_launcher)//通知栏小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//通知栏下拉是图标
                .setContentIntent(pendingIntent)//关联点击通知栏跳转页面
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                .setAutoCancel(true)//设置点击通知栏消息后，通知消息自动消失
//                .setSound(Uri.fromFile(new File("/system/MP3/music.mp3"))) //通知栏消息提示音
                .setVibrate(new long[]{0, 1000, 1000, 1000}) //通知栏消息震动
                .setLights(Color.GREEN, 1000, 2000) //通知栏消息闪灯(亮一秒间隔两秒再亮)
                .setDefaults(NotificationCompat.DEFAULT_ALL); //通知栏提示音、震动、闪灯等都设置为默认
        if (type == 2) {
            //短文本
            notification = builder.build();
            //Constant.TYPE1为通知栏消息标识符，每个id都是不同的
            manager.notify(Constant.TYPE1, notification);
        } else if (type == 1) {
            //长文本
            notification = builder2.build();
            //Constant.TYPE1为通知栏消息标识符，每个id都是不同的
            manager.notify(Constant.TYPE1, notification);
        } else {
            //带图片
            notification = builder.setStyle(new NotificationCompat.BigPictureStyle().
                    bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))
                    .build();
            manager.notify(Constant.TYPE3, notification);
        }
    }

    //唤醒屏幕并解锁
    public void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright:ddd");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public void noLocation(){
        typeface = ResourcesCompat.getFont(this, R.font.montserrat);

        SweetAlertDialog sDialog = new SweetAlertDialog(EmergencyContactPage.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Please find a open space, and try again.Can not locate you now.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        handler.removeCallbacksAndMessages(null);
                        sDialog.dismiss();
                    }
                });
        sDialog.show();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void sendMessage(){
        Toast.makeText(EmergencyContactPage.this, "Information saving successfully, It will notify your emergency contact people.", Toast.LENGTH_SHORT).show();
/*                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                @SuppressLint("MissingPermission") String phonenumber = telephonyManager.getLine1Number();*/
        String firstmessage = "Message from LookOut:\n" + sp.getString("username","") + " has added your number as an Emergency contact."
                + "\nYou will be receiving " + sp.getString("username","") + "'s location in case of emergency.\nThank You!";
        SmsManager manage=SmsManager.getDefault();
        List<String> all=manage.divideMessage(firstmessage);
        Iterator<String> it=all.iterator();
        while(it.hasNext()){
            manage.sendTextMessage(sp.getString("phone",""), null, it.next(), null, null);  //逐条发送短息
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                    displayD();
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    SweetAlertDialog sDialog = new SweetAlertDialog(EmergencyContactPage.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Details Saved!")
                            .setContentText("Permission Required to Send SMS")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            });
                    sDialog.show();
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public void displayD(){
        SweetAlertDialog sDialog = new SweetAlertDialog(EmergencyContactPage.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Details Saved!")
                .setContentText(sp.getString("friendname","") + " will be contacted in case of an emergency!")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                });
        sDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1) && (resultCode == RESULT_OK)) {
            Cursor cursor = null;
            try {
                Uri uri = data.getData();
                cursor = getContentResolver().query(uri, new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    phone.setText(cursor.getString(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


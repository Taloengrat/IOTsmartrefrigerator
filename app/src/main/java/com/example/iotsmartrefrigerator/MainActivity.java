package com.example.iotsmartrefrigerator;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.iotsmartrefrigerator.MyBroadcastReceiver.ACTION_SNOOZE;
import static com.example.iotsmartrefrigerator.MyBroadcastReceiver.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 16;

    ImageView egg1, egg2, egg3, egg4, egg5, egg6, water, refresh, map;

    ImageView egg2_1, egg2_2, egg2_3, egg2_4, egg2_5, egg2_6;
    public final int WRITE_PERMISSON_REQUEST_CODE = 1;
    TextView txWater, txName;
    Switch aSwitch;
    Spinner spinner;
    boolean stateSwith = false;
    public MediaPlayer sd, buttonsd;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    String content;

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String TAG = "main";

    private String mLastUpdateTime;

    FusedLocationProviderClient mFusedLocationClient;
    SettingsClient mSettingsClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    int currentPath = 1;
    FirebaseDatabase database;

    // boolean flag to toggle the ui
    Boolean mRequestingLocationUpdates;
    DatabaseReference myRef1_1, myRef1_2, myRef1_3, myRef1_4, myRef1_5, myRef1_6;
    DatabaseReference myRef2_1, myRef2_2, myRef2_3, myRef2_4, myRef2_5, myRef2_6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BindingData();

        createNotificationChannel();

        sd = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        buttonsd = MediaPlayer.create(getApplicationContext(), R.raw.button);

        database = FirebaseDatabase.getInstance();

        bindRefDatabase1();

        final DatabaseReference waters = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ml");

        waters.addValueEventListener(eventListenerwater);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonsd.start();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/device/led_control");


                if (stateSwith) {
                    stateSwith = false;
                } else {
                    stateSwith = true;
                }

                if (stateSwith) {
                    myRef.setValue(1);
                } else {
                    myRef.setValue(0);
                }
            }
        });

        // ผูกตัวแปรไฟล์ java กับไฟล์ xml

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }
    private void bindRefDatabase1() {
        myRef1_1 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg1");
        myRef1_2 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg2");
        myRef1_3 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg3");
        myRef1_4 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg4");
        myRef1_5 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg5");
        myRef1_6 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg6");

        myRef2_1 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg7");
        myRef2_2 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg8");
        myRef2_3 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg9");
        myRef2_4 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg10");
        myRef2_5 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg11");
        myRef2_6 = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() +  "/egg12");
    }

    private void bindMenuSpinner() {
        String[] items = new String[]{"ถาดไข่ที่ 1", "ถาดไข่ที่ 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    private void BindingData() {

        spinner = findViewById(R.id.spinner);
        map = findViewById(R.id.map);
        txName = findViewById(R.id.txName);
        egg1 = findViewById(R.id.egg1_1);
        egg2 = findViewById(R.id.egg1_2);
        egg3 = findViewById(R.id.egg1_3);
        egg4 = findViewById(R.id.egg1_4);
        egg5 = findViewById(R.id.egg1_5);
        egg6 = findViewById(R.id.egg1_6);

        egg2_1 = findViewById(R.id.egg2_1);
        egg2_2 = findViewById(R.id.egg2_2);
        egg2_3 = findViewById(R.id.egg2_3);
        egg2_4 = findViewById(R.id.egg2_4);
        egg2_5 = findViewById(R.id.egg2_5);
        egg2_6 = findViewById(R.id.egg2_6);

        water = findViewById(R.id.tank);
        refresh = findViewById(R.id.refresh);

        txWater = findViewById(R.id.txWater);
        aSwitch = findViewById(R.id.aswitch);

        txName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        bindMenuSpinner();

        spinner.setOnItemSelectedListener(new myOnItemSelectedListener());
    }

    public class myOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

            switch (pos) {
                case 0:
                    Toast.makeText(getApplicationContext(), "ถาดที่ 1", Toast.LENGTH_LONG).show();

                    myRef1_1.addValueEventListener(eventListenerEgg1);
                    myRef1_2.addValueEventListener(eventListenerEgg1);
                    myRef1_3.addValueEventListener(eventListenerEgg1);
                    myRef1_4.addValueEventListener(eventListenerEgg1);
                    myRef1_5.addValueEventListener(eventListenerEgg1);
                    myRef1_6.addValueEventListener(eventListenerEgg1);

                    myRef2_1.removeEventListener(eventListenerEgg2);
                    myRef2_2.removeEventListener(eventListenerEgg2);
                    myRef2_3.removeEventListener(eventListenerEgg2);
                    myRef2_4.removeEventListener(eventListenerEgg2);
                    myRef2_5.removeEventListener(eventListenerEgg2);
                    myRef2_6.removeEventListener(eventListenerEgg2);

                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "ถาดที่ 2", Toast.LENGTH_LONG).show();

                    myRef2_1.addValueEventListener(eventListenerEgg2);
                    myRef2_2.addValueEventListener(eventListenerEgg2);
                    myRef2_3.addValueEventListener(eventListenerEgg2);
                    myRef2_4.addValueEventListener(eventListenerEgg2);
                    myRef2_5.addValueEventListener(eventListenerEgg2);
                    myRef2_6.addValueEventListener(eventListenerEgg2);

                    myRef1_1.removeEventListener(eventListenerEgg1);
                    myRef1_2.removeEventListener(eventListenerEgg1);
                    myRef1_3.removeEventListener(eventListenerEgg1);
                    myRef1_4.removeEventListener(eventListenerEgg1);
                    myRef1_5.removeEventListener(eventListenerEgg1);
                    myRef1_6.removeEventListener(eventListenerEgg1);


                    break;
                default:
                    Toast.makeText(getApplicationContext(), "not define", Toast.LENGTH_LONG).show();
            }



        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    ValueEventListener eventListenerEgg1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);

            Log.v("path", dataSnapshot.getRef().toString());


            if (value >= 500 && value <= 999) {

                if (dataSnapshot.getRef().toString().endsWith("1")) {

                    egg1.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("2")) {
                    egg2.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("3")) {
                    egg3.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("4")) {
                    egg4.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("5")) {
                    egg5.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("6")) {
                    egg6.setVisibility(View.INVISIBLE);
                }
            } else {
                if (dataSnapshot.getRef().toString().endsWith("1")) {

                    egg1.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("2")) {
                    egg2.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("3")) {
                    egg3.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("4")) {
                    egg4.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("5")) {
                    egg5.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("6")) {
                    egg6.setVisibility(View.VISIBLE);
                }
            }
            if (egg1.getVisibility() == View.INVISIBLE && egg2.getVisibility() == View.INVISIBLE && egg3.getVisibility() == View.INVISIBLE
                    && egg4.getVisibility() == View.INVISIBLE && egg5.getVisibility() == View.INVISIBLE && egg6.getVisibility() == View.INVISIBLE) {

                sd.start();


                content = "ไข่ถาดที่ 1 หมด";
                LongOperation lo = new LongOperation(MainActivity.this);
                lo.execute("IOTsmartRefrigerator");

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.notification);
                dialog.setCancelable(false);

                TextView content = dialog.findViewById(R.id.textContent);

                content.setText("ไข่ถาดที่ 1 หมด");

                Button btOk = dialog.findViewById(R.id.ok);

                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonsd.start();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener eventListenerEgg2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);

            Log.v("path", dataSnapshot.getRef().toString());

            ///

            if (value >= 500 && value <= 999) {

                if (dataSnapshot.getRef().toString().endsWith("7")) {

                    egg1.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("8")) {
                    egg2.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("9")) {
                    egg3.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("10")) {
                    egg4.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("11")) {
                    egg5.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("12")) {
                    egg6.setVisibility(View.INVISIBLE);
                }
            } else {
                if (dataSnapshot.getRef().toString().endsWith("7")) {
                    egg1.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("8")) {
                    egg2.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("9")) {
                    egg3.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("10")) {
                    egg4.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("11")) {
                    egg5.setVisibility(View.VISIBLE);
                } else if (dataSnapshot.getRef().toString().endsWith("12")) {
                    egg6.setVisibility(View.VISIBLE);
                }
            }
            if (egg2_1.getVisibility() == View.INVISIBLE && egg2_2.getVisibility() == View.INVISIBLE && egg2_3.getVisibility() == View.INVISIBLE
                    && egg2_4.getVisibility() == View.INVISIBLE && egg2_5.getVisibility() == View.INVISIBLE && egg2_6.getVisibility() == View.INVISIBLE) {

                sd.start();


                content = "ไข่ถาดที่ 2 หมด";
                LongOperation lo = new LongOperation(MainActivity.this);
                lo.execute("IOTsmartRefrigerator");

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.notification);
                dialog.setCancelable(false);

                Button btOk = dialog.findViewById(R.id.ok);

                TextView content = dialog.findViewById(R.id.textContent);

                content.setText("ไข่ถาดที่ 2 หมด");

                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonsd.start();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener eventListenerwater = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long valueWater = dataSnapshot.getValue(Long.class);


            txWater.setText("ปริมาณน้ำ\n" + valueWater + " มิลลิลิตร");

            if (valueWater >= -10 && valueWater <= 10) {


                water.setImageResource(R.drawable.hidewater);
                content = "น้ำหมด";
                LongOperation lo = new LongOperation(MainActivity.this);
                lo.execute("IOTsmartRefrigerator");

                buttonsd.start();
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.notification);
                dialog.setCancelable(false);

                Button btOk = dialog.findViewById(R.id.ok);
                TextView tx = dialog.findViewById(R.id.textContent);

                tx.setText("น้ำหมด");
                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonsd.start();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } else {
                water.setImageResource(R.drawable.fullwater);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getLight = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/device/led_control");

        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/device/led_control").setValue(1);
        getLight.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int led_control = dataSnapshot.getValue(Integer.class);

                if (led_control == 1) {
                    aSwitch.setChecked(true);
                    Handler pd = new Handler();
                    pd.postDelayed(close_led, 15000);


                } else if (led_control == 0) {
                    aSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map:

                if (PermissionUtility.askPermissionForActivity(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, WRITE_PERMISSON_REQUEST_CODE)) {

                    Uri uri = Uri.parse("https://www.google.com/maps/search/%E0%B9%80%E0%B8%8B%E0%B9%80%E0%B8%A7%E0%B9%88%E0%B8%99/@14.0376805,100.7106937,14z"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }


                break;


        }
    }


    private class LongOperation extends AsyncTask<String, String, String> {

        private static final String TAG = "longoperation";
        private Context ctx;
        private AtomicInteger notificationId = new AtomicInteger(0);

        LongOperation(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            for (String s : params) {
                Log.e(TAG, s);

                publishProgress(s);

                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }
            }
            return "Executed";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String title : values) {
                sendNotification(title, notificationId.incrementAndGet());
            }
        }

        void sendNotification(String title, int notificationId) {

            // Create an explicit intent for an Activity in your app
        /* Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0); */

            Intent snoozeIntent = new Intent(ctx, MyBroadcastReceiver.class);
            snoozeIntent.setAction(ACTION_SNOOZE);
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

            Log.e(TAG, snoozeIntent.getExtras().toString());

            Log.e(TAG, "snoozeIntent id: " + snoozeIntent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));

            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(ctx, notificationId, snoozeIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(String.format("%s", title))
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(false)
                    // Add the action button
                    .addAction(R.drawable.ic_launcher_foreground, ctx.getString(R.string.snooze),
                            snoozePendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        }
    }

    Runnable close_led = new Runnable() {
        @Override
        public void run() {

            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/device/led_control").setValue(0);

            aSwitch.setChecked(false);


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/device/led_control").setValue(0);


    }


















}

package com.example.iotsmartrefrigerator;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.iotsmartrefrigerator.MyBroadcastReceiver.ACTION_SNOOZE;
import static com.example.iotsmartrefrigerator.MyBroadcastReceiver.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {
    
    ImageView egg1, egg2 ,egg3 ,egg4 ,egg5 ,egg6, water,refresh, map;
    TextView txWater;
    Switch aSwitch;
    boolean stateSwith = false;
    public MediaPlayer sd, buttonsd;
    String content;

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String TAG = "main";


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BindingData();

        createNotificationChannel();




        sd = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        buttonsd = MediaPlayer.create(getApplicationContext(), R.raw.button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database.getReference("egg0");
        DatabaseReference myRef2 = database.getReference("egg1");
        DatabaseReference myRef3 = database.getReference("egg2");
        DatabaseReference myRef4 = database.getReference("egg3");
        DatabaseReference myRef5 = database.getReference("egg4");
        DatabaseReference myRef6 = database.getReference("egg5");
        final DatabaseReference waters = database.getReference("ml");




        myRef1.addValueEventListener(eventListenerEgg);
        myRef2.addValueEventListener(eventListenerEgg);
        myRef3.addValueEventListener(eventListenerEgg);
        myRef4.addValueEventListener(eventListenerEgg);
        myRef5.addValueEventListener(eventListenerEgg);
        myRef6.addValueEventListener(eventListenerEgg);

     waters.addValueEventListener(eventListenerwater);



     aSwitch.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             buttonsd.start();
             FirebaseDatabase database = FirebaseDatabase.getInstance();
             DatabaseReference myRef = database.getReference("device/led_control");


             if (stateSwith){
                 stateSwith = false;
             }else{
                 stateSwith = true;
             }

            if (stateSwith){
                myRef.setValue(1);
            }else{
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

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });


    }

    private void BindingData() {
        egg1 = findViewById(R.id.egg1);
        egg2 = findViewById(R.id.egg2);
        egg3 = findViewById(R.id.egg3);
        egg4 = findViewById(R.id.egg4);
        egg5 = findViewById(R.id.egg5);
        egg6 = findViewById(R.id.egg6);
        water = findViewById(R.id.tank);
        refresh = findViewById(R.id.refresh);
        map = findViewById(R.id.map);
        txWater = findViewById(R.id.txWater);
        aSwitch = findViewById(R.id.aswitch);

    }

    ValueEventListener eventListenerEgg = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);

            Log.v("path",dataSnapshot.getRef().toString());


            if (value >=500 && value <= 999){

                if (dataSnapshot.getRef().toString().endsWith("egg0")){

                    egg1.setVisibility(View.INVISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg1")){
                    egg2.setVisibility(View.INVISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg2")){
                    egg3.setVisibility(View.INVISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg3")){
                    egg4.setVisibility(View.INVISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg4")){
                    egg5.setVisibility(View.INVISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg5")) {
                    egg6.setVisibility(View.INVISIBLE);
                }
            }else{
                if (dataSnapshot.getRef().toString().endsWith("egg0")){

                    egg1.setVisibility(View.VISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg1")){
                    egg2.setVisibility(View.VISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg2")){
                    egg3.setVisibility(View.VISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg3")){
                    egg4.setVisibility(View.VISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg4")){
                    egg5.setVisibility(View.VISIBLE);
                }else if (dataSnapshot.getRef().toString().endsWith("egg5")) {
                    egg6.setVisibility(View.VISIBLE);
                }
            }

            if (egg1.getVisibility() == View.INVISIBLE && egg2.getVisibility() == View.INVISIBLE && egg3.getVisibility() == View.INVISIBLE
            && egg4.getVisibility() == View.INVISIBLE && egg5.getVisibility() == View.INVISIBLE && egg6.getVisibility() == View.INVISIBLE){

                sd.start();


                content = "ไข่หมด";
                LongOperation lo = new LongOperation(MainActivity.this);
                lo.execute("IOTsmartRefrigerator");

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.notification);
                dialog.setCancelable(false);

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

    ValueEventListener eventListenerwater = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long valueWater = dataSnapshot.getValue(Long.class);



             txWater.setText("ปริมาณน้ำ\n"+valueWater+" มิลลิลิตร");

            if (valueWater >= -10 && valueWater<=10){


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
                TextView tx = dialog.findViewById(R.id.textView2);

                tx.setText("น้ำหมด");
                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonsd.start();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }else{
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
        DatabaseReference getLight = database.getReference("device/led_control");

        getLight.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long led_control = dataSnapshot.getValue(Long.class);

                if (led_control==1){
                    aSwitch.setChecked(true);
                }else if (led_control == 0)
                {
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
            for (String title: values) {
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

}

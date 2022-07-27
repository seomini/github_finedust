package com.example.hivemqmqmq;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private  Button btn;
    private  static  final String TAG = "MyTag";
    private String topic,clientID;
    private MqttAndroidClient client;
    private TextView txt,txtCondition,txtComment,txtAddress;
    private ProgressBar progressBar;
    private ImageView imageView;
    LinearLayout backGround;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        connectx();
        //Animation anim = AnimationUtils.loadAnimation(this.R.anim.anim_translate_twits);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                imageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(1000);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }
    private void init(){
        //btn =findViewById(R.id.btn_sub);
        txt =findViewById(R.id.txt_sub);
        txtCondition =findViewById(R.id.txt_Condition);
        txtComment =findViewById(R.id.txt_Comment);
        txtAddress =findViewById(R.id.txt_Address);
        progressBar =findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        clientID ="xxx";
        topic ="testtopic/Isaac";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientID);

        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectx();
            }
        });*/
    }
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    private  void connectx(){
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private  void sub(){
        try{
            backGround= (LinearLayout)findViewById(R.id.background);
            client.subscribe(topic,0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //log
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String x =new String(message.getPayload());
                    double y = Double.valueOf(x).doubleValue();
                    //int z = Integer.valueOf(x).intValue();
                    Log.d(TAG, "topic:"+ topic );
                    Log.d(TAG, "message:"+ new String(message.getPayload()) );
                    txt.setText(x);
                    txtAddress.setText(getTime());
                    if(0<y&&y<=30){
                        txtCondition.setText("좋음");
                        txtComment.setText("신선한 공기 많이 마시세요~");
                        backGround.setBackgroundColor(Color.parseColor("#0277BD"));
                        progressBar.setProgress((int)y);
                        imageView.setImageResource(R.drawable.s_fn_good);
                    }
                    else if(30<y&&y<=80){
                        txtCondition.setText("보통");
                        txtComment.setText("쾌적한 날씨네요~");
                        backGround.setBackgroundColor(Color.parseColor("#008490"));
                        progressBar.setProgress((int)y);
                        imageView.setImageResource(R.drawable.s_fn_nomal);

                    }
                    else if(80<y&&y<=150){
                        txtCondition.setText("나쁨");
                        txtComment.setText("");
                        backGround.setBackgroundColor(Color.parseColor("#E65100"));
                        progressBar.setProgress((int)y);
                        imageView.setImageResource(R.drawable.s_fn_bad);
                    }
                    else if(150<y){
                        txtCondition.setText("매우 나쁨");
                        txtComment.setText("");
                        backGround.setBackgroundColor(Color.parseColor("#D74315"));
                        progressBar.setProgress((int)y);
                        imageView.setImageResource(R.drawable.s_fn_verybad);
                    }
                    else{
                        txtCondition.setText("ERROR");
                        txtComment.setText("ERROR");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //toast or log
                }
            });
        }catch (MqttException e){

        }
    }

    public void onShakeImage() {
        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cycles_n);

        ImageView imageView;
        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.startAnimation(shake);
    }

}
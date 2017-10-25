package com.androidsocket.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lashou.pushlib.service.MessageListener;
import com.lashou.pushlib.Push;

public class MainActivity extends AppCompatActivity {
    private TextView textValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textValue = (TextView) findViewById(R.id.text_value);
        Push.start(this, "1234", new MessageListener() {
            @Override
            public void onGetID(String id) {
                Log.i("push------>", id);
            }

            @Override
            public void onMessageReceive(final String message) {
                    Log.i("push------>", message);
                textValue.setText(message);
            }

            @Override
            public void onHeartBeat(String beatTime) {
                Log.i("push------>time", beatTime);
            }

            @Override
            public void onError(String str) {
                Log.i("push------>", str);
            }
        });

    }

    @Override
    protected void onDestroy() {
        Push.stop();
        super.onDestroy();
    }
}


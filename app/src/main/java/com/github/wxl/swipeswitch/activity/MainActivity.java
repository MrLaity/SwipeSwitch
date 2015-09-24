package com.github.wxl.swipeswitch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.wxl.swipeswitch.R;
import com.github.wxl.swipeswitch.view.SwipeSwitch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeSwitch swipeView = (SwipeSwitch) findViewById(R.id.swipeView);
        swipeView.setFrontBitmap(R.mipmap.ic_launcher);
        swipeView.setStatusListener(new SwipeSwitch.StatusListener() {
            @Override
            public void statusOpen() {
                Toast.makeText(MainActivity.this, "open", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void statusClose() {
                Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

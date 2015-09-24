package com.github.wxl.swipeswitch.activity;

import android.app.Activity;
import android.os.Bundle;

import com.github.wxl.slideswitch.R;
import com.github.wxl.swipeswitch.SwipeSwitch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeSwitch swipeView = (SwipeSwitch) findViewById(R.id.swipeView);
        swipeView.setFrontBitmap(R.mipmap.ic_launcher);

    }

}

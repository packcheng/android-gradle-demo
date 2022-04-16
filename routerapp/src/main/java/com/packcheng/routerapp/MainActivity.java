package com.packcheng.routerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.packcheng.router.annotations.Destination;
import com.packcheng.router.runtime.ZbcRouter;
import com.packcheng.tracker.helper.annotation.ZbcTimerTracker;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

@Destination(url = "router://page-home", description = "应用主页")
public class MainActivity extends AppCompatActivity {

    @ZbcTimerTracker(desc = "MainActivity::onCreate")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String channel = ChannelReaderUtil.getChannel(getApplicationContext());
        setTitle("MainActivity:" + channel);

        findViewById(R.id.btn_go_kotlin_page).setOnClickListener(v -> goKtPage());
        findViewById(R.id.btn_go_reading_page).setOnClickListener(v -> goReadingPage());
    }

    @ZbcTimerTracker(desc = "MainActivity::goKtPage")
    private void goKtPage() {
        ZbcRouter.INSTANCE.go(MainActivity.this, "router://kotlin?name=zbc&age=19");
    }

    @ZbcTimerTracker(desc = "MainActivity::goReadingPage")
    private void goReadingPage() {
        ZbcRouter.INSTANCE.go(MainActivity.this, "router://reading");
    }
}
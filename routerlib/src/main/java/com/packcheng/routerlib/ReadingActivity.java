package com.packcheng.routerlib;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.packcheng.router.annotations.Destination;
import com.packcheng.router.runtime.ZbcRouter;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/5 17:59
 */
@Destination(url = "router://reading", description = "阅读页面")
public class ReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setTitle("ReadingActivity");

        findViewById(R.id.btn_go_kotlin_page).setOnClickListener(v ->
                ZbcRouter.INSTANCE.go(ReadingActivity.this, "router://kotlin?name=ReadingActivity&age=20"));
    }
}

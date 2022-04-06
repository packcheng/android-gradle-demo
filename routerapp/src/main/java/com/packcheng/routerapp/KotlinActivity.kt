package com.packcheng.routerapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.packcheng.router.annotations.Destination

/**
 *
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/5 12:22
 */
@Destination(url = "router://kotlin", description = "测试kotlin页面")
class KotlinActivity : AppCompatActivity() {

    val mTvMsg by lazy {
        findViewById(R.id.tv_msg) as? TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        title = "KotlinActivity"

        val name = intent.getStringExtra("name")
        val age = intent.getStringExtra("age")

        mTvMsg!!.text = "name=$name, age=$age"
    }
}
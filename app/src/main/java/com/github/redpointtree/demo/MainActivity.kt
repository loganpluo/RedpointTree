package com.github.redpointtree.demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.redpointtree.RedPointTreeCenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val tag = "MainActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RedPointTreeCenter.getInstance().put(this, "messagebox", R.xml.messagebox)

        gotoRedPointTreeInSimpleActivity.setOnClickListener {
            val intent = Intent(this@MainActivity,RedPointTreeInSimpleActivity::class.java)
            startActivity(intent)
        }

        gotoCrossHierarchyActivity.setOnClickListener {
            val intent = Intent(this@MainActivity,CrossHierarchyActivity::class.java)
            startActivity(intent)
        }
    }


}

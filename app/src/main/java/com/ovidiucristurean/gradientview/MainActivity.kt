package com.ovidiucristurean.gradientview

import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gradientView: GradientView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gradientView = findViewById(R.id.gradientView)
    }

    override fun onStart() {
        super.onStart()
        gradientView.setShape(OvalShape())
        gradientView.startAnimation()
    }

    override fun onPause() {
        super.onPause()
        gradientView.stopAnimation()
    }

    override fun onStop() {
        super.onStop()
        gradientView.stopAnimation()
    }
}
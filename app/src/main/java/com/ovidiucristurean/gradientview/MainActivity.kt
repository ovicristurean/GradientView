package com.ovidiucristurean.gradientview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var gradientView: GradientView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gradientView = findViewById(R.id.gradientView)
        button = findViewById(R.id.gradientButton)
        button.setOnClickListener {
            //gradientView.updateGradient()
        }
    }

    override fun onStart() {
        super.onStart()
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

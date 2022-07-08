package com.lazyiones.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lazyiones.weather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.indexHorizontalScrollView.setTwentyFourHourView(binding.twentyFourHourView)
//        binding.twentyFourHour.setData()
    }

}
package com.example.wanderer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng

class StreetViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_streetView) as SupportStreetViewPanoramaFragment?

        streetViewPanoramaFragment?.getStreetViewPanoramaAsync{streetviewPanorama ->
            streetviewPanorama.setPosition(LatLng(-12.048761,-77.042413))
            //streetviewPanorama.setPosition(LatLng(latitude,longitude))
        }
    }
}
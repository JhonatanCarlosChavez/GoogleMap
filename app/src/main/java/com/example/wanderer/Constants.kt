package com.example.wanderer

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlin.random.Random

object Constants {

    fun getPolyLineCords():ArrayList<LatLng>
    {
        return arrayListOf(
            LatLng(12.345,13.234),
            LatLng(17.345,13.234),
            LatLng(17.345,18.234),
            LatLng(20.98, 20.98)
        )
    }

    fun getPolygonCords():ArrayList<LatLng>
    {
        return arrayListOf(
            LatLng(45.123,50.123),
            LatLng(45.123,55.123),
            LatLng(50.123,55.123),
            LatLng(50.123,50.123)
        )
    }

    fun getHeatmapData():ArrayList<LatLng>
    {
        val data = ArrayList<LatLng>()

        for (i in 1..200)
        {
            val latitude = Random.nextDouble(50.0,75.0)
            val longitude = Random.nextDouble(50.0,75.0)
            data.add(LatLng(latitude,longitude))
        }

        return data
    }

    fun getWeightedHeatmapData():ArrayList<WeightedLatLng>
    {
        val data = ArrayList<WeightedLatLng>()

        for (i in 1..200)
        {
            val latitude = Random.nextDouble(50.0,75.0)
            val longitude = Random.nextDouble(50.0,75.0)
            val intensity = Random.nextDouble(1.0,1000.0)
            data.add(WeightedLatLng(LatLng(latitude,longitude),intensity))
        }

        return data
    }
}
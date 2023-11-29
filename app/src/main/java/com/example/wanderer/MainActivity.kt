package com.example.wanderer

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.heatmaps.HeatmapTileProvider

class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    private var mGoogleMap:GoogleMap? = null;

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext,getString(R.string.google_map_api_key))
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,"some Error in Search",Toast.LENGTH_LONG).show()
            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addMarker(latLng)
                marker.title = "$add"
                marker.snippet = "$id"
                zoomOnMap(latLng)
            }

        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapOptionButtom:ImageButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this , mapOptionButtom)
        popupMenu.menuInflater.inflate(R.menu.map_options,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
                changeMap(menuItem.itemId)
            true    
        }

        mapOptionButtom.setOnClickListener{
            popupMenu.show()
        }

        val fbStreetView = findViewById<FloatingActionButton>(R.id.fbStreetView)
        fbStreetView.setOnClickListener{
            val intent = Intent(this,StreetViewActivity::class.java)
            //intent.putExtra("latitude",latitude)
            //intent.putExtra("longitude",longitude)
            startActivity(intent)
        }
    }

    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun changeMap(itemId: Int) {
        when(itemId){
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        //  Add Marker
            addMarker(LatLng(13.123,12.123))
        // Add draggable marker
        addDraggableMarker(LatLng(12.456, 14.765))

        // Add custom Marker
        addCustomMaker(R.drawable.flag_marker, LatLng(13.999, 12.456))

        // Add PolyLines
        drawLines()
        // Add Polygon
        drawPolygons(Constants.getPolygonCords())

        //Add Image
        addImage()
        //zoomOnMap(LatLng(4.123, 5.123))

        mGoogleMap?.setOnMapClickListener {
            //mGoogleMap?.clear()
           // addMarker(it)
           // addCircle(it)
            addMarker(it)
            latitude = it.latitude
            longitude = it.longitude
        }

        mGoogleMap?.setOnMapClickListener {position ->
            addCustomMaker(R.drawable.flag_marker, position)
        }

        mGoogleMap?.setOnMarkerClickListener {marker ->
            marker.remove()
            false
        }

        mGoogleMap?.setOnPolylineClickListener {
            it.color = ContextCompat.getColor(this, R.color.black)
        }

        mGoogleMap?.setOnPolygonClickListener {
            it.fillColor = ContextCompat.getColor(this,R.color.white)
        }

        addHeatmap()
    }

    private fun addMarker(position:LatLng):Marker
    {
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Marker")
        )

        return marker!!
    }

    private fun addDraggableMarker(position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions()
            .position(LatLng(13.234, 12.543))
            .title("Draggable Marker")
            .draggable(true)
        )
    }

    private fun addCustomMaker(icon:Int, position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Custom Marker")
            .icon(BitmapDescriptorFactory.fromResource(icon))
        )

    }

    private var circle:Circle? = null
    private fun addCircle(centre:LatLng)
    {
        circle?.remove()
        circle = mGoogleMap?.addCircle(CircleOptions()
            .center(centre)
            .radius(1000.0)
            .strokeWidth(8f)
            .strokeColor(Color.parseColor("#FF0000"))
            .fillColor(ContextCompat.getColor( this,R.color.blue))
        )
    }

    private fun drawLines()
    {
        val DOT: PatternItem = Dot()
        val GAP: PatternItem = Gap(20f)
        val PATTERN_POLYLINE_DOTTED = listOf(GAP,DOT)

        val polyLine = mGoogleMap?.addPolyline(PolylineOptions()
            .clickable(true)
            .addAll(Constants.getPolyLineCords())
            .endCap(RoundCap())
            .startCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.flag_marker)))
            .color(ContextCompat.getColor(this,R.color.blue))
            .jointType(JointType.ROUND)
            .width(12f)
            .pattern(PATTERN_POLYLINE_DOTTED)

        )
    }

    private fun drawPolygons(list:ArrayList<LatLng>)
    {
        val DOT: PatternItem = Dot()
        val DASH: PatternItem = Dash(20f)
        val GAP: PatternItem = Gap(20f)
        val strokePatern = listOf(DOT,DASH,DOT,GAP)

        val polygon = mGoogleMap?.addPolygon(PolygonOptions()
            .clickable(true)
            .addAll(list)
            .fillColor(-0xff00ff)
            .strokeColor( -0xffaabb)
            .strokeWidth(15f)
            .strokePattern(strokePatern)
        )
    }

    private fun addImage(){
        mGoogleMap?.addGroundOverlay(GroundOverlayOptions()
            .position(LatLng(4.123, 5.123), 100f)
            .image(BitmapDescriptorFactory.fromResource(R.drawable.gato))
        )
    }

    private fun addHeatmap()
    {
        val heatmapProvider = HeatmapTileProvider.Builder()
            .weightedData(Constants.getWeightedHeatmapData())
            .radius(20)
            .maxIntensity(1000.0)
            .build()
        mGoogleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
    }

}
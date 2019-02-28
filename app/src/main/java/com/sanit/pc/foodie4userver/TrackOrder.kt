package com.sanit.pc.foodie4userver

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.Remote.IGeoCoordinates
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap


class TrackOrder : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var  lattitude:Double = 0.toDouble()
    private var longitude:Double = 0.toDouble()

    private lateinit var mLastLocation:Location
    private  var mMarker: Marker? = null

    //location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
//
    lateinit var mService:IGeoCoordinates
//    internal lateinit var currentPlace:MyPlaces



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mService = Common.getGeoCodeServices()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){

            if(checkSelfPermission()){
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())

            }

        }else{
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        }



    }//onCreate()



    private fun checkSelfPermission():Boolean {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),123)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),123)

            }
            return false

        }else {
            return true
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                mLastLocation = p0!!.lastLocation

                if(mMarker!=null){
                    mMarker!!.remove()
                }

                lattitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                var latlng = LatLng(lattitude,longitude)
                var markeroptions = MarkerOptions()
                    .position(latlng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                mMarker = mMap!!.addMarker(markeroptions)
                //move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10.5f))

                //After add marker for your location, Add marker for this Order and draw route
                drawRoute(latlng, Common.currentRequest.address)


            }
        }

    }

    @SuppressLint("RestrictedApi")
    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    mLastLocation = it

                    if(mMarker!=null){
                        mMarker!!.remove()
                    }

                    lattitude = mLastLocation.latitude
                    longitude = mLastLocation.longitude

                    var latlng = LatLng(lattitude,longitude)
                    var markeroptions = MarkerOptions()
                        .position(latlng)
                        .title("Your Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    mMarker = mMap!!.addMarker(markeroptions)
                    //move camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.5f))

                    //After add marker for your location, Add marker for this Order and draw route
                    drawRoute(latlng, Common.currentRequest.address)

                }


            }


        }else{
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                mLastLocation = it

                if(mMarker!=null){
                    mMarker!!.remove()
                }

                lattitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                var latlng = LatLng(lattitude,longitude)
                var markeroptions = MarkerOptions()
                    .position(latlng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                mMarker = mMap!!.addMarker(markeroptions)
                //move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10.5f))

                //After add marker for your location, Add marker for this Order and draw route
                drawRoute(latlng, Common.currentRequest.address)

            }

        }

    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    if(checkSelfPermission()){
                        buildLocationRequest()
                        buildLocationCallBack()
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())

                        mMap.isMyLocationEnabled = true
                    }
                }else{
                    Toast.makeText(this,"Please enable gps",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun drawRoute(yourLocation: LatLng, address: String) {
//        mService.getGeoCode(address,resources.getString(R.string.google_server_key)).enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                Log.d("Response",response.toString())
//                try {
//                    val jsonObject = JSONObject(response.body()!!.toString())
//                    val lat = (jsonObject.get("results") as JSONArray)
//                        .getJSONObject(0)
//                        .getJSONObject("geometry")
//                        .getJSONObject("location")
//                        .get("lat").toString()
//                    val lng = (jsonObject.get("results") as JSONArray)
//                        .getJSONObject(0)
//                        .getJSONObject("geometry")
//                        .getJSONObject("location")
//                        .get("lng").toString()

        val geoCoder = Geocoder(this)
        val address:List<Address> = geoCoder.getFromLocationName(address,5)
        val lat = address[0].latitude
        val lng = address[0].longitude

        val orderLocation = LatLng(lat, lng)

//                    val orderLocation = LatLng(java.lang.Double.parseDouble(lat), java.lang.Double.parseDouble(lng))
                    var bitmap = BitmapFactory.decodeResource(resources, R.drawable.box)
                    bitmap = Common.scaleBitmap(bitmap, 70, 70)

                    val marker = MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title("Order of" + Common.currentRequest.phone)
                        .position(orderLocation)
                    mMap.addMarker(marker)

                    //draw route
                    mService.getDirections(
                        yourLocation.latitude.toString() + "," + yourLocation.longitude,
                        orderLocation.latitude.toString() + "," + orderLocation.longitude,
                        resources.getString(R.string.google_server_key)
                    )
                        .enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                try {
                                    ParserTask().execute(response.body()!!.toString())
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {

                            }
                        })
//
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }

//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//
//            }
//        })
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
        internal var mDialog = ProgressDialog(this@TrackOrder)
        override fun onPreExecute() {
            super.onPreExecute()
            mDialog.setMessage("Please Waiting...")
            mDialog.show()
        }

        override fun doInBackground(vararg strings: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(strings[0])
                val parser = DirectionJSONParser()
                routes = parser.parse(jObject)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return routes
        }

        override fun onPostExecute(lists: List<List<HashMap<String, String>>>) {
            mDialog.dismiss()

            val points:MutableList<LatLng> = mutableListOf()
            val lineOptions = PolylineOptions()
            for (i in lists.indices) {
                //                points = new ArrayList();
                //                lineOptions = new PolylineOptions();

                val path = lists[i]

                for (j in path.indices) {
                    val point = path[j]

                    val lat = java.lang.Double.parseDouble(point["lat"]!!)
                    val lng = java.lang.Double.parseDouble(point["lng"]!!)
                    val position = LatLng(lat, lng)

                    points.add(position)

                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.BLUE)
                lineOptions.geodesic(true)

            }

            mMap.addPolyline(lineOptions)


        }
    }


}




























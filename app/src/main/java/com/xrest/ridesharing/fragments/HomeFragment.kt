package com.xrest.ridesharing.fragments

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.directions.route.*
import com.xrest.ridesharing.API.RetrofitService
import com.xrest.ridesharing.R
import com.xrest.ridesharing.Repository.UserRepository
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
        RoutingListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationManager:LocationManager
    lateinit var fusedLocationProvideClient:FusedLocationProviderClient
    lateinit var socket:Socket
    private lateinit var origin:LatLng
    lateinit var destination:LatLng
    var polylines:MutableList<Polyline> = mutableListOf()
    private lateinit var floatingActionButton: FloatingActionButton
    lateinit var Lottiedialog:Dialog
    lateinit var lottie:LottieAnimationView
    lateinit var mediaPlayer: MediaPlayer
    lateinit var vibrator:Vibrator
    lateinit var dialog:BottomSheetDialog

    private  var places = mutableListOf<Place.Field>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        Lottiedialog= Dialog(requireContext());
        Lottiedialog.setContentView(R.layout.searching_dialog);
lottie= Lottiedialog.findViewById<LottieAnimationView>(R.id.loading)
        floatingActionButton = root.findViewById(R.id.fabtn)
        if(!Places.isInitialized())
        {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        fusedLocationProvideClient =LocationServices.getFusedLocationProviderClient(requireActivity())
        places.add(Place.Field.NAME)
        places.add(Place.Field.LAT_LNG)
        places.add(Place.Field.ADDRESS)
        places.add(Place.Field.PHOTO_METADATAS)


        fusedLocationProvideClient.lastLocation.addOnSuccessListener {
            if(it!=null)
            {
                Toast.makeText(requireContext(), "${it.latitude}", Toast.LENGTH_SHORT).show()
                mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))

            }
        }
        floatingActionButton.setOnClickListener{
            var intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, places).build(requireContext())
            startActivityForResult(intent, 200)
        }

        try {

            socket = IO.socket("http://192.168.0.108:2021")
            socket.connect()

            var json= JSONObject()
            json.put("message", "Hello Nabin")
            socket.emit("message", json.toString())
            socket.on("LatLang"){
                print("${it[0].toString()}")

            }
            socket.on("message", object : Emitter.Listener {
                override fun call(vararg args: Any?) {
//                    var json = args[0] as JSONObject
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Main) {
                        }
                    }

                }

            })
        }catch (ex: URISyntaxException)
        {

        }

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root


    }
    @SuppressWarnings("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.isMyLocationEnabled=true
        mMap.uiSettings.isZoomControlsEnabled =true
        mMap.setOnMyLocationChangeListener(this)

        if(RetrofitService.user!!.UserType=="User"){
            mMap.setOnMapClickListener(this)
        }


        socket.on("UserRequest"){
            var json = JSONObject(it[0].toString())
            Log.d("Hello",json.toString())
            var riderObj = json.getJSONObject("userDetails")
            var jsonObj = json.getJSONObject("newData")
            //id
            if(RetrofitService.user!!.UserType=="Rider")
            {
                if(riderObj.getString("_id")==RetrofitService.user!!._id)
                {

                    CoroutineScope(Dispatchers.IO).launch {
                        var response = UserRepository().getUser(jsonObj.getString("user_id"))
                        withContext(Main)
                        {
                            vibratePlay()
                            Toast.makeText(context, "${it[0]}", Toast.LENGTH_SHORT).show()
                            val req = Dialog(requireContext())

                            var array:FloatArray =FloatArray(10)

                            Location.distanceBetween( jsonObj.getString("destinationLat").toDouble()
                                    , jsonObj.getString("destinationLong").toDouble()
                                    , jsonObj.getString("latitude").toDouble(),
                                    jsonObj.getString("longitude").toDouble(), array)

                            var destAddress = getAddress(LatLng(jsonObj.getString("destinationLat").toDouble(), jsonObj.getString("destinationLong").toDouble()))
                            var originAddress = getAddress(LatLng(jsonObj.getString("latitude").toDouble(), jsonObj.getString("longitude").toDouble()))
//        val placeImage: ImageView = dialog.findViewById<ImageView>(R.id.idPlace)!!
                            req.setContentView(R.layout.bottomsheet)

                            var place:TextView = req.findViewById<TextView>(R.id.place)!!
                            var price:TextView = req.findViewById<TextView>(R.id.price)!!
                            var distance:TextView = req.findViewById<TextView>(R.id.distance)!!
                            var requestBtn : Button = req.findViewById<Button>(R.id.idRequest)!!
                            var km=(array[0]/1000)

                            place.text = "From ${originAddress} To ${destAddress}"
                            distance.text = km.toString()
                            price.text="User PhoneNumber: ${response.data!!.PhoneNumber}"
                            requestBtn.text="Accept"
                            requestBtn.setOnClickListener(){

                                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${jsonObj.getString("destinationLat").toDouble()},${jsonObj.getString("destinationLong").toDouble()}&mode=l"))
                                intent.setPackage("com.google.android.apps.maps")
                                if(intent.resolveActivity(requireActivity().packageManager)!=null)
                                {
                                    startActivity(intent)
                                }
vibrator.cancel()
                                mediaPlayer.pause()
                                socket.emit("accept",json.toString());
                            }
                            price.text =((km*2)*30).toString()
                            req.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
                            req.show()

                        }
                    }






                }
            }
            else{

socket.on("accept"){
    var json = JSONObject(it[0].toString())
    Log.d("Hello",json.toString())
    var riderObj = json.getJSONObject("userDetails")
    var jsonObj = json.getJSONObject("newData")
    if(jsonObj.getString("user_id")==RetrofitService!!.user!!._id)
    {
        CoroutineScope(Dispatchers.IO).launch {
    var response = UserRepository().getUser(riderObj.getString("_id"))
            delay(5000);
            if(response.success==true)
            {
                withContext(Main)
                {
                    Lottiedialog.cancel()
                    lottie.setAnimation(R.raw.rider_found)
                    lottie.playAnimation()
                }
                delay(5000);
                withContext(Main)
                {
                    lottie.loop(false);
                    Lottiedialog.cancel()


                }
                withContext(Main)
                {
                    var riderDialog = Dialog(requireContext())
                    riderDialog.setContentView(R.layout.bottomsheet)


//        val placeImage: ImageView = dialog.findViewById<ImageView>(R.id.idPlace)!!
                    var place:TextView = riderDialog.findViewById<TextView>(R.id.place)!!
                    var price:TextView = riderDialog.findViewById<TextView>(R.id.price)!!
                    var distance:TextView = riderDialog.findViewById<TextView>(R.id.distance)!!
                    var requestBtn : Button = riderDialog.findViewById<Button>(R.id.idRequest)!!
                    place.text ="Rider: ${response.data!!.FullName}"
                    price.text ="Rider Number:${response.data!!.PhoneNumber}"
                    distance.text=""
                    requestBtn.setText("OK")
                    requestBtn.setOnClickListener(){
                        riderDialog.cancel()
                    }
                    riderDialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT)
                    riderDialog.show()

                }
            }

        }




    }

}



            }


        }




    }

    private fun getAddress(latLng: LatLng): String {
        var address = Geocoder(requireContext(), Locale.getDefault())
        var addressLine =address.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return addressLine[0].getAddressLine(0).toString()

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && data!=null)
        {
            if(requestCode==200 && data!=null){
                mMap.clear()
                var place= Autocomplete.getPlaceFromIntent(data)
                polylines.clear()
                findRoutes(origin, place.latLng!!)
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "${place.address}", Toast.LENGTH_SHORT).show()
                    }
                }



                mMap.addMarker(MarkerOptions().position(place.latLng).title(place.address))

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().zoom(14f).target(place.latLng!!).build()))
            }
        }

    }
    private fun findRoutes(origin: LatLng, destination: LatLng) {


        if(origin ==null || destination ==null)
        {
            Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        }
        else{
            var routes = Routing.Builder().
            travelMode(AbstractRouting.TravelMode.DRIVING)
                    .key(getString(R.string.google_maps_key))
                    .withListener(this)
                    .waypoints(origin, destination)
                    .build()
            routes.execute()
        }
    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onRoutingFailure(p0: RouteException?) {

    }

    override fun onRoutingStart() {

    }

    override fun onRoutingSuccess(p0: ArrayList<Route>?, p1: Int) {
        if (polylines != null) {
            polylines!!.clear()
        }
        var polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polylines = mutableListOf()

        for( i in 0..p0!!.size -1 ) {
            if (i == p1)
            {
                polyOptions.color(resources.getColor(R.color.black))
                polyOptions.width(7f)
                polyOptions.addAll(p0.get(p1).points)
                var pLine = mMap.addPolyline((polyOptions))
                polylineStartLatLng =pLine.points.get(0)
                var k:Int = pLine.points.size
                polylineEndLatLng = pLine.points.get(k - 1)
                polylines!!.add(pLine)

            }
            else{


            }


        }
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng)
        startMarker.title("My Location")
        mMap.addMarker(startMarker)


        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng)
        endMarker.title("Destination")
        mMap.addMarker(endMarker)
    }

    override fun onRoutingCancelled() {

    }

    override fun onMapClick(p0: LatLng) {
        mMap.clear()
        polylines.clear()
        var array:FloatArray =FloatArray(10)
        Location.distanceBetween(origin.latitude, origin.longitude, p0.latitude, p0.longitude, array)
        Toast.makeText(requireContext(), "${array[0]}", Toast.LENGTH_SHORT).show()
        findRoutes(origin, p0)
         dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.bottomsheet)

//        val placeImage: ImageView = dialog.findViewById<ImageView>(R.id.idPlace)!!
        var place:TextView = dialog.findViewById<TextView>(R.id.place)!!
        var price:TextView = dialog.findViewById<TextView>(R.id.price)!!
        var distance:TextView = dialog.findViewById<TextView>(R.id.distance)!!
        var requestBtn : Button = dialog.findViewById<Button>(R.id.idRequest)!!
        var km=(array[0]/1000)


        requestBtn.setOnClickListener{
            var json= JSONObject()
            json.put("user_id",RetrofitService.user!!._id.toString())
            json.put("latitude", RetrofitService.user!!.Latitude.toString())
            json.put("longitude", RetrofitService.user!!.Longitude.toString())
            json.put("destinationLat", p0.latitude.toString())
            json.put("destinationLong", p0.longitude.toString())
            socket.emit("UserRequest", json.toString())

            Lottiedialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT)
            Lottiedialog.show()
            Lottiedialog.setCancelable(false)


        }

        distance.text ="Distance: "+ (array[0]/1000).toString()+ "KM"
        price.text ="Price: Rs"+ (km*25).toString()

        try {
            place.text="Destination: " + getAddress(p0)
        }catch (ex: Exception){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "${ex.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(true)
        dialog.show()

    }

    override fun onMyLocationChange(it: Location) {
        var json =JSONObject()
        json.put("Latitude", it.latitude)
        json.put("Longitude", it.longitude)
        socket.emit("LatLang", json.toString())
        origin = LatLng(it.latitude, it.longitude)
        RetrofitService.user!!.Latitude = origin.latitude.toString()

        RetrofitService.user!!.Longitude = origin.latitude.toString()
        if(RetrofitService.user!!.UserType == "Rider"){
            var json= JSONObject()
            json.put("_id",RetrofitService.user!!._id.toString())
            json.put("latitude", RetrofitService.user!!.Latitude.toString())
            json.put("longitude", RetrofitService.user!!.Longitude.toString())
            socket.on("RiderLatLang", ){
              var jsons = it[0].toString()
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Main) {
//                        Toast.makeText(context, "${jsons}", Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
            socket.emit("RiderLatLang", json.toString())

        }


//        socket.on("LatLang", object : Emitter.Listener {
//            override fun call(vararg args: Any?) {
//                var json = JSONObject(args[0].toString())
//                var lat = json.getString("Latitude")
//                var lon = json.getString("Longitude")
//                CoroutineScope(Dispatchers.Main).launch {
//                    withContext(Dispatchers.Main) {
//                        mMap.addMarker(MarkerOptions().title("Current Location").position(LatLng(lat.toDouble(), lon.toDouble())))
//                    }
//                }
//
//            }
//
//        })


    }

    @SuppressWarnings("MissingPermission")
    fun vibratePlay(){
         vibrator = requireContext().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200000, VibrationEffect.DEFAULT_AMPLITUDE))
            mediaPlayer = MediaPlayer.create(requireContext(),R.raw.ringtone)
            mediaPlayer.start()
            Toast.makeText(requireContext(),"media playing",Toast.LENGTH_SHORT).show()
        } else {
            vibrator.vibrate(200000)
        }
    }

    override fun onResume() {

        var actioBar = (requireActivity() as AppCompatActivity).supportActionBar
        actioBar!!.hide()
        var permissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
        for(permission in permissions)
        {
            if(ActivityCompat.checkSelfPermission(requireContext(), permission)!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(requireActivity(), permissions, 1)
            }
        }
        super.onResume()
    }



}
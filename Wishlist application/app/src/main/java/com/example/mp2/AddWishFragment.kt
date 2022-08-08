package com.example.mp2

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.navigation.fragment.findNavController
import com.example.mp2.data.Wish
import com.example.mp2.data.WishDatabase
import com.example.mp2.databinding.FragmentWishAddBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddWishFragment : Fragment() {
    private var wishID: Int? = null
    private var isEdit: Boolean? = null
    private var _binding: FragmentWishAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wishID = it.getInt("wishID")
            isEdit = it.getBoolean("isEdit")
        }
    }
    lateinit var locationManager: LocationManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWishAddBinding.inflate(inflater, container, false)
        binding.isEdit = isEdit
        locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        if (isEdit == true) {
            thread {
                var wish = WishDatabase.db().wishDao().loadAllByIds(wishID!!)[0]
                binding.wish = wish
                getMap().changeLocation(wish.getLatLng())
                val resolver = binding.root.context.contentResolver
                val uri = Uri.parse(wish!!.imgPath)
                this.requireActivity().runOnUiThread {
                    try {
                        binding.imagePreview.setImageBitmap(
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    resolver,
                                    uri
                                )
                            )
                        )
                    }
                    catch(e: Exception) {
                        binding.imagePreview.setImageResource(R.drawable.not_found)
                    }
                }


            }




        } else {
            binding.wish = Wish(0, "", LocalDateTime.now(), "", "", "","")
        }

        return binding.root

    }
    private fun getMap() = binding.map.getFragment<MapsFragment>()
    fun updateLocation() {
        binding.wish!!.mapLat =
            getMap().currentLocation.latitude.toString()
        binding.wish!!.mapLng =
            getMap().currentLocation.longitude.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==201){
            for( i in grantResults) {
                var decision = true
                if (i == PERMISSION_DENIED) {
                    decision = false
                }
                if (decision) {
                    getCurrentLocation()
                }
                else{
                    Snackbar.make(requireView(),"Musisz dać uprawniwnenie",500).show()
                }
            }
        }
    }
    var listener = LocationListener {
        getMap().changeLocation(LatLng(it.latitude,it.longitude))
        updateLocation()
        updateLocationName()
        println("WYWOLANO METODE")

    }
    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),201)

            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0f,listener )


    }
    fun stopUpdates() {
        locationManager.removeUpdates(listener)
    }


    fun takePicture(baseContext: Context) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 200)
    }
    private var imgBitmap: Bitmap? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null){
            imgBitmap = data.extras?.get("data") as Bitmap
            //
            isChanged = true
            binding.imagePreview.setImageBitmap(imgBitmap)
        }
    }
    private var isChanged = false
    @Throws(IOException::class)
    fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")

            return uri

        } catch (e: IOException) {

            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(orphanUri, null, null)
            }

            throw e
        }
    }
    fun updateLocationName() {
        binding.fieldAddress.setText(
            Wish.getAddressName(
                requireView().context,
                getMap().currentLocation
            )
        )
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLocationName()
        binding.btnPhoto.setOnClickListener {

            context?.let { it1 -> takePicture(it1) }
        }
        binding.btnReset.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnSave.setOnClickListener {

            if(isEdit==false && isChanged == false) {
                Snackbar.make(this.requireContext(),this.requireView(),"Nie mozesz nie zrobić djęcia!!!",10).show()

            }
            else {
                updateLocation()
                if (isChanged) {
                    val uri = saveBitmap(this.requireContext(),imgBitmap!!,Bitmap.CompressFormat.JPEG,"image/jpeg","picture")
                    binding.wish!!.imgPath=uri.toString()
                }
                if (isEdit == true) {
                    thread {
                        WishDatabase.db().wishDao().update(binding.wish!!)
                    }

                } else {
                    binding.wish!!.added = LocalDateTime.now()
                    thread {
                        WishDatabase.db().wishDao().insertAll(binding.wish!!)
                    }
                }


                findNavController().navigate(R.id.wishAdd_wishList)
            }

        }
        binding.btnExit.setOnClickListener {
            if (isEdit == true) {
                thread {
                    WishDatabase.db().wishDao().delete(binding.wish!!)
                }
            } else {

            }
            findNavController().navigate(R.id.wishAdd_wishList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopUpdates()
        _binding = null
    }


}
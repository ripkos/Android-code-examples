package com.example.mp2.data
import android.content.Context
import android.location.Geocoder
import java.time.LocalDateTime
import androidx.room.*;
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "wish")
data class Wish(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "name") var name:String,
    @ColumnInfo(name = "added") var added:LocalDateTime,

    //img
    @ColumnInfo(name = "img_path") var imgPath:String,

    //map pos
    @ColumnInfo(name = "map_lat") var mapLat:String,
    @ColumnInfo(name = "map_lng") var mapLng:String,
    @ColumnInfo(name = "desc") var desc:String,


    ) {
    companion object{
        fun getAddressName(context: Context, currentLocation:LatLng) :String {
            val geocoder = Geocoder(context)
            val address = geocoder.getFromLocation(currentLocation.latitude,currentLocation.longitude,1)[0]
            return address.getAddressLine(0)// + ", " + address.adminArea
        }

    }

    fun getLatLng(): LatLng {
        return LatLng(mapLat.toDouble(),mapLng.toDouble())
    }

}

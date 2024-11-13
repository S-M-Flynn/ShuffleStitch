package ca.unb.mobiledev.shufflestitch

import android.os.Parcel
import android.os.Parcelable

data class Item(
    val id: Long,
    val path: String,
    val tops: Boolean,
    val bottoms: Boolean,
    val fullBody: Boolean,
    val shoes: Boolean,
    val casual: Boolean,
    val professional: Boolean,
    val formal: Boolean,
    val athletic: Boolean
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}


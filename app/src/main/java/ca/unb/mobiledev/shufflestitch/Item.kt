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
    val outerWear: Boolean,
    val accessories: Boolean,
    val casual: Boolean,
    val professional: Boolean,
    val formal: Boolean,
    val athletic: Boolean,
    val winter: Boolean,
    val fall: Boolean,
    val spring: Boolean,
    val summer: Boolean,
    val count: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong()?: 0,
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()?: 0
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(path)
        parcel.writeByte(if (tops) 1 else 0)
        parcel.writeByte(if (bottoms) 1 else 0)
        parcel.writeByte(if (fullBody) 1 else 0)
        parcel.writeByte(if (shoes) 1 else 0)
        parcel.writeByte(if (outerWear) 1 else 0)
        parcel.writeByte(if (accessories) 1 else 0)
        parcel.writeByte(if (casual) 1 else 0)
        parcel.writeByte(if (professional) 1 else 0)
        parcel.writeByte(if (formal) 1 else 0)
        parcel.writeByte(if (athletic) 1 else 0)
        parcel.writeByte(if (winter) 1 else 0)
        parcel.writeByte(if (fall) 1 else 0)
        parcel.writeByte(if (spring) 1 else 0)
        parcel.writeByte(if (summer) 1 else 0)
        parcel.writeInt(count)
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}


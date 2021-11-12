package com.example.to_doapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var id: Int ?= 0,
    var title: String,
    var isCompleted: Boolean = false
) : Parcelable
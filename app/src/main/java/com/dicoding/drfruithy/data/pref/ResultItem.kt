package com.dicoding.drfruithy.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultItem(
    val title: String,
    val diseaseName: String,
    val imageUri: String,
    val description: String,
    val handling: String,
    val treatment: String
): Parcelable

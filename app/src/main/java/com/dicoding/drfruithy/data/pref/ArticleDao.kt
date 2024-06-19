package com.dicoding.drfruithy.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleDao(
    var image: String ?= null,
    var nama_penyakit: String ?= null,
    var deskripsi_penyakit: String ?= null,
    var penanganan: String ?= null,
    var pengobatan: String ?= null
): Parcelable
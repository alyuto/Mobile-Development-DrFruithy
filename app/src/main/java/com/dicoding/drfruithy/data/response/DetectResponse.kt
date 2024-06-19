package com.dicoding.drfruithy.data.response

import com.fasterxml.jackson.annotation.JsonProperty

class DetectResponse (

    @field:JsonProperty("data")
    val data: Data? = null,

    @field:JsonProperty("message")
    val message: String? = null,

    @field:JsonProperty("status")
    val status: String? = null
)

data class Data(

    @field:JsonProperty("disease")
    val disease: String? = null,

    @field:JsonProperty("confidence")
    val confidence: Double? = null
)
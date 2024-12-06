package com.example.cornlab.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("recom")
    val event: Recom? = null
)

data class Recom(

    @field:SerializedName("summary")
    val summary: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("imageCover")
    val imageCover: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("steps")
    val steps: List<String?>? = null

)

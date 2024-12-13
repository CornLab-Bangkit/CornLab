package com.example.cornlab.data.response

import com.google.gson.annotations.SerializedName

data class RecomendationsResponse(

	@field:SerializedName("listRecoms")
	val listRecoms: List<ListRecomsItem> = listOf(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListRecomsItem(

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
	val steps: String? = null
)

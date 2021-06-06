package com.korefu.developerslife

import com.google.gson.annotations.SerializedName

data class Entry(
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("gifURL") val gifURL: String?
)

data class DevelopersLifeResponse(
    @field:SerializedName("result") val result: Array<Entry>
)
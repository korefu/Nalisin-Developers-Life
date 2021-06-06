package com.korefu.developerslife

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DevelopersLifeService {

    @GET("{type}/{page}?json=true")
    suspend fun getEntries(
        @Path(value = "type", encoded = true) type: String,
        @Path(value = "page", encoded = true) page: Int
    ): Response<DevelopersLifeResponse>
}
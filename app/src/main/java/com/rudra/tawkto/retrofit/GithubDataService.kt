package com.rudra.tawkto.retrofit

import com.rudra.tawkto.room.UserEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubDataService {
    @GET("users?")
    fun getUsersList (@Query("since") last_id: String) : Call<ArrayList<UserEntity>>

    @GET("users/{login}")
    fun getUserProfile (@Path("login") login: String) : Call<UserEntity>
}
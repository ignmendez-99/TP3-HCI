package com.example.ultrahome.apiConnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // HOME CONTROLS
    @POST("homes")
    @Headers("Content-Type: application/json")
    Call<Result<Home>> addHome(@Body Home home);

    @PUT("homes/{homeId}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> modifyHome(@Path("homeId") String homeId, @Body Home home);

    @DELETE("homes/{homeId}")
    Call<Result<Boolean>> deleteHome(@Path("homeId") String homeId);

    @GET("homes/{homeId}")
    Call<Result<Home>> getHome(@Path("homeId") String homeId);

    @GET("homes")
    Call<Result<List<Home>>> getHomes();

    // ROOM CONTROLS
    @POST("rooms")
    @Headers("Content-Type: application/json")
    Call<Result<Room>> addRoom(@Body Room room);

    @PUT("rooms/{roomId}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> modifyRoom(@Path("roomId") String roomId, @Body Room room);

    @DELETE("rooms/{roomId}")
    Call<Result<Boolean>> deleteRoom(@Path("roomId") String roomId);

    @GET("rooms/{roomId}")
    Call<Result<Room>> getRoom(@Path("roomId") String roomId);

    @GET("rooms")
    Call<Result<List<Room>>> getRooms();
}

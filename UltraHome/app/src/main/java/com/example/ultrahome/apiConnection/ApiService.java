package com.example.ultrahome.apiConnection;

import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Room;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.faucet.FaucetState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator.RefrigeratorState;

import java.util.ArrayList;
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

    ////////////// HOME CONTROLS ////////////////////

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

    ////////////// ROOM CONTROLS ////////////////////

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

    ////////////// DEVICE GENERAL CONTROLS ////////////////////

    @POST("devices")
    @Headers("Content-Type: application/json")
    Call<Result<Device>> addDevice(@Body Device device);

    @GET("devices")
    Call<Result<List<Device>>> getDevices();

    @DELETE("devices/{deviceId}")
    Call<Result<Boolean>> deleteDevice(@Path("deviceId") String deviceId);

    @GET("devices/{deviceId}/state")
    Call<Result<DeviceState>> getDeviceState(@Path("deviceId") String deviceId);

//    TODO: UNUSED AS IT DIDN'T WORK

    ////////////// HOME-ROOM CONTROLS ////////////////////

    @GET("homes/{homeId}/rooms")
    Call<Result<List<Room>>> getRoomsInThisHome(@Path("homeId") String homeId);

    @POST("homes/{homeId}/rooms/{roomId}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> linkRoomWithHome(@Path("homeId") String homeId, @Path("roomId") String roomId);

    ////////////// ROOM-DEVICE CONTROLS ////////////////////

    @GET("rooms/{roomId}/devices")
    Call<Result<List<Device>>> getDevicesInThisRoom(@Path("roomId") String roomId);

    @POST("rooms/{roomId}/devices/{deviceId}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> linkDeviceWithHome(@Path("roomId") String roomId, @Path("deviceId") String deviceId);

    ////////////// DOOR CONTROLS ////////////////////

    @GET("devices/{deviceId}/state")
    Call<Result<DoorState>> getDoorState(@Path("deviceId") String deviceId);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> executeActionOnDoor(@Path("deviceId") String deviceId, @Path("actionName") String actionName);

    ////////////// BLINDS CONTROLS ////////////////////

    @GET("devices/{deviceId}/state")
    Call<Result<BlindsState>> getBlindsState(@Path("deviceId") String deviceId);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> openOrCloseBlinds(@Path("deviceId") String deviceId, @Path("actionName") String actionName);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Integer>> changeBlindsLevel(@Path("deviceId") String deviceId, @Path("actionName") String actionName, @Body Integer [] data);

    ////////////// FAUCET CONTROLS ////////////////////

    @GET("devices/{deviceId}/state")
    Call<Result<FaucetState>> getFaucetState(@Path("deviceId") String deviceId);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> openOrCloseFaucet(@Path("deviceId") String deviceId, @Path("actionName") String actionName);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> dispenseExactAmount(@Path("deviceId") String deviceId, @Path("actionName") String actionName, @Body ArrayList<Object> data);    // TODO: FIX THIS WITH HELP OF ENGINEER ALBERTO

    ////////////// REFRIGERATOR CONTROLS ////////////////////

    @GET("devices/{deviceId}/state")
    Call<Result<RefrigeratorState>> getRefrigeratorState(@Path("deviceId") String deviceId);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Boolean>> changeRefrigeratorMode(@Path("deviceId") String deviceId, @Path("actionName") String actionName, @Body String [] data);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Integer>> setFridgeTemp(@Path("deviceId") String deviceId, @Path("actionName") String actionName, @Body Integer [] data);

    @PUT("devices/{deviceId}/{actionName}")
    @Headers("Content-Type: application/json")
    Call<Result<Integer>> setFreezerTemp(@Path("deviceId") String deviceId, @Path("actionName") String actionName, @Body Integer [] data);
}

package com.example.ultrahome.apiConnection;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Lights;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.ErrorResult;
import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Room;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private Retrofit retrofit;
    private ApiService service;
    private static ApiClient instance = null;
    // Use IP 10.0.2.2 instead of 127.0.0.1 when running Android emulator in the
    // same computer that runs the API.
    private final String BaseURL = "http://10.0.2.2:8080/api/";

    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public Error getError(ResponseBody response) {
        Converter<ResponseBody, ErrorResult> errorConverter =
                this.retrofit.responseBodyConverter(ErrorResult.class, new Annotation[0]);
        try {
            ErrorResult responseError = errorConverter.convert(response);
            return responseError.getError();
        } catch (IOException e) {
            return null;
        }
    }

    ////////////// HOME CALLS ////////////////////

    public Call<Result<Home>> addHome(Home home, Callback<Result<Home>> callback) {
        Call<Result<Home>> call = this.service.addHome(home);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<List<Home>>> getHomes(Callback<Result<List<Home>>> callback) {
        Call<Result<List<Home>>> call = this.service.getHomes();
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> deleteHome(String homeId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.deleteHome(homeId);
        call.enqueue(callback);
        return call;
    }

    ////////////// ROOM CALLS ////////////////////

    public Call<Result<Room>> addRoom(Room room, Callback<Result<Room>> callback) {
        Call<Result<Room>> call = this.service.addRoom(room);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> modifyRoom(Room room, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.modifyRoom(room.getId(), room);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> deleteRoom(String roomId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.deleteRoom(roomId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Room>> getRoom(String roomId, Callback<Result<Room>> callback) {
        Call<Result<Room>> call = this.service.getRoom(roomId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<List<Room>>> getRooms(Callback<Result<List<Room>>> callback) {
        Call<Result<List<Room>>> call = this.service.getRooms();
        call.enqueue(callback);
        return call;
    }

    ////////////// DEVICE GENERAL CALLS ////////////////////

    public Call<Result<Lights>> addDevice(Lights device, Callback<Result<Lights>> callback) {
        Call<Result<Lights>> call = this.service.addDevice(device);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<List<Lights>>> getDevices(Callback<Result<List<Lights>>> callback) {
        Call<Result<List<Lights>>> call = this.service.getDevices();
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> deleteDevice(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.deleteDevice(deviceId);
        call.enqueue(callback);
        return call;
    }

    ////////////// HOME-ROOM CALLS ////////////////////

    public Call<Result<List<Room>>> getRoomsInThisHome(String homeId, Callback<Result<List<Room>>> callback) {
        Call<Result<List<Room>>> call = this.service.getRoomsInThisHome(homeId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> linkRoomWithHome(String homeId, String roomId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.linkRoomWithHome(homeId, roomId);
        call.enqueue(callback);
        return call;
    }

    ////////////// ROOM-DEVICE CALLS ////////////////////

    public Call<Result<List<Lights>>> getDevicesInThisRoom(String roomId, Callback<Result<List<Lights>>> callback) {
        Call<Result<List<Lights>>> call = this.service.getDevicesInThisRoom(roomId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> linkDeviceWithRoom(String roomId, String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.linkDeviceWithHome(roomId, deviceId);
        call.enqueue(callback);
        return call;
    }
}

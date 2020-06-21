package com.example.ultrahome.apiConnection;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.Door;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.Lights;
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

    public Call<Result<Device>> addDevice(Device device, Callback<Result<Device>> callback) {
        Call<Result<Device>> call = this.service.addDevice(device);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<List<Device>>> getDevices(Callback<Result<List<Device>>> callback) {
        Call<Result<List<Device>>> call = this.service.getDevices();
        call.enqueue(callback);
        return call;
    }

    public Call<Result<DeviceState>> getDeviceState(String deviceId, Callback<Result<DeviceState>> callback) {
        Call<Result<DeviceState>> call = this.service.getDeviceState(deviceId);
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

    public Call<Result<List<Device>>> getDevicesInThisRoom(String roomId, Callback<Result<List<Device>>> callback) {
        Call<Result<List<Device>>> call = this.service.getDevicesInThisRoom(roomId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> linkDeviceWithRoom(String roomId, String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.linkDeviceWithHome(roomId, deviceId);
        call.enqueue(callback);
        return call;
    }

    ////////////// DOOR CALLS ////////////////////

    public Call<Result<DoorState>> getDoorState(String deviceId, Callback<Result<DoorState>> callback) {
        Call<Result<DoorState>> call = this.service.getDoorState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> openDoor(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.executeActionOnDoor(deviceId, "open");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> closeDoor(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.executeActionOnDoor(deviceId, "close");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> lockDoor(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.executeActionOnDoor(deviceId, "lock");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> unlockDoor(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.executeActionOnDoor(deviceId, "unlock");
        call.enqueue(callback);
        return call;
    }
}

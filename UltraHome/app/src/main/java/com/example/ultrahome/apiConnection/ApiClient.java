package com.example.ultrahome.apiConnection;

import com.example.ultrahome.apiConnection.entities.Routine.Routine;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceTypeComplete;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.Door;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.faucet.FaucetState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.LightState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.Lights;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.ErrorResult;
import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Room;
import com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator.RefrigeratorState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum.VacuumState;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
    private final String BaseURL = "http://10.0.2.2:8081/api/";

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

    ////////////// DEVICE-TYPE CALLS ////////////////////

    public Call<Result<DeviceTypeComplete>> getDeviceType(String deviceTypeId, Callback<Result<DeviceTypeComplete>> callback){
        Call<Result<DeviceTypeComplete>> call = this.service.getDeviceType(deviceTypeId);
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

    ////////////// ROUTINE CALLS ////////////////////

    public Call<Result<List<Routine>>> getRoutines(Callback<Result<List<Routine>>> callback){
        Call<Result<List<Routine>>> call = this.service.getRoutines();
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Routine>> addRoutine(Routine routine, Callback<Result<Routine>> callback){
        Call<Result<Routine>> call = this.service.addRoutine(routine);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> deleteRoutine(String routineId, Callback<Result<Boolean>> callback){
        Call<Result<Boolean>> call = this.service.deleteRoutine(routineId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> getRoutine(String routineId, Callback<Result<Boolean>> callback){
        Call<Result<Boolean>> call = this.service.deleteRoutine(routineId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> modifyRoutine(String routineId, Routine routine, Callback<Result<Boolean>> callback){
        Call<Result<Boolean>> call = this.service.modifyRoutine(routineId, routine);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<List<Boolean>>> executeRoutine(String routineId, Callback<Result<List<Boolean>>> callback){
        Call<Result<List<Boolean>>> call = this.service.executeRoutine(routineId);
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

    ////////////// BLINDS CALLS ////////////////////

    public Call<Result<BlindsState>> getBlindsState(String deviceId, Callback<Result<BlindsState>> callback) {
        Call<Result<BlindsState>> call = this.service.getBlindsState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> openBlinds(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.openOrCloseBlinds(deviceId, "open");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> closeBlinds(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.openOrCloseBlinds(deviceId, "close");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Integer>> setBlindsLevel(String deviceId, Integer newLevel, Callback<Result<Integer>> callback) {
        Integer [] aux = new Integer[1];
        aux[0] = newLevel;

        Call<Result<Integer>> call = this.service.changeBlindsLevel(deviceId, "setLevel", aux);
        call.enqueue(callback);
        return call;
    }

    ////////////// FAUCET CALLS ////////////////////

    public Call<Result<FaucetState>> getFaucetState(String deviceId, Callback<Result<FaucetState>> callback) {
        Call<Result<FaucetState>> call = this.service.getFaucetState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> openFaucet(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.openOrCloseFaucet(deviceId, "open");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> closeFaucet(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.openOrCloseFaucet(deviceId, "close");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> dispenseExactAmount(String deviceId, Integer amount, String unit, Callback<Result<Boolean>> callback) {    // TODO: FIX THIS WITH HELP OF ENGINEER ALBERTO
        ArrayList<Object> aux = new ArrayList<>();
        aux.add(amount);
        aux.add(unit);
        Call<Result<Boolean>> call = this.service.dispenseExactAmount(deviceId, "setLevel", aux);
        call.enqueue(callback);
        return call;
    }

    ////////////// REFRIGERATOR CALLS ////////////////////

    public Call<Result<RefrigeratorState>> getRefrigeratorState(String deviceId, Callback<Result<RefrigeratorState>> callback) {
        Call<Result<RefrigeratorState>> call = this.service.getRefrigeratorState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> changeRefrigeratorMode(String deviceId, String newMode, Callback<Result<Boolean>> callback) {
        String [] aux = new String[1];
        aux[0] = newMode;

        Call<Result<Boolean>> call = this.service.changeRefrigeratorMode(deviceId, "setMode", aux);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Integer>> setFridgeTemp(String deviceId, Integer newTemp, Callback<Result<Integer>> callback) {
        Integer [] aux = new Integer[1];
        aux[0] = newTemp;

        Call<Result<Integer>> call = this.service.setFridgeTemp(deviceId, "setTemperature", aux);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Integer>> setFreezerTemp(String deviceId, Integer newTemp, Callback<Result<Integer>> callback) {
        Integer [] aux = new Integer[1];
        aux[0] = newTemp;

        Call<Result<Integer>> call = this.service.setFreezerTemp(deviceId, "setFreezerTemperature", aux);
        call.enqueue(callback);
        return call;
    }

    ////////////// LIGHT CALLS ////////////////////

    public Call<Result<LightState>> getLightState(String deviceId, Callback<Result<LightState>> callback) {
        Call<Result<LightState>> call = this.service.getLightState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> turnOnLight(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.turnOnOrOffLight(deviceId, "turnOn");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Boolean>> turnOffLight(String deviceId, Callback<Result<Boolean>> callback) {
        Call<Result<Boolean>> call = this.service.turnOnOrOffLight(deviceId, "turnOff");
        call.enqueue(callback);
        return call;
    }

    public Call<Result<Integer>> setLightBrightness(String deviceId, Integer newBrightness, Callback<Result<Integer>> callback) {
        Integer [] aux = new Integer[1];
        aux[0] = newBrightness;

        Call<Result<Integer>> call = this.service.setLightBrightness(deviceId, "setBrightness", aux);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<String>> setLightColor(String deviceId, String newColor, Callback<Result<String>> callback) {
        String [] aux = new String[1];
        aux[0] = newColor;

        Call<Result<String>> call = this.service.setLightColor(deviceId, "setColor", aux);
        call.enqueue(callback);
        return call;
    }

    ////////////// VACUUM CALLS ////////////////////

    public Call<Result<VacuumState>> getVacuumState(String deviceId, Callback<Result<VacuumState>> callback) {
        Call<Result<VacuumState>> call = this.service.getVacuumState(deviceId);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<String>> setVacuumMode(String deviceId, String newMode, Callback<Result<String>> callback) {
        String [] aux = new String[1];
        aux[0] = newMode;

        Call<Result<String>> call = this.service.setLightColor(deviceId, "setMode", aux);
        call.enqueue(callback);
        return call;
    }

    public Call<Result<String>> setVacuumState(String deviceId, String newState, Callback<Result<String>> callback) {
        Call<Result<String>> call;
        switch (newState) {
            case "start":
                call = this.service.setVacuumMode(deviceId, "start");
                break;
            case "pause":
                call = this.service.setVacuumMode(deviceId, "pause");
                break;
            case "dock":
                call = this.service.setVacuumMode(deviceId, "dock");
                break;
            default:
                call = this.service.setVacuumMode(deviceId, "start");
                break;

        }
        call.enqueue(callback);
        return call;
    }

    public Call<Result<String>> setVacuumLocation(String deviceId, String newLocation, Callback<Result<String>> callback) {
        String [] aux = new String[1];
        aux[0] = newLocation;

        Call<Result<String>> call = this.service.setLightColor(deviceId, "setLocation", aux);
        call.enqueue(callback);
        return call;
    }
}

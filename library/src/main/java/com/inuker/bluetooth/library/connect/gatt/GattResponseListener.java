package com.inuker.bluetooth.library.connect.gatt;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface GattResponseListener {

    int GATT_RESP_SERVICE_DISCOVER = 1;

    int GATT_RESP_CHARACTER_READ = 2;

    int GATT_RESP_CHARACTER_WRITE = 3;

    int GATT_RESP_DESCRIPTOR_WRITE = 4;

    int GATT_RESP_READ_RSSI = 5;
}

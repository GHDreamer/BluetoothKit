package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

import java.util.UUID;

/**
 * Created by liwentian on 2016/8/24.
 */
public interface IBleConnectMaster {

    void connect(BluetoothResponse response);

    void disconnect();

    void read(UUID service, UUID character, BluetoothResponse response);

    void write(UUID service, UUID character, byte[] bytes, BluetoothResponse response);

    void notify(UUID service, UUID character, BluetoothResponse response);

    void unnotify(UUID service, UUID character, BluetoothResponse response);

    void readRssi(BluetoothResponse response);
}

package com.inuker.bluetooth.library.connect;

/**
 * Created by liwentian on 2016/8/23.
 */
public interface IBluetoothApi {

    int CODE_CONNECT = 1;
    int CODE_DISCONNECT = 2;
    int CODE_READ = 3;
    int CODE_WRITE = 4;
    int CODE_NOTIFY = 5;
    int CODE_UNNOTIFY = 6;
    int CODE_READ_RSSI = 7;
}

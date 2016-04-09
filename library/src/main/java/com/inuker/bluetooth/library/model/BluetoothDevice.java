package com.inuker.bluetooth.library.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ApiLevel:5 蓝牙设备
 */
public class BluetoothDevice implements Parcelable {

    public android.bluetooth.BluetoothDevice device;

    public int rssi;

    public boolean isConnected;

    public byte[] scanRecord;

    public int deviceType;

    public String name;

    public static final int DEVICE_TYPE_CLASSIC = 1;

    public static final int DEVICE_TYPE_BLE = 2;

    public BluetoothDevice() {

    }

    public BluetoothDevice(android.bluetooth.BluetoothDevice device, int deviceType) {
        this.device = device;
        this.deviceType = deviceType;
    }

    public BluetoothDevice(android.bluetooth.BluetoothDevice device, int rssi, byte[] scanRecord, int deviceType) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("name = " + name);
        sb.append(", mac = " + device.getAddress());
        sb.append(", connected = " + isConnected);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, 0);
        dest.writeInt(this.rssi);
        dest.writeByte(isConnected ? (byte) 1 : (byte) 0);
        dest.writeByteArray(this.scanRecord);
        dest.writeInt(this.deviceType);
        dest.writeString(this.name);
    }

    public BluetoothDevice(Parcel in) {
        this.device = in.readParcelable(android.bluetooth.BluetoothDevice.class.getClassLoader());
        this.rssi = in.readInt();
        this.isConnected = in.readByte() != 0;
        this.scanRecord = in.createByteArray();
        this.deviceType = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<BluetoothDevice> CREATOR = new Creator<BluetoothDevice>() {
        public BluetoothDevice createFromParcel(Parcel source) {
            return new BluetoothDevice(source);
        }

        public BluetoothDevice[] newArray(int size) {
            return new BluetoothDevice[size];
        }
    };
}


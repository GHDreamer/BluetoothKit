BluetoothKit---Android Bluetooth Framework
===========================

这个库用于Android蓝牙BLE设备通信，支持设备扫描，连接，读写，通知。在系统API基础上封装了一层异步任务队列，使所有任务串行化，同时解决了BLE蓝牙通信中可能会遇到的一系列坑，使得Android蓝牙开发非常方便。

------

# **更新日志**

### **Version 1.2.4**
- BleGattProfile新增Characteristic的Property属性

### **Version 1.2.3**
- 蓝牙连接状态回调新增mac参数

### **Version 1.2.2**
 - 支持蓝牙连接和发现服务分开配置
 - 重构部分蓝牙通信核心代码
 - 新增蓝牙beacon解析工具类
 - 实时断开连接，不参与串行化

------

# **用法**

1、在Android Studio的build.gradle中，在dependencies里添加一行:

```groovy
compile 'com.inuker.bluetooth:library:1.2.4'
```

2、创建一个BluetoothClient，建议作为一个单例: 

```Java
BluetoothClient mClient = new BluetoothClient(context);
```

## **设备扫描** 

支持经典蓝牙和BLE设备混合扫描，可自定义扫描策略:

```Java
SearchRequest request = new SearchRequest.Builder()
        .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
        .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
        .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
        .build();

mClient.search(request, new SearchResponse() {
    @Override
    public void onSearchStarted() {

    }

    @Override
    public void onDeviceFounded(SearchResult device) {
        Beacon beacon = new Beacon(device.scanRecord);
        BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
    }

    @Override
    public void onSearchStopped() {

    }

    @Override
    public void onSearchCanceled() {

    }
});
```

可以随时停止扫描:

```Java
mClient.stopSearch();
```

## **Beacon解析**

可以在广播中携带设备的自定义数据，用于设备识别，数据广播，事件通知等，这样手机端无需连接设备就可以获取设备推送的数据。

扫描到的beacon数据为byte[]，在SearchResult的scanRecord中，按如下形式生成Beacon对象，

```
Beacon beacon = new Beacon(device.scanRecord);
```

Beacon数据结构如下:

```
public class Beacon {

    public byte[] mBytes;

    public List<BeaconItem> mItems;
}
```

BeaconItem是按type来区分的，

```
public class BeaconItem {
    /**
     * 广播中声明的长度
     */
    public int len;

    /**
     * 广播中声明的type
     */
    public int type;

    /**
     * 广播中的数据部分
     */
    public byte[] bytes;
}
```

然后根据自定义的协议，解析对应的BeaconItem中的bytes，首先创建一个BeaconParser，传入对应的BeaconItem，然后根据协议不断读取数据，
如果协议中某个字段占1个字节，则调用readByte，若占用两个字节则调用readShort，如果要取某个字节的某个bit则调用getBit。注意parser
每读一次数据，指针就会相应向后移动，可以调用setPosition设置当前指针的位置。

```
BeaconItem beaconItem; // 设置成beacon中对应的item
BeaconParser beaconParser = new BeaconParser(beaconItem);
int firstByte = beaconParser.readByte(); // 读取第1个字节
int secondByte = beaconParser.readByte(); // 读取第2个字节
int productId = beaconParser.readShort(); // 读取第3,4个字节
boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
beaconParser.setPosition(0); // 将读取起点设置到第1字节处
```

## **BLE设备通信** 
### **● 连接**

连接过程包括了普通的连接(connectGatt)和发现服务(discoverServices)，这里收到回调时表明服务发现已完成。回调参数BleGattProfile包括了所有的service和characteristic的uuid。返回的code表示操作状态，包括成功，失败或超时等，所有常量都在Constants类中。

```Java
mClient.connect(MAC, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile profile) {
        if (code == REQUEST_SUCCESS) {
        
        }
    }
});
```

可以配置连接参数如下，

```
BleConnectOptions options = new BleConnectOptions.Builder()
        .setConnectRetry(3)   // 连接如果失败重试3次
        .setConnectTimeout(30000)   // 连接超时30s
        .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
        .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
        .build();

mClient.connect(MAC, options, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile data) {

    }
});
```

### **● 连接状态**

如果要监听蓝牙连接状态可以注册回调，只有两个状态：连接和断开。

```
mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);

private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

    @Override
    public void onConnectStatusChanged(String mac, int status) {
        if (status == STATUS_CONNECTED) {

        } else if (status == STATUS_DISCONNECTED) {

        }
    }
};

mClient.unregisterConnectStatusListener(MAC, mBleConnectStatusListener);
```

### **● 断开连接**
```Java
mClient.disconnect(MAC);
```

### **● 读Characteristic**
```Java
mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
    @Override
    public void onResponse(int code, byte[] data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 写Characteristic**

要注意这里写的byte[]不能超过20字节，如果超过了需要自己分成几次写。建议的办法是第一个byte放剩余要写的字节的长度。

```Java
mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

这个写是带了WRITE_TYPE_NO_RESPONSE标志的，实践中发现比普通的write快2~3倍，建议用于固件升级。

```Java
mClient.writeNoRsp(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 打开Notify**

这里有两个回调，onNotify是接收通知的。

```Java
mClient.notify(MAC, serviceUUID, characterUUID, new BleNotifyResponse() {
    @Override
    public void onNotify(UUID service, UUID character, byte[] value) {
        
    }

    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 关闭Notify**
```Java
mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 打开Indicate**

和Notify类似，

```Java
mClient.indicate(MAC, serviceUUID, characterUUID, new BleNotifyResponse() {
    @Override
    public void onNotify(UUID service, UUID character, byte[] value) {
        
    }

    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 关闭Indicate**

```Java
mClient.unindicate(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 读Rssi**
```Java
mClient.readRssi(MAC, new BleReadRssiResponse() {
    @Override
    public void onResponse(int code, Integer rssi) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

<br/>
# **作者**
 - Email: dingjikerbo@gmail.com
 - Blog: http://blog.csdn.net/dingjikerbo
 - QQ群: 112408886

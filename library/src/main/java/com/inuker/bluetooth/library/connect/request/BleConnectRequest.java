package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.BleResponse;

public class BleConnectRequest extends BleRequest {

    public BleConnectRequest(BleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_CONNECT;
    }

    @Override
    protected int getDefaultRetryLimit() {
        // TODO Auto-generated method stub
        return 2;
    }

    /**
     * 红米note 2上发现service特别慢，这里给超时延长点
     * @return
     */
    @Override
    public int getTimeoutLimit() {
        return 30000;
    }
}

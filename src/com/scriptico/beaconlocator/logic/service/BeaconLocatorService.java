/*
 * Copyright 2014 Cyril Deba
 * http://www.scriptico.com
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.scriptico.beaconlocator.logic.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.scriptico.beaconlocator.logic.service.beacon.Beacon;
import com.scriptico.beaconlocator.logic.service.beacon.BeaconFactory;

/**
 * Service to detect available iBeacon devices
 * 
 * The service starts the device scanner on bind and stops it when any device is
 * found.
 * 
 * To process the found device, the service sends a broadcast intent with the
 * found device as a {@link BeaconLocatorService#DEVICE_KEY} parameter.
 * {@link BeaconLocatorBroadcastReceiver} is responsible to process the received
 * broadcast
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 * @see BeaconLocatorBroadcastReceiver
 */
public class BeaconLocatorService extends Service {
    private final static String TAG = BeaconLocatorService.class.getSimpleName();

    public static final String START_LOCATOR_SCANNER = "com.scriptico.beaconlocator.logic.service.receiver.START_LOCATOR_SCANNER";

    public final static String BEACON_ACTION_KEY = "beaconAction";
    public final static String DEVICE_KEY = "device";

    private final IBinder mBinder = new BeaconLocatorServiceBinder();

    private BluetoothAdapter mBluetoothAdapter;

    private BroadcastReceiver mLocatorScannerBroadcastReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i(TAG, "Starting the locator service for the received START_LOCATOR_SCANNER message");

	    BeaconLocatorService.this.startScanner();
	}
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

	@Override
	public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
	    Log.i(TAG, "a new device " + device.getAddress() + " has been found with " + rssi + " rssi");

	    Beacon beaconDevice = BeaconFactory.getInstance().newBeaconDevice(rssi, scanRecord);

	    Log.i(TAG, "Beacon device proximityUUID: " + beaconDevice.getProximityUUID());
	    Log.i(TAG, "Beacon device major: " + beaconDevice.getMajor());
	    Log.i(TAG, "Beacon device minor: " + beaconDevice.getMinor());
	    Log.i(TAG, "Beacon device distance: " + beaconDevice.getDistance());

	    broadcastBeaconDetected(beaconDevice);

	    mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.i(TAG, "Beacon locator service has been started");

	mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

	registerReceiver(mLocatorScannerBroadcastReceiver, new IntentFilter(START_LOCATOR_SCANNER));

	startScanner();

	return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
	return mBinder;
    }

    @Override
    public void onDestroy() {
	unregisterReceiver(mLocatorScannerBroadcastReceiver);

	mBluetoothAdapter.stopLeScan(mLeScanCallback);

	Log.i(TAG, "The new beacons locator scanner has been stopped");

	super.onDestroy();
    }

    public boolean startScanner() {
	mBluetoothAdapter.startLeScan(mLeScanCallback);

	Log.i(TAG, "The new beacons locator scanner has been started");

	return true;
    }

    private void broadcastBeaconDetected(Beacon device) {
	Intent intent = new Intent(BeaconLocatorBroadcastReceiver.BEACON_DETECTED_ACTION);
	intent.putExtra(DEVICE_KEY, device);
	sendBroadcast(intent);
    }

    public class BeaconLocatorServiceBinder extends Binder {
	public BeaconLocatorService getService() {
	    return BeaconLocatorService.this;
	}
    }

}

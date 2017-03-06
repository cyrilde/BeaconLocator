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
package com.scriptico.beaconlocator.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.logic.service.BeaconLocatorBroadcastReceiver;
import com.scriptico.beaconlocator.logic.service.BeaconLocatorService;
import com.scriptico.beaconlocator.ui.fragments.BeaconContentFragment;
import com.scriptico.beaconlocator.ui.fragments.HomeFragment;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class MainActivity extends Activity {

    private static final int ENABLE_BL_REQUEST_CODE = 0;

    private BluetoothAdapter mBluetoothAdapter;

    private BeaconLocatorBroadcastReceiver mBeaconLocatorBroadcastReceiver;

    private final DialogInterface.OnClickListener mDialogFinishClickListener = new DialogInterface.OnClickListener() {

	@Override
	public void onClick(DialogInterface dialog, int which) {
	    switch (which) {
	    case DialogInterface.BUTTON_POSITIVE:
		enableBluetoothAdapter();
		break;
	    case DialogInterface.BUTTON_NEGATIVE:
		finish();
		break;
	    }
	}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);

	mBeaconLocatorBroadcastReceiver = new BeaconLocatorBroadcastReceiver();

	registerReceiver(mBeaconLocatorBroadcastReceiver, new IntentFilter(
		BeaconLocatorBroadcastReceiver.BEACON_DETECTED_ACTION));

	// checking if the current device supports the BLE feature
	if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
	    // showing the error message
	    Toast.makeText(this, R.string.error_ble_not_supported, Toast.LENGTH_SHORT).show();

	    // closing the activity
	    finish();
	}

	mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

	// checking if bluetooth is enabled and ready for use
	if (null == mBluetoothAdapter || !mBluetoothAdapter.isEnabled()) {
	    enableBluetoothAdapter();
	} else {
	    // bluetooth is ready. starting the beacon locator service
	    startBeaconLocatorService();

	    // showing the home view
	    startHomeFragment();
	}
    }

    @Override
    protected void onResume() {
	if (null != getIntent().getExtras()
		&& getIntent().getExtras().containsKey(BeaconLocatorService.BEACON_ACTION_KEY)) {

	    // starting content fragment
	    startContentFragment();

	}
	super.onResume();
    }

    protected void enableBluetoothAdapter() {
	// bluetooth is not enabled. making a request to enable it
	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

	// starting the activity
	startActivityForResult(enableBtIntent, ENABLE_BL_REQUEST_CODE);
    }

    public void startHomeFragment() {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// add a fragment
	HomeFragment homeFragment = new HomeFragment();
	fragmentTransaction.add(R.id.content, homeFragment);
	fragmentTransaction.commit();
    }

    public void startContentFragment() {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// add a fragment
	BeaconContentFragment contentFragment = new BeaconContentFragment();

	Bundle arguments = new Bundle();
	arguments.putSerializable(BeaconLocatorService.BEACON_ACTION_KEY,
		getIntent().getExtras().getSerializable(BeaconLocatorService.BEACON_ACTION_KEY));
	contentFragment.setArguments(arguments);

	fragmentTransaction.add(R.id.content, contentFragment);
	fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	// checking the activity result code
	if (requestCode == ENABLE_BL_REQUEST_CODE) {

	    // bluetooth is allowed by user
	    if (resultCode == RESULT_OK) {
		startBeaconLocatorService();
	    }

	    // user has declined the bluetooth usage
	    if (resultCode == RESULT_CANCELED) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder = builder.setMessage(R.string.warning_bluetooth_required);
		builder = builder.setPositiveButton(R.string.btn_enable, mDialogFinishClickListener);
		builder = builder.setNegativeButton(R.string.btn_close, mDialogFinishClickListener);
		builder.show();
	    }
	}
    }

    /**
     * Binds {@link BeaconLocatorService} to the current activity
     */
    protected void startBeaconLocatorService() {
	startService(new Intent(getBaseContext(), BeaconLocatorService.class));
    }
}

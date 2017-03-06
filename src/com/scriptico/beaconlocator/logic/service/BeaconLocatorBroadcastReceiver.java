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

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.util.Log;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.data.dto.BeaconDto;
import com.scriptico.beaconlocator.data.storage.BeaconStorage;
import com.scriptico.beaconlocator.logic.service.beacon.Beacon;
import com.scriptico.beaconlocator.ui.activities.MainActivity;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class BeaconLocatorBroadcastReceiver extends BroadcastReceiver {

    public static final String BEACON_DETECTED_ACTION = "com.scriptico.beaconlocator.logic.service.receiver.BEACON_DETECTED";

    private final static String TAG = BeaconLocatorBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
	Log.d(TAG, "Message has been recieved");

	if (BEACON_DETECTED_ACTION.equals(intent.getAction())) {
	    Log.d(TAG, "Beacon is detected message is reveived. Processing the received message");
	    onBeaconDetectedMessage(context, intent);
	}

    }

    private void onBeaconDetectedMessage(Context context, Intent intent) {
	if (null != intent && null != intent.getExtras()) {

	    Beacon detectedDevice = (Beacon) intent.getExtras().getSerializable(BeaconLocatorService.DEVICE_KEY);

	    if (null != detectedDevice) {
		Log.d(TAG, "Checking if the detected beacon is registered");
		BeaconDto registeredBeacon = BeaconStorage.getInstance().get(detectedDevice.getProximityUUID(),
			detectedDevice.getMajor(), detectedDevice.getMinor());

		if (null != registeredBeacon) {
		    Log.d(TAG, "The detected beacon is registered in the application");
		    BeaconActionDto action = BeaconHelper.getInstance().getBeaconActionForDistance(registeredBeacon,
			    detectedDevice.getDistance());

		    if (BeaconHelper.getInstance().isBeaconActionTimedOut(action) && !isAppOnBackground(context)) {
			sendNotificationForBeaconAction(context, action);

			((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
		    }
		}
	    }
	}
    }

    private void sendNotificationForBeaconAction(Context context, BeaconActionDto action) {
	NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);

	Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);

	notificationIntent.putExtra(BeaconLocatorService.BEACON_ACTION_KEY, action);

	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

	PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent,
		PendingIntent.FLAG_CANCEL_CURRENT);

	Notification.Builder builder = new Notification.Builder(context);
	builder = builder.setContentIntent(contentIntent);
	builder = builder.setSmallIcon(R.drawable.ic_launcher);
	// TODO small icons?
	// setting the notification icon
	try {
	    builder = builder.setLargeIcon(BitmapFactory.decodeStream(context.getAssets().open(
		    action.getNotificationIcon().getUrl())));
	} catch (IOException e) {
	    // TODO log?
	}

	// setting the notification message
	builder = builder.setContentText(action.getNotificationMessage());

	builder = builder.setTicker(action.getNotificationMessage()).setWhen(System.currentTimeMillis());

	Notification notification = builder.build();
	notificationManager.notify(1, notification);

    }

    protected boolean isAppOnBackground(Context context) {
	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	if (null == appProcesses) {
	    return true;
	}
	final String packageName = context.getPackageName();
	for (RunningAppProcessInfo appProcess : appProcesses) {
	    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
		    && appProcess.processName.equals(packageName)) {
		return false;
	    }
	}
	return true;
    }

}

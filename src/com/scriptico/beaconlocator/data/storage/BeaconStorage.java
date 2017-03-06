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
package com.scriptico.beaconlocator.data.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.os.Environment;
import android.util.Log;

import com.scriptico.beaconlocator.data.dto.BeaconDto;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class BeaconStorage {

    private final static String TAG = BeaconStorage.class.getSimpleName();

    private static final String BEACON_FILE_EXTENSION = ".bcn";

    private Set<BeaconDto> mBeaconsHolder = new HashSet<BeaconDto>(0);

    private static final BeaconStorage sInstance = new BeaconStorage();

    private BeaconStorage() {
	File rootLocation = Environment.getExternalStoragePublicDirectory(BeaconDemoStorage.BEACONS_PATH);
	if (!rootLocation.exists()) {
	    rootLocation.mkdirs();
	}

	try {
	    if (rootLocation.exists()) {
		File[] files = rootLocation.listFiles();
		if (null != files) {
		    for (File current : files) {
			Log.d(TAG, "Current beacon file: " + current.getAbsolutePath());
			
			BeaconDto beacon = read(current);
			mBeaconsHolder.add(beacon);
		    }
		}
	    }
	} catch (Exception e) {
	    Log.e(TAG, "Unable to load beacons from the beacons external media storage");
	}
    }

    public List<BeaconDto> getAll() {
	return new ArrayList<BeaconDto>(mBeaconsHolder);
    }

    public void remove(BeaconDto candidate) {
	if (mBeaconsHolder.remove(candidate)) {
	    delete(getFilenameForBeaconUuid(candidate.getUuid()));
	}
    }

    public void saveOrUpdate(BeaconDto candidate) {
	mBeaconsHolder.add(candidate);
	try {
	    write(candidate);
	} catch (Exception e) {
	    Log.e(TAG, "Unable to write beacon to the external beacon storage");
	}
    }

    public BeaconDto get(String uuid, int majorId, int minorId) {
	BeaconDto example = new BeaconDto();
	example.setUuid(uuid);
	example.setMajorId(majorId);
	example.setMinorId(minorId);

	for (Iterator<BeaconDto> iterator = mBeaconsHolder.iterator(); iterator.hasNext();) {
	    BeaconDto current = iterator.next();
	    if (example.equals(current)) {
		return current;
	    }

	}

	return null;
    }

    protected String getFilenameForBeaconUuid(String uuid) {
	return uuid.replace("-", "") + BEACON_FILE_EXTENSION;
    }

    private BeaconDto read(File source) throws OptionalDataException, ClassNotFoundException, IOException {
	FileInputStream fis = new FileInputStream(source);
	ObjectInputStream ois = new ObjectInputStream(fis);
	Object object = ois.readObject();
	ois.close();
	return (BeaconDto) object;
    }

    private void write(BeaconDto candidate) throws OptionalDataException, ClassNotFoundException, IOException {
	File file = new File(Environment.getExternalStoragePublicDirectory(BeaconDemoStorage.BEACONS_PATH),
		getFilenameForBeaconUuid(candidate.getUuid()));
	if (!file.exists()) {
	    file.createNewFile();
	}
	FileOutputStream fos = new FileOutputStream(file);
	ObjectOutputStream oos = new ObjectOutputStream(fos);
	oos.writeObject(candidate);
	oos.close();
	fos.close();
    }

    private boolean delete(String filename) {
	File file = new File(Environment.getExternalStoragePublicDirectory(BeaconDemoStorage.BEACONS_PATH), filename);
	return file.delete();
    }

    public static BeaconStorage getInstance() {
	return sInstance;
    }
}

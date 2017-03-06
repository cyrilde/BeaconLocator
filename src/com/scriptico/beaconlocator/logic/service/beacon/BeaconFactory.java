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
package com.scriptico.beaconlocator.logic.service.beacon;

/**
 * Factory to create new {@link Beacon}s.
 * 
 * 
 * The distance estimate provided by iOS is based on the ratio of the iBeacon
 * signal strength (rssi) over the calibrated transmitter power (txPower). The
 * txPower is the known measured signal strength in rssi at 1 meter away. Each
 * iBeacon must be calibrated with this txPower value to allow accurate distance
 * estimates.
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class BeaconFactory {

    private static final BeaconFactory sInstance = new BeaconFactory();

    /**
     * Returns the standard {@link Beacon}'s factory.
     * 
     */
    public static BeaconFactory getInstance() {
	return sInstance;
    }

    /**
     * Constructs {@link Beacon} from the device advertisement record.
     * 
     * 
     * @param rssi
     *            - the RSSI value for the remote device as reported by the
     *            hardware.
     * 
     * @param scanRecord
     *            - the content of the advertisement record offered by the
     *            remote device.
     * 
     * @return {@link Beacon}
     */
    public Beacon newBeaconDevice(int rssi, byte[] scanRecord) {
	Beacon result = new Beacon();

	populate(result, rssi, scanRecord);

	return result;
    }

    /**
     * Populates {@link Beacon}
     * 
     * @param target
     *            - an instance of {@link Beacon} to populate
     * @param rssi
     *            - the RSSI value for the remote device as reported by the
     *            hardware
     * @param scanRecord
     *            - the content of the advertisement record offered by the
     *            remote device
     */
    protected void populate(Beacon target, int rssi, byte[] scanRecord) {

	int startByteIndex = getStartByteIndexFromScanRecord(scanRecord);

	String proximityUUID = getProximityUUIDFromScanRecord(scanRecord, startByteIndex);

	int major = getMajorFromScanRecord(scanRecord, startByteIndex);
	int minor = getMinorFromScanRecord(scanRecord, startByteIndex);
	int txPower = getTxPowerFromScanRecord(scanRecord, startByteIndex);

	double distance = estimateDistance(rssi, txPower);

	target.setProximityUUID(proximityUUID);
	target.setMajor(major);
	target.setMinor(minor);
	target.setDistance(distance);
    }

    protected int getStartByteIndexFromScanRecord(byte[] scanRecord) {
	int result = 2;

	while (result <= 5) {
	    if (((int) scanRecord[result + 2] & 0xff) == 0x02 && ((int) scanRecord[result + 3] & 0xff) == 0x15) {
		break;
	    }

	    result++;
	}

	return result;
    }

    protected String getProximityUUIDFromScanRecord(byte[] scanRecord, int startByteIndex) {
	byte[] uuidAsBytes = new byte[16];

	System.arraycopy(scanRecord, startByteIndex + 4, uuidAsBytes, 0, 16);

	String hexString = encodeHexString(uuidAsBytes);

	StringBuilder stringBuider = new StringBuilder();
	stringBuider.append(hexString.substring(0, 8));
	stringBuider.append("-");
	stringBuider.append(hexString.substring(8, 12));
	stringBuider.append("-");
	stringBuider.append(hexString.substring(12, 16));
	stringBuider.append("-");
	stringBuider.append(hexString.substring(16, 20));
	stringBuider.append("-");
	stringBuider.append(hexString.substring(20, 32));

	return stringBuider.toString();
    }

    protected int getMajorFromScanRecord(byte[] scanRecord, int startByteIndex) {
	return (scanRecord[startByteIndex + 20] & 0xff) * 0x100 + (scanRecord[startByteIndex + 21] & 0xff);
    }

    protected int getMinorFromScanRecord(byte[] scanRecord, int startByteIndex) {
	return (scanRecord[startByteIndex + 22] & 0xff) * 0x100 + (scanRecord[startByteIndex + 23] & 0xff);
    }

    protected int getTxPowerFromScanRecord(byte[] scanRecord, int startByteIndex) {
	return scanRecord[startByteIndex + 24];
    }

    // TODO has to be implemented
    protected double estimateDistance(int rssi, int txPower) {

	return 0.0d;
    }

    /**
     * Converts an array of bytes into a string
     * 
     * @param data
     *            a byte[] to convert to Hex characters
     * 
     * @return a string containing hexadecimal characters
     */
    protected String encodeHexString(byte[] data) {
	StringBuilder stringBuilder = new StringBuilder();
	for (byte currentByte : data) {
	    stringBuilder.append(String.format("%02x", currentByte & 0xff));
	}
	return stringBuilder.toString();
    }

}

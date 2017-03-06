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

import java.io.Serializable;

/**
 * Represents the iBeacon device
 * 
 * 
 * The {@code Beacon} object has to be created by using
 * {@link BeaconFactory#newBeaconDevice(byte[])}
 * 
 * @author Cyril Deba
 * @version 1.0
 */
public class Beacon implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proximityUUID;
    private int major;
    private int minor;
    private double distance;

    /**
     * Constructs a new {@code Beacon}.
     * 
     * To instantiate a new {@code Beacon} object use
     * {@link BeaconFactory#newBeaconDevice(byte[])} only please
     */
    protected Beacon() {
    }

    /**
     * Returns the device proximity UUID
     * 
     * The proximity UUID is a 128-bit value that uniquely identifies one or
     * more beacons as a certain type or from a certain organization
     * 
     * @return the device proximity UUID
     */
    public String getProximityUUID() {
	return proximityUUID;
    }

    /**
     * Returns the device major identifier
     * 
     * The major identifier is a 16-bit unsigned integer represented as a string
     * that can be used to group related beacons that have the same proximity
     * UUID.
     * 
     * The major identifier might identify different stores in the same chain, a
     * specific train service route, a specific gallery within a museum, etc.
     * 
     * @return the device major
     */
    public int getMajor() {
	return major;
    }

    /**
     * Returns the device minor identifier
     * 
     * The minor identifier is a 16-bit unsigned integer that differentiates
     * beacons with the same proximity UUID and major value.
     * 
     * The minor identifier might identify different checkout tills within a
     * specific store, a particular carriage number within a train, an exhibit
     * within a gallery, etc.
     * 
     * @return the device minor
     */
    public int getMinor() {
	return minor;
    }

    /**
     * Returns the distance estimate to the device in meters.
     * 
     * The distance estimate is based on the ratio of the device signal strength
     * (rssi) over the calibrated transmitter power (txPower).
     * 
     * http://www.s2is.org/Issues/v1/n2/papers/paper14.pdf
     * 
     * @return the distance
     */
    public double getDistance() {
	return distance;
    }

    protected void setProximityUUID(String proximityUUID) {
	this.proximityUUID = proximityUUID;
    }

    protected void setMajor(int major) {
	this.major = major;
    }

    protected void setMinor(int minor) {
	this.minor = minor;
    }

    protected void setDistance(double distance) {
	this.distance = distance;
    }
}

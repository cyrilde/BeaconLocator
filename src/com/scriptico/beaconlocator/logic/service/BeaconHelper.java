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

import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.data.dto.BeaconDto;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 *
 */
public class BeaconHelper {

    private static final BeaconHelper sInstance = new BeaconHelper();

    private BeaconHelper() {

    }

    public static BeaconHelper getInstance() {
	return sInstance;
    }

    protected boolean isBeaconActionTimedOut(BeaconActionDto action) {
	// TODO action last run for the beacon action
	// get the configured timeout
	// check the action last run
	return true;
    }

    public BeaconActionDto getBeaconActionForDistance(BeaconDto beacon, double distance) {
	BeaconActionDto actionWithoutDistance = null;

	for (BeaconActionDto current : beacon.getActions()) {
	    if (null == actionWithoutDistance && current.getDistance() == 0d) {
		actionWithoutDistance = current;
		continue;
	    }

	    if (current.getDistance() > 0d && current.getDistance() <= distance) {
		return current;
	    }
	}

	return actionWithoutDistance;
    }

}

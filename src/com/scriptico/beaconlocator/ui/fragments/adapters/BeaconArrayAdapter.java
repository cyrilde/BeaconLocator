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
package com.scriptico.beaconlocator.ui.fragments.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconDto;

/**
 * Adapter for {@link BeaconDto}
 * 
 * @author Cyril Deba
 * @version 1.0
 */
public class BeaconArrayAdapter extends ArrayAdapter<BeaconDto> {

    private Context context;
    private List<BeaconDto> objects;

    public BeaconArrayAdapter(Context context, List<BeaconDto> objects) {
	super(context, R.layout.row_beacon, objects);

	this.context = context;
	this.objects = objects;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	// getting the inflater
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	// getting the row view
	View rowView = inflater.inflate(R.layout.row_beacon, parent, false);

	// retrieving a beacon for the selecting position
	BeaconDto beacon = objects.get(position);

	// setting the beacon name
	((TextView) rowView.findViewById(R.id.beaconName)).setText(beacon.getName());

	// setting the beacon uuid
	((TextView) rowView.findViewById(R.id.beaconUuid)).setText(beacon.getUuid());

	// setting the beacon major id
	((TextView) rowView.findViewById(R.id.beaconMajorId)).setText(Integer.toString(beacon.getMajorId()));

	// setting the beacon minor id
	((TextView) rowView.findViewById(R.id.beaconMinorId)).setText(Integer.toString(beacon.getMinorId()));

	return rowView;
    }
    
}

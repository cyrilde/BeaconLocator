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
import android.widget.ImageButton;
import android.widget.TextView;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.ui.fragments.listeners.OnBeaconActionAdapterClickListener;

/**
 * Adapter for {@link BeaconActionDto}
 * 
 * @author Cyril Deba
 * @version 1.0
 */
public class BeaconActionArrayAdapter extends ArrayAdapter<BeaconActionDto> {

    private OnBeaconActionAdapterClickListener mOnItemClickListener;

    private Context mContext;
    private List<BeaconActionDto> mItems;

    public BeaconActionArrayAdapter(Context context, List<BeaconActionDto> items) {
	super(context, R.layout.row_beacon_action, items);

	this.mContext = context;
	this.mItems = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	// getting the inflater
	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	// getting the row view
	View rowView = inflater.inflate(R.layout.row_beacon_action, parent, false);

	// retrieving a beacon action for the selecting position
	BeaconActionDto beaconAction = mItems.get(position);

	// setting the beacon action name
	((TextView) rowView.findViewById(R.id.beacon_action_name)).setText(beaconAction.getName());

	// setting the edit button
	ImageButton editActionButton = (ImageButton) rowView.findViewById(R.id.beacon_action_edit);
	editActionButton.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (null != mOnItemClickListener) {
		    mOnItemClickListener.onEdit(position);
		}

	    }
	});

	// setting the delete button
	ImageButton deleteActionButton = (ImageButton) rowView.findViewById(R.id.beacon_action_delete);
	deleteActionButton.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (null != mOnItemClickListener) {
		    mOnItemClickListener.onDelete(position);
		}

	    }
	});

	return rowView;
    }

    /**
     * @param onItemClickListener
     *            the onItemClickListener to set
     */
    public void setOnItemClickListener(OnBeaconActionAdapterClickListener onItemClickListener) {
	this.mOnItemClickListener = onItemClickListener;
    }

}

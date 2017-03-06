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
package com.scriptico.beaconlocator.ui.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scriptico.beaconlocator.R;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class HomeFragment extends Fragment {

    private Button mViewAllBeaconsBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	getActivity().getActionBar().hide();
	LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_home, container, false);

	// initializing controls
	mViewAllBeaconsBtn = (Button) linearLayout.findViewById(R.id.btnBeacons);

	// adding onclick listeners to controls
	mViewAllBeaconsBtn.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// get an instance of FragmentTransaction from your Activity
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		// add a fragment
		BeaconListFragment myFragment = new BeaconListFragment();
		fragmentTransaction.replace(R.id.content, myFragment);
		fragmentTransaction.commit();

	    }
	});

	return linearLayout;
    }

}

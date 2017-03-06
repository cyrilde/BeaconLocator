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

import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconDto;
import com.scriptico.beaconlocator.data.storage.BeaconStorage;
import com.scriptico.beaconlocator.ui.fragments.adapters.BeaconArrayAdapter;
import com.scriptico.beaconlocator.ui.fragments.callbacks.OnBeaconEditorCallback;
import com.scriptico.beaconlocator.ui.fragments.callbacks.OnBeaconListActionBarCallback;
import com.scriptico.beaconlocator.ui.fragments.observers.BeaconActionModeObserver;

/**
 * Fragment to display registered beacons
 * 
 * @author Cyril Deba
 * @version 1.0
 */
public class BeaconListFragment extends AbstractApplicationFragment {
    public static final String TAG = BeaconListFragment.class.getSimpleName();

    private int mSelectedBeaconPosition = -1;
    private boolean mBeaconSelected = false;

    private BeaconArrayAdapter mBeaconsAdapter;

    private ActionMode mActionMode;

    private TextView mNoBeaconsInfoView;
    private ListView mBeaconsListView;

    private OnBeaconEditorCallback mOnBeaconEditorCallback = new OnBeaconEditorCallback() {

	@Override
	public void onSave(Object item) {
	    save(item);
	}

	@Override
	public void onFinish(Object item) {
	    save(item);
	}

	void save(Object item) {
	    if (item instanceof BeaconDto) {
		BeaconStorage.getInstance().saveOrUpdate((BeaconDto) item);
	    }
	}

	@Override
	public void onDelete(Object item) {
	    if (item instanceof BeaconDto) {
		BeaconStorage.getInstance().remove((BeaconDto) item);
		Toast.makeText(getActivity(), "Beacon was successfully removed", Toast.LENGTH_SHORT).show();
		mBeaconsAdapter.remove((BeaconDto) item);
		mBeaconsAdapter.notifyDataSetChanged();
		if (mBeaconsAdapter.isEmpty()) {
		    mNoBeaconsInfoView.setVisibility(View.VISIBLE);
		} else {
		    mNoBeaconsInfoView.setVisibility(View.GONE);
		}
		Toast.makeText(getActivity(), R.string.txt_confirmation_beacon_removed, Toast.LENGTH_SHORT).show();
	    }
	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	List<BeaconDto> registeredBeacons = BeaconStorage.getInstance().getAll();
	mBeaconsAdapter = new BeaconArrayAdapter(getActivity(), registeredBeacons);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	LinearLayout fragmentRootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_beacon_list, container,
		false);

	mBeaconsListView = (ListView) fragmentRootLayout.findViewById(R.id.beacons_list);
	mBeaconsListView.setAdapter(mBeaconsAdapter);
	mBeaconsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	mBeaconsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
		if (mSelectedBeaconPosition == position && mBeaconSelected) {
		    mActionMode.finish();
		    mBeaconSelected = false;
		} else {
		    mSelectedBeaconPosition = position;
		    mBeaconSelected = true;
		    view.setSelected(true);

		    OnBeaconListActionBarCallback actionBarCallback = new OnBeaconListActionBarCallback();
		    actionBarCallback.setBeaconActionModeObserver(new BeaconActionModeObserver() {

			@Override
			public void onFinish() {
			    // marking the selected view as not selected
			    view.setSelected(false);
			    mBeaconSelected = false;
			    mSelectedBeaconPosition = -1;
			};

			@Override
			public void onUpdate() {
			    startBeaconEditorFragment(mBeaconsAdapter.getItem(position), true);
			}

			@Override
			public void onDelete() {
			    // retrieving the beacon object to remove
			    BeaconDto candidate = mBeaconsAdapter.getItem(position);
			    // making callback
			    mOnBeaconEditorCallback.onDelete(candidate);
			}
		    });

		    // starting the contextual action bar with the created
		    // callback
		    mActionMode = getActivity().startActionMode(actionBarCallback);
		}
	    }
	});

	mNoBeaconsInfoView = (TextView) fragmentRootLayout.findViewById(R.id.beacon_actions_empty);

	mNoBeaconsInfoView = (TextView) fragmentRootLayout.findViewById(R.id.beacons_list_empty);
	if (mBeaconsAdapter.isEmpty()) {
	    mNoBeaconsInfoView.setVisibility(View.VISIBLE);
	} else {
	    mNoBeaconsInfoView.setVisibility(View.GONE);
	}

	return fragmentRootLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// inflating the action bar menu
	inflater.inflate(R.menu.menu_beacons_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    startHomeFragment();
	    return true;
	case R.id.menu_item_create:
	    startBeaconEditorFragment(new BeaconDto(), false);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public int getDisplayTitleId() {
	return R.string.txt_beacons;
    }

    private void startHomeFragment() {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// removing the current fragment
	fragmentTransaction.remove(this);

	HomeFragment homeFragment = new HomeFragment();

	fragmentTransaction.add(R.id.content, homeFragment);
	fragmentTransaction.commit();
    }

    private void startBeaconEditorFragment(BeaconDto candidate, boolean editMode) {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// removing the current fragment
	fragmentTransaction.detach(BeaconListFragment.this);

	// adding the editor fragment
	BeaconEditorFragment beaconEditorFragment = new BeaconEditorFragment();

	Bundle arguments = new Bundle();
	arguments.putSerializable(EditorFragment.CANDIDATE_TO_EDIT_KEY, candidate);
	arguments.putSerializable(EditorFragment.EDIT_MODE_KEY, editMode);
	beaconEditorFragment.setArguments(arguments);
	beaconEditorFragment.setOnEditorCallback(mOnBeaconEditorCallback);
	fragmentTransaction.add(R.id.content, beaconEditorFragment, BeaconEditorFragment.TAG);
	fragmentTransaction.commit();
    }
}

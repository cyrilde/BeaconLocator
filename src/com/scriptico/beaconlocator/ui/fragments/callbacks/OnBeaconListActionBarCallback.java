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
package com.scriptico.beaconlocator.ui.fragments.callbacks;

/**
 * 
 * @author Cyril Deba
 * @version 1.0
 */
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.ui.fragments.observers.BeaconActionModeObserver;

public class OnBeaconListActionBarCallback implements Callback {

    
    private BeaconActionModeObserver beaconActionModeObserver;

    public void setBeaconActionModeObserver(BeaconActionModeObserver beaconActionModeObserver) {
	this.beaconActionModeObserver = beaconActionModeObserver;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	mode.getMenuInflater().inflate(R.menu.menu_row_beacon_new, menu);
	return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	switch (item.getItemId()) {
	case R.id.edit_beacon:
	    if (null != beaconActionModeObserver) {
		// notifying the observer on updating
		beaconActionModeObserver.onUpdate();

		// finishing the contextual action bar
		mode.finish();
	    }
	    return true;
	case R.id.delete_beacon:
	    if (null != beaconActionModeObserver) {
		// notifying the observer on deleting
		beaconActionModeObserver.onDelete();
	    }

	    // finishing the contextual action bar
	    mode.finish();
	    return true;
	default:
	    mode.finish();
	    return false;
	}
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
	if (null != beaconActionModeObserver) {
	    // notifying the observer on finishing
	    beaconActionModeObserver.onFinish();
	}
    }

}

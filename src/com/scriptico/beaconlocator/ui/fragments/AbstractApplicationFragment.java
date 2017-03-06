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

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.scriptico.beaconlocator.R;

/**
 * Base fragment for all application fragments
 * 
 * The class handles base for all fragments functionality related to the action
 * bar behavior (the display title processing and enabling the fragment
 * participation in populating the options menu by receiving a call to
 * onCreateOptionsMenu and related methods.
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public abstract class AbstractApplicationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// allowing the fragment to receive calls to onCreateOptionsMenu
	setHasOptionsMenu(true);

	super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
	// preparing action bar
	prepareActionBar();
	
        super.onResume();
    }

    protected void prepareActionBar() {
	// retrieving the activity action bar
	ActionBar actionBar = getActivity().getActionBar();
	// setting the action bar to display
	actionBar.show();
	// enabling the custom layout usage
	actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	// setting the custom layout
	actionBar.setCustomView(getActionBarCustomView(), getLayoutParams());
	// enabling the home button
	actionBar.setDisplayShowHomeEnabled(true);
	actionBar.setDisplayHomeAsUpEnabled(true);
	// enabling the default android action bar title
	actionBar.setDisplayShowTitleEnabled(true);
	// making the home selection icon invisible
	actionBar.setIcon(android.R.color.transparent);
	// setting the default android action bar title
	actionBar.setTitle(R.string.action_bar_title_back);
    }

    /**
     * Returns the custom action bar view
     * 
     * The main purpose of the custom action bar is to support the centered
     * editor display title.
     * 
     * @see EditorFragment#getDisplayTitleId()
     * 
     * @return an instance of {@link View}
     */
    protected View getActionBarCustomView() {
	LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	// inflating the action bar view
	View actionBarView = (View) inflater.inflate(R.layout.beacon_application_action_bar, null);

	// initializing the display title text view
	String resolvedTitle = getResources().getString(getDisplayTitleId());
	((TextView) actionBarView.findViewById(R.id.action_bar_title)).setText(resolvedTitle);
	return actionBarView;
    }

    /**
     * Returns layout parameters for the custom action bar layout
     * 
     * The custom action bar layout has to wrap its content (i.e. the fragment
     * title) and has be centered in its parent (i.e. in the action bar)
     * 
     * @return an instance of {@link LayoutParams}
     */
    protected final LayoutParams getLayoutParams() {
	return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    /**
     * Returns the display title resource id
     * 
     * The implementation of the method has to return an appropriate display
     * title resource id. Eventually, the resolved title value will be displayed
     * in the fragment action bar.
     * 
     * @return the title string resource id
     */
    public abstract int getDisplayTitleId();

}

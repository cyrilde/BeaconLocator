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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.data.dto.ContentHolderDto;
import com.scriptico.beaconlocator.data.dto.MediaContentHolderDto;
import com.scriptico.beaconlocator.data.dto.enums.ContentType;

/**
 * Base abstract fragment the beacon action editors
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public class BeaconActionEditorFragment extends EditorFragment {

    private static final String TAG = BeaconActionEditorFragment.class.getSimpleName();

    private BeaconActionDto mBeaconActionDtoCandidate = new BeaconActionDto();

    private boolean mActionContentAutoselected = true;
    private boolean mContentMediaAutoselected = true;
    private boolean mNotificationIconAutoselected = true;

    private EditText mActionNameControl;
    private EditText mActionDistanceControl;
    private Spinner mActionContentTypeControl;
    private EditText mContentUrlControl;
    private Spinner mContentMediaControl;
    private EditText mNotificationMessageControl;
    private Spinner mNotificationIconControl;

    private ArrayAdapter<ContentType> mContentTypeAdapter;
    private ArrayAdapter<MediaContentHolderDto> mMediaContentHolderAdapter;
    private ArrayAdapter<ContentHolderDto> mNotificationIconAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// initializing the media content holder adapter
	mMediaContentHolderAdapter = new ArrayAdapter<MediaContentHolderDto>(getActivity(), android.R.layout.simple_spinner_item,
		new ArrayList<MediaContentHolderDto>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	LinearLayout fragmentRootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_beacon_action_editor, container, false);

	// allowing to hide the keyboard by clicking outside of the edit text
	// fields
	fragmentRootLayout.setOnTouchListener(new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
		// hiding the keyboard
		hideKeyboard();
		return true;
	    }
	});

	// initializing the fragment controls
	initializeEditorControls(fragmentRootLayout);

	if (isEditMode()) {
	    // binding the item to edit to the controls
	    bindItemToControls();
	}

	return fragmentRootLayout;
    }

    @Override
    protected void hideKeyboard() {
	// getting a view in focus
	View focusedView = getActivity().getCurrentFocus();
	if (null != focusedView) {
	    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    // hiding the keyboad
	    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
	    // requesting the focus on the root view
	    getActivity().findViewById(R.id.beacon_action_editor).requestFocus();
	}
    }

    @Override
    public void finish() {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// removing the current fragment
	fragmentTransaction.remove(this);

	// attaching the beacon editor fragment
	fragmentTransaction.attach(fragmentManager.findFragmentByTag(BeaconEditorFragment.TAG));

	fragmentTransaction.commit();
    }

    @Override
    public int getDisplayTitleId() {
	return isEditMode() ? R.string.action_bar_title_edit_beacon_action : R.string.action_bar_title_add_beacon_action;
    }

    @Override
    protected Serializable getItem() {
	return this.mBeaconActionDtoCandidate;
    }

    @Override
    protected boolean populateItem() {
	// checking if the data inputs have errors
	boolean valid = isEditorInputDataValid();
	if (valid) {
	    // setting name
	    mBeaconActionDtoCandidate.setName(mActionNameControl.getText().toString());

	    // setting distance
	    Editable distanceValue = mActionDistanceControl.getText();
	    if (distanceValue.length() > 0) {
		try {
		    mBeaconActionDtoCandidate.setDistance(Double.parseDouble(distanceValue.toString()));
		} catch (NumberFormatException e) {
		    Log.w(TAG, "Unable to parse the beacon distance value: " + distanceValue.toString());
		}
	    }

	    // setting the content type
	    ContentType selectedContentType = (ContentType) mActionContentTypeControl.getSelectedItem();
	    mBeaconActionDtoCandidate.setContentType(selectedContentType);

	    // setting the content url
	    if (selectedContentType == ContentType.IMAGE || selectedContentType == ContentType.VIDEO) {
		// media content is selected
		mBeaconActionDtoCandidate.setContent((MediaContentHolderDto) mContentMediaControl.getSelectedItem());
	    } else {
		// either web or app url is selected
		ContentHolderDto contentHolder = new ContentHolderDto();
		contentHolder.setUrl(mContentUrlControl.getText().toString());
		mBeaconActionDtoCandidate.setContent(contentHolder);
	    }

	    // setting the notification icon
	    mBeaconActionDtoCandidate.setNotificationIcon((ContentHolderDto) mNotificationIconControl.getSelectedItem());

	    // setting the notification message
	    mBeaconActionDtoCandidate.setNotificationMessage(mNotificationMessageControl.getText().toString());
	}
	return valid;
    }

    @Override
    protected boolean isEditorInputDataValid() {
	// validating the action name control
	boolean actionNameValid = validateActionNameControl();
	// validating the action distance control
	boolean actionDistanceValid = validateActionDistanceControl();
	// validating the action content url control
	boolean actionContentUrlValid = validateContentUrlControl();
	// validating the action notification message control
	boolean actionNotificationMessageValid = validateNotificationMessageControl();

	// all guys above have to be valid
	return actionNameValid && actionDistanceValid && actionContentUrlValid && actionNotificationMessageValid;
    }

    @Override
    protected void initializeEditorControls(LinearLayout rootLayout) {
	// initializing a control for the beacon action name
	initActionNameControl(rootLayout);
	// initializing a control for the beacon distance
	initActionDistanceControl(rootLayout);
	// initializing a control for the beacon content type
	initActionContentTypeControl(rootLayout);
	// initializing a control for the content url
	initContentUrlControl(rootLayout);
	// initializing a control for the media content
	initContentMediaControl(rootLayout);
	// initializing a control for the beacon notification message
	initNotificationMessageControl(rootLayout);
	// initializing a control for the beacon notification item
	initNotificationIconControl(rootLayout);

	// marking the current item as not changed
	setHasChanges(false);
    }

    @Override
    protected void setItem(Serializable candidate) {
	if (candidate instanceof BeaconActionDto) {
	    this.mBeaconActionDtoCandidate = (BeaconActionDto) candidate;
	}
    }

    @Override
    protected void bindItemToControls() {
	// binding the beacon action name
	mActionNameControl.setText(mBeaconActionDtoCandidate.getName());

	// binding the beacon action distance
	if (mBeaconActionDtoCandidate.getDistance() > 0) {
	    mActionDistanceControl.setText(String.valueOf(mBeaconActionDtoCandidate.getDistance()));
	}

	// binding the beacon action content type
	ContentType contentType = mBeaconActionDtoCandidate.getContentType();

	if (null != contentType) {
	    mActionContentTypeControl.setSelection(mContentTypeAdapter.getPosition(contentType));
	} else {
	    // selection the first entry by default
	    mActionContentTypeControl.setSelection(0);
	}

	if (contentType == ContentType.IMAGE || contentType == ContentType.VIDEO) {
	    // updating media content adapter
	    updateMediaContentAdapter(contentType);

	    // binding the action content
	    if (null != mBeaconActionDtoCandidate.getContent()) {
		int mediaContentPosition = mMediaContentHolderAdapter.getPosition((MediaContentHolderDto) mBeaconActionDtoCandidate.getContent());
		mContentMediaControl.setSelection(mediaContentPosition);
	    } else {
		// setting the first entry by default
		mContentMediaControl.setSelection(0);
	    }
	} else {
	    if (null != mBeaconActionDtoCandidate.getContent()) {
		// binding the content url control
		mContentUrlControl.setText(mBeaconActionDtoCandidate.getContent().getUrl());
	    }
	}

	// binding the notification message
	mNotificationMessageControl.setText(mBeaconActionDtoCandidate.getNotificationMessage());

	// binding the notification
	if (null != mBeaconActionDtoCandidate.getNotificationIcon()) {
	    int iconPosition = mNotificationIconAdapter.getPosition(mBeaconActionDtoCandidate.getNotificationIcon());
	    mNotificationIconControl.setSelection(iconPosition);
	} else {
	    // setting the first item by default
	    mNotificationIconControl.setSelection(0);
	}

    }

    /**
     * Validates the beacon action name control
     * 
     * @return true if the control data is valid; otherwise, false will be
     *         returned
     */
    protected boolean validateActionNameControl() {
	if (mActionNameControl.getText().length() < 1) {
	    // action name cannot be empty
	    mActionNameControl.setError("the beacon action name cannot be empty");
	    return false;
	}
	return true;
    }

    /**
     * Validates the beacon action distance control
     * 
     * @return true if the control data is valid; otherwise, false will be
     *         returned
     */
    protected boolean validateActionDistanceControl() {
	if (mActionDistanceControl.getText().length() > 0) {
	    try {
		// retrieving and parsing control data
		double value = Double.parseDouble(mActionDistanceControl.getText().toString());
		if (value < 0) {
		    // distance cannot be negative
		    mActionDistanceControl.setError("the beacon action distance cannot be negative");
		    return false;
		}
	    } catch (NumberFormatException e) {
		// unable to parse the distance value
		Log.d(TAG, "unable to parse the distance value: " + mActionDistanceControl.getText().toString());
		mActionDistanceControl.setError("the beacon action distance has to be a number");
		return false;
	    }
	}
	return true;
    }

    /**
     * Validates the content url controller
     * 
     * @return true if the control contains valid data; otherwise, false will be
     *         returned
     */
    protected boolean validateContentUrlControl() {
	// getting the content type
	ContentType contentType = (ContentType) mActionContentTypeControl.getSelectedItem();
	if (contentType == ContentType.APP_URL || contentType == ContentType.WEB_URL) {
	    // url cannot be empty
	    if (mContentUrlControl.getText().length() < 1) {
		mContentUrlControl.setError("the beacon action content url cannot be empty");
		return false;
	    }
	}
	return true;
    }

    /**
     * Validates the notification message control
     * 
     * @return true if the control contains valid data; otherwise, false will be
     *         returned
     */
    protected boolean validateNotificationMessageControl() {
	if (mNotificationMessageControl.getText().length() < 1) {
	    // notification message cannot be empty
	    mNotificationMessageControl.setError("the action notification message cannot be empty");
	    return false;
	}
	return true;
    }

    /**
     * Initializes the beacon action name control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initActionNameControl(LinearLayout rootLayout) {
	mActionNameControl = (EditText) rootLayout.findViewById(R.id.beacon_action_name);
	mActionNameControl.addTextChangedListener(mTextWatcher);
	mActionNameControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mActionNameControl.setError(null);
		if (!hasFocus) {
		    validateActionNameControl();
		}
	    }
	});
    }

    /**
     * Initializes the beacon action distance control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initActionDistanceControl(LinearLayout rootLayout) {
	mActionDistanceControl = (EditText) rootLayout.findViewById(R.id.beacon_action_distance);
	mActionDistanceControl.addTextChangedListener(mTextWatcher);
	mActionDistanceControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mActionDistanceControl.setError(null);
		if (!hasFocus) {
		    validateActionDistanceControl();
		}
	    }
	});
    }

    /**
     * Initializes the beacon action content type control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initActionContentTypeControl(LinearLayout rootLayout) {
	mActionContentTypeControl = (Spinner) rootLayout.findViewById(R.id.beacon_action_content_type);

	// initializing the spinner adapter
	mContentTypeAdapter = new ArrayAdapter<ContentType>(getActivity(), android.R.layout.simple_spinner_item, ContentType.values());
	mActionContentTypeControl.setAdapter(mContentTypeAdapter);
	mActionContentTypeControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// hiding the keyboard if the previously selected item was in
		// focus and the keyboard was opened
		hideKeyboard();

		ContentType selectedContentType = mContentTypeAdapter.getItem(position);
		if (selectedContentType == ContentType.IMAGE || selectedContentType == ContentType.VIDEO) {
		    mContentMediaControl.setVisibility(View.VISIBLE);

		    updateMediaContentAdapter(selectedContentType);

		    // removing the error state for the content url control if
		    // was set previously
		    mContentUrlControl.setError(null);
		    // hiding the content url control
		    mContentUrlControl.setVisibility(View.GONE);
		} else {
		    mContentUrlControl.setVisibility(View.VISIBLE);
		    mContentMediaControl.setVisibility(View.GONE);
		}

		// do not count initial selection as a change
		if (mActionContentAutoselected) {
		    mActionContentAutoselected = false;
		} else {
		    setHasChanges(true);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "no action is designed");
	    }
	});

    }

    /**
     * Initializes the beacon action content url control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initContentUrlControl(LinearLayout rootLayout) {
	mContentUrlControl = (EditText) rootLayout.findViewById(R.id.beacon_action_content_type_txt);
	mContentUrlControl.addTextChangedListener(mTextWatcher);
	mContentUrlControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mContentUrlControl.setError(null);
		if (!hasFocus) {
		    validateContentUrlControl();
		}

	    }
	});
    }

    /**
     * Initializes the content media control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initContentMediaControl(LinearLayout rootLayout) {
	mContentMediaControl = (Spinner) rootLayout.findViewById(R.id.beacon_action_content_type_media);
	mContentMediaControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// do not count initial selection as a change
		if (mContentMediaAutoselected) {
		    mContentMediaAutoselected = false;
		} else {
		    setHasChanges(true);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "no action is designed");
	    }
	});

	// setting the spinner adapter
	mContentMediaControl.setAdapter(mMediaContentHolderAdapter);
	ContentType selectedContent = (ContentType) mActionContentTypeControl.getSelectedItem();
	updateMediaContentAdapter(selectedContent);

    }

    /**
     * Initializes the beacon action notification message control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initNotificationMessageControl(LinearLayout rootLayout) {
	mNotificationMessageControl = (EditText) rootLayout.findViewById(R.id.beacon_action_notification_message);
	mNotificationMessageControl.addTextChangedListener(mTextWatcher);
	mNotificationMessageControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mContentUrlControl.setError(null);
		if (!hasFocus) {
		    validateNotificationMessageControl();
		}
	    }
	});
    }

    /**
     * Initializes the beacon action notification icon control
     * 
     * @param rootLayout
     *            - the fragment root layout
     */
    private void initNotificationIconControl(LinearLayout rootLayout) {
	mNotificationIconControl = (Spinner) rootLayout.findViewById(R.id.beacon_action_notification_icon);
	mNotificationIconControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// do not count initial selection as a change
		if (mNotificationIconAutoselected) {
		    mNotificationIconAutoselected = false;
		} else {
		    setHasChanges(true);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "no action is designed");
	    }
	});
	// initializing the spinner adapter
	mNotificationIconAdapter = new ArrayAdapter<ContentHolderDto>(getActivity(), android.R.layout.simple_spinner_item, getSampleNotificationIcons());
	mNotificationIconControl.setAdapter(mNotificationIconAdapter);
    }

    /**
     * Updates the media content adapter based on the given content type
     * 
     * @param contentType
     */
    private void updateMediaContentAdapter(ContentType contentType) {
	// updating the media content array adapter
	mMediaContentHolderAdapter.clear();

	if (contentType == ContentType.IMAGE) {
	    mMediaContentHolderAdapter.addAll(getSampleMediaImages());
	}

	if (contentType == ContentType.VIDEO) {
	    mMediaContentHolderAdapter.addAll(getSampleMediaVideos());
	}
	// selecting the first item by default
	mContentMediaControl.setSelection(0);

	mMediaContentHolderAdapter.notifyDataSetChanged();
    }

    // TODO test data
    private List<ContentHolderDto> getSampleNotificationIcons() {
	List<ContentHolderDto> result = new ArrayList<ContentHolderDto>();
	for (int i = 1; i < 6; i++) {
	    MediaContentHolderDto icon = new MediaContentHolderDto();
	    icon.setName("Sample icon " + i);
	    icon.setUrl("content/icons/glyphicons_sample_icon_0" + i + ".png");
	    result.add(icon);
	}
	return result;
    }

    // TODO test data
    private List<MediaContentHolderDto> getSampleMediaImages() {
	List<MediaContentHolderDto> result = new ArrayList<MediaContentHolderDto>();
	for (int i = 1; i < 6; i++) {
	    MediaContentHolderDto icon = new MediaContentHolderDto();
	    icon.setName("Sample image " + i);
	    icon.setUrl("content/images/wikipedia_simple_image_0" + i + ".jpg");
	    result.add(icon);
	}
	return result;
    }

    // TODO test data
    private List<MediaContentHolderDto> getSampleMediaVideos() {
	List<MediaContentHolderDto> result = new ArrayList<MediaContentHolderDto>();
	for (int i = 1; i < 6; i++) {
	    MediaContentHolderDto icon = new MediaContentHolderDto();
	    icon.setName("Sample video " + i);
	    icon.setUrl("content/videos/wikipedia_simple_video_0" + i + ".ogg");
	    result.add(icon);
	}
	return result;
    }
}

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

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.data.dto.BeaconDto;
import com.scriptico.beaconlocator.ui.fragments.adapters.BeaconActionArrayAdapter;
import com.scriptico.beaconlocator.ui.fragments.callbacks.OnBeaconEditorCallback;
import com.scriptico.beaconlocator.ui.fragments.listeners.OnBeaconActionAdapterClickListener;

/**
 * @author Cyril Deba
 * @version 1.0
 *
 */
public class BeaconEditorFragment extends EditorFragment {

    public static final String TAG = BeaconEditorFragment.class.getSimpleName();

    private BeaconActionArrayAdapter mBeaconActionAdapter;

    private BeaconDto mBeaconDtoCandidate = new BeaconDto();

    private EditText mBeaconNameControl;
    private EditText mBeaconUuidControl;
    private EditText mBeaconMajorIdControl;
    private EditText mBeaconMinorIdControl;

    private TextView mNoActionsWarningView;
    private ImageButton mAddNewActionButton;

    private OnBeaconEditorCallback mOnBeaconActionEditorCallback = new OnBeaconEditorCallback() {

	@Override
	public void onFinish(Object item) {
	    save(item);
	}

	@Override
	public void onSave(Object item) {
	    save(item);
	}

	void save(Object item) {
	    if (item instanceof BeaconActionDto) {
		BeaconActionDto beaconActionDto = (BeaconActionDto) item;
		if (!mBeaconDtoCandidate.getActions().contains(beaconActionDto)) {
		    mBeaconDtoCandidate.getActions().add(beaconActionDto);
		    setHasChanges(true);
		}
	    }
	}

	@Override
	public void onDelete(Object item) {
	    if (item instanceof BeaconActionDto) {
		BeaconActionDto beaconActionDto = (BeaconActionDto) item;
		mBeaconDtoCandidate.getActions().remove(beaconActionDto);
		setHasChanges(true);
	    }
	}
    };

    private OnBeaconActionAdapterClickListener mOnBeaconAdapterClickListener = new OnBeaconActionAdapterClickListener() {

	@Override
	public void onEdit(int position) {
	    startActionEditorFragment(mBeaconActionAdapter.getItem(position), true);
	}

	@Override
	public void onDelete(int position) {
	    BeaconActionDto candidate = mBeaconActionAdapter.getItem(position);

	    // removing the beacon action
	    mBeaconDtoCandidate.getActions().remove(candidate);
	    mBeaconActionAdapter.notifyDataSetChanged();

	    Toast.makeText(getActivity(), R.string.txt_confirmation_beacon_removed, Toast.LENGTH_SHORT).show();
	}
    };

    private DataSetObserver mBeaconActionsDataSetObserver = new DataSetObserver() {
	@Override
	public void onChanged() {
	    super.onChanged();
	    // checking if the adapter has actions
	    if (mBeaconActionAdapter.getCount() == 0) {
		// showing the no actions info message
		mNoActionsWarningView.setVisibility(View.VISIBLE);
	    } else {
		mNoActionsWarningView.setVisibility(View.GONE);
	    }
	}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	LinearLayout fragmentRootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_beacon_editor, container,
		false);

	// allowing to hide the keyboard by clicking outside of the edit text
	// fields
	fragmentRootLayout.setOnTouchListener(new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
		hideKeyboard();
		return true;
	    }
	});

	// retrieving the actions list view
	ListView actionsListView = (ListView) fragmentRootLayout.findViewById(R.id.beacon_actions);

	// setting the actions adapter
	actionsListView.setAdapter(mBeaconActionAdapter);

	// initializing the fragment elements
	initializeEditorControls(fragmentRootLayout);

	if (mBeaconDtoCandidate.getActions().isEmpty()) {
	    mNoActionsWarningView.setVisibility(View.VISIBLE);
	} else {
	    mNoActionsWarningView.setVisibility(View.GONE);
	}

	if (isEditMode()) {
	    bindItemToControls();
	}

	return fragmentRootLayout;
    }

    @Override
    public int getDisplayTitleId() {
	return isEditMode() ? R.string.action_bar_title_edit_beacon : R.string.action_bar_title_add_beacon;
    }

    @Override
    protected void hideKeyboard() {
	View focusedView = getActivity().getCurrentFocus();
	if (null != focusedView) {
	    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
		    Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
	    // requesting the focus on the root view
	    getActivity().findViewById(R.id.beacon_editor).requestFocus();
	}
    }

    @Override
    protected void finish() {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// removing the current fragment
	fragmentTransaction.remove(this);

	// attaching the beacon list view fragment
	fragmentTransaction.replace(R.id.content, new BeaconListFragment());

	fragmentTransaction.commit();
    }

    @Override
    protected boolean populateItem() {
	boolean valid = isEditorInputDataValid();
	if (valid) {
	    // setting namemBeaconDtoCandidate
	    mBeaconDtoCandidate.setName(mBeaconNameControl.getText().toString());
	    mBeaconDtoCandidate.setUuid(mBeaconUuidControl.getText().toString());
	    mBeaconDtoCandidate.setMajorId(Integer.parseInt(mBeaconMajorIdControl.getText().toString()));
	    mBeaconDtoCandidate.setMinorId(Integer.parseInt(mBeaconMinorIdControl.getText().toString()));
	}
	return valid;
    }

    @Override
    protected boolean isEditorInputDataValid() {
	boolean beaconNameValid = validateBeaconNameControl();
	boolean beaconUuidValid = validateBeaconUuidControl();
	boolean beaconMajorIdValid = validateBeaconMajorIdControl();
	boolean beaconMinorIdValid = validateBeaconMinorIdControl();
	boolean beaconAcionsValid = validateBeaconActions();
	return beaconNameValid && beaconUuidValid && beaconMajorIdValid && beaconMinorIdValid && beaconAcionsValid;
    }

    @Override
    protected void initializeEditorControls(LinearLayout rootLayout) {
	mBeaconNameControl = (EditText) rootLayout.findViewById(R.id.beacon_name);
	mBeaconNameControl.addTextChangedListener(mTextWatcher);
	mBeaconNameControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mBeaconNameControl.setError(null);
		if (!hasFocus) {
		    validateBeaconNameControl();
		}
	    }

	});

	mBeaconUuidControl = (EditText) rootLayout.findViewById(R.id.beacon_uuid);
	mBeaconUuidControl.addTextChangedListener(mTextWatcher);
	mBeaconUuidControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mBeaconUuidControl.setError(null);
		if (!hasFocus) {
		    validateBeaconUuidControl();
		}
	    }

	});

	mBeaconMajorIdControl = (EditText) rootLayout.findViewById(R.id.beacon_major_id);
	mBeaconMajorIdControl.addTextChangedListener(mTextWatcher);
	mBeaconMajorIdControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mBeaconMajorIdControl.setError(null);
		if (!hasFocus) {
		    validateBeaconMajorIdControl();
		}
	    }

	});

	mBeaconMinorIdControl = (EditText) rootLayout.findViewById(R.id.beacon_minor_id);
	mBeaconMinorIdControl.addTextChangedListener(mTextWatcher);
	mBeaconMinorIdControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		mBeaconMinorIdControl.setError(null);
		if (!hasFocus) {
		    validateBeaconMinorIdControl();
		}
	    }

	});

	mNoActionsWarningView = (TextView) rootLayout.findViewById(R.id.beacon_actions_empty);

	mAddNewActionButton = (ImageButton) rootLayout.findViewById(R.id.beacon_action_add);
	mAddNewActionButton.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		startActionEditorFragment(new BeaconActionDto(), false);
	    }
	});
    }

    protected boolean validateBeaconNameControl() {
	if (mBeaconNameControl.getText().length() < 1) {
	    mBeaconNameControl.setError("the beacon name cannot be empty");
	    return false;
	}
	return true;
    }

    protected boolean validateBeaconUuidControl() {
	Editable beaconUuidEditable = mBeaconUuidControl.getText();
	if (beaconUuidEditable.length() < 1) {
	    mBeaconUuidControl.setError("the beacon uuid cannot be empty");
	    return false;
	}
	return true;
    }

    protected boolean validateBeaconMajorIdControl() {
	Editable candidate = mBeaconMajorIdControl.getText();
	if (candidate.length() < 1 || "-".equals(candidate.toString())) {
	    mBeaconMajorIdControl.setError("the beacon major id cannot be empty");
	    return false;
	}
	try {
	    Integer.parseInt(candidate.toString());
	} catch (NumberFormatException e) {
	    Log.d(TAG, "unable to parse major id: " + candidate.toString());
	    return false;
	}
	return true;
    }

    protected boolean validateBeaconMinorIdControl() {
	Editable candidate = mBeaconMinorIdControl.getText();
	if (candidate.length() < 1 || "-".equals(candidate.toString())) {
	    mBeaconMinorIdControl.setError("the beacon major id cannot be empty");
	    return false;
	}
	try {
	    Integer.parseInt(candidate.toString());
	} catch (NumberFormatException e) {
	    Log.d(TAG, "unable to parse minor id: " + candidate.toString());
	    return false;
	}
	return true;
    }

    protected boolean validateBeaconActions() {
	if (mBeaconDtoCandidate.getActions().isEmpty()) {
	    Toast.makeText(getActivity(), "The beacon has to have one action at least", Toast.LENGTH_SHORT).show();
	    return false;
	}

	return true;
    }

    @Override
    protected Serializable getItem() {
	return mBeaconDtoCandidate;
    }

    @Override
    protected void setItem(Serializable candidate) {
	if (candidate instanceof BeaconDto) {
	    this.mBeaconDtoCandidate = (BeaconDto) candidate;
	    // initializing the actions adapter
	    mBeaconActionAdapter = new BeaconActionArrayAdapter(getActivity(), mBeaconDtoCandidate.getActions());
	    mBeaconActionAdapter.setOnItemClickListener(mOnBeaconAdapterClickListener);
	    mBeaconActionAdapter.registerDataSetObserver(mBeaconActionsDataSetObserver);
	}
    }

    @Override
    protected void bindItemToControls() {
	mBeaconNameControl.setText(mBeaconDtoCandidate.getName());
	mBeaconUuidControl.setText(mBeaconDtoCandidate.getUuid());
	if (null != mBeaconDtoCandidate.getMajorId()) {
	    mBeaconMajorIdControl.setText(String.valueOf(mBeaconDtoCandidate.getMajorId()));
	}
	if (null != mBeaconDtoCandidate.getMinorId()) {
	    mBeaconMinorIdControl.setText(String.valueOf(mBeaconDtoCandidate.getMinorId()));
	}
    }

    private void startActionEditorFragment(BeaconActionDto candidate, boolean editMode) {
	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

	// detaching the current fragment
	fragmentTransaction.detach(BeaconEditorFragment.this);

	BeaconActionEditorFragment actionEditor = new BeaconActionEditorFragment();
	Bundle arguments = new Bundle();
	arguments.putSerializable(CANDIDATE_TO_EDIT_KEY, candidate);
	arguments.putBoolean(EDIT_MODE_KEY, editMode);
	actionEditor.setArguments(arguments);
	actionEditor.setOnEditorCallback(mOnBeaconActionEditorCallback);

	fragmentTransaction.add(R.id.content, actionEditor);
	fragmentTransaction.commit();
    }
    
}

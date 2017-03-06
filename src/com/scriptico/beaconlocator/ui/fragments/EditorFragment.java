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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.ui.fragments.callbacks.OnBeaconEditorCallback;

/**
 * Base abstract fragment for all application editors
 * 
 * Before to start the fragment the editor mode and item to edit must be set
 * 
 * To listen for the editor actions such as save or delete, the
 * {@link OnBeaconEditorCallback} should be set
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 */
public abstract class EditorFragment extends AbstractApplicationFragment {

    /**
     * Key to store/retrieve the item to edit in {@link Bundle} for the editor
     * fragment before its star
     */
    public static final String CANDIDATE_TO_EDIT_KEY = "candidate";

    /**
     * Key to store/retrieve the current editor mode in {@link Bundle} for the
     * editor fragment
     */
    public static final String EDIT_MODE_KEY = "editMode";

    private static final String TAG = BeaconListFragment.class.getSimpleName();

    /**
     * Holds the the editor mode
     */
    private boolean mEditMode = false;

    /**
     * Holds a flag if the item to edit has changes
     */
    private boolean mHasChanges = false;

    /**
     * Callback to listen the editor actions
     */
    private OnBeaconEditorCallback onEditorCallback;

    /**
     * Text watched to handle text inputs changes
     * 
     * {@link TextWatcher#onTextChanged(CharSequence, int, int, int)} and
     * {@link TextWatcher#beforeTextChanged(CharSequence, int, int, int)} should
     * not have any validation logic if a onFocus listener for the input actions
     * is set
     */
    protected final TextWatcher mTextWatcher = new TextWatcher() {

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	    // validation is performed when the control focus is lost
	    Log.d(TAG, "mTextWatcher.onTextChanged: no action is required");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    // validation is performed when the control focus is lost
	    Log.d(TAG, "mTextWatcher.beforeTextChanged: no action is required");
	}

	@Override
	public void afterTextChanged(Editable s) {
	    // marking the item to edit as changed
	    setHasChanges(true);
	}
    };

    /**
     * Alert dialog onClick listener to process
     * {@link DialogInterface#BUTTON_POSITIVE} and
     * {@link DialogInterface#BUTTON_NEGATIVE} clicks
     */
    private final DialogInterface.OnClickListener mDialogFinishClickListener = new DialogInterface.OnClickListener() {

	@Override
	public void onClick(DialogInterface dialog, int which) {
	    switch (which) {
	    case DialogInterface.BUTTON_POSITIVE:
		// populating the edited item
		boolean result = populateItem();
		if (result) {
		    if (null != onEditorCallback) {
			// notifying the call back on finish
			onEditorCallback.onFinish(getItem());
		    }
		    // finishing the current fragment
		    finish();
		    break;
		}
		// the edited data contains errors
		Toast.makeText(getActivity(), "Unable to save changes\nSome input fields have errors", Toast.LENGTH_SHORT).show();
		break;

	    case DialogInterface.BUTTON_NEGATIVE:
		// finishing the current fragment without saving changes
		finish();
	    }
	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// retrieving and setting item to edit
	setItem(getArguments().getSerializable(CANDIDATE_TO_EDIT_KEY));

	// retrieving the current mode flag
	mEditMode = getArguments().getBoolean(EDIT_MODE_KEY);
	
	super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// resolving the proper menu id for the current mode
	int menuId = isEditMode() ? R.menu.menu_editor_edit : R.menu.menu_editor_new;
	// inflating the action bar menu
	inflater.inflate(menuId, menu);
    }

    /**
     * Returns the current edit mode
     * 
     * The editor fragment can be started either in the edit or new mode
     * 
     * The current fragment editor mode should be defined before the fragment
     * starting by setting {@link EditorFragment#EDIT_MODE_KEY} in the fragment
     * settings {@link Bundle#putBoolean(String, boolean)}
     * 
     * @return true if the fragment in the edit mode; otherwise, false will be
     *         returned
     */
    public boolean isEditMode() {
	return mEditMode;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// hiding the keyboard
	hideKeyboard();

	switch (item.getItemId()) {
	case android.R.id.home:
	    // checking if the object was edited
	    if (!hasChanges()) {
		// no changes were found, finishing the current fragment
		finish();
		return true;
	    }
	    // the item to edit was changed, showing the alert box
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder = builder.setMessage(R.string.warning_unsaved_changes);
	    builder = builder.setPositiveButton(R.string.btn_yes, mDialogFinishClickListener);
	    builder = builder.setNegativeButton(R.string.btn_no, mDialogFinishClickListener);
	    builder.show();

	    return true;
	case R.id.menu_item_editor_save:
	    // checking if the item to edit was changed
	    if (hasChanges()) {
		// checking if the entered data is valid
		if (isEditorInputDataValid()) {
		    // data is valid, populating the item
		    populateItem();
		    if (null != onEditorCallback) {
			// notifying the editor callback
			onEditorCallback.onSave(getItem());
		    }
		    // the item to edit has been saved, marking the item as not
		    // changed
		    setHasChanges(false);
		    // showing the successfully saved changes message
		    Toast.makeText(getActivity(), "The beacon changes were successfully saved", Toast.LENGTH_SHORT).show();
		} else {
		    // the entered data has errors, showing the warning message
		    Toast.makeText(getActivity(), "Unable to save the beacon action\nSome input fields have errors", Toast.LENGTH_SHORT).show();
		}
	    }
	    return true;
	case R.id.menu_item_editor_delete:
	    if (null != onEditorCallback) {
		// notifying the editor callback on delete
		onEditorCallback.onDelete(getItem());
		// finishing the current fragment
		finish();
	    }
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    /**
     * Sets the editor callback
     * 
     * @see OnBeaconEditorCallback
     * 
     * @param callback
     *            - instance of {@link OnBeaconEditorCallback}
     */
    public final void setOnEditorCallback(OnBeaconEditorCallback callback) {
	this.onEditorCallback = callback;
    }

    /**
     * Checks if the item to edit was changed
     * 
     * @return true if the item was changed; otherwise, false will be returned
     */
    protected final boolean hasChanges() {
	return mHasChanges;
    }

    /**
     * Marks the item to edit as changed
     * 
     * @param hasChanges
     */
    protected final void setHasChanges(boolean hasChanges) {
	this.mHasChanges = hasChanges;
    }

    /**
     * Hides the keyboard and removes the focus from the first input on the view
     * (default android behavior).
     * 
     * The root layout has to be defined as focusable and focusableInTouchMode
     * to support such custom behavior
     * 
     */
    protected abstract void hideKeyboard();

    /**
     * Finishing the current fragment
     */
    protected abstract void finish();

    /**
     * Populating the edited item
     * 
     * @see EditorFragment#isEditorInputDataValid()
     * 
     * @return false if the item was not populated due the invalid data;
     *         otherwise, true will be returned
     * 
     */
    protected abstract boolean populateItem();

    /**
     * Checks if the changed data is valid
     * 
     * @return true if the data is valid; otherwise, false will be returned
     */
    protected abstract boolean isEditorInputDataValid();

    /**
     * Initialized the editor controls (e.g. inputs, spinners, etc.)
     * 
     * @param rootLayout
     *            - the fragment root view
     */
    protected abstract void initializeEditorControls(LinearLayout rootLayout);

    /**
     * Returns the item
     * 
     * The returned item will not be populated
     * 
     * @return the item to edit
     */
    protected abstract Serializable getItem();

    /**
     * Sets the current item
     * 
     * @param candidate
     *            - the candidate to edit
     */
    protected abstract void setItem(Serializable candidate);

    /**
     * Binds the item to edit to the fragment controls
     * 
     * The method must be called after
     * {@link EditorFragment#initializeEditorControls(LinearLayout)}
     * 
     */
    protected abstract void bindItemToControls();
}

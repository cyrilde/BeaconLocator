package com.scriptico.beaconlocator.ui.fragments;

import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.scriptico.beaconlocator.R;
import com.scriptico.beaconlocator.data.dto.BeaconActionDto;
import com.scriptico.beaconlocator.data.dto.MediaContentHolderDto;
import com.scriptico.beaconlocator.data.dto.enums.ContentType;
import com.scriptico.beaconlocator.logic.service.BeaconLocatorService;

public class BeaconContentFragment extends Fragment {
    private static final String TAG = BeaconContentFragment.class.getSimpleName();

    private BeaconActionDto mBeaconAction;

    private ImageView mImageContentView;

    private TextView mNoBeaconActionFoundWarning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// retrieving the beacon action object
	mBeaconAction = (BeaconActionDto) getArguments().getSerializable(BeaconLocatorService.BEACON_ACTION_KEY);

	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	getActivity().getActionBar().hide();

	LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_content, container, false);

	if (null == mBeaconAction) {
	    // something went wrong, showing the warning message
	    ((TextView) rootLayout.findViewById(R.id.beacon_action_empty)).setVisibility(View.VISIBLE);

	    return rootLayout;
	}

	// beacon action exists; initializing the proper content view
	// TODO adjust code later: use the custom view and place it to the
	// holder.

	if (mBeaconAction.getContentType() == ContentType.IMAGE) {
	    // initializing image content view
	    mImageContentView = (ImageView) rootLayout.findViewById(R.id.image_content_view);

	    // close it on click
	    mImageContentView.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    startHomeFragment();

		    // sending the activity to background
		    Intent intent = new Intent(Intent.ACTION_MAIN);
		    intent.addCategory(Intent.CATEGORY_HOME);
		    startActivity(intent);
		}
	    });

	    // setting the content image
	    // TODO settings define if the image has to be scaled
	    try {
		Bitmap image = BitmapFactory.decodeStream(getActivity().getAssets().open(
			mBeaconAction.getContent().getUrl()));
		mImageContentView.setImageBitmap(image);
	    } catch (IOException e) {
		// TODO has to be changed
		Log.d(TAG, "an IOException occurred at the content fragment. failed to set the image content");
	    }
	    
	    return rootLayout;
	}

	if (mBeaconAction.getContentType() == ContentType.VIDEO) {
	    // TODO initializing video content view
	    return rootLayout;
	}

	return rootLayout;
    }

    @Override
    public void onDestroy() {
	getActivity().sendBroadcast(new Intent(BeaconLocatorService.START_LOCATOR_SCANNER));

	super.onDestroy();
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
}

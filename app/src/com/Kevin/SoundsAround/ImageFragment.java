package com.Kevin.SoundsAround;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Kevin on 5/1/2014.
 */
public class ImageFragment extends Fragment {
    public static final int BUNDLE_SIZE = 1;
    public static final String IMAGE_ID = "IMAGE";

    public static final ImageFragment newInstance(int image) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle(BUNDLE_SIZE);
        bundle.putInt(IMAGE_ID, image);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int imageID = getArguments().getInt(IMAGE_ID);
        View v = inflater.inflate(R.layout.image_fragment, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageFragmentView);
        imageView.setImageResource(imageID);
        return v;
    }
}

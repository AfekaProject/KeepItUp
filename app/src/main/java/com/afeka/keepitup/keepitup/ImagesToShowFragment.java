package com.afeka.keepitup.keepitup;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ImagesToShowFragment extends Fragment {
    private final static String ID_BUNDLE = "ID";
    private ViewPager mPager;
    private ArrayList<Bitmap> imgArr = new ArrayList<>();// for checking!
    private Database db;
    private int currentTransID;

    private OnFragmentInteractionListener mListener;

    public ImagesToShowFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ImagesToShowFragment newInstance(String param1, String param2) {
        ImagesToShowFragment fragment = new ImagesToShowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images_to_show, container, false);


      //  imgArr.add(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrowdown_icon));
     //   imgArr.add(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage_icon));

        currentTransID = getArguments().getInt(ID_BUNDLE);
        db = new Database(getContext());
        imgArr = db.getTransactionById(currentTransID).getDocuments();
        mPager = view.findViewById(R.id.pager);
        System.out.println("num of images: " + imgArr.size() );

        ArrayList<Bitmap> imgToShow =new ArrayList<>();


        for(int i=0; i <imgToShow.size(); i++){
            imgToShow.add(getImageResized(getContext(),getImageUri(getContext(),imgArr.get(i))));
        }

        ImageAdapter pagerAdapter = new ImageAdapter(getContext(),imgArr);
        mPager.setAdapter(pagerAdapter);


        return view;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{50, 50, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm.getWidth() < 400 && i < sampleSizes.length);
        return bm;
    }
    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        return actuallyUsableBitmap;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     /*   if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package com.afeka.keepitup.keepitup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagesFragment extends Fragment implements FragmentCallback{
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;
    private static final int REQUEST_IMG_ID = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_OK = 400;
    private static final String TEMP_IMAGE_NAME = "tempImg";
    private static final int MAX_IMGS = 5;
    private Button addNewImgButton;
    private static int MIN_WIDTH = DEFAULT_MIN_WIDTH_QUALITY;
    private ImageSwitcher imgSwitcher;
    private ArrayList <Bitmap> bmList = new ArrayList<>();
    private Button prevBtn;
    private Button nextBtn;
    private int switcherPosition = -1;
    private Button submitBtn;
    private ImageView deleteBtn;

    private OnFragmentInteractionListener mListener;

    public ImagesFragment() {
        // Required empty public constructor
    }

    private FragmentCallback fragmentCallback;

    public void setFragmentCallback(FragmentCallback callback) {
        this.fragmentCallback = callback;
    }
    // TODO: Rename and change types and number of parameters
    public static ImagesFragment newInstance(String param1, String param2) {
        ImagesFragment fragment = new ImagesFragment();
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
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_images, container, false);



        addNewImgButton = view.findViewById(R.id.add_img_button);
        addNewImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent chooseImageIntent = getPickImageIntent(getContext());
                //startActivityForResult(chooseImageIntent, REQUEST_IMG_ID);
                //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (chooseImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(chooseImageIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });


        imgSwitcher = view.findViewById(R.id.img_switcher);
        nextBtn = view.findViewById(R.id.nextBtn);
        prevBtn = view.findViewById(R.id.prevBtn);

        submitBtn = view.findViewById(R.id.submit_img_button);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fragmentCallback.onDataSent(bmList);
               getFragmentManager().popBackStackImmediate();

            }
        });

        deleteBtn = view.findViewById(R.id.delete_button);

        setImgSwitcher();


        return view;
    }


    private void setImgSwitcher(){
        imgSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imgView = new ImageView(getContext());
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                return imgView;
            }
        });

        if(!bmList.isEmpty())
        imgSwitcher.setImageDrawable(new BitmapDrawable(getContext().getResources(), bmList.get(0)));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switcherPosition < bmList.size()-1 ) {
                    switcherPosition++;
                    imgSwitcher.setImageDrawable(new BitmapDrawable(getContext().getResources(), bmList.get(switcherPosition)));
                }
                deleteBtn.setVisibility(View.GONE);
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(switcherPosition > 0) {
                    switcherPosition--;
                    imgSwitcher.setImageDrawable(new BitmapDrawable(getContext().getResources(), bmList.get(switcherPosition)));
                }
                deleteBtn.setVisibility(View.GONE);
            }
        });

       imgSwitcher.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!bmList.isEmpty())
            deleteBtn.setVisibility(View.VISIBLE);
           }
       });

       deleteBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               bmList.remove(switcherPosition);

               if(switcherPosition > 0 )
                   switcherPosition--;

               if(bmList.size() > 0)
               imgSwitcher.setImageDrawable(new BitmapDrawable(getContext().getResources(),bmList.get(switcherPosition)));
               else
                  imgSwitcher.setImageDrawable(null);

                deleteBtn.setVisibility(View.GONE);
           }
       });

    }

    public void setBmList(ArrayList<Bitmap> bmList) {
        this.bmList = bmList;
        switcherPosition = 0;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
          //  Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap imageBitmap = getImageFromResult(getActivity(), resultCode, data);
            if(bmList.size() < MAX_IMGS){
                bmList.add(imageBitmap);
                switcherPosition++;
            }
            Drawable drawable = new BitmapDrawable(getContext().getResources(), imageBitmap);
            imgSwitcher.setImageDrawable(drawable);
        }


        /*
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case REQUEST_IMG_ID:
                    Bitmap bitmap = getImageFromResult(getActivity(), resultCode, data);
                    Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);

                    imgSwitcher.setImageDrawable(drawable);

                    if(bmList.size() < MAX_IMGS){
                        bmList.add(bitmap);
                        switcherPosition++;
                    }

                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
*/


    }

    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) {
        Bitmap bm = null;
        File imageFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null  ||
                    imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {
                selectedImage = Uri.fromFile(imageFile);

            } else {
                selectedImage = imageReturnedIntent.getData();
            }


            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }

    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }

        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {

            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{20, 20, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm.getWidth() < MIN_WIDTH && i < sampleSizes.length);
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

    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;

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
      /*  if (context instanceof OnFragmentInteractionListener) {
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

    @Override
    public void onDataSent(ArrayList<Bitmap> bitmapList) {
        fragmentCallback.onDataSent(bmList);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("RestrictedApi")
    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));


        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.img_select));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

}
interface FragmentCallback {
    void onDataSent(ArrayList<Bitmap> bitmapList);
}


package com.afeka.keepitup.keepitup;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter{
    private ArrayList<Bitmap> imgArr;
    private Context context;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, ArrayList<Bitmap> imgArr) {
        this.context = context;
        this.imgArr = imgArr;

        if(imgArr.size() == 0)
            imgArr.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.noimage_icon));

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imgArr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //swipe images

        View imgLayout = inflater.inflate(R.layout.sliding_img, container, false);

        ImageView imgView = imgLayout.findViewById(R.id.image_sliding);
        imgView.setImageBitmap(getResizedBitmap(imgArr.get(position), container.getMeasuredWidth(),container.getMeasuredHeight()));
        container.addView(imgLayout,0);

        return imgLayout;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}

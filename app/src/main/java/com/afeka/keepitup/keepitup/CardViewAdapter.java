package com.afeka.keepitup.keepitup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;

public class CardViewAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Integer> cardsToShow;

    public CardViewAdapter(Context context, ArrayList<Integer> list) {
        this.context = context;
        this.cardsToShow = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cardsToShow.size();
    }

    @Override
    public Object getItem(int i) {
        return cardsToShow.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = inflater.inflate(R.layout.my_card,
                    parent, false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        return view;
    }

    static class ViewHolder {
        private TextView nameTextView;

    }
    }


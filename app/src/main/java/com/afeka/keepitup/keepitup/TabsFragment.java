package com.afeka.keepitup.keepitup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TabsFragment extends Fragment {
    private GridView mGrid;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private TextView newItemButton;
    private OnFragmentInteractionListener mListener;

    public TabsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabsFragment newInstance(String param1, String param2) {
        TabsFragment fragment = new TabsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        mGrid = view.findViewById(R.id.card_grid);
        ArrayList<Integer> checkingArr = new ArrayList<>();
        checkingArr.add(1);
        checkingArr.add(2);
        CardViewAdapter cardAdapter = new CardViewAdapter(getContext(),checkingArr);
        mGrid.setAdapter(cardAdapter);

         Spinner spinner = view.findViewById(R.id.spinner_filter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.warranty_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getContext(), i + "selected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            newItemButton = view.findViewById(R.id.addItem);
            newItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MenuActivity)getActivity()).replaceFragment(new NewTransFragment());
                }
            });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNewItemClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
  /*    if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewItemClicked();
    }



}

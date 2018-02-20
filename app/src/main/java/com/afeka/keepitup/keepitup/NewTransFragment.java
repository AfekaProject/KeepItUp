package com.afeka.keepitup.keepitup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.Settings.System.DATE_FORMAT;


public class NewTransFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Spinner chargeTypeSpinner;
    private Spinner notificSpinner;
    private ImageView calendarStart;
    private ImageView calendarEnd;
    private EditText startDate;
    private EditText endDate;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewTransFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTransFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTransFragment newInstance(String param1, String param2) {
        NewTransFragment fragment = new NewTransFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_trans, container, false);

        //initial charge spinner
        chargeTypeSpinner = view.findViewById(R.id.charge_spinner);
        ArrayAdapter<CharSequence> chargeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.charge_type, android.R.layout.simple_spinner_item);
        chargeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chargeTypeSpinner.setAdapter(chargeAdapter);

        //initial notification spinner
        notificSpinner = view.findViewById(R.id.notificationTime_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.notification_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificSpinner.setAdapter(adapter);
        notificSpinner.setSelection(0);

        calendarStart = view.findViewById(R.id.calendar_start);
        calendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment newFragment = new CalendarFragment();
                                newFragment.show(getActivity().getSupportFragmentManager(), "dateStartPicker");
                newFragment.setCallBack(onStartDate);
            }
        });

        calendarEnd = view.findViewById(R.id.calendar_end);
        calendarEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment newFragment = new CalendarFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "dateEndPicker");
                newFragment.setCallBack(onEndDate);

            }
        });
        startDate = view.findViewById(R.id.startDate_trans);
        endDate = view.findViewById(R.id.endDate_trans);




            return view;
    }

    DatePickerDialog.OnDateSetListener onStartDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            startDate.setText(dayOfMonth+"/"+month+"/"+year);
        }
    };

        DatePickerDialog.OnDateSetListener onEndDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            endDate.setText(dayOfMonth+"/"+month+"/"+year);
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}



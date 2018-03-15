package com.afeka.keepitup.keepitup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class TransactionShowFragment extends Fragment {
    private Database db;
    private int transactionID;
    private Transaction currentTranscation;
    private TextView name, company, startDate, endDate, notification, notes;
    private OnFragmentInteractionListener mListener;
    private View toImgButton;

    public TransactionShowFragment() {
        // Required empty public constructor
    }


    public static TransactionShowFragment newInstance(String param1, String param2) {
        TransactionShowFragment fragment = new TransactionShowFragment();
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
        View view = inflater.inflate(R.layout.fragment_transaction_show, container, false);
        setTransactionID();

        toImgButton = view.findViewById(R.id.watchImg_button);

        toImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID",transactionID);
                ImagesToShowFragment imagesToShowFragment = new ImagesToShowFragment();
                imagesToShowFragment.setArguments(bundle);
                ((MenuActivity)getActivity()).replaceFragment(imagesToShowFragment);
            }
        });


        return view;
    }

    private void setTransactionID(){
        Bundle bundle = getArguments();
        if(bundle != null)
        transactionID = bundle.getInt("ID");

        //get transaction from db by id


    }

    private void setInfo(View view){
        name = view.findViewById(R.id.name_textView);
        company = view.findViewById(R.id.company_textView);
        startDate = view.findViewById(R.id.fromDate_textView);
        endDate = view.findViewById(R.id.toDate_textView);
        notification = view.findViewById(R.id.notification_textView);

        name.setText(getResources().getText(R.string.name) + currentTranscation.getName());

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
   /*     if (context instanceof OnFragmentInteractionListener) {
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

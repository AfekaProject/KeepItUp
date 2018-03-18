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

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class TransactionShowFragment extends Fragment {
    private static final String ID_BUNDLE = "ID";
    private static final String EDIT_BUNDLE = "EDIT";
    private Database db;
    private int transactionID;
    private Transaction currentTransaction;
    private TextView name, company,price, startDate, endDate, notification, notes, chargeType, numOfImg;
    private OnFragmentInteractionListener mListener;
    private View toImgButton;
    private Button editButton;

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
        setInfo(view);
        toImgButton = view.findViewById(R.id.watchImg_button);

        toImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(ID_BUNDLE,transactionID);
                ImagesToShowFragment imagesToShowFragment = new ImagesToShowFragment();
                imagesToShowFragment.setArguments(bundle);
                ((MenuActivity)getActivity()).replaceFragment(imagesToShowFragment);
            }
        });

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(EDIT_BUNDLE,transactionID);
                NewTransFragment newTransFragment = new NewTransFragment();
                newTransFragment.setArguments(bundle);
                ((MenuActivity)getActivity()).replaceFragment(newTransFragment);
            }
        });

        return view;
    }

    private void setTransactionID(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            transactionID = bundle.getInt(ID_BUNDLE);

            //get transaction from db by id
            db = new Database(getContext());
            System.out.println("ID to show: " + transactionID);
            currentTransaction = db.getTransactionById(transactionID);

        }
    }

    private void setInfo(View view){
        name = view.findViewById(R.id.name_textView);
        company = view.findViewById(R.id.company_textView);
        startDate = view.findViewById(R.id.fromDate_textView);
        endDate = view.findViewById(R.id.toDate_textView);
        chargeType = view.findViewById(R.id.charge_textView);
        notification = view.findViewById(R.id.notification_textView);
        notes = view.findViewById(R.id.notes_textView);
        numOfImg = view.findViewById(R.id.numOfImg_textView);


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        name.setText(currentTransaction.getName());
        company.setText(currentTransaction.getCompany());
        startDate.setText(df.format(currentTransaction.getStartDate()));
        endDate.setText(df.format(currentTransaction.getEndDate()));
        chargeType.setText(currentTransaction.getChargeType().toString());
        notification.setText(currentTransaction.getNotification().toString());
        notes.setText(currentTransaction.getNotes());
        price.setText(Double.toString(currentTransaction.getPrice()));
        numOfImg.setText(getString(R.string.numOfImg) +" " +  Integer.toString(currentTransaction.getDocuments().size()));

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

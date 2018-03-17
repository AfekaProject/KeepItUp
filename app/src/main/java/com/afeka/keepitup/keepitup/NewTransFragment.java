package com.afeka.keepitup.keepitup;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewTransFragment extends Fragment {

    private static final int ALARAM = 100;
    private static final String ARG_PARAM2 = "param2";
    private Database db;
    private Spinner chargeTypeSpinner,notificSpinner, transTypeSpinner;
    private ImageView calendarStartDate,calendarEndDate;
    private EditText startDate,endDate;
    private Button addImgButton,submitBtn;
    private TextView numOfImgView;
    private ArrayList<Bitmap> bmList;
    private Date calStart = null, calEnd = null;
    private TextView errorView, name, companyName, price, notes;
    private int transType, details;
    private Transaction currentTransaction;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public NewTransFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewTransFragment newInstance(String param1, String param2) {
        NewTransFragment fragment = new NewTransFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Database db = new Database(getContext());
       // Transaction t = new Transaction.TransactionBuilder(0,"Guy", Transaction.TransactionType.Provider,"Migdal", android.icu.util.Calendar.getInstance().getTime()).build();
        //db.addTransaction(t);
        //Transaction tran = db.getTransactionList(Transaction.TransactionType.Provider).get(0);
        //Log.e("lala", tran.getName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_trans, container, false);

        numOfImgView = view.findViewById(R.id.numOfImg_text);

           if(bmList!=null)
            numOfImgView.setText(Integer.toString(bmList.size() ));

        setTransType(view);
        //type spinner
        transTypeSpinner = view.findViewById(R.id.typeTrans_spinner);
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

        calendarStartDate = view.findViewById(R.id.calendar_start);
        calendarStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment newFragment = new CalendarFragment();
                                newFragment.show(getActivity().getSupportFragmentManager(), "dateStartPicker");
                newFragment.setCallBack(onStartDate);
            }
        });

        calendarEndDate = view.findViewById(R.id.calendar_end);
        calendarEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment newFragment = new CalendarFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "dateEndPicker");
                newFragment.setCallBack(onEndDate);

            }
        });
        startDate = view.findViewById(R.id.startDate_trans);
        endDate = view.findViewById(R.id.endDate_trans);


        addImgButton = view.findViewById(R.id.add_img_button);
        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ImagesFragment imagesFragment =  new ImagesFragment();
               imagesFragment.setFragmentCallback(fragmentCallback);

               if(bmList!=null)
                   imagesFragment.setBmList(bmList);

                ((MenuActivity)getActivity()).replaceFragment(imagesFragment);
            }
        });

        name = view.findViewById(R.id.name_trans);
        companyName = view.findViewById(R.id.company_trans);
        price = view.findViewById(R.id.price_trans);
        notes = view.findViewById(R.id.notes_trans);
        errorView = view.findViewById(R.id.error_textView);
        submitBtn = view.findViewById(R.id.submit_button);

        setDetails();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createTrans()) {
                    Snackbar snackbar = Snackbar.make(view,"Transaction was added!", BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.show();
                    getFragmentManager().popBackStackImmediate();
                }


            }
        });




        return view;
    }
   FragmentCallback fragmentCallback = new FragmentCallback() {
        @Override
        public void onDataSent(ArrayList<Bitmap> bitmapList) {
            bmList = bitmapList;
        }
    };

    DatePickerDialog.OnDateSetListener onStartDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {
           Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calStart = cal.getTime();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            startDate.setText(df.format(calStart));
        }
    };

        DatePickerDialog.OnDateSetListener onEndDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calEnd = cal.getTime();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            endDate.setText(df.format(calEnd));
        }
    };


    private void setTransType(View view){
        Bundle bundle = getArguments();
        transType = bundle.getInt("TYPE");
        Spinner typeSpinner = view.findViewById(R.id.typeTrans_spinner);

        if(transType != -1) {
            typeSpinner.setSelection(transType);
            typeSpinner.setEnabled(false);
        }

    }

    private Boolean createTrans(){
        transType = transTypeSpinner.getSelectedItemPosition();
        if(checkValidInfo()) {
            Transaction.TransactionBuilder transaction =
                    new Transaction.TransactionBuilder(0, name.getText().toString(),
                            Transaction.TransactionType.values()[transType]
                            , companyName.getText().toString(), calStart);

            if (calEnd != null)
                transaction.setEndDate(calEnd);
            else if(price.getText().length() > 0 )
                transaction.setPrice(Double.parseDouble(price.getText().toString()));
            else if(notes.getText().length() > 0)
                transaction.setNotes(notes.getText().toString());



            int position = notificSpinner.getSelectedItemPosition();
            switch (position){
                case 0:
                    transaction.setNotification(Transaction.ForwardNotification.Never);
                    break;
                case 1:
                    transaction.setNotification(Transaction.ForwardNotification.OneDay);
                    break;
                case 2:
                    transaction.setNotification(Transaction.ForwardNotification.TwoDays);
                    break;
                case 3:
                    transaction.setNotification(Transaction.ForwardNotification.TreeDays);
                    break;
                case 4:
                    transaction.setNotification(Transaction.ForwardNotification.Week);
                    break;

                default:
                    transaction.setNotification(Transaction.ForwardNotification.Never);
                    break;
            }

            position = chargeTypeSpinner.getSelectedItemPosition();
            switch (position){
                case 0:
                    transaction.setChargeType(Transaction.ChargeType.Cash);
                    break;
                case 1:
                    transaction.setChargeType(Transaction.ChargeType.CreditCard);
                    break;
                case 2:
                    transaction.setChargeType(Transaction.ChargeType.BankCheck);
                    break;
                case 3:
                    transaction.setChargeType(Transaction.ChargeType.StandingOrder);
                    break;

                default:
                    transaction.setChargeType(Transaction.ChargeType.Cash);
                    break;
            }


            transaction.setDocuments(bmList);

            currentTransaction = transaction.build();
            db = new Database(getContext());

            if(details == -1)
                db.removeTransaction(details);

            long id = db.addTransaction(currentTransaction);


            if(notificSpinner.getSelectedItemPosition()!=0)
                setAlarm(name.getText().toString());

                return true;

        }else{
            return false;
        }
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private Boolean checkValidInfo(){
        errorView.setVisibility(View.GONE);
        if(!checkValidDate()) {
            setErrorText("Invalid date!");//need to be in string file!!
            return false;
        }
        else if(name.getText().length() == 0){
            setErrorText("Invalid name!"); //need to be in string file!!
            return false;
        }
        else if(calEnd == null && notificSpinner.getSelectedItemPosition() != 0) {
            setErrorText("End date is necessary for notification!");
            return false;
        }

        return true;
    }

    private void setErrorText(String errorToShow){
        errorView.setVisibility(View.VISIBLE);
        errorView.setTextColor(getResources().getColor(R.color.red));
        errorView.setText(errorToShow);
    }

    private Boolean checkValidDate() {
        if (calStart != null && calEnd != null) {
            if (calEnd.getTime() - calStart.getTime() > 0)
                return true;
            else
                return false;
        }
        else if(calStart!=null)
            return true;

        return false;

    }

    private void setAlarm(String name){
        int notificationTime = notificSpinner.getSelectedItemPosition(), subTimeDay=0;

        switch (notificationTime){
            case 1:
                subTimeDay = -1;
                break;
            case 2:
                subTimeDay = -2;
                break;
            case 3:
                subTimeDay = -3;
                break;
            case 4:
                subTimeDay = -7;
                break;
        }

        Calendar dateToNotification = Calendar.getInstance();
        dateToNotification.setTime(calEnd);
        dateToNotification.add(dateToNotification.DATE,subTimeDay);

        Intent intent = new Intent(getContext(),NotificationReceiver.class);
        intent.putExtra("NAME",name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),currentTransaction.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+100,pendingIntent);
        System.out.println("Notification for " + currentTransaction.getId() + " was added!");
    }

    private void setDetails(){
        Bundle bundle = getArguments();
        details = bundle.getInt("EDIT");

        if(details != -1 ){
            Database db = new Database(this.getActivity().getBaseContext());
            Transaction transaction = db.getTransactionById(details);

            name.setText(transaction.getName());
            companyName.setText(transaction.getCompany());
            startDate.setText(transaction.getStartDate().toString());
            endDate.setText(transaction.getEndDate().toString());
            notes.setText(transaction.getNotes());
        }
    }


}


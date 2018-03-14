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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewTransFragment extends Fragment {

    private static final int ALARAM = 100;
    private static final String ARG_PARAM2 = "param2";
    private Database db;
    private Spinner chargeTypeSpinner;
    private Spinner notificSpinner;
    private ImageView calendarStartDate,calendarEndDate;
    private EditText startDate,endDate;
    private Button addImgButton,submitBtn;
    private TextView numOfImgView;
    private ArrayList<Bitmap> bmList;
    private Date calStart, calEnd;
    private TextView errorView, name, companyName, price, notes;
    private Transaction currentTransaction;



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
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_trans, container, false);

        numOfImgView = view.findViewById(R.id.numOfImg_text);

           if(bmList!=null)
            numOfImgView.setText(Integer.toString(bmList.size() ));


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
            calEnd = cal.getTime();

            endDate.setText(dayOfMonth+"/"+month+"/"+year);
        }
    };

    private Boolean createTrans(){
        if(checkValidInfo()) {
            Transaction.TransactionBuilder transaction =
                    new Transaction.TransactionBuilder(0, name.getText().toString(),
                            Transaction.TransactionType.Insurance, companyName.getText().toString(), calStart);

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

            currentTransaction = transaction.build();
            db = new Database(getContext());
            db.addTransaction(currentTransaction);

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


    private Boolean checkValidInfo(){
        errorView.setVisibility(View.GONE);
        if(!checkValidDate()) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Invalid date!"); //need to be in string file!!
            return false;
        }
        else if(name.getText().length() == 0){
            errorView.setVisibility(View.VISIBLE);
            errorView.setBackgroundColor(getResources().getColor(R.color.red));
            errorView.setText("Invalid name!"); //need to be in string file!!
            return false;
        }
        return true;


    }

    private Boolean checkValidDate() {
        if (calStart != null && calEnd != null) {
            if (calStart.getTime() > calEnd.getTime())
                return false;
        }
        else if(calStart!=null)
            return true;

        return true;

    }

    void setAlaram(){
        Intent intent = new Intent(getContext(),NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),currentTransaction.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


    }


}


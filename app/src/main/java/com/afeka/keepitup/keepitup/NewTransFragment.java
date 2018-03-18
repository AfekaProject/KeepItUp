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

    private final static String NAME_BUNDLE = "NAME";
    private final static String EDIT_BUNDLE = "EDIT";
    private final static String TYPE_BUNDLE = "TYPE";
    private final static String TAG_START_PICKER = "dateStartPicker";
    private final static String TAG_END_PICKER = "dateEndPicker";
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
    private int editFlag = 0;


    private OnFragmentInteractionListener mListener;

    public NewTransFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewTransFragment newInstance(String param1, String param2) {
        NewTransFragment fragment = new NewTransFragment();

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
                                newFragment.show(getActivity().getSupportFragmentManager(), TAG_START_PICKER);
                newFragment.setCallBack(onStartDate);
            }
        });

        calendarEndDate = view.findViewById(R.id.calendar_end);
        calendarEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment newFragment = new CalendarFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), TAG_END_PICKER);
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
                    Snackbar snackbar;
                    if(editFlag == 0)
                    snackbar = Snackbar.make(view,R.string.transAdded, BaseTransientBottomBar.LENGTH_LONG);
                    else
                        snackbar = Snackbar.make(view,R.string.transUpdate, BaseTransientBottomBar.LENGTH_LONG);

                    snackbar.show();
                    TabsFragment tabsFragment = new TabsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(TYPE_BUNDLE, -1);
                    tabsFragment.setArguments(bundle);

                    ((MenuActivity)getActivity()).replaceFragment(tabsFragment);
                }


            }
        });




        return view;
    }
   FragmentCallback fragmentCallback = new FragmentCallback() {
        @Override
        public void onDataSent(ArrayList<Bitmap> bitmapList) {
            bmList = bitmapList;
            numOfImgView.setText(Integer.toString(bmList.size()));
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
        transType = bundle.getInt(TYPE_BUNDLE);
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
                    new Transaction.TransactionBuilder(name.getText().toString(),
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

            if(editFlag == 1)
                db.removeTransaction(details);

            db.addTransaction(currentTransaction,getContext());

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
            setErrorText(getString(R.string.invalidDate));
            return false;
        }
        else if(name.getText().length() == 0){
            setErrorText(getString(R.string.invalidName));
            return false;
        }
        else if(calEnd == null && notificSpinner.getSelectedItemPosition() != 0) {
            setErrorText(getString(R.string.invalidEndDate));
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
        if (endDate.getText().length() >0  && startDate.getText().length() > 0) {
            if (calEnd.getTime() - calStart.getTime() > 0)
                return true;
            else
                return false;
        }
        else if(startDate.getText() != null)
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
        intent.putExtra(NAME_BUNDLE,name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),currentTransaction.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateToNotification.getTimeInMillis(),pendingIntent);
        System.out.println("Notification for " + currentTransaction.getId() + " was added!");
    }

    private void setDetails() {
        Bundle bundle = getArguments();
        details = bundle.getInt(EDIT_BUNDLE);

        if(editFlag == 0) {
            if (details != -1) {
                editFlag = 1;
                Database db = new Database(this.getActivity().getBaseContext());
                Transaction transaction = db.getTransactionById(details);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                bmList = transaction.getDocuments();
                name.setText(transaction.getName());
                companyName.setText(transaction.getCompany());
                calStart = transaction.getStartDate();
                calEnd = transaction.getEndDate();
                startDate.setText(df.format(transaction.getStartDate()));
                endDate.setText(df.format(transaction.getEndDate()));
                notes.setText(transaction.getNotes());
                numOfImgView.setText(Integer.toString(transaction.getDocuments().size()));

                switch (transaction.getChargeType()) {
                    case Cash:
                        chargeTypeSpinner.setSelection(0);
                        break;
                    case CreditCard:
                        chargeTypeSpinner.setSelection(1);
                        break;
                    case BankCheck:
                        chargeTypeSpinner.setSelection(2);
                        break;
                    case StandingOrder:
                        chargeTypeSpinner.setSelection(3);
                        break;
                }

                switch (transaction.getNotification()) {
                    case Never:
                        notificSpinner.setSelection(0);
                        break;
                    case OneDay:
                        notificSpinner.setSelection(1);
                        break;
                    case TwoDays:
                        notificSpinner.setSelection(2);
                        break;
                    case TreeDays:
                        notificSpinner.setSelection(3);
                        break;
                    case Week:
                        notificSpinner.setSelection(4);
                        break;
                }
            }
        }
    }
}


package com.afeka.keepitup.keepitup;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import java.util.Calendar;


public class CalendarFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{

    private DatePickerDialog.OnDateSetListener onDateSet;
    int year,month,day;

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


       // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),R.style.Theme_AppCompat_Light_Dialog_Alert, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

       onDateSet.onDateSet(datePicker, year,  month,  dayOfMonth);
    }
}

package com.mx.cellairispos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import util.Payments;
import util.PaymentsAdapter;


/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class PaymentListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    final static public String TAG = "payments_list";

    //UI elements
    private EditText filterFrom, filterTo;

    private PaymentsAdapter adapter;
    private OnPaymentSelectedListener paymentSelectedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_payments_list, container, false);

        ListView paymentsList = (ListView) view.findViewById(R.id.payments_list);
        filterFrom = (EditText) view.findViewById(R.id.filter_from);
        filterTo = (EditText) view.findViewById(R.id.filter_to);

        filterFrom.setOnClickListener(this);
        filterTo.setOnClickListener(this);
        view.findViewById(R.id.calendar_from_button).setOnClickListener(this);
        view.findViewById(R.id.calendar_to_button).setOnClickListener(this);
        paymentsList.setOnItemClickListener(this);

        //A little bit of dummy data


        List<Payments> list = Payments.getAll(getActivity());
        adapter = new PaymentsAdapter(getActivity(), list);
        paymentsList.setAdapter(adapter);

        if (!list.isEmpty())
            view.findViewById(R.id.empty_view).setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.calendar_from_button:
            case R.id.filter_from:
                showCalendarFrom();
                break;
            case R.id.filter_to:
            case R.id.calendar_to_button:
                showCalendarTo();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Calls the OnPaymentSelectedListener interface method with the selected item
        paymentSelectedListener.onPaymentSelected(((Payments)parent.getItemAtPosition(position)).getId());
    }

    private void showCalendarFrom() {

        //Creates a DatePickerDialog to filter and shows the result to the UI
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        filterFrom.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        queryPayments();
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showCalendarTo() {

        //Creates a DatePickerDialog to filter and shows the result to the UI
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        filterTo.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        queryPayments();
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        //And what we are going to do with this data... variable call
        datePicker.show();
    }

    public void queryPayments() {

        //Performs the filtering on the list view
        String from = filterFrom.getText().toString();
        String to = filterTo.getText().toString();

        if (!from.isEmpty())
            from = from + " 00:00:00";
        if (!to.isEmpty())
            to = to + " 23:59:59";

        adapter.filterPayments(from, to);
    }

    public interface OnPaymentSelectedListener{
        public void onPaymentSelected(int id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Makes sure that MainActivity implements the OnPaymentSelectedListener interface
        try {
            paymentSelectedListener = (OnPaymentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPaymentSelectedListener.");
        }
    }
}

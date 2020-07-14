package com.expense.tracker.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.expense.tracker.R;
import com.expense.tracker.Service.SmsReader;
import com.expense.tracker.Service.TransactionReader;
import com.expense.tracker.adapter.TransactionsAdapter;
import com.expense.tracker.model.Sms;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.model.TransactionType;
import com.expense.tracker.views.TrackerActivity;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends Fragment {
    ListView transactionsList;
    View view;
    public static Transactions inst;
    ProgressBar progressBar;
    EditText searchBar;
    TransactionsAdapter transactionsAdapter;
    public Transactions(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toast.makeText(TrackerActivity.getInstance(), "Transactions fragment", Toast.LENGTH_SHORT).show();
        view = inflater.inflate(R.layout.transaction, container, false);
        transactionsList = view.findViewById(R.id.transactions);
        progressBar = view.findViewById(R.id.progressBarLogin);
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int i, int i1, int i2) {
                transactionsAdapter.getFilter().filter(searchText.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        checkRunTimePermission(TrackerActivity.getInstance(),1);
        return view;
    }
    /**
     * This function is used to check if the permissions are granted for the application
     * @param activity current activity object
     */
    private void checkRunTimePermission(Activity activity, int requestCode) {
        String[] permissionArray = new String[]{Manifest.permission.READ_SMS};
        ActivityCompat.requestPermissions(activity,permissionArray,requestCode);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void populateTransactions(List<Transaction> transactions) {
        transactionsAdapter = new TransactionsAdapter(TrackerActivity.getInstance(), transactions);
        transactionsAdapter.notifyDataSetChanged();
        if(view != null){
            transactionsList = view.findViewById(R.id.transactions);
            transactionsList.setAdapter(transactionsAdapter);
        }
        progressBar.setVisibility(View.GONE);
    }

    public static Transactions getInstance() {
        return inst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inst = this;
    }
}

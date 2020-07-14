package com.expense.tracker.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.expense.tracker.R;
import com.expense.tracker.model.Constants;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.views.TrackerActivity;
import com.expense.tracker.views.fragments.Expenditure;

import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<Transaction> transactions;
    private List<Transaction> mDisplayedValues;    // Values to be displayed

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        this.mDisplayedValues = transactions;
    }



    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Transaction getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TransactionDataHolder transactionDataHolder = new TransactionDataHolder();
        if(view == null) {
            if (inflater != null) {
                view = inflater.inflate(R.layout.row, viewGroup, false);
                transactionDataHolder.amount = view.findViewById(R.id.transactionAmount);
                transactionDataHolder.transactionType = view.findViewById(R.id.transactionType);
                transactionDataHolder.date = view.findViewById(R.id.transactionDate);
                transactionDataHolder.transactionTag = view.findViewById(R.id.addTag);
                view.setTag(transactionDataHolder);
            }
        }else {
            transactionDataHolder = (TransactionDataHolder) view.getTag();
        }
        transactionDataHolder.amount.setText(String.format("₹%s", transactions.get(i).getAmount()));
        transactionDataHolder.date.setText(transactions.get(i).getDate());
        transactionDataHolder.transactionTag.setText(transactions.get(i).getTag());
        final String[] tag = {transactionDataHolder.transactionTag.getText().toString()};
        transactionDataHolder.transactionTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(transactions, i);
                updateListView();
            }
        });
//        transactionDataHolder.transactionTag.setText(tag[0]);
        if(!transactions.get(i).getTag().equals(Constants.DefaultTagName)){
            transactionDataHolder.transactionTag.setEnabled(false);
        }else {
            transactionDataHolder.transactionTag.setEnabled(true);
        }
        switch (transactions.get(i).getTransactionType()){
            case Debit:
                transactionDataHolder.transactionType.setImageResource(R.drawable.phone);
                break;
            case Credit:
                transactionDataHolder.transactionType.setImageResource(R.drawable.incoming);
                break;
        }
        return view;

    }

    public void updateListView(){
        TrackerActivity.transactions = transactions;
        Expenditure.getInstance().updateCharts();
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }
    private void showDialog(final List<Transaction> transactions, final int pos) {
        final Dialog dialog = new Dialog(TrackerActivity.getInstance());
        dialog.setContentView(R.layout.tag_dialog);
        dialog.show();
        final String[] selectedStatus = {""};
        Button changeTagSubmit = dialog.findViewById(R.id.changeTagButton);
        Button changeTagCancel = dialog.findViewById(R.id.changeTagCancel);
        final RadioGroup tagButtonGroup = dialog.findViewById(R.id.tag_group);
        final RadioButton groceriesButton = dialog.findViewById(R.id.groceries);
        final RadioButton shoppingButton = dialog.findViewById(R.id.shopping);
        final RadioButton miscellaneousButton = dialog.findViewById(R.id.miscellaneous);
        final RadioButton foodButton = dialog.findViewById(R.id.food);
        final EditText customButtonText = dialog.findViewById(R.id.customTagEditText);
        tagButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(id == R.id.custom_tag){
                    customButtonText.setEnabled(true);
                }else {
                    customButtonText.setEnabled(false);
                }
            }
        });
        changeTagCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactions.get(pos).setTag(Constants.DefaultTagName);
                updateListView();
                dialog.dismiss();
            }
        });
        changeTagSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tagButtonGroup.getCheckedRadioButtonId()){
                    case R.id.groceries:
                        selectedStatus[0] = groceriesButton.getText().toString();
                        break;
                    case R.id.shopping:
                        selectedStatus[0] = shoppingButton.getText().toString();
                        break;
                    case R.id.food:
                        selectedStatus[0] = foodButton.getText().toString();
                        break;
                    case R.id.miscellaneous:
                        selectedStatus[0] = miscellaneousButton.getText().toString();
                        break;
                    case R.id.custom_tag:
                        selectedStatus[0] = customButtonText.getText().toString();
                        break;
                }
                if(selectedStatus[0].equals("") || selectedStatus[0].equals(" ")){
                    customButtonText.setError(context.getString(R.string.editTextError));
                }else {
                    transactions.get(pos).setTag(selectedStatus[0]);
                    updateListView();
                        dialog.dismiss();
                    }
                }
            });
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                if (results.count == 0) {
                    transactions = (ArrayList<Transaction>)results.values;
                    notifyDataSetInvalidated();
                }
                else {
                    transactions = (ArrayList<Transaction>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Transaction> FilteredArrList = new ArrayList<Transaction>();

                if (transactions == null || transactions.size() == 0) {
                    transactions = new ArrayList<Transaction>(mDisplayedValues); // saves the original data in mOriginalValues
                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = transactions.size();
                    results.values = transactions;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < transactions.size(); i++) {
                        String data = transactions.get(i).getTag();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Transaction(transactions.get(i).getAmount(),transactions.get(i).getTransactionType(),transactions.get(i).getTag(),transactions.get(i).getDate()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    static class TransactionDataHolder {
        TextView amount, date;
        ImageView transactionType;
        Button transactionTag;
    }
}

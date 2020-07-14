package com.expense.tracker.Service;

import android.util.Log;
import com.expense.tracker.model.Sms;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.model.TransactionType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionReader {
    private static final String TAG  = TransactionReader.class.getSimpleName();
    private static final String startsWithRupeesRegex = "(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)";
    private static final String debit  = "(?i)(debited|debit|withdraw|withdrawn)";
    private static final String credit = "(?i)(credited|credit|deposited)";
    public static List<Transaction> FindAndUpdateTransactions(List<Sms> messages){
        List<Transaction> transactions = new ArrayList<>();
        for(Sms smsMessage : messages){
            Transaction transaction = CheckTransaction(smsMessage);
            if(transaction != null){
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    private static Transaction CheckTransaction(Sms smsMessage) {
        String message = smsMessage.getMsg();
        Transaction transaction;
        Pattern regex = Pattern.compile(startsWithRupeesRegex);
        Matcher m = regex.matcher(message);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(smsMessage.getTime().trim()));
        String date = formatter.format(calendar.getTime());
        if(m.find()){
            Log.i("amount_value= ", "" + m.group(1));
            String amount = m.group(1);
            if(amount != null) {
                amount = amount.trim();
                amount = amount.replaceAll(",", "");
                try {
                    double tAmount = Double.valueOf(amount);
                    TransactionType transactionType = FindTransactionType(message);
                    if (transactionType != TransactionType.Unknown) {
                        transaction = new Transaction(tAmount, transactionType, "Add Tag", date);
                        return transaction;
                    }
                } catch (NumberFormatException ne) {
                    Log.e(TAG, ne.toString());
                    return null;
                }
            }
        }
        return null;
    }

    private static TransactionType FindTransactionType(String message) {
        Pattern regex = Pattern.compile(debit);
        Matcher m = regex.matcher(message);
        if(m.find()) {
            return TransactionType.Debit;
        }
        regex = Pattern.compile(credit);
        m = regex.matcher(message);
        if(m.find()){
            return TransactionType.Credit;
        }
        return TransactionType.Unknown;
    }
}

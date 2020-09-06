package com.expense.tracker.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expense.tracker.R;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.model.TransactionType;
import com.expense.tracker.views.TrackerActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Expenditure extends Fragment {
//    private AnyChartView anyChartView;
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    private static Expenditure inst;
    XAxis xAxis;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Toast.makeText(TrackerActivity.getInstance(), "Expenditure fragment", Toast.LENGTH_SHORT).show();
        View view = inflater.inflate(R.layout.expenditure, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        inst = this;
        createPieChart();
        createBarChart();
        return view;
    }

    public static Expenditure getInstance() {
        return inst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void createPieChart() {
//        Pie pie = AnyChart.pie();
        ArrayList<PieEntry> feedData = new ArrayList<>();
        List<Transaction> transactions = TrackerActivity.transactions;
        Map<String, Double> values = new HashMap<>();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                String key = transaction.getTransactionType().toString();
                double amount = values.containsKey(key) ? values.get(key) : 0;
                amount = amount + transaction.getAmount();
                values.put(key, amount);
            }
            if (!values.isEmpty()) {
                for (String key : values.keySet()) {
                    feedData.add(new PieEntry(values.get(key).floatValue(),key));
                }
                pieDataSet = new PieDataSet(feedData, "Expenditure Chart");
                pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                setPieChartAttributes();
            }
        }
    }
    private void setPieChartAttributes(){
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(2f);

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSliceSpace(5f);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }
    public void createBarChart() {
        ArrayList<BarEntry> feedData = new ArrayList<>();
        List<Transaction> transactions = TrackerActivity.transactions;
        Map<String, Double> values = new HashMap<>();
        HashSet<String> labels = new HashSet<>();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                //creating a bar chart of expenses only
                if(transaction.getTransactionType() == TransactionType.Debit) {
                    String key = transaction.getTag();
                    if (key.equals("Add Tag")) {
                        key = "UnCategorized";
                    }
                    labels.add(key);
                    double amount = values.containsKey(key) ? values.get(key) : 0;
                    amount = amount + transaction.getAmount();
                    values.put(key, amount);
                }
            }
            if (!values.isEmpty()) {
                float i = 0;
                for (String key : values.keySet()) {
                    feedData.add(new BarEntry(i,values.get(key).floatValue()));
                    i++;
                }
                barDataSet = new BarDataSet(feedData, "Expenditure Chart");
                barData = new BarData(barDataSet);
                setBarChartAttributes(labels);
                barChart.setData(barData);
            }
        }
    }
    private void setBarChartAttributes(HashSet<String> labels){
        int n = labels.size();
        String[] arrLabels = new String[n];
        // Copying contents of set to arr[]
        System.arraycopy(labels.toArray(), 0, arrLabels, 0, n);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        Legend l = barChart.getLegend();
        l.setEnabled(false);
        xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(arrLabels.length);
        //Now add the labels to be added on the vertical axis
        XAxisValueFormatter formatter = new XAxisValueFormatter(arrLabels);
        xAxis.setValueFormatter(formatter);
    }


    public void updateCharts(){
        createPieChart();
        createBarChart();
    }
}

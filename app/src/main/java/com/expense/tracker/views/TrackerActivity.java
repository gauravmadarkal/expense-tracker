package com.expense.tracker.views;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.expense.tracker.R;
import com.expense.tracker.service.SmsReader;
import com.expense.tracker.service.TransactionReader;
import com.expense.tracker.model.Sms;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.views.fragments.Expenditure;
import com.expense.tracker.views.fragments.Transactions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TrackerActivity extends AppCompatActivity {

    public static TrackerActivity instance;
    private static final String TAG = TrackerActivity.class.getSimpleName();
    public static List<Transaction> transactions;
//    private ProgressDialog progress;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabstoolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
//        checkRunTimePermission(this, 1);
    }


    public static TrackerActivity getInstance(){
        return instance;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                Log.e(TAG,"granted");
                if (grantResults.length > 0) {
                    for (int value : grantResults) {
                        if (value == PackageManager.PERMISSION_GRANTED)
                        {
                            new Runnable(){
                                @Override
                                public void run() {
                                    FetchData();
                                }
                            }.run();
                        }
                    }
                }
                break;
        }
    }

    private void FetchData() {
        List<Sms> messages = SmsReader.getAllSms(TrackerActivity.getInstance());
        transactions = TransactionReader.FindAndUpdateTransactions(messages);
        Transactions.getInstance().populateTransactions(transactions);
        Expenditure.getInstance().createPieChart();
        Expenditure.getInstance().createBarChart();
        if(progressBar !=null){
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Transactions(), "Transactions");
        adapter.addFragment(new Expenditure(), "Reports");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

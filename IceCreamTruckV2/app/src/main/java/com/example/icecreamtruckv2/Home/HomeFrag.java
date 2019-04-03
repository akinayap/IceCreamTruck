package com.example.icecreamtruckv2.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icecreamtruckv2.R;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HomeFrag extends Fragment {
    ViewPager viewPager;
    int images[] = {R.drawable.us1, R.drawable.us2};
    PicPagerAdapter picPagerAdapter;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.home_frag, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.pager);

        picPagerAdapter = new PicPagerAdapter(getContext(), images);
        viewPager.setAdapter(picPagerAdapter);
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }
}
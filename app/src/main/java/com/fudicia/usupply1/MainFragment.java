package com.fudicia.usupply1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    private FragmentTabHost fragmentTabHost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabs, container, false);

        fragmentTabHost = v.findViewById(R.id.tabhost);
        fragmentTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabcontent);

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Topp").setIndicator("Topp"),
                HomeFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Senaste").setIndicator("Senaste"),
                NewHomeFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Bokmärken").setIndicator("Bokmärken"),
                FavouritesFragment.class, null);

        return v;
    }
}

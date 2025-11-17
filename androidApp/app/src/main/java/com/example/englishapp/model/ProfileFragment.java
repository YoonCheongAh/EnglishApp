package com.example.englishapp.model;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.profile_tabs);
        View personalInfoLayout = view.findViewById(R.id.personal_info_layout);
        View progressLayout = view.findViewById(R.id.progress_layout);

        // Hiển thị mặc định
        personalInfoLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    personalInfoLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                } else {
                    personalInfoLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}

package com.example.siateacher.Student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.siateacher.chatsFragment;
import com.example.siateacher.quizFragment;

public class StudentSectionsPagerAdapter extends FragmentPagerAdapter {

    public StudentSectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                StudentHomeFragment reqFragment = new StudentHomeFragment();
                return reqFragment;

            case 1:
                quizFragment quizFragment = new quizFragment();
                return quizFragment;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "home";

            case 1:
                return "quiz";

            default:
                return null;

        }
    }
}


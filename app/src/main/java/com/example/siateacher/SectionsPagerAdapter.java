package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                chatsFragment chaFragment = new chatsFragment();
                return chaFragment;

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
                 return "chats";

             case 1:
                 return "quiz";

             default:
                 return null;

         }
    }
}

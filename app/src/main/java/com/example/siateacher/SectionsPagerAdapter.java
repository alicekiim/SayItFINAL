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
                UsersFragment reqFragment = new UsersFragment();
                return reqFragment;

            case 1:
                chatsFragment chaFragment = new chatsFragment();
                return chaFragment;

            case 2:
                quizFragment quizFragment = new quizFragment();
                return quizFragment;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
         switch (position){
             case 0:
                 return "home";

             case 1:
                 return "chats";

             case 2:
                 return "quiz";

             default:
                 return null;

         }
    }
}

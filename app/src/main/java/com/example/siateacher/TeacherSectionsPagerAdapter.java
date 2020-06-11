// Code adapted from tutorial 'Lapit Chat tutorial' by Arathi Singh.
// Tutorial found at: https://www.youtube.com/watch?v=fECOH4w_Kl4&list=PLGCjwl1RrtcQ3o2jmZtwu2wXEA4OIIq53&index=8

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


class TeacherSectionsPagerAdapter extends FragmentPagerAdapter {

    public TeacherSectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {//each fragment is instantiated for each tab

        switch (position) {
            case 0:
                TeacherChatsFragment chatFrag = new TeacherChatsFragment();
                return chatFrag;

            case 1:
                quizFragment quizFrag = new quizFragment();
                return quizFrag;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    } //no. of tabs

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { //naming the tabs
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

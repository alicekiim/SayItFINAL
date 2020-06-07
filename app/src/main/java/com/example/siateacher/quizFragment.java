package com.example.siateacher;



import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class quizFragment extends Fragment {

    private View mMainView;


    public quizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_quiz, container, false);

        //add link to quiz
        TextView text1 = mMainView.findViewById(R.id.link1);
        text1.setMovementMethod(LinkMovementMethod.getInstance());

        //add link to quiz
        TextView text2 = mMainView.findViewById(R.id.link1);
        text2.setMovementMethod(LinkMovementMethod.getInstance());

        //add link to quiz
        TextView text3 = mMainView.findViewById(R.id.link3);
        text3.setMovementMethod(LinkMovementMethod.getInstance());

        return mMainView;
    }

}

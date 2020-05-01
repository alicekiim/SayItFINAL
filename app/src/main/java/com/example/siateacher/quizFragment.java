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
        TextView text = mMainView.findViewById(R.id.link);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        return mMainView;
    }

}

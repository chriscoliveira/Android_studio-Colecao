package com.gmail.coliveira.christian.colecao.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.coliveira.christian.colecao.R;
import com.gmail.coliveira.christian.colecao.ZInfoDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {

    public ResumoFragment() {
        // Required empty public constructor
    }

    ZInfoDB zInfoDB = new ZInfoDB();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_inicio, container, false);

        //

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            }
}

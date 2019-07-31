package com.gmail.coliveira.christian.colecao.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.gmail.coliveira.christian.colecao.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversorFragment extends Fragment {
    String site = "http://dateconverter.net/?hl=pt";

    public ConversorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.op_conversor, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

// Inflate the layout for this fragment
        Button button = view.findViewById(R.id.buttonLink);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web();
            }
        });

web();


    }

    public void web(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(site));
        startActivity(i);
    }
}

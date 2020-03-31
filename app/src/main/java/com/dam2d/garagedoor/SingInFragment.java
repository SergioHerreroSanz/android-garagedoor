package com.dam2d.garagedoor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;

import static com.dam2d.garagedoor.MainActivity.fragmentManager;
import static com.dam2d.garagedoor.MainActivity.signInIntent;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingInFragment extends Fragment {

    public SingInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sing_in, container, false);

        final Button singIn = view.findViewById(R.id.singIn_button_singIn);
        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signInIntent);
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, new ActionsFragment()).commit();
            }
        });

        return view;
    }
}

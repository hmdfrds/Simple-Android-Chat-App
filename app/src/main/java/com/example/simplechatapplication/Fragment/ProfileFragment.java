package com.example.simplechatapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.simplechatapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ProfileFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    TextView textProfileEmail, textProfileName;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        textProfileEmail = view.findViewById(R.id.textProfileEmail);
        textProfileName = view.findViewById(R.id.textProfileName);




        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());

        documentReference.addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                textProfileEmail.setText(documentSnapshot.getString("email"));
                textProfileName.setText(documentSnapshot.getString("name"));



            }
        });


        return view;
    }
}
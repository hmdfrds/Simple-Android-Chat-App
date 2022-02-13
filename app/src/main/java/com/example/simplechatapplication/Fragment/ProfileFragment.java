package com.example.simplechatapplication.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.simplechatapplication.HomeActivity;
import com.example.simplechatapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;


public class ProfileFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    TextView textProfileEmail, textProfileName;
    Button change_language;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref",getActivity().MODE_PRIVATE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        textProfileEmail = view.findViewById(R.id.textProfileEmail);
        textProfileName = view.findViewById(R.id.textProfileName);
        change_language=view.findViewById(R.id.change_language);

        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale current = getResources().getConfiguration().locale;
                String languageToLoad  = current.toLanguageTag();
                if(languageToLoad.equals("ms")){
                    languageToLoad="en-US";
                }
                else {
                    languageToLoad="ms";
                }
                Log.e(languageToLoad, "onClick: " );
                Log.e(current.toLanguageTag(), "onClick: " );



// Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
                myEdit.putString("language", languageToLoad);

// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
                myEdit.commit();

                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config,getActivity().getResources().getDisplayMetrics());
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });




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
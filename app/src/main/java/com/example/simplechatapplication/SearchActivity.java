package com.example.simplechatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.simplechatapplication.Adapter.AccountAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    RecyclerView rvAccount;
    SearchView searchView;
    AccountAdapter accountAdapter;
    ArrayList<Map<String,Object>> account;
    ArrayList<Map<String,Object>> accountFromDb;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        account = new ArrayList<>();
        accountFromDb = new ArrayList<>();

        rvAccount = findViewById(R.id.rvAccount);
        searchView = findViewById(R.id.searchViewAccount);

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){
                            if(!ds.getData().get("uid").toString().equals(firebaseUser.getUid())){
                                accountFromDb.add(ds.getData());
                            }
                        }

                    }
                });
            }
        });

        accountAdapter = new AccountAdapter(SearchActivity.this, account, new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, Object> item) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);

                intent.putExtra("uid",item.get("uid").toString());
                startActivity(intent);
            }
        });
        rvAccount.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rvAccount.setAdapter(accountAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                account.clear();

                if(!newText.isEmpty()){
                    for(Map<String,Object> a : accountFromDb){
                        if(a.get("email").toString().toLowerCase().trim().contains(newText.trim().toLowerCase())){
                            account.add(a);
                        }
                    }
                }
                accountAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
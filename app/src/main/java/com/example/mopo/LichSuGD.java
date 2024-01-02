package com.example.mopo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LichSuGD extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_gd);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);

        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference transactionsRef = firestore.collection("users").document(userId).collection("transaction_history");

            // Truy vấn để lấy các giao dịch được sắp xếp theo timestamp (giảm dần)
            Query query = transactionsRef.orderBy("ngayGio", Query.Direction.DESCENDING);

            query.limit(50);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        transactionList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            transactionList.add(transaction);
                        }
                        transactionAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}

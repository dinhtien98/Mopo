package com.example.mopo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class CreateAccount extends AppCompatActivity {

    private EditText editTextTK, editTextMK;
    private Button btTaotk;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextTK = findViewById(R.id.tk);
        editTextMK = findViewById(R.id.mk);
        btTaotk = findViewById(R.id.btCreateAccount);

        btTaotk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String tk = editTextTK.getText().toString().trim();
        String mk = editTextMK.getText().toString().trim();

        if (TextUtils.isEmpty(tk)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mk)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản người dùng trong Firebase Authentication
        mAuth.createUserWithEmailAndPassword(tk, mk).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Tạo trường "balance" với giá trị mặc định là 0 trong Firestore
                    String userId = mAuth.getCurrentUser().getUid();
                    DocumentReference userRef = firestore.collection("users").document(userId);

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("balance", 0);
                    userData.put("email", tk);

                    userRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> firestoreTask) {
                            if (firestoreTask.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Lỗi khi tạo tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại hoặc có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

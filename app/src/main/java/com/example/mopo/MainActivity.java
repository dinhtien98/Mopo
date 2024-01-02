package com.example.mopo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    private ImageButton imageButton;
    private EditText editTextTK, editTextMK;
    private Button btLogin, btQuenmk, btTaotk;
    private int dem = 0;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        imageButton = findViewById(R.id.btSee);
        editTextTK = findViewById(R.id.tk);
        editTextMK = findViewById(R.id.mk);
        btLogin = findViewById(R.id.btLogin);
        btQuenmk = findViewById(R.id.btQuenmk);
        btTaotk = findViewById(R.id.btTaotk);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dem == 0) {
                    imageButton.setImageResource(R.drawable.hinh1);
                    dem = 1;
                    editTextMK.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    imageButton.setImageResource(R.drawable.hinh0);
                    dem = 0;
                    editTextMK.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btQuenmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, forgotpassword.class);
                startActivity(intent);
            }
        });

        btTaotk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });
    }


    private void login() {
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

        mAuth.signInWithEmailAndPassword(tk, mk).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, home.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Đăng nhập không thành công. Sai tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
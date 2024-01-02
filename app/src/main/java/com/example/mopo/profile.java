package com.example.mopo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    private ImageView imAvartar;
    private TextView tk;
    private EditText ten, ngaysinh, gioitinh, sdt, diachi, mk, tvTK;
    private Button sua, luu, xacnhan, doimk, backhome, dangxuat, xacthucemail;
    private LinearLayout linear7, linear7_1;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_profile);

        imAvartar = findViewById(R.id.Avatar);
        ten = findViewById(R.id.Name);
        ngaysinh = findViewById(R.id.NgaySinh);
        gioitinh = findViewById(R.id.GioiTinh);
        sdt = findViewById(R.id.SDT);
        diachi = findViewById(R.id.DiaChi);
        mk = findViewById(R.id.tvMK);
        sua = findViewById(R.id.btSua);
        luu = findViewById(R.id.btLuu);
        xacnhan = findViewById(R.id.btXacNhan);
        doimk = findViewById(R.id.btDoiMK);
        linear7 = findViewById(R.id.linear7);
        backhome = findViewById(R.id.btbackhome);
        tk = findViewById(R.id.tvTK);
        dangxuat = findViewById(R.id.btdangxuat);
        tvTK = findViewById(R.id.tvTK);
        xacthucemail = findViewById(R.id.btDoiemail);

        firestore = FirebaseFirestore.getInstance();

        initUI();

        sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivityEditable();
            }
        });

        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, home.class);
                startActivity(intent);
            }
        });

        xacthucemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Đã gửi xác thực qua Email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivityNonEditable();
                String name = ten.getText().toString().trim();
                String sdtValue = sdt.getText().toString().trim();
                String gioiTinhValue = gioitinh.getText().toString().trim();
                String ngaySinhValue = ngaysinh.getText().toString().trim();
                String diaChiValue = diachi.getText().toString().trim();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Cập nhập thành công", Toast.LENGTH_SHORT).show();
                                        saveUserInfoToFirestore(sdtValue, gioiTinhValue, ngaySinhValue, diaChiValue);
                                    }
                                }
                            });
                }
            }
        });

        doimk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear7.setVisibility(View.VISIBLE);
                backhome.setVisibility(View.GONE);
            }
        });

        xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newPassword = mk.getText().toString().trim();
                backhome.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Cập nhập thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Vui lòng nhập lại mật khẩu mới", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    linear7.setVisibility(View.GONE);
                }
            }
        });

        dangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // Redirect to the main activity after logout
                Intent intent = new Intent(profile.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setActivityNonEditable() {
        ten.setClickable(false);
        ten.setFocusable(false);
        ten.setFocusableInTouchMode(false);
        ten.setTextColor(Color.parseColor("#FF000000"));
        ngaysinh.setClickable(false);
        ngaysinh.setFocusable(false);
        ngaysinh.setFocusableInTouchMode(false);
        ngaysinh.setTextColor(Color.parseColor("#FF000000"));
        gioitinh.setClickable(false);
        gioitinh.setFocusable(false);
        gioitinh.setFocusableInTouchMode(false);
        gioitinh.setTextColor(Color.parseColor("#FF000000"));
        sdt.setClickable(false);
        sdt.setFocusable(false);
        sdt.setFocusableInTouchMode(false);
        sdt.setTextColor(Color.parseColor("#FF000000"));
        diachi.setClickable(false);
        diachi.setFocusable(false);
        diachi.setFocusableInTouchMode(false);
        diachi.setTextColor(Color.parseColor("#FF000000"));
    }

    private void setActivityEditable() {
        ten.setClickable(true);
        ten.setFocusable(true);
        ten.setFocusableInTouchMode(true);
        ten.setTextColor(Color.parseColor("#F44336"));
        ngaysinh.setClickable(true);
        ngaysinh.setFocusable(true);
        ngaysinh.setFocusableInTouchMode(true);
        ngaysinh.setTextColor(Color.parseColor("#F44336"));
        gioitinh.setClickable(true);
        gioitinh.setFocusable(true);
        gioitinh.setFocusableInTouchMode(true);
        gioitinh.setTextColor(Color.parseColor("#F44336"));
        sdt.setClickable(true);
        sdt.setFocusable(true);
        sdt.setFocusableInTouchMode(true);
        sdt.setTextColor(Color.parseColor("#F44336"));
        diachi.setClickable(true);
        diachi.setFocusable(true);
        diachi.setFocusableInTouchMode(true);
        diachi.setTextColor(Color.parseColor("#F44336"));
    }

    private void initUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            ten.setText(name);
            tk.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.mopo).into(imAvartar);

            // Load user information from Firestore
            loadUserInfoFromFirestore();
        }
    }

    private void loadUserInfoFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            firestore.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    String sdtValue = document.getString("sdt");
                                    String gioiTinhValue = document.getString("gioiTinh");
                                    String ngaySinhValue = document.getString("ngaySinh");
                                    String diaChiValue = document.getString("diaChi");

                                    // Update UI with retrieved information
                                    sdt.setText(sdtValue);
                                    gioitinh.setText(gioiTinhValue);
                                    ngaysinh.setText(ngaySinhValue);
                                    diachi.setText(diaChiValue);
                                }
                            } else {
                                // Handle errors
                            }
                        }
                    });
        }
    }

    private void saveUserInfoToFirestore(String sdtValue, String gioiTinhValue, String ngaySinhValue, String diaChiValue) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("sdt", sdtValue);
            userInfo.put("gioiTinh", gioiTinhValue);
            userInfo.put("ngaySinh", ngaySinhValue);
            userInfo.put("diaChi", diaChiValue);

            firestore.collection("users").document(user.getUid())
                    .set(userInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Thông tin đã được lưu vào Firestore", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Lỗi khi lưu thông tin vào Firestore", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

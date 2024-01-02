package com.example.mopo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import pl.droidsonroids.gif.GifImageView;

public class home extends AppCompatActivity {

    private ImageView naprut,nhantien,QRthanhtoan,quetma,chuyentien,chuyentienvenganhang,thanhtoanhoadon,naptiendienthoai,profile,lichsugd;
    private GifImageView QR_code;
    private EditText timkiem;
    private TextView Monny;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ẩn ActionBar (thanh tiêu đề)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_home);
        naprut = findViewById(R.id.imageNapRut);
        nhantien = findViewById(R.id.imageNhantien);
        QRthanhtoan = findViewById(R.id.imageQRthanhtoan);
        quetma = findViewById(R.id.imageQuetma);
        chuyentien = findViewById(R.id.imageChuyentien);
        chuyentienvenganhang = findViewById(R.id.imageChuyenTienVeNganHang);
        thanhtoanhoadon = findViewById(R.id.imageThanhToanHoaDon);
        naptiendienthoai = findViewById(R.id.imageNapTienDienThoai);
        profile = findViewById(R.id.imageProfile);
        QR_code = findViewById(R.id.imgbtnQR);
        lichsugd = findViewById(R.id.imageLichsuGD);
        Monny = findViewById(R.id.tvMonny);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Monny = findViewById(R.id.tvMonny);

        // Load số tiền từ Firestore
        loadBalanceFromFirestore();

        lichsugd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, LichSuGD.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, profile.class);
                startActivity(intent);
            }
        });
        chuyentien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, ChuyenTien.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when the activity is resumed
        loadBalanceFromFirestore();
    }

    private void loadBalanceFromFirestore() {
        // Lấy thông tin người dùng hiện tại
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Đường dẫn của tài khoản người dùng trong Firestore
            String userDocumentPath = "users/" + user.getUid();

            // Truy vấn Firestore để lấy số tiền từ tài khoản người dùng
            firestore.document(userDocumentPath).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Lấy giá trị của trường "balance"
                                Double balance = document.getDouble("balance");
                                if (balance != null) {
                                    // Hiển thị số tiền trong TextView
                                    Monny.setText(String.valueOf(balance));
                                }
                            }
                        }
                    });
        }
    }
}
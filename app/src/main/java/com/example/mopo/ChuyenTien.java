package com.example.mopo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.EventListener;

public class ChuyenTien extends AppCompatActivity {

    private EditText tk, sotien;
    private Button XacNhanChuyenTien,btBackHome;
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
        setContentView(R.layout.activity_chuyen_tien);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tk = findViewById(R.id.etTKnhantien);
        sotien = findViewById(R.id.etSoTien);
        XacNhanChuyenTien = findViewById(R.id.btXacNhanChuyenTien);
        btBackHome = findViewById(R.id.btBackHome);

        btBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChuyenTien.this, home.class);
                startActivity(intent);
            }
        });

        // Thêm sự kiện lắng nghe khi giá trị của tk thay đổi
        tk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Kiểm tra tài khoản nhận tiền khi giá trị của tk thay đổi
                String taiKhoanNhanTien = editable.toString().trim();
                kiemTraTaiKhoanNhanTienFirestore(taiKhoanNhanTien);
            }
        });

        XacNhanChuyenTien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chuyenTien();
            }
        });
    }

    // Hàm kiểm tra tài khoản nhận tiền trên Firestore
    private void kiemTraTaiKhoanNhanTienFirestore(String taiKhoanNhanTien) {
        firestore.collection("users")
                .whereEqualTo("email", taiKhoanNhanTien)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot targetQuerySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            return;
                        }

                        if (targetQuerySnapshot != null && !targetQuerySnapshot.isEmpty()) {
                            // Tài khoản nhận tiền tồn tại
                            XacNhanChuyenTien.setEnabled(true);
                        } else {
                            // Tài khoản nhận tiền không tồn tại
                            XacNhanChuyenTien.setEnabled(false);
                        }
                    }
                });
    }

    // Hàm thêm thông tin giao dịch vào Firestore
    private void themGiaoDich(String loaiGiaoDich, String taiKhoanDoiTac, double soTien) {

        long timestamp = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

        String formattedDate = sdf.format(new Date(timestamp));

        // Lấy thông tin người dùng hiện tại
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Đường dẫn của tài khoản người dùng trong Firestore
            String userDocumentPath = "users/" + user.getUid();

            // Tạo đối tượng lưu trữ thông tin giao dịch cho người gửi
            Map<String, Object> giaoDichDataNguoiGui = new HashMap<>();
            giaoDichDataNguoiGui.put("loaiGiaoDich", loaiGiaoDich);
            giaoDichDataNguoiGui.put("taiKhoanDoiTac", taiKhoanDoiTac);
            giaoDichDataNguoiGui.put("soTien", "-"+soTien);
            giaoDichDataNguoiGui.put("ngayGio", formattedDate);

            // Thêm thông tin giao dịch vào collection "transaction_history" của người gửi
            firestore.collection(userDocumentPath + "/transaction_history").add(giaoDichDataNguoiGui)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> giaoDichTaskNguoiGui) {
                            if (giaoDichTaskNguoiGui.isSuccessful()) {
                                // Thêm thông tin giao dịch cho người nhận
                                firestore.collection("users")
                                        .whereEqualTo("email", taiKhoanDoiTac)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> targetTask) {
                                                if (targetTask.isSuccessful() && !targetTask.getResult().isEmpty()) {
                                                    DocumentSnapshot targetDoc = targetTask.getResult().getDocuments().get(0);

                                                    // Tạo đối tượng lưu trữ thông tin giao dịch cho người nhận
                                                    Map<String, Object> giaoDichDataNguoiNhan = new HashMap<>();
                                                    giaoDichDataNguoiNhan.put("loaiGiaoDich", loaiGiaoDich);
                                                    giaoDichDataNguoiNhan.put("taiKhoanDoiTac", user.getEmail()); // Email của người gửi
                                                    giaoDichDataNguoiNhan.put("soTien", "+"+soTien);
                                                    giaoDichDataNguoiNhan.put("ngayGio", formattedDate);

                                                    // Thêm thông tin giao dịch vào collection "transaction_history" của người nhận
                                                    firestore.collection("users").document(targetDoc.getId()).collection("transaction_history")
                                                            .add(giaoDichDataNguoiNhan)
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> giaoDichTask) {
                                                                    if (giaoDichTask.isSuccessful()) {
                                                                        Toast.makeText(getApplicationContext(), "Giao dịch thành công", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "Lỗi khi lưu thông tin giao dịch", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    // Nếu không tìm thấy người dùng đích
                                                    Toast.makeText(getApplicationContext(), "Người dùng đích không tồn tại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Lỗi khi lưu thông tin giao dịch cho người gửi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Hàm chuyển tiền
    private void chuyenTien() {
        String taiKhoanNhanTien = tk.getText().toString().trim();
        String soTienChuyen = sotien.getText().toString().trim();

        if (soTienChuyen.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền muốn chuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        double soTien = Double.parseDouble(soTienChuyen);

        // Lấy thông tin người dùng nguồn
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DocumentReference nguonRef = firestore.collection("users").document(user.getUid());

            nguonRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot nguonDoc = task.getResult();
                        if (nguonDoc != null && nguonDoc.exists()) {
                            double soDuNguon = nguonDoc.getDouble("balance");

                            if (soDuNguon >= soTien) {
                                // Trừ số tiền từ người dùng nguồn
                                double soDuMoiNguon = soDuNguon - soTien;
                                Map<String, Object> nguonUpdate = new HashMap<>();
                                nguonUpdate.put("balance", soDuMoiNguon);

                                nguonRef.update(nguonUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> nguonTask) {
                                        if (nguonTask.isSuccessful()) {
                                            // Lấy thông tin người dùng đích
                                            firestore.collection("users")
                                                    .whereEqualTo("email", taiKhoanNhanTien)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> targetTask) {
                                                            if (targetTask.isSuccessful() && !targetTask.getResult().isEmpty()) {
                                                                DocumentSnapshot targetDoc = targetTask.getResult().getDocuments().get(0);
                                                                double soDuDich = targetDoc.getDouble("balance");

                                                                // Cộng số tiền vào người dùng đích
                                                                double soDuMoiDich = soDuDich + soTien;
                                                                Map<String, Object> dichUpdate = new HashMap<>();
                                                                dichUpdate.put("balance", soDuMoiDich);

                                                                firestore.collection("users").document(targetDoc.getId())
                                                                        .update(dichUpdate)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> dichTask) {
                                                                                if (dichTask.isSuccessful()) {
                                                                                    // Thêm thông tin giao dịch vào Firestore khi chuyển tiền thành công
                                                                                    themGiaoDich("chuyenTien", taiKhoanNhanTien, soTien);
                                                                                } else {
                                                                                    Toast.makeText(getApplicationContext(), "Lỗi khi chuyển tiền", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                // Nếu không tìm thấy người dùng đích
                                                                Toast.makeText(getApplicationContext(), "Người dùng đích không tồn tại", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Nếu có lỗi khi cập nhật người dùng nguồn
                                            Toast.makeText(getApplicationContext(), "Lỗi khi chuyển tiền", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Nếu số dư không đủ
                                Toast.makeText(getApplicationContext(), "Số dư không đủ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }
}

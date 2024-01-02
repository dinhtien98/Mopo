package com.example.mopo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // Assuming you have TextViews with these IDs in your transaction_item layout
        holder.loaiGiaoDichTextView.setText(transaction.getLoaiGiaoDich());
        holder.taiKhoanDoiTacTextView.setText(transaction.getTaiKhoanDoiTac());
        holder.ngayGioTextView.setText(transaction.getNgayGio());
        holder.soTienTextView.setText(transaction.getSoTien());
    }


    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView loaiGiaoDichTextView;
        public TextView taiKhoanDoiTacTextView;
        public TextView ngayGioTextView;
        public TextView soTienTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            loaiGiaoDichTextView = itemView.findViewById(R.id.loaiGiaoDichTextView);
            taiKhoanDoiTacTextView = itemView.findViewById(R.id.taiKhoanDoiTacTextView);
            ngayGioTextView = itemView.findViewById(R.id.ngayGioTextView);
            soTienTextView = itemView.findViewById(R.id.soTienTextView);
        }
    }
}

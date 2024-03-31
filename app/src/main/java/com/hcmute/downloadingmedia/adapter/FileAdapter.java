package com.hcmute.downloadingmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmute.downloadingmedia.MainActivity;
import com.hcmute.downloadingmedia.R;

import java.io.File;
import java.util.List;

import com.hcmute.downloadingmedia.R;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {


    private List<File> fileList;
    private Context context;

    public FileAdapter(List<File> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textView = (TextView) itemView.findViewById(R.id.text_view_priority);

        }

        //GETTERS
        public TextView getTextView() {
            return textView;
        }

        //SETTERS
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File currentFile = fileList.get(position);
        String filename = currentFile.getName();
        holder.getTextView().setText(filename);
        holder.itemView.setOnClickListener((v) -> showFileOptionsDialog(filename));

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    //
    private void showFileOptionsDialog(String fileName) {
        CharSequence[] items = {"Rename", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your action");
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    showRenameFileDialog(fileName);
                    break;
                case 1:
                    deleteFiles(fileName);
                    break;
            }
        });
        builder.show();
    }

    private void showRenameFileDialog(String oldFileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rename file");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String fileExtension = oldFileName.substring(oldFileName.lastIndexOf('.'));
            String newFileName = input.getText().toString();
            //
            if(!newFileName.trim().isEmpty()){
                if(!newFileName.endsWith(fileExtension)){
                    newFileName += fileExtension;
                }
            }
            renameFile(oldFileName, newFileName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteFiles(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void renameFile(String oldFileName, String newFileName) {
        File oldFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), oldFileName);
        File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), newFileName);
        if (oldFile.exists()) {
            if (oldFile.renameTo(newFile)) {
                Toast.makeText(context, "Rename successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Cannot rename", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

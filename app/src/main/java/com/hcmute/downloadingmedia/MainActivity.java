package com.hcmute.downloadingmedia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.hcmute.downloadingmedia.adapter.FileAdapter;
import com.hcmute.downloadingmedia.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // using broadcast method

    private static ActivityMainBinding mainBinding;

    private long downloadID;

    // using broadcast method
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                loadingFiles();
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED);
        //
        mainBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //
        mainBinding.downloadBtn.setOnClickListener(v -> showDownloadDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingFiles();
       }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startDownload(String url) {
        if (!url.trim().isEmpty()) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName)
                    .setDescription("Downloading...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadID  = downloadManager.enqueue(request);
            }

        }
    }
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the file URL to download");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);

        builder.setPositiveButton("Download", (dialog, which) -> {
            String url = input.getText().toString();
            if (!url.trim().isEmpty()) {
                startDownload(url);
                loadingFiles();
            } else {
                Toast.makeText(getApplicationContext(), "Please enter a valid PDF URL", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadingFiles(){
        String downloadPath =  Environment.getExternalStorageDirectory().toString()+"/Download/";
        File directory = new File(downloadPath);
        File[] files = directory.listFiles();
        mainBinding.recyclerview.setAdapter(new FileAdapter(Arrays.asList(files), this));
    }
}
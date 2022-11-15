package com.example.txtdb;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

public class Entities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entities);

        Intent intent = getIntent();
        String directory = intent.getStringExtra("chosedDirectoryBLEA!S!U!K!A");

//        File file = new File(directory.replace("content://", ""));

//        Log.i("Test", "We find it : " + directory);
//        Log.i("Test", "We find it : " + System.getProperty("user.dir"));
        File file = new File("/data/data/com.example.txtdb/txt_db");
        Log.i("Test", "Files in Dir : " + Arrays.toString(file.list()));


    }
}
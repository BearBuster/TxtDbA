package com.example.txtdb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.txtdb.workers.DbHelper;
import com.example.txtdb.workers.parserAddaptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    Button DirChooserBtn;
    TextView textView;
    DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DirChooserBtn = findViewById(R.id.DirChooserBtn);
        textView = findViewById(R.id.textId);

        //init tables from files
        File tablesPath = new File("/data/data/com.example.txtdb/txt_db");
        Set<String> tablesName = Arrays.stream(tablesPath.list()).filter(i -> !i.contains("Struct")).collect(Collectors.toSet());
        List<parserAddaptor> tables = new ArrayList<>();
        for(String tableName: tablesName){
            try {
                tables.add(new parserAddaptor(tableName, "/data/data/com.example.txtdb/txt_db/" ));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Log.i("Test", "Files in Dir : " + tablesName );

        //inti tables in sqlite
        for (parserAddaptor table: tables){
            table.createSql(dbHelper.getWritableDatabase());
        }

        //reade data from sqlite

        for (parserAddaptor table: tables) {
            List<List<String>> recordsList = new ArrayList<>(new ArrayList<>());
            List<String> columns = null;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + table.tableName, null);
            cursor.moveToFirst();
            do {
                columns = Arrays.asList(cursor.getColumnNames());
                List<String> tmpRecord = new ArrayList<>();
                for (String col: columns){
                    tmpRecord.add(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                }
                recordsList.add(tmpRecord);
            }while (cursor.moveToNext());
            textView.append(columns + "\n");
            for (List<String> record: recordsList){
                textView.append(record + "\n");
            }
        }



        System.out.println(tablesName);


    }
}
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.txtdb.workers.DbHelper;
import com.example.txtdb.workers.parserAddaptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    Button DirChooserBtn;
    TextView textView;
    EditText editText;
    DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DirChooserBtn = findViewById(R.id.DirChooserBtn);
        textView = findViewById(R.id.textId);
        editText = findViewById(R.id.simpleEditText);

        DirChooserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor_2 = null;
                cursor_2 = dbHelper.getReadableDatabase().rawQuery(String.valueOf(editText.getText()), null);
                System.out.println(editText.getText());
                textView.setText("");
                if (cursor_2.getCount() > 0) {
                    cursor_2.moveToFirst();
                    List<String> columns = Arrays.asList(cursor_2.getColumnNames());
                    textView.append("Columns : " + columns + "\n");
                    List<String> tmpRecord = new ArrayList<>();
                    do {
                        tmpRecord.clear();
                        for (String col : columns) {
                            try {
                                tmpRecord.add(cursor_2.getString(cursor_2.getColumnIndexOrThrow(col)));
                            } catch (Exception e) {
                                textView.setText(e.getMessage());
                            }
                        }
                        textView.append(tmpRecord + "\n");
                    } while (cursor_2.moveToNext());
                }else{
                    textView.setText("No Data");
                }
            }
        });

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
        List<String> tablesNameList = new ArrayList<>();
        for (parserAddaptor table: tables) {
            tablesNameList.add(table.tableName);
            List<List<String>> recordsList = new ArrayList<>(new ArrayList<>());
            List<String> columns = null;
            Cursor cursor = null;
            if(table.tableName == "Users"){
                cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + table.tableName + "WHERE age <= 13", null);
            }else {
                cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + table.tableName, null);
            }
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
        textView.append("\nTables:" + tablesNameList);



        System.out.println(tablesName);


    }
}
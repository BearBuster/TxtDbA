package com.example.txtdb.workers;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class parserAddaptor {
    public String tableName;
    Map<String, List<String>> columnsMap = new LinkedHashMap<String, List<String>>();
    List<List<String>> recordsList = new ArrayList<>(new ArrayList<>());

    @Override
    public String toString() {
        return "parserAddaptor{" +
                "tableName='" + tableName + '\'' +
                ", columnsMap=" + columnsMap +
                ", recordsList=" + recordsList +
                '}';
    }

    public parserAddaptor(String tableName, String path) throws FileNotFoundException {
        this.tableName = tableName;
        File columns = new File(path + tableName + "Struct");
        File records = new File(path + tableName);

        //Columns init
        FileReader columsReader = new FileReader(columns);
        Scanner columnsScanner = new Scanner(columsReader);
        while (columnsScanner.hasNextLine()){
            List<String> LineData = Arrays.asList(columnsScanner.nextLine().split("[|]"));
            columnsMap.put(LineData.get(0), LineData.subList(1, LineData.size()));
        }

        //Record init
        FileReader recordsReader = new FileReader(records);
        Scanner recordScanner = new Scanner(recordsReader);
        while(recordScanner.hasNextLine()){
            recordsList.add(Arrays.asList(recordScanner.nextLine().split("[|]")));
        }
    }

    public boolean createSql(SQLiteDatabase db){
        String createTable;
        String insertRecords;

        //Table
        createTable = "CREATE TABLE " + this.tableName + "( ";
        for(Map.Entry<String, List<String>> column: columnsMap.entrySet()){
            createTable += column.getKey();
            createTable += " " + String.join(" ", column.getValue()) + ",";
        }
        createTable = createTable.substring(0, createTable.length() - 1);
        createTable += ")";

        db.execSQL(createTable);

        //Records
        insertRecords = "INSERT INTO " + this.tableName + "(" + String.join(",", columnsMap.keySet()) + ") VALUES ";
        for (List<String> record: recordsList){
            insertRecords += "(" + String.join(",", record) + "),";
        }
        insertRecords = insertRecords.substring(0, insertRecords.length() - 1);
        db.execSQL(insertRecords);
        System.out.println(createTable);
        System.out.println(insertRecords);

        return true;
    }
}

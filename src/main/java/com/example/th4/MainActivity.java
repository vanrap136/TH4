package com.example.th4;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtMaLop, edtTenLop, edtSiSo;
    Button btnInsert, btnDelete, btnUpdate, btnQuery;
    ListView lv;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtMaLop = findViewById(R.id.edtMaLop);
        edtTenLop = findViewById(R.id.edtTenLop);
        edtSiSo = findViewById(R.id.edtSiSo);
        btnInsert = findViewById(R.id.btnInsert);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnQuery = findViewById(R.id.btnQuery);
        lv = findViewById(R.id.lv);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        lv.setAdapter(myadapter);
        mydatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);
        try {
            String sql = "CREATE TABLE tbllop(MaLop TEXT PRIMARY KEY, TenLop TEXT, SiSo INTEGER)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table đã tồn tại");
        }

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MaLop = edtMaLop.getText().toString();
                String TenLop = edtTenLop.getText().toString();
                String strSiSo = edtSiSo.getText().toString();

                if (MaLop.isEmpty() || TenLop.isEmpty() || strSiSo.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int SiSo = Integer.parseInt(strSiSo);

                if (SiSo <= 0) {
                    Toast.makeText(MainActivity.this, "Sĩ số phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem MaLop đã tồn tại chưa
                Cursor cursor = mydatabase.query("tbllop", null, "MaLop = ?", new String[]{MaLop}, null, null, null);
                if (cursor.getCount() > 0) {
                    Toast.makeText(MainActivity.this, "Mã lớp đã tồn tại", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }
                cursor.close();

                ContentValues myvalue = new ContentValues();
                myvalue.put("MaLop", MaLop);
                myvalue.put("TenLop", TenLop);
                myvalue.put("SiSo", SiSo);
                String msg = "";
                if (mydatabase.insert("tbllop", null, myvalue) == -1) {
                    msg = "Fail to Insert Record!";
                } else {
                    msg = "Insert record Successfully";
                    // Cập nhật lại ListView
                    updateListView();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MaLop = edtMaLop.getText().toString();
                String strSiSo = edtSiSo.getText().toString();

                if (MaLop.isEmpty() || strSiSo.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int SiSo = Integer.parseInt(strSiSo);

                if (SiSo <= 0) {
                    Toast.makeText(MainActivity.this, "Sĩ số phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem MaLop có tồn tại không
                Cursor cursor = mydatabase.query("tbllop", null, "MaLop = ?", new String[]{MaLop}, null, null, null);
                if (cursor.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "Mã lớp không tồn tại", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }
                cursor.close();

                ContentValues myvalue = new ContentValues();
                myvalue.put("SiSo", SiSo);
                int n = mydatabase.update("tbllop", myvalue, "MaLop = ?", new String[]{MaLop});
                String msg = "";
                if (n == 0) {
                    msg = "No record to Update";
                } else {
                    msg = n + " record is updated";
                    // Cập nhật lại ListView
                    updateListView();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int SiSo = Integer.parseInt(edtSiSo.getText().toString());
                String MaLop = edtMaLop.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("SiSo", SiSo);
                int n = mydatabase.update("tbllop", myvalue, "MaLop = ?", new String[]{MaLop});
                String msg = "";
                if (n == 0) {
                    msg = "No record to Update";
                } else {
                    msg = n + " record is updated";
                    // Cập nhật lại ListView
                    updateListView();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mylist.clear();
                Cursor c = mydatabase.query("tbllop", null, null, null, null, null, null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String data = c.getString(0) + " - " + c.getString(1) + " - " + c.getString(2);
                    mylist.add(data);
                    c.moveToNext();
                }
                c.close();
                myadapter.notifyDataSetChanged();
            }
        });
    }

    // Phương thức cập nhật ListView
    private void updateListView() {
        mylist.clear();
        Cursor c = mydatabase.query("tbllop", null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String data = c.getString(0) + " - " + c.getString(1) + " - " + c.getString(2);
            mylist.add(data);
            c.moveToNext();
        }
        c.close();
        myadapter.notifyDataSetChanged();
    }
}

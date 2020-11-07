package com.example.myfirstandroidapp;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GetContactGraphActivity extends AppCompatActivity {
    private BroadcastReceiver fileDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((Button) findViewById(R.id.submit_subject_and_date)).setEnabled(true);
            FileInputStream contactGraphFile =  null;
            try {
                contactGraphFile = getApplicationContext().openFileInput("ContactGraph");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            Scanner scanner = new Scanner(contactGraphFile);

            TableLayout tableLayout = (TableLayout) findViewById(R.id.contact_graph);
            tableLayout.removeAllViews();
            TableRow tableRowHeader = new TableRow(getApplicationContext());
            {
                TextView textView = new TextView(getApplicationContext());
                textView.setText("subject_id");
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setPadding(10, 2, 10, 2);
                textView.setBackgroundResource(R.drawable.cell_shape);
                tableRowHeader.addView(textView);
            }
            String[] subjectIds = getResources().getStringArray(R.array.subject_array);
            for(String subjectId : subjectIds) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(subjectId);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setPadding(10, 2, 10, 2);
                textView.setBackgroundResource(R.drawable.cell_shape);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tableRowHeader.addView(textView);
            }
            tableLayout.addView(tableRowHeader);

            for(String subjectId : subjectIds) {
                TableRow tableRow = new TableRow(getApplicationContext());
                {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(subjectId);
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                    textView.setPadding(10, 2, 10, 2);
                    textView.setBackgroundResource(R.drawable.cell_shape);
                    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tableRow.addView(textView);
                }
                for(String contactSubjectId : subjectIds) {
                    String adjacency = scanner.next();
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(adjacency);
                    textView.setPadding(10, 2, 10, 2);
                    textView.setBackgroundResource(R.drawable.cell_shape);
                    if(adjacency.equals("1")){
                        textView.setTextColor(Color.RED);
                    }
                    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_contact_graph);

        getApplicationContext().registerReceiver(fileDownloadReceiver, new IntentFilter("Contact Graph File Downloaded"));
        final TextView inputDate = findViewById(R.id.selected_date);

        final Button selectDateButton = (Button) findViewById(R.id.select_date_button);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = 2011;
                int month = 06;
                int day = 15;

                DatePickerDialog datePickerDialog = new DatePickerDialog(GetContactGraphActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                inputDate.setText(String.format("%4d-%2d-%2d", year, monthOfYear + 1, dayOfMonth)
                                        .replace(' ', '0'));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.subject_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subject_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button submit = (Button) findViewById(R.id.submit_subject_and_date);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)(view)).setEnabled(false);
                ((TableLayout) findViewById(R.id.contact_graph)).removeAllViews();
                Intent intent = new Intent(getApplicationContext(), DownloadFileService.class);
                intent.putExtra("subject_id", spinner.getSelectedItem().toString());
                intent.putExtra("date", inputDate.getText().toString());
                startService(intent);
            }
        });
    }
}

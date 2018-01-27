package com.zak.stump.coinmotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private AutoCompleteTextView autoFrom;
    private EditText valueFrom;
    private AutoCompleteTextView autoTo;
    private EditText valueTo;
    private DatePicker dateOfRate;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        autoFrom = (AutoCompleteTextView) findViewById(R.id.autoFrom);
        valueFrom = (EditText) findViewById(R.id.valueFrom);
        autoTo = (AutoCompleteTextView) findViewById(R.id.autoTo);
        valueTo = (EditText) findViewById(R.id.valueTo);
        dateOfRate = (DatePicker) findViewById(R.id.dateOfRate);
        convertButton = (Button) findViewById(R.id.convertButton);

        convertButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.convertButton:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String valueFromString = valueFrom.getText().toString().trim();
        if (TextUtils.isEmpty(valueFromString)) {
            Toast.makeText(this, "valueFrom empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String valueToString = valueTo.getText().toString().trim();
        if (TextUtils.isEmpty(valueToString)) {
            Toast.makeText(this, "valueTo empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Otherwise print values
        Toast.makeText(this, valueFromString + valueToString, Toast.LENGTH_SHORT).show();



    }
}

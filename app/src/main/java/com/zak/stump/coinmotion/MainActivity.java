package com.zak.stump.coinmotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText valueFrom;
    private EditText valueTo;
    private DatePicker dateOfRate;
    private Button convertButton;
    private AutoCompleteTextView autoFrom;
    private AutoCompleteTextView autoTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        valueFrom = (EditText) findViewById(R.id.valueFrom);
        valueTo = (EditText) findViewById(R.id.valueTo);
        dateOfRate = (DatePicker) findViewById(R.id.dateOfRate);
        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setOnClickListener(this);
        autoFrom = (AutoCompleteTextView) findViewById(R.id.autoFrom);
        autoFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoFrom.showDropDown();
            }
        });
        autoTo = (AutoCompleteTextView) findViewById(R.id.autoTo);
        autoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoTo.showDropDown();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, CUR_CODES);
        autoFrom.setAdapter(adapter);
        autoTo.setAdapter(adapter);
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

    private static final String[] CUR_CODES = new String[]{
            "EUR", "USD", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBP", "HKD",
            "HRK", "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP",
            "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    };
}

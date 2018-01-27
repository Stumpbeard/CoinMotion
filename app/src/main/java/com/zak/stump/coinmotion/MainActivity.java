package com.zak.stump.coinmotion;

import android.app.DownloadManager;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText valueFrom;
    private EditText valueTo;
    private DatePicker dateOfRate;
    private Button convertButton;
    private AutoCompleteTextView autoFrom;
    private AutoCompleteTextView autoTo;
    private Locale deviceLocale;
    private Currency defaultCur;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting locale for purposes of default currency code
        deviceLocale = Locale.getDefault();
        defaultCur = Currency.getInstance(deviceLocale);

        initView();
    }

    private void initView() {
        valueFrom = (EditText) findViewById(R.id.valueFrom);
        valueTo = (EditText) findViewById(R.id.valueTo);

        dateOfRate = (DatePicker) findViewById(R.id.dateOfRate);
        Date currentDate = Calendar.getInstance().getTime();
        dateOfRate.setMaxDate(currentDate.getTime());

        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setOnClickListener(this);

        autoFrom = (AutoCompleteTextView) findViewById(R.id.autoFrom);
        autoFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoFrom.showDropDown();
            }
        });
        autoFrom.setText(defaultCur.getCurrencyCode());

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

        requestQueue = Volley.newRequestQueue(this);
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
        // Error if amount to be converted is not entered
        String valueFromString = valueFrom.getText().toString().trim();
        if (TextUtils.isEmpty(valueFromString)) {
            Toast.makeText(this, "Please enter a value to be converted.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Error if both currencies are not selected
        if (!CUR_CODES.contains(autoFrom.getText().toString()) || !CUR_CODES.contains(autoTo.getText().toString())) {
            Toast.makeText(this, "Please choose currencies for conversion from the list.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect and format strings for URL from datepicker and autofills
        String day = String.format(deviceLocale,"%02d", dateOfRate.getDayOfMonth());
        String month = String.format(deviceLocale,"%02d", dateOfRate.getMonth() + 1);
        String year = String.format(deviceLocale,"%04d", dateOfRate.getYear());
        String curFrom = autoFrom.getText().toString();
        String curTo = autoTo.getText().toString();

        String url = String.format(deviceLocale, "https://api.fixer.io/%s-%s-%s?base=%s&symbols=%s", year, month, day, curFrom, curTo);
        System.out.println(url);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, "Succeeded request", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed request, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(objRequest);

    }

    private static final List CUR_CODES = Arrays.asList(
            "EUR", "USD", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBP", "HKD",
            "HRK", "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP",
            "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    );
}

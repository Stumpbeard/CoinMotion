package com.zak.stump.coinmotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
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
    private TextView curFrom;
    private TextView curTo;
    private TextView instructions;
    private ImageButton switchButton;
    private TextView dateInstr;

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

        curFrom = (TextView) findViewById(R.id.curFrom);
        curFrom.setOnClickListener(this);

        curTo = (TextView) findViewById(R.id.curTo);
        curTo.setOnClickListener(this);

        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setOnClickListener(this);

        autoFrom = (AutoCompleteTextView) findViewById(R.id.autoFrom);
        // Open up all options when clicked
        autoFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoFrom.showDropDown();
            }
        });
        // Autofill first box with currency code of system locale
        autoFrom.setText(defaultCur.getCurrencyCode().trim());
        if (CUR_CODES.contains(autoFrom.getText().toString())) {
            Currency currency = Currency.getInstance(autoFrom.getText().toString());
            curFrom.setText(currency.getSymbol());
        }
        // Watch for matching currencies and update symbol
        autoFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (CUR_CODES.contains(autoFrom.getText().toString())) {
                    Currency currency = Currency.getInstance(charSequence.toString());
                    curFrom.setText(currency.getSymbol());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoTo = (AutoCompleteTextView) findViewById(R.id.autoTo);
        autoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoTo.showDropDown();
            }
        });
        autoTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (CUR_CODES.contains(autoTo.getText().toString())) {
                    Currency currency = Currency.getInstance(charSequence.toString());
                    curTo.setText(currency.getSymbol());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currency_choices, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.currency_choices, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoFrom.setAdapter(adapter1);
        autoTo.setAdapter(adapter2);

        requestQueue = Volley.newRequestQueue(this);

        instructions = (TextView) findViewById(R.id.dateInstr);
        instructions.setOnClickListener(this);

        switchButton = (ImageButton) findViewById(R.id.switchButton);
        switchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.convertButton:
                submit();
                break;
            case R.id.switchButton:
                switchCurrencies();
                break;
        }
    }

    // Exchange the values of the two currencies
    private void switchCurrencies() {
        String tempFrom = autoFrom.getText().toString();
        String tempTo = autoTo.getText().toString();
        autoFrom.setText(tempTo);
        autoTo.setText(tempFrom);

        String valueToText = valueTo.getText().toString();
        if (!TextUtils.isEmpty(valueToText)){
            tempFrom = valueFrom.getText().toString();
            tempTo = valueTo.getText().toString();
            valueFrom.setText(tempTo);
            valueTo.setText(tempFrom);
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
        String day = String.format(deviceLocale, "%02d", dateOfRate.getDayOfMonth());
        String month = String.format(deviceLocale, "%02d", dateOfRate.getMonth() + 1);
        String year = String.format(deviceLocale, "%04d", dateOfRate.getYear());
        String curFrom = autoFrom.getText().toString();
        String curTo = autoTo.getText().toString();

        String url = String.format(deviceLocale, "https://api.fixer.io/%s-%s-%s?base=%s&symbols=%s", year, month, day, curFrom, curTo);
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Get values from response
                    JSONObject objRates = response.getJSONObject("rates");
                    Double rate = objRates.getDouble(autoTo.getText().toString());

                    // Do exchange math
                    Double fromAmount = Double.valueOf(valueFrom.getText().toString());
                    String toAmount = String.valueOf(fromAmount * rate);

                    // Populate field
                    valueTo.setText(toAmount);

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Problem fetching conversion.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed request, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        // API acts unexpected if base and symbols are the same, so set to same value
        if (autoFrom.getText().toString().equals(autoTo.getText().toString())) {
            valueTo.setText(valueFrom.getText().toString());
            return;
        }
        // Otherwise fire request
        requestQueue.add(objRequest);

    }

    private static final List CUR_CODES = Arrays.asList(
            "EUR", "USD", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBP", "HKD",
            "HRK", "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP",
            "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    );
}

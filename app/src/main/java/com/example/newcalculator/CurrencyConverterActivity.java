package com.example.newcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CurrencyConverterActivity extends AppCompatActivity {

    Spinner spinnerFromCurrency, spinnerToCurrency;
    EditText amountEditText;
    TextView resultTextView;
    Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        // Initialize views
        spinnerFromCurrency = findViewById(R.id.spinner_from_currency);
        spinnerToCurrency = findViewById(R.id.spinner_to_currency);
        amountEditText = findViewById(R.id.edit_text_amount);
        resultTextView = findViewById(R.id.result_text_view);
        convertButton = findViewById(R.id.convert_button);

        // Set up spinners with currency options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromCurrency.setAdapter(adapter);
        spinnerToCurrency.setAdapter(adapter);

        // Set up convert button click listener
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void convertCurrency() {
        String fromCurrency = spinnerFromCurrency.getSelectedItem().toString();
        String toCurrency = spinnerToCurrency.getSelectedItem().toString();
        String amountText = amountEditText.getText().toString();

        if (amountText.isEmpty()) {
            resultTextView.setText("Please enter a valid amount.");
            return;
        }

        double amount = Double.parseDouble(amountText);

        // Get conversion rate
        double conversionRate = getConversionRate(fromCurrency, toCurrency);
        double convertedAmount = amount * conversionRate;

        // Display the result
        resultTextView.setText(String.format("Converted Amount: %.2f %s", convertedAmount, toCurrency));
    }

    private double getConversionRate(String fromCurrency, String toCurrency) {
        // Placeholder for conversion rates (could be replaced with real data from an API)
        if (fromCurrency.equals("RS(₹)") && toCurrency.equals("EUR(€)")) {
            return 0.011; // Example rate
        }
        if (fromCurrency.equals("RS(₹)") && toCurrency.equals("USD($)")) {
            return 0.012; // Example rate
        }
        if (fromCurrency.equals("RS(₹)") && toCurrency.equals("YEN(¥)")) {
            return 1.81; // Example rate
        }if (fromCurrency.equals("RS(₹)") && toCurrency.equals("POUND(£)")) {
            return 0.0093; // Example rate
        }
        if (fromCurrency.equals("EUR(€)") && toCurrency.equals("RS(₹)")) {
            return 90.40; // Example rate
        }if (fromCurrency.equals("USD($)") && toCurrency.equals("RS(₹)")) {
            return 86.21; // Example rate
        }if (fromCurrency.equals("YEN(¥)") && toCurrency.equals("RS(₹)")) {
            return 0.55; // Example rate
        }if (fromCurrency.equals("POUND(£)") && toCurrency.equals("RS(₹)")) {
            return 107.59; // Example rate
        }

        if (fromCurrency.equals("USD($)") && toCurrency.equals("EUR(€)")) {
            return 0.95; // Example rate
        }if (fromCurrency.equals("USD($)") && toCurrency.equals("YEN(¥)")) {
            return 156.12; // Example rate
        }if (fromCurrency.equals("USD($)") && toCurrency.equals("POUND(£)")) {
            return 0.80; // Example rate
        }
        if (fromCurrency.equals("EUR(€)") && toCurrency.equals("USD($)")) {
            return 1.05; // Example rate
        } if (fromCurrency.equals("EUR(€)") && toCurrency.equals("YEN(¥)")) {
            return 164.00; // Example rate
        } if (fromCurrency.equals("EUR(€)") && toCurrency.equals("POUND(£)")) {
            return 0.84; // Example rate
        }

        if (fromCurrency.equals("YEN(¥)") && toCurrency.equals("USD($)")) {
            return 0.0064; // Example rate
        }if (fromCurrency.equals("YEN(¥)") && toCurrency.equals("EUR(€)")) {
            return 0.0061; // Example rate
        }if (fromCurrency.equals("YEN(¥)") && toCurrency.equals("POUND(£)")) {
            return 0.0051; // Example rate
        }

        if (fromCurrency.equals("POUND(£)") && toCurrency.equals("USD($)")) {
            return 1.25; // Example rate
        }
        if (fromCurrency.equals("POUND(£)") && toCurrency.equals("EUR(€)")) {
            return 1.19; // Example rate
        }
        if (fromCurrency.equals("POUND(£)") && toCurrency.equals("YEN(¥)")) {
            return 194.18; // Example rate
        }
        // Add more currency conversion logic here...

        return 1.0; // Default conversion rate (same currency)
    }
}

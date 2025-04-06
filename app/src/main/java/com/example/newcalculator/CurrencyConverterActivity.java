package com.example.newcalculator;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrencyConverterActivity extends AppCompatActivity {
    // Currency class to hold comprehensive information
    public static class Currency {
        String code;
        String name;
        String country;
        String flag;
        String symbol;
        String continent;

        public Currency(String code, String name, String country, String flag, String symbol, String continent) {
            this.code = code;
            this.name = name;
            this.country = country;
            this.flag = flag;
            this.symbol = symbol;
            this.continent = continent;
        }

        @Override
        public String toString() {
            return flag + " " + code + " - " + name;
        }
    }

    // Comprehensive Currency List
    private static final List<Currency> CURRENCIES = new ArrayList<Currency>() {{
        add(new Currency("USD", "United States Dollar", "United States of America", "🇺🇸", "$", "North America"));
        add(new Currency("EUR", "Euro", "European Union", "🇪🇺", "€", "Europe"));
        add(new Currency("GBP", "British Pound Sterling", "United Kingdom", "🇬🇧", "£", "Europe"));
        add(new Currency("INR", "Indian Rupee", "India", "🇮🇳", "₹", "Asia"));
        add(new Currency("JPY", "Japanese Yen", "Japan", "🇯🇵", "¥", "Asia"));
        add(new Currency("AUD", "Australian Dollar", "Australia", "🇦🇺", "A$", "Oceania"));
        add(new Currency("CAD", "Canadian Dollar", "Canada", "🇨🇦", "C$", "North America"));
        add(new Currency("CHF", "Swiss Franc", "Switzerland", "🇨🇭", "Fr.", "Europe"));
        add(new Currency("CNY", "Chinese Yuan", "China", "🇨🇳", "¥", "Asia"));
        add(new Currency("SGD", "Singapore Dollar", "Singapore", "🇸🇬", "S$", "Asia"));
    }};

    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private EditText amountEditText;
    private TextView resultTextView, detailTextView;
    private Button convertButton;
    private ImageView swapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_24); // Set custom back arrow icon if needed
        }
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
        // Initialize views
        spinnerFromCurrency = findViewById(R.id.spinner_from_currency);
        spinnerToCurrency = findViewById(R.id.spinner_to_currency);
        amountEditText = findViewById(R.id.edit_text_amount);
        resultTextView = findViewById(R.id.result_text_view);
        detailTextView = findViewById(R.id.detail_text_view);
        convertButton = findViewById(R.id.btn_convert);
        swapButton = findViewById(R.id.btn_swap);
        Button btnConvert = findViewById(R.id.btn_convert);
        btnConvert.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        // Create custom adapter for spinners
        CurrencyAdapter currencyAdapter = new CurrencyAdapter(this,
                android.R.layout.simple_spinner_item, CURRENCIES);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapters
        spinnerFromCurrency.setAdapter(currencyAdapter);
        spinnerToCurrency.setAdapter(currencyAdapter);

        // Set default selections
        spinnerFromCurrency.setSelection(3); // INR
        spinnerToCurrency.setSelection(0);   // USD

        // Convert button click listener
        convertButton.setOnClickListener(v -> performConversion());

        // Swap button click listener
        swapButton.setOnClickListener(v -> {
            int fromPosition = spinnerFromCurrency.getSelectedItemPosition();
            int toPosition = spinnerToCurrency.getSelectedItemPosition();
            spinnerFromCurrency.setSelection(toPosition);
            spinnerToCurrency.setSelection(fromPosition);
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void performConversion() {
        String amountStr = amountEditText.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Currency fromCurrency = (Currency) spinnerFromCurrency.getSelectedItem();
            Currency toCurrency = (Currency) spinnerToCurrency.getSelectedItem();

            // Execute conversion task
            new CurrencyConversionTask(amount, fromCurrency, toCurrency).execute();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom Spinner Adapter
    private class CurrencyAdapter extends ArrayAdapter<Currency> {
        public CurrencyAdapter(CurrencyConverterActivity activity,
                               int simpleSpinnerItem,
                               List<Currency> currencies) {
            super(activity, simpleSpinnerItem, currencies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            Currency currency = getItem(position);
            view.setText(currency.toString());
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            Currency currency = getItem(position);
            view.setText(currency.toString());
            return view;
        }
    }

    private class CurrencyConversionTask extends AsyncTask<Void, Void, Double> {
        private double amount;
        private Currency fromCurrency;
        private Currency toCurrency;

        CurrencyConversionTask(double amount, Currency fromCurrency, Currency toCurrency) {
            this.amount = amount;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
        }

        @Override
        protected Double doInBackground(Void... voids) {
            try {
                // Fetch real-time exchange rates
                String apiUrl = String.format(
                        "https://open.exchangerate-api.com/v6/latest/%s",
                        fromCurrency.code
                );
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject rates = jsonResponse.getJSONObject("rates");

                return rates.getDouble(toCurrency.code);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Double rate) {
            if (rate != null) {
                double convertedAmount = amount * rate;

                @SuppressLint("DefaultLocale")
                String resultText = String.format(
                        "%.2f %s (%s) = %.2f %s (%s)",
                        amount, fromCurrency.code, fromCurrency.symbol,
                        convertedAmount, toCurrency.code, toCurrency.symbol
                );

                resultTextView.setText(resultText);

                // Update currency details
                String details = String.format(
                        "From: %s (%s, %s)\nTo: %s (%s, %s)",
                        fromCurrency.name, fromCurrency.country, fromCurrency.continent,
                        toCurrency.name, toCurrency.country, toCurrency.continent
                );
                detailTextView.setText(details);
            } else {
                // Fallback to offline rates
                double offlineRate = getOfflineExchangeRate(fromCurrency.code, toCurrency.code);
                double convertedAmount = amount * offlineRate;

                @SuppressLint("DefaultLocale")
                String resultText = String.format(
                        "%.2f %s (%s) = %.2f %s (%s) (Offline Rate)",
                        amount, fromCurrency.code, fromCurrency.symbol,
                        convertedAmount, toCurrency.code, toCurrency.symbol
                );

                resultTextView.setText(resultText);
                Toast.makeText(
                        CurrencyConverterActivity.this,
                        "Using offline exchange rates",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // Offline exchange rates method
    private double getOfflineExchangeRate(String fromCurrency, String toCurrency) {
        // Predefined offline rates
        if (fromCurrency.equals("INR") && toCurrency.equals("EUR")) {
            return 0.011; // Example rate
        }
        if (fromCurrency.equals("INR") && toCurrency.equals("USD")) {
            return 0.012; // Example rate
        }
        if (fromCurrency.equals("INR") && toCurrency.equals("JPY")) {
            return 1.81; // Example rate
        }if (fromCurrency.equals("INR") && toCurrency.equals("POUND(£)")) {
            return 0.0093; // Example rate
        }
        if (fromCurrency.equals("EUR") && toCurrency.equals("INR")) {
            return 90.40; // Example rate
        }if (fromCurrency.equals("USD") && toCurrency.equals("INR")) {
            return 86.21; // Example rate
        }if (fromCurrency.equals("JPY") && toCurrency.equals("INR")) {
            return 0.55; // Example rate
        }if (fromCurrency.equals("POUND(£)") && toCurrency.equals("INR")) {
            return 107.59; // Example rate
        }

        if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            return 0.95; // Example rate
        }if (fromCurrency.equals("USD") && toCurrency.equals("JPY")) {
            return 156.12; // Example rate
        }if (fromCurrency.equals("USD") && toCurrency.equals("GBP")) {
            return 0.80; // Example rate
        }
        if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) {
            return 1.05; // Example rate
        } if (fromCurrency.equals("EUR") && toCurrency.equals("JPY")) {
            return 164.00; // Example rate
        } if (fromCurrency.equals("EUR") && toCurrency.equals("GBP")) {
            return 0.84; // Example rate
        }

        if (fromCurrency.equals("JPY") && toCurrency.equals("USD")) {
            return 0.0064; // Example rate
        }if (fromCurrency.equals("JPY") && toCurrency.equals("EUR")) {
            return 0.0061; // Example rate
        }if (fromCurrency.equals("JPY") && toCurrency.equals("GBP")) {
            return 0.0051; // Example rate
        }

        if (fromCurrency.equals("GBP") && toCurrency.equals("USD")) {
            return 1.25; // Example rate
        }
        if (fromCurrency.equals("GBP") && toCurrency.equals("EUR")) {
            return 1.19; // Example rate
        }
        if (fromCurrency.equals("GBP") && toCurrency.equals("JPY")) {
            return 194.18; // Example rate
        }
        // Add more currency conversion logic here...

        return 1.0; // Default conversion rate (same currency)
    }
}

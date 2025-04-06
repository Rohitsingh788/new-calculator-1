package com.example.newcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DiscountCheckActivity extends AppCompatActivity {

    private TextInputEditText editTextOriginalPrice;
    private TextInputEditText editTextDiscountPercentage;
    private MaterialButton buttonCalculateDiscount;
    private MaterialTextView textViewDiscountResult;
    private TextInputLayout layoutOriginalPrice;
    private TextInputLayout layoutDiscountPercentage;

    // Formatter for comma-separated numbers
    private NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_check);
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        // Set Click Listener for Back Button
        buttonBack.setOnClickListener(v -> finish());
        // Initialize number formatter
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        MaterialButton buttonCalculate = findViewById(R.id.buttonCalculateDiscount);

        buttonCalculate.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        // Initialize views
        initializeViews();

        // Setup input validation and formatting
        setupInputValidation();

        // Set click listener for calculate button
        buttonCalculateDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDiscount();
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        editTextOriginalPrice = findViewById(R.id.editTextOriginalPrice);
        editTextDiscountPercentage = findViewById(R.id.editTextDiscountPercentage);
        buttonCalculateDiscount = findViewById(R.id.buttonCalculateDiscount);
        textViewDiscountResult = findViewById(R.id.textViewDiscountResult);
        layoutOriginalPrice = findViewById(R.id.layoutOriginalPrice);
        layoutDiscountPercentage = findViewById(R.id.layoutDiscountPercentage);

        // Add text change listener for formatting
        editTextOriginalPrice.addTextChangedListener(new CommaSeparatorTextWatcher(editTextOriginalPrice));
    }

    private void setupInputValidation() {
        // Input validation logic remains the same as previous example
    }

    private void calculateDiscount() {
        // Remove commas before parsing
        String priceStr = editTextOriginalPrice.getText().toString().replace(",", "").trim();
        String percentageStr = editTextDiscountPercentage.getText().toString().trim();

        // Validate inputs
        if (priceStr.isEmpty() || percentageStr.isEmpty()) {
            Toast.makeText(this, "Please enter both price and percentage", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convert inputs to numbers
            double originalPrice = Double.parseDouble(priceStr);
            double discountPercentage = Double.parseDouble(percentageStr);

            // Calculate discount
            double discountAmount = originalPrice * (discountPercentage / 100);
            double discountedPrice = originalPrice - discountAmount;

            // Format results with comma-separated numbers
            String result = String.format(
                    "Original Price: %s\n" +
                            "Discount: %s%%\n" +
                            "Discount Amount(You Save): %s\n" +
                            "Final Price: %s",
                    numberFormat.format(originalPrice),
                    numberFormat.format(discountPercentage),
                    numberFormat.format(discountAmount),
                    numberFormat.format(discountedPrice)
            );

            // Display results
            textViewDiscountResult.setText(result);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom TextWatcher for adding commas while typing
    private class CommaSeparatorTextWatcher implements TextWatcher {
        private TextInputEditText editText;
        private String current = "";

        public CommaSeparatorTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(current)) {
                editText.removeTextChangedListener(this);

                String cleanString = s.toString().replace(",", "");

                try {
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = numberFormat.format(parsed);

                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                } catch (NumberFormatException e) {
                    // Handle parsing error if needed
                }

                editText.addTextChangedListener(this);
            }
        }
    }
}
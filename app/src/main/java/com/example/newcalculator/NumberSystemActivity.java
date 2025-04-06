package com.example.newcalculator;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class NumberSystemActivity extends AppCompatActivity {

    private TextInputEditText editTextInputNumber;
    private Spinner spinnerInputBase;
    private Spinner spinnerOutputBase;
    private MaterialButton buttonConvert;
    private TextView textViewResult;
    private FloatingActionButton buttonExchangeBase;

    // Base names
    private String[] bases = {"Binary", "Decimal", "Hexadecimal", "Octal"};
    private int[] baseValues = {2, 10, 16, 8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_system);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
        // Initialize views
        initializeViews();

        // Setup base spinners
        setupBaseSpinners();
        MaterialButton buttonConvert = findViewById(R.id.buttonConvert);

        buttonConvert.setOnTouchListener((v, event) -> {
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

        // Set convert button click listener
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertNumber();
            }
        });

        // Set exchange base button click listener
        buttonExchangeBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeBaseSpinners();
            }
        });
    }

    private void initializeViews() {
        editTextInputNumber = findViewById(R.id.editTextInputNumber);
        spinnerInputBase = findViewById(R.id.spinnerInputBase);
        spinnerOutputBase = findViewById(R.id.spinnerOutputBase);
        buttonConvert = findViewById(R.id.buttonConvert);
        textViewResult = findViewById(R.id.textViewResult);
        buttonExchangeBase = findViewById(R.id.buttonExchangeBase);
    }

    private void setupBaseSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                bases
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInputBase.setAdapter(adapter);
        spinnerOutputBase.setAdapter(adapter);

        // Set default selections
        spinnerInputBase.setSelection(1); // Decimal
        spinnerOutputBase.setSelection(0); // Binary
    }

    private void exchangeBaseSpinners() {
        // Get current selections
        int inputBaseSelection = spinnerInputBase.getSelectedItemPosition();
        int outputBaseSelection = spinnerOutputBase.getSelectedItemPosition();

        // Swap spinner selections
        spinnerInputBase.setSelection(outputBaseSelection);
        spinnerOutputBase.setSelection(inputBaseSelection);

        // Optionally, convert the number after swapping
        convertNumber();
    }

    private void convertNumber() {
        String inputNumberStr = editTextInputNumber.getText().toString().trim();

        if (inputNumberStr.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputBasePosition = spinnerInputBase.getSelectedItemPosition();
        int outputBasePosition = spinnerOutputBase.getSelectedItemPosition();

        int inputBase = baseValues[inputBasePosition];
        int outputBase = baseValues[outputBasePosition];

        try {
            // Convert to decimal first
            long decimalValue = convertToDecimal(inputNumberStr, inputBase);

            // Convert from decimal to target base
            String convertedNumber = convertFromDecimal(decimalValue, outputBase);
            // Special case: Ensure hex-to-binary conversion maintains 4-bit per hex digit
            if (inputBase == 16 && outputBase == 2) {
                convertedNumber = convertHexToBinary(inputNumberStr);
            }
            // Display result
            textViewResult.setText(String.format(
                    "Input: %s (%s)\nOutput: %s (%s)",
                    inputNumberStr,
                    bases[inputBasePosition],
                    convertedNumber,
                    bases[outputBasePosition]
            ));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number for selected base", Toast.LENGTH_SHORT).show();
        }
    }
    private String convertHexToBinary(String hexNumber) {
        StringBuilder binaryString = new StringBuilder();

        // Convert each hex digit to a 4-bit binary equivalent
        for (char hexChar : hexNumber.toUpperCase().toCharArray()) {
            int decimalValue = Integer.parseInt(String.valueOf(hexChar), 16);
            String binaryChunk = String.format("%4s", Integer.toBinaryString(decimalValue)).replace(' ', '0');
            binaryString.append(binaryChunk);
        }

        return binaryString.toString();
    }

    private long convertToDecimal(String number, int base) {
        return Long.parseLong(number, base);
    }

    private String convertFromDecimal(long decimalValue, int base) {
        switch (base) {
            case 2:
                return Long.toBinaryString(decimalValue);
            case 8:
                return Long.toOctalString(decimalValue);
            case 16:
                return Long.toHexString(decimalValue).toUpperCase();
            case 10:
            default:
                return String.valueOf(decimalValue);
        }
    }
}
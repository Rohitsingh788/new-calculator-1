package com.example.newcalculator;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

public class BMICalculatorActivity extends AppCompatActivity {

    private EditText etWeight, etHeight;
    private MaterialButton btnCalculateBMI;
    private TextView tvBMIResult, tvBMICategory, tvBMIDescription;
    private Spinner spinnerWeightUnit, spinnerHeightUnit;
    private CardView cardBMIResult;
    private String selectedWeightUnit = "kg";
    private String selectedHeightUnit = "cm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
        MaterialButton btnCalculateBMI = findViewById(R.id.btnCalculateBMI);

        btnCalculateBMI.setOnTouchListener((v, event) -> {
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
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnCalculateBMI = findViewById(R.id.btnCalculateBMI);
        tvBMIResult = findViewById(R.id.tvBMIResult);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        tvBMIDescription = findViewById(R.id.tvBMIDescription);
        spinnerWeightUnit = findViewById(R.id.spinnerWeightUnit);
        spinnerHeightUnit = findViewById(R.id.spinnerHeightUnit);
        cardBMIResult = findViewById(R.id.cardBMIResult);

        // Setup spinners
        setupWeightUnitSpinner();
        setupHeightUnitSpinner();

        btnCalculateBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });

    }

    private void setupWeightUnitSpinner() {
        ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter.createFromResource(
                this, R.array.weight_units, android.R.layout.simple_spinner_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeightUnit.setAdapter(weightAdapter);

        spinnerWeightUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWeightUnit = parent.getItemAtPosition(position).toString();
                etWeight.setHint("Weight (" + selectedWeightUnit + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupHeightUnitSpinner() {
        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(
                this, R.array.height_units, android.R.layout.simple_spinner_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHeightUnit.setAdapter(heightAdapter);

        spinnerHeightUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHeightUnit = parent.getItemAtPosition(position).toString();
                etHeight.setHint("Height (" + selectedHeightUnit + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calculateBMI() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convert weight to kg
            double weight = Double.parseDouble(weightStr);
            if (selectedWeightUnit.equals("Pounds")) {
                weight = convertPoundsToKg(weight); // Convert pounds to kg
            }

            // Convert height to meters
            double height = convertHeightToMeters(Double.parseDouble(heightStr), selectedHeightUnit);

            double bmi = weight / (height * height);

            // Update UI
            updateBMIResult(bmi);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBMIResult(double bmi) {
        // Format BMI result
        String bmiResultText = String.format("BMI: %.1f", bmi);
        tvBMIResult.setText(bmiResultText);

        // Determine BMI Category and Description
        String category;
        String description;
        int categoryColor;

        if (bmi < 18.5) {
            category = "Underweight";
            description = "You may need to gain some weight. Consult a healthcare professional.";
            categoryColor = 0xFFFF9800; // Orange
        } else if (bmi >= 18.5 && bmi < 25) {
            category = "Normal weight";
            description = "Great! You have a healthy weight range.";
            categoryColor = 0xFF4CAF50; // Green
        } else if (bmi >= 25 && bmi < 30) {
            category = "Overweight";
            description = "You may be at risk. Consider diet and exercise.";
            categoryColor = 0xFFFF5722; // Deep Orange
        } else {
            category = "Obese";
            description = "High health risk. Consult a healthcare professional.";
            categoryColor = 0xFFF44336; // Red
        }

        tvBMICategory.setText(category);
        tvBMICategory.setTextColor(categoryColor);
        tvBMIDescription.setText(description);

        // Show result card
        cardBMIResult.setVisibility(View.VISIBLE);
    }

    // Conversion methods remain the same as in previous implementation
    private double convertPoundsToKg(double Pounds) {
        return Pounds * 0.45359237;
    }

    private double convertHeightToMeters(double height, String unit) {
        switch (unit) {
            case "centimeters":
                return height / 100.0;
            case "m":
                return height;
            case "feet":
                return height * 0.3048;
            case "inches":
                return height * 0.0254;
            default:
                return height;
        }
    }
}
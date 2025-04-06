package com.example.newcalculator;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UnitConverterActivity extends AppCompatActivity {
    // Unit Conversion Categories
    private static final String[] CONVERSION_CATEGORIES = {
            "Length",
            "Weight",
            "Temperature",
            "Area",
            "Volume",
            "Speed",
            "Time",
            "Power"

    };

    // Conversion Maps for Different Categories
    private Map<String, Map<String, Double>> conversionFactors;

    // UI Components
    private Spinner categorySpinner;
    private Spinner fromUnitSpinner;
    private Spinner toUnitSpinner;
    private EditText inputValueEditText;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish()); // Close the activity when back arrow is clicked

        // Initialize conversion factors
        initializeConversionFactors();

        // Initialize views
        initializeViews();

        // Setup spinners
        setupCategorySpinner();

        Button convertButton = findViewById(R.id.convert_button);

        convertButton.setOnTouchListener((v, event) -> {
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
    }

    private void initializeConversionFactors() {
        conversionFactors = new HashMap<>();
// Power Conversions (base unit: Watt)
        Map<String, Double> powerConversions = new LinkedHashMap<>();
        powerConversions.put("Watt", 1.0);
        powerConversions.put("Kilowatt", 0.001);
        powerConversions.put("Megawatt", 0.000001);
        powerConversions.put("Horsepower", 0.001341);
        conversionFactors.put("Power", powerConversions);

        // Length Conversions (base unit: meter, arranged from smallest to biggest)
        Map<String, Double> lengthConversions = new LinkedHashMap<>();
        lengthConversions.put("Millimeter", 1000.0);
        lengthConversions.put("Centimeter", 100.0);
        lengthConversions.put("Meter", 1.0);
        lengthConversions.put("Kilometer", 0.001);
        lengthConversions.put("Mile", 0.000621371);
        conversionFactors.put("Length", lengthConversions);

        // Weight Conversions (base unit: kilogram, arranged from smallest to biggest)
        Map<String, Double> weightConversions = new LinkedHashMap<>();
        weightConversions.put("Milligram", 1000000.0);
        weightConversions.put("Gram", 1000.0);
        weightConversions.put("Kilogram", 1.0);
        weightConversions.put("Metric Ton", 0.001);
        weightConversions.put("Pound", 2.20462);
        conversionFactors.put("Weight", weightConversions);

        // Temperature Conversions (unique conversion logic)
        Map<String, Double> temperatureConversions = new LinkedHashMap<>();
        temperatureConversions.put("Kelvin", 1.0);
        temperatureConversions.put("Celsius", 1.0);
        temperatureConversions.put("Fahrenheit", 1.0);
        conversionFactors.put("Temperature", temperatureConversions);

        // Area Conversions (base unit: square meter, arranged from smallest to biggest)
        Map<String, Double> areaConversions = new LinkedHashMap<>();
        areaConversions.put("Square Inch", 1550.0031);
        areaConversions.put("Square Foot", 10.7639);
        areaConversions.put("Square Meter", 1.0);
        areaConversions.put("Hectare", 0.0001);
        areaConversions.put("Square Kilometer", 0.000001);
        conversionFactors.put("Area", areaConversions);

        // Volume Conversions (base unit: cubic meter, arranged from smallest to biggest)
        Map<String, Double> volumeConversions = new LinkedHashMap<>();
        volumeConversions.put("Milliliter", 1000000.0);
        volumeConversions.put("Liter", 1000.0);
        volumeConversions.put("Cubic Meter", 1.0);
        volumeConversions.put("Cubic Kilometer", 0.000000001);
        conversionFactors.put("Volume", volumeConversions);

        // Speed Conversions (base unit: meters per second, arranged from smallest to biggest)
        Map<String, Double> speedConversions = new LinkedHashMap<>();
        speedConversions.put("Meters/Second", 1.0);
        speedConversions.put("Kilometers/Hour", 3.6);
        speedConversions.put("Miles/Hour", 2.23694);
        speedConversions.put("Knots", 1.94384);
        conversionFactors.put("Speed", speedConversions);

        // Time Conversions (base unit: seconds, arranged from smallest to biggest)
        Map<String, Double> timeConversions = new LinkedHashMap<>();
        timeConversions.put("Second", 1.0);
        timeConversions.put("Minute", 1.0 / 60);
        timeConversions.put("Hour", 1.0 / 3600);
        timeConversions.put("Day", 1.0 / 86400);
        timeConversions.put("Week", 1.0 / (86400 * 7));
        timeConversions.put("Month", 1.0 / (86400 * 30));
        timeConversions.put("Year", 1.0 / (86400 * 365));
        conversionFactors.put("Time", timeConversions);
    }

    private void initializeViews() {
        categorySpinner = findViewById(R.id.category_spinner);
        fromUnitSpinner = findViewById(R.id.from_unit_spinner);
        toUnitSpinner = findViewById(R.id.to_unit_spinner);
        inputValueEditText = findViewById(R.id.input_value_edit_text);
        resultTextView = findViewById(R.id.result_text_view);

        // Convert button click listener
        findViewById(R.id.convert_button).setOnClickListener(v -> performConversion());
    }

    private void setupCategorySpinner() {
        // Category Spinner Setup
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CONVERSION_CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Category Selection Listener
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = CONVERSION_CATEGORIES[position];
                setupUnitSpinners(selectedCategory);
                clearResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupUnitSpinners(String category) {
        Map<String, Double> currentConversions = conversionFactors.get(category);
        if (currentConversions == null) return;

        String[] units = currentConversions.keySet().toArray(new String[0]);

        // From Unit Spinner
        ArrayAdapter<String> fromUnitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        fromUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromUnitSpinner.setAdapter(fromUnitAdapter);

        // To Unit Spinner
        ArrayAdapter<String> toUnitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        toUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toUnitSpinner.setAdapter(toUnitAdapter);
        fromUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearResult(); // Clear result when "From Unit" changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        toUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearResult(); // Clear result when "To Unit" changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void performConversion() {
        String inputValueStr = inputValueEditText.getText().toString().trim();
        if (inputValueStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputValueStr);
            String category = categorySpinner.getSelectedItem().toString();
            String fromUnit = fromUnitSpinner.getSelectedItem().toString();
            String toUnit = toUnitSpinner.getSelectedItem().toString();

            double result;
            // Special handling for temperature
            if (category.equals("Temperature")) {
                result = convertTemperature(inputValue, fromUnit, toUnit);
            } else {
                result = convertUnits(category, inputValue, fromUnit, toUnit);
            }

            resultTextView.setText(String.format("%s %s", formatResult(result), toUnit));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }
    private String formatResult(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value); // No decimal places if whole number
        } else {
            return String.format("%.4f", value).replaceAll("0*$", "").replaceAll("\\.$", ""); // Remove trailing zeros
        }
    }


    private double convertUnits(String category, double value, String fromUnit, String toUnit) {
        Map<String, Double> conversions = conversionFactors.get(category);
        if (conversions == null) return 0;

        double fromFactor = conversions.get(fromUnit);
        double toFactor = conversions.get(toUnit);

        // Convert to base unit first, then to target unit
        return (value / fromFactor) * toFactor;
    }

    private double convertTemperature(double value, String fromUnit, String toUnit) {
        // Temperature conversion logic remains the same as in previous implementation
        switch (fromUnit) {
            case "Celsius":
                switch (toUnit) {
                    case "Fahrenheit": return (value * 9/5) + 32;
                    case "Kelvin": return value + 273.15;
                    default: return value;
                }
            case "Fahrenheit":
                switch (toUnit) {
                    case "Celsius": return (value - 32) * 5/9;
                    case "Kelvin": return (value - 32) * 5/9 + 273.15;
                    default: return value;
                }
            case "Kelvin":
                switch (toUnit) {
                    case "Celsius": return value - 273.15;
                    case "Fahrenheit": return (value - 273.15) * 9/5 + 32;
                    default: return value;
                }
            default: return value;
        }
    }
    private void clearResult() {
        resultTextView.setText("");
    }
}
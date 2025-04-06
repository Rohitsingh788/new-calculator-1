package com.example.newcalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
public class GeometryCalculatorActivity extends AppCompatActivity {
    private Spinner shapeSpinner, calculationTypeSpinner, unitSpinner;
    private LinearLayout inputContainer;
    private Button calculateButton;
    private TextView resultTextView;

    private static final String[] SHAPE_TYPES = {
            "Square", "Rectangle", "Circle", "Triangle",
            "Cube", "Cuboid", "Sphere", "Cylinder", "Cone"
    };

    private static final String[][] CALCULATION_TYPES = {
            {"Area", "Perimeter"},           // Square
            {"Area", "Perimeter"},            // Rectangle
            {"Area", "Circumference"},        // Circle
            {"Area", "Perimeter"},            // Triangle
            {"Volume", "Surface Area"},       // Cube
            {"Volume", "Surface Area"},       // Cuboid
            {"Volume", "Surface Area"},       // Sphere
            {"Volume", "Surface Area", "Lateral Surface Area"}, // Cylinder
            {"Volume", "Surface Area", "Lateral Surface Area"}  // Cone
    };

    private static final String[] UNITS = {"cm", "m", "mm", "km", "inch", "ft"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geometry);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
        initializeViews();
        setupSpinners();
        setupCalculateButton();
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnTouchListener((v, event) -> {
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

    private void initializeViews() {
        shapeSpinner = findViewById(R.id.shapeSpinner);
        calculationTypeSpinner = findViewById(R.id.calculationTypeSpinner);
        unitSpinner = findViewById(R.id.unitSpinner);
        inputContainer = findViewById(R.id.inputContainer);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);
    }

    private void setupSpinners() {
        // Shape Spinner
        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                SHAPE_TYPES
        );
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);

        // Unit Spinner
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                UNITS
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        shapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalculationTypeSpinner(position);
                updateInputFields(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        calculationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateInputFields(shapeSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateCalculationTypeSpinner(int shapePosition) {
        ArrayAdapter<String> calculationTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CALCULATION_TYPES[shapePosition]
        );
        calculationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calculationTypeSpinner.setAdapter(calculationTypeAdapter);
    }

    private void updateInputFields(int shapePosition) {
        inputContainer.removeAllViews();
        String selectedShape = SHAPE_TYPES[shapePosition];
        String selectedCalculationType = calculationTypeSpinner.getSelectedItem().toString();

        switch (selectedShape) {
            case "Square":
                addEditText("Side Length");
                break;
            case "Rectangle":
                addEditText("Length");
                addEditText("Width");
                break;
            case "Circle":
                addEditText("Radius");
                break;
            case "Triangle":
                if (selectedCalculationType.equals("Area")) {
                    addEditText("Base");
                    addEditText("Height");
                } else if (selectedCalculationType.equals("Perimeter")) {
                    addEditText("Side 1");
                    addEditText("Side 2");
                    addEditText("Side 3");
                }
                break;
            case "Cube":
                addEditText("Side Length");
                break;
            case "Cuboid":
                addEditText("Length");
                addEditText("Width");
                addEditText("Height");
                break;
            case "Sphere":
                addEditText("Radius");
                break;
            case "Cylinder":
                addEditText("Radius");
                addEditText("Height");
                break;
            case "Cone":
                if (selectedCalculationType.equals("Surface Area")) {
                    addEditText("Radius");
                    addEditText("Slant Height");
                } else {
                    addEditText("Radius");
                    addEditText("Height");
                }
                break;
        }
    }

    private void addEditText(String hint) {
        // Inflate the custom input field layout
        View inputFieldView = LayoutInflater.from(this)
                .inflate(R.layout.geometry_input_field, inputContainer, false);

        TextInputLayout textInputLayout = inputFieldView.findViewById(R.id.textInputLayout);
        TextInputEditText editText = inputFieldView.findViewById(R.id.inputField);

        // Set hint
        textInputLayout.setHint(hint);

        // Optional: Add focus change listener to further customize hint behavior
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textInputLayout.setHintEnabled(true);
            }
        });

        inputContainer.addView(inputFieldView);
    }

    private double getInputValue(int index) {
        View inputView = inputContainer.getChildAt(index);
        TextInputEditText editText = inputView.findViewById(R.id.inputField);
        return Double.parseDouble(editText.getText().toString());
    }
    private void setupCalculateButton() {
        calculateButton.setOnClickListener(v -> {
            String selectedShape = shapeSpinner.getSelectedItem().toString();
            String selectedCalculationType = calculationTypeSpinner.getSelectedItem().toString();
            String selectedUnit = unitSpinner.getSelectedItem().toString();

            double result = calculateGeometry(selectedShape, selectedCalculationType);

            if (result != -1) {
                resultTextView.setText(String.format("Result: %.2f %s (%s)",
                        result,
                        selectedCalculationType.toLowerCase(),
                        selectedUnit));
            } else {
                resultTextView.setText("Invalid input");
            }
        });
    }

    private double calculateGeometry(String shape, String calculationType) {
        try {
            switch (shape) {
                case "Square":
                    double side = getInputValue(0);
                    return calculationType.equals("Area") ? side * side : 4 * side;
                case "Rectangle":
                    double length = getInputValue(0);
                    double width = getInputValue(1);
                    return calculationType.equals("Area") ? length * width : 2 * (length + width);
                case "Circle":
                    double radius = getInputValue(0);
                    return calculationType.equals("Area")
                            ? Math.PI * radius * radius
                            : 2 * Math.PI * radius;
                case "Triangle":
                    if (calculationType.equals("Area")) {
                        double base = getInputValue(0);
                        double height = getInputValue(1);
                        return 0.5 * base * height;
                    } else if (calculationType.equals("Perimeter")) {
                        double side1 = getInputValue(0);
                        double side2 = getInputValue(1);
                        double side3 = getInputValue(2);
                        return side1 + side2 + side3;
                    }
                case "Cube":
                    double cubeSide = getInputValue(0);
                    return calculationType.equals("Volume")
                            ? Math.pow(cubeSide, 3)
                            : 6 * Math.pow(cubeSide, 2);
                case "Cuboid":
                    double cuboidLength = getInputValue(0);
                    double cuboidWidth = getInputValue(1);
                    double cuboidHeight = getInputValue(2);
                    return calculationType.equals("Volume")
                            ? cuboidLength * cuboidWidth * cuboidHeight
                            : 2 * (cuboidLength * cuboidWidth + cuboidLength * cuboidHeight + cuboidWidth * cuboidHeight);
                case "Sphere":
                    double sphereRadius = getInputValue(0);
                    return calculationType.equals("Volume")
                            ? (4.0/3.0) * Math.PI * Math.pow(sphereRadius, 3)
                            : 4 * Math.PI * Math.pow(sphereRadius, 2);
                case "Cylinder":
                    double cylinderRadius = getInputValue(0);
                    double cylinderHeight = getInputValue(1);
                    switch (calculationType) {
                        case "Volume":
                            return Math.PI * Math.pow(cylinderRadius, 2) * cylinderHeight;
                        case "Surface Area":
                            return 2 * Math.PI * cylinderRadius * (cylinderRadius + cylinderHeight);
                        case "Lateral Surface Area":
                            return 2 * Math.PI * cylinderRadius * cylinderHeight;
                    }
                case "Cone":
                    double coneRadius = getInputValue(0);
                    double coneHeight = getInputValue(1);

                    // Calculate Slant Height (if not provided)
                    double slantHeight = Math.sqrt((coneRadius * coneRadius) + (coneHeight * coneHeight));

                    switch (calculationType) {
                        case "Volume":
                            return (1.0 / 3.0) * Math.PI * Math.pow(coneRadius, 2) * coneHeight;
                        case "Surface Area":
                            return Math.PI * coneRadius * (coneRadius + slantHeight);
                        case "Lateral Surface Area":
                            return Math.PI * coneRadius * slantHeight;
                    }

                default:
                    return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }


}
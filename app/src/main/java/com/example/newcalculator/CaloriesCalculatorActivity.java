package com.example.newcalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;

public class CaloriesCalculatorActivity extends AppCompatActivity {
    private EditText etAge, etWeight, etHeight, etWeightChange;
    private RadioGroup genderGroup;
    private Spinner spinnerActivityLevel, spinnerGoal;
    private TextView tvResult;
    private PieChart pieChart;
    private EditText etDishName;
    private Button btnFetchCalories;
    private TextView tvFoodCalories;
    private TextView tvCaloriesResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnTouchListener((v, event) -> {
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

        // Initialize UI elements
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etWeightChange = findViewById(R.id.etWeightChange);
        genderGroup = findViewById(R.id.genderGroup);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        tvResult = findViewById(R.id.tvResult);
        pieChart = findViewById(R.id.pieChart);

        // Populate activity level spinner
        String[] activityLevels = {"Sedentary", "Light", "Moderate", "Active", "Very Active"};
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activityLevels);
        spinnerActivityLevel.setAdapter(activityAdapter);

        // Populate goal spinner
        String[] goals = {"Maintain Weight", "Lose Weight", "Gain Weight"};
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, goals);
        spinnerGoal.setAdapter(goalAdapter);

        // Button click event
        btnCalculate.setOnClickListener(v -> calculateCalories());
        // Initialize UI elements

    }

    private void calculateCalories() {
        // Get input values
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightChangeStr = etWeightChange.getText().toString().trim();

        // Validate input
        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert inputs to numbers
        int age = Integer.parseInt(ageStr);
        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr);
        float weightChange = weightChangeStr.isEmpty() ? 0 : Float.parseFloat(weightChangeStr);

        // Ensure height is in CM
        if (height < 50) {
            Toast.makeText(this, "Height should be in cm, not meters!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine gender
        boolean isMale = genderGroup.getCheckedRadioButtonId() == R.id.rbMale;

        // ✅ Correct BMR Calculation
        double bmr = isMale
                ? 88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age)
                : 447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age);

        // ✅ Activity level multiplier
        double activityMultiplier;
        switch (spinnerActivityLevel.getSelectedItemPosition()) {
            case 0: activityMultiplier = 1.2; break;   // Sedentary
            case 1: activityMultiplier = 1.375; break; // Light
            case 2: activityMultiplier = 1.55; break;  // Moderate
            case 3: activityMultiplier = 1.725; break; // Active
            case 4: activityMultiplier = 1.9; break;   // Very Active
            default: activityMultiplier = 1.2;
        }

        // ✅ Correct TDEE Calculation
        double tdee = bmr * activityMultiplier;

        // ✅ Fix Calories Needed for Weight Change
        double calorieAdjustmentPerDay = 0;
        int goalType = spinnerGoal.getSelectedItemPosition(); // 0 = Maintain, 1 = Lose, 2 = Gain

        if (goalType == 1 && weightChange > 0) {
            // If goal is "Lose Weight", subtract calories
            calorieAdjustmentPerDay = -(weightChange * 7700) / 30;
        } else if (goalType == 2 && weightChange > 0) {
            // If goal is "Gain Weight", add calories
            calorieAdjustmentPerDay = (weightChange * 7700) / 30;
        }

        // ✅ Fix Goal Intake Calculation
        double goalCalories = tdee;
        if (goalType == 1) { // Lose weight
            goalCalories = tdee + calorieAdjustmentPerDay;
        } else if (goalType == 2) { // Gain weight
            goalCalories = tdee + calorieAdjustmentPerDay;
        }

        // ✅ Display Corrected Results
        tvResult.setText(String.format(
                "BMR: %.2f kcal\nTDEE: %.2f kcal\nGoal Intake: %.2f kcal\nCalories Adjustment: %.2f kcal/day",
                bmr, tdee, goalCalories, calorieAdjustmentPerDay));

        // ✅ Update Pie Chart
        setPieChart(bmr, tdee, calorieAdjustmentPerDay);
    }




    private void setPieChart(double bmr, double tdee, double goalAdjustment) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // ✅ Correct calculations
        double activityCalories = tdee - bmr; // Activity Calories = TDEE - BMR
        double weightChangeCalories = Math.abs(goalAdjustment); // Ensure positive values in Pie Chart

        // ✅ Add valid values to Pie Chart
        if (bmr > 0) entries.add(new PieEntry((float) bmr, "BMR"));
        if (activityCalories > 0) entries.add(new PieEntry((float) activityCalories, "Activity"));
        if (weightChangeCalories > 0) entries.add(new PieEntry((float) weightChangeCalories, "Weight Change"));

        // 🚨 Prevent empty chart
        if (entries.isEmpty()) {
            Toast.makeText(this, "No valid data for Pie Chart", Toast.LENGTH_SHORT).show();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Calorie Breakdown");
        dataSet.setColors(new int[]{
                android.R.color.holo_blue_light,  // BMR
                android.R.color.holo_green_light, // Activity
                android.R.color.holo_red_light    // Weight Change
        }, this);

        // ✅ Fix: Show actual values instead of percentages
        pieChart.setUsePercentValues(false); // 🚀 Change this to show actual values!

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false); // Remove default text

        // ✅ Improve visibility
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.GRAY);
        // ✅ Refresh Pie Chart
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }


}

package com.example.newcalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaxCalculatorActivity extends AppCompatActivity {

    private Spinner countrySpinner, taxPayerSpinner, ageCategorySpinner, yearSpinner;
    private EditText incomeInput;
    private Button calculateButton;
    private TextView incomeTaxResult, surchargeResult, cessResult, totalTaxResult;
    private TextView afterTaxIncomeResult;
    private String selectedCountry, selectedTaxPayer, selectedAgeCategory, selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PieChart taxPieChart = findViewById(R.id.tax_pie_chart);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_calculator);

        yearSpinner = findViewById(R.id.year_spinner);
        taxPayerSpinner = findViewById(R.id.tax_payer_spinner);
        ageCategorySpinner = findViewById(R.id.age_category_spinner);
        ImageView backButton = findViewById(R.id.buttonBack);

        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
        // Initialize views
        afterTaxIncomeResult = findViewById(R.id.after_tax_income_result);
        taxPayerSpinner = findViewById(R.id.tax_payer_spinner);
        ageCategorySpinner = findViewById(R.id.age_category_spinner);
        incomeInput = findViewById(R.id.income_input);
        calculateButton = findViewById(R.id.calculate_button);
        incomeTaxResult = findViewById(R.id.income_tax_result);
        surchargeResult = findViewById(R.id.surcharge_result);
        cessResult = findViewById(R.id.cess_result);
        totalTaxResult = findViewById(R.id.total_tax_result);

        // Set up spinners
        setupSpinner(taxPayerSpinner, R.array.tax_payer_types);
        setupSpinner(ageCategorySpinner, R.array.age_categories);
        // Setup spinners
        setupSpinner(yearSpinner, R.array.financial_years);
        Button calculateButton = findViewById(R.id.calculate_button);

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
        // Calculate button click listener
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTax();
            }
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set a default selection
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (spinner == countrySpinner) {
                    selectedCountry = selectedItem;
                } else if (spinner == taxPayerSpinner) {
                    selectedTaxPayer = selectedItem;
                } else if (spinner == ageCategorySpinner) {
                    selectedAgeCategory = selectedItem;
                } else if (spinner == yearSpinner) {
                    selectedYear = selectedItem;
                    // Optional: Add a toast to verify year selection
                    Toast.makeText(TaxCalculatorActivity.this,
                            "Selected Financial Year: " + selectedYear,
                            Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Set default values if nothing is selected
                if (spinner == yearSpinner) {
                    selectedYear = "2023-2024";
                } else if (spinner == taxPayerSpinner) {
                    selectedTaxPayer = "Individual";
                } else if (spinner == ageCategorySpinner) {
                    selectedAgeCategory = "Less than 60 years";
                }
            }
        });
    }


    private void calculateTax() {
        String incomeStr = incomeInput.getText().toString();
        if (incomeStr.isEmpty()) {
            incomeTaxResult.setText("Please enter income.");
            return;
        }

        double income;
        try {
            income = Double.parseDouble(incomeStr);
        } catch (NumberFormatException e) {
            incomeTaxResult.setText("Invalid income value.");
            return;
        }

        // Ensure selectedTaxPayer and selectedAgeCategory are not null
        if (selectedTaxPayer == null || selectedAgeCategory == null) {
            incomeTaxResult.setText("Please select all fields.");
            return;
        }

        Log.d("TaxCalculator", "Income: " + income);
        Log.d("TaxCalculator", "Age Category: " + selectedAgeCategory);
        Log.d("TaxCalculator", "Tax Payer: " + selectedTaxPayer);

        double tax = calculateIncomeTax(income, selectedAgeCategory, selectedTaxPayer, selectedYear);
        double surcharge = calculateSurcharge(tax, income, selectedTaxPayer); // Pass income and tax payer type
        double cess = calculateCess(tax + surcharge);
        double totalTax = tax + surcharge + cess;

        // Calculate After-Tax Income
        double afterTaxIncome = income - totalTax;

        // Display results
        incomeTaxResult.setText(String.format("Income Tax: ₹%.2f", tax));
        surchargeResult.setText(String.format("Surcharge: ₹%.2f", surcharge));
        cessResult.setText(String.format("Health and Education Cess: ₹%.2f", cess));
        totalTaxResult.setText(String.format("Total Tax Liability: ₹%.2f", totalTax));
        afterTaxIncomeResult.setText(String.format("After-Tax Income: ₹%.2f", afterTaxIncome));
        updatePieChart(tax, surcharge, cess, afterTaxIncome);
    }
    private void updatePieChart(double tax, double surcharge, double cess, double afterTaxIncome) {
        PieChart taxPieChart = findViewById(R.id.tax_pie_chart);

        // Ensure values are non-negative
        tax = Math.max(tax, 0);
        surcharge = Math.max(surcharge, 0);
        cess = Math.max(cess, 0);
        afterTaxIncome = Math.max(afterTaxIncome, 0);

        // Avoid tiny values overlapping by setting a minimum display threshold
        double total = tax + surcharge + cess + afterTaxIncome;
        if (total == 0) {
            taxPieChart.clear();
            return;
        }

        // Define colors manually (as per previous setup)
        int[] colors = new int[]{
                Color.rgb(244, 67, 54),    // Red - Income Tax
                Color.rgb(76, 175, 80),   // Green - Surcharge
                Color.rgb( 33, 150, 243),    // Blue - Cess
                Color.rgb(255, 193, 7)     // Yellow - After-Tax Income
        };

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (tax > 0) entries.add(new PieEntry((float) tax, "Income Tax"));
        if (surcharge > 0) entries.add(new PieEntry((float) surcharge, "Surcharge"));
        if (cess > 0) entries.add(new PieEntry((float) cess, "Cess"));
        if (afterTaxIncome > 0) entries.add(new PieEntry((float) afterTaxIncome, "After-Tax Income"));

        PieDataSet dataSet = new PieDataSet(entries, "Tax Breakdown");
        dataSet.setColors(colors);  // Keep previous colors
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f); // Adds space between slices to prevent overlap
        dataSet.setValueTextColor(Color.BLACK); // Ensure text is visible

        PieData pieData = new PieData(dataSet);
        taxPieChart.setData(pieData);

        // Pie Chart Customizations
        taxPieChart.setUsePercentValues(false); // Show actual values, not percentages
        taxPieChart.getDescription().setEnabled(false);
        taxPieChart.setEntryLabelTextSize(12f);
        taxPieChart.setEntryLabelColor(Color.BLACK);
        taxPieChart.setHoleRadius(40f);
        taxPieChart.setTransparentCircleRadius(45f);
        taxPieChart.animateY(1000);

        // Customize Legend
        Legend legend = taxPieChart.getLegend();
        legend.setTextColor(Color.GRAY);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE); // ✅ Make legend labels square
        legend.setFormSize(12f); // ✅ Adjust square size

        taxPieChart.invalidate(); // Refresh the chart
    }

    private double calculateIncomeTax(double income, String ageCategory, String taxPayer, String financialYear) {
        double tax = 0;
        // Tax rules for India (FY 2023-24)
        if (financialYear.equals("2023-2024")) {
            if (taxPayer.equals("Individual")) {
                // For individuals, apply age-specific tax slabs
                if (ageCategory.equals("Less than 60 years")) {
                    if (income <= 250000) {
                        tax = 0;
                    } else if (income <= 500000) {
                        tax = (income - 250000) * 0.05;
                    } else if (income <= 1000000) {
                        tax = 12500 + (income - 500000) * 0.20;
                    } else {
                        tax = 112500 + (income - 1000000) * 0.30;
                    }
                } else if (ageCategory.equals("60 to 80 years")) {
                    if (income <= 300000) {
                        tax = 0;
                    } else if (income <= 500000) {
                        tax = (income - 300000) * 0.05;
                    } else if (income <= 1000000) {
                        tax = 10000 + (income - 500000) * 0.20;
                    } else {
                        tax = 110000 + (income - 1000000) * 0.30;
                    }
                } else if (ageCategory.equals("Above 80 years")) {
                    if (income <= 500000) {
                        tax = 0;
                    } else if (income <= 1000000) {
                        tax = (income - 500000) * 0.20;
                    } else {
                        tax = 100000 + (income - 1000000) * 0.30;
                    }
                }
            } else if (taxPayer.equals("Company")) {
                // For companies, apply corporate tax rate (example: 25%)
                tax = income * 0.30;
            }
        return tax;
        }
        if (financialYear.equals("2025-2026")) {
            double standardDeduction = 75000;
            double taxableIncome = income - standardDeduction;
            tax = 0;
            double rebate = 0;
            if (taxPayer.equals("Individual")) {


                if (taxableIncome <= 400000) {
                    tax = 0;
                } else if (taxableIncome <= 800000) {
                    tax = (taxableIncome - 400000) * 0.05;
                } else if (taxableIncome <= 1200000) {
                    tax = (400000 * 0.05) + (taxableIncome - 800000) * 0.10;
                } else if (taxableIncome <= 1600000) {
                    tax = (400000 * 0.05) + (400000 * 0.10) + (taxableIncome - 1200000) * 0.15;
                } else if (taxableIncome <= 2000000) {
                    tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + (taxableIncome - 1600000) * 0.20;
                } else if (taxableIncome <= 2400000) {


                    tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + (400000 * 0.20) + (taxableIncome - 2000000) * 0.25;
                } else {
                    tax = (400000 * 0.05) + (400000 * 0.10) + (400000 * 0.15) + (400000 * 0.20) + (400000 * 0.25) + (taxableIncome - 2400000) * 0.30;
                }
                // Apply rebate based on income
                if (income <= 800000) {
                    rebate = 20000;
                } else if (income <= 900000) {
                    rebate = 30000;
                } else if (income <= 1000000) {
                    rebate = 40000;
                } else if (income <= 1100000) {
                    rebate = 50000;
                } else if (income <= 1275000) {
                    rebate = 62400;
                }
                tax = Math.max(0, tax - rebate); // Ensure tax doesn't go negative
                // Apply age-based reductions
                if (tax > 0) {
                    if (ageCategory.equals("60 to 80 years")) {
                        tax *= 0.9; // 10% discount for senior citizens
                    } else if (ageCategory.equals("Above 80 years")) {
                        tax *= 0.8; // 20% discount for very senior citizens
                    }
                }
            }  else if (taxPayer.equals("Company")) {
                if (income <= 10000000) {
                    tax = taxableIncome * 0.25;
                } else if (income <= 100000000) {
                    tax = (10000000 * 0.25) + (taxableIncome - 10000000) * 0.30;
                } else {
                    tax = (10000000 * 0.25) + (90000000 * 0.30) + (taxableIncome - 100000000) * 0.35;
                }
            }
        }

        return tax;

    }

    private double calculateSurcharge(double tax, double income, String taxPayer) {
        double surcharge = 0;

        if (taxPayer.equals("Individual")) {
            // Surcharge rules for individuals
            if (income > 5000000 && income <= 10000000) {
                surcharge = tax * 0.10; // 10% surcharge
            } else if (income > 10000000 && income <= 20000000) {
                surcharge = tax * 0.15; // 15% surcharge
            } else if (income > 20000000 && income <= 50000000) {
                surcharge = tax * 0.25; // 25% surcharge
            } else if (income > 50000000) {
                surcharge = tax * 0.37; // 37% surcharge
            }
        } else if (taxPayer.equals("Company")) {
            // Surcharge rules for companies
            if (income > 10000000 && income <= 100000000) {
                surcharge = tax * 0.07; // 7% surcharge
            } else if (income > 100000000) {
                surcharge = tax * 0.12; // 12% surcharge
            }
        }

        return surcharge;
    }
    private double calculateCess(double tax) {
        // 4% Health and Education Cess
        return tax * 0.04;
    }
}
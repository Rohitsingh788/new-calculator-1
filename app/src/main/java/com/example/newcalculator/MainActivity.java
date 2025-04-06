package com.example.newcalculator;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFS_NAME = "CalculatorHistory";
    private static final String HISTORY_KEY = "calculationHistory";
    private LinkedList<String> calculationHistory = new LinkedList<>();
    MaterialButton buttonHistory;
    TextView resultTv, solutionTv;
    MaterialButton buttonC, buttonBrackOpen, buttonBrackClose;
    MaterialButton buttonDivide, buttonMultiply, buttonPlus, buttonMinus, buttonEquals;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonAC, buttonDot;
    MaterialButton buttonX2, buttonX3, buttonFact;
    MaterialButton Currency_Converter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadCalculationHistory();
        // Initialize TextViews
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        // Initialize Buttons
        assignId(buttonC, R.id.button_c);
        assignId(buttonBrackOpen, R.id.button_open_bracket);
        assignId(buttonBrackClose, R.id.button_close_bracket);
        assignId(buttonDivide, R.id.button_divide);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonPlus, R.id.button_plus);
        assignId(buttonMinus, R.id.button_minus);
        assignId(buttonEquals, R.id.button_equals);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDot, R.id.button_dot);
        assignId(buttonX2, R.id.X2);
        assignId(buttonX3, R.id.X3);
        assignId(buttonFact, R.id.fact);


        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }
    private void loadCalculationHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String historyJson = prefs.getString(HISTORY_KEY, null);

        if (historyJson != null) {
            try {
                // Convert JSON string back to LinkedList
                Type listType = new TypeToken<LinkedList<String>>(){}.getType();
                calculationHistory = new Gson().fromJson(historyJson, listType);
            } catch (Exception e) {
                calculationHistory = new LinkedList<>();
            }
        } else {
            calculationHistory = new LinkedList<>();
        }
    }
    // Method to save calculation history
    private void saveCalculationHistory() {
        // Limit history to last 50 entries
        if (calculationHistory.size() > 50) {
            calculationHistory = new LinkedList<>(
                    calculationHistory.subList(
                            calculationHistory.size() - 50,
                            calculationHistory.size()
                    )
            );
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Convert history to JSON string
        String historyJson = new Gson().toJson(calculationHistory);
        editor.putString(HISTORY_KEY, historyJson);
        editor.apply();
    }

    // Method to show the PopupMenu
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        // Force icons to show in PopupMenu
        try {
            Field mFieldPopup = popupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            Object mPopup = mFieldPopup.get(popupMenu);
            mPopup.getClass()
                    .getDeclaredMethod("setForceShowIcon", boolean.class)
                    .invoke(mPopup, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId(); // Store dynamically retrieved ID

                if (itemId == R.id.menu_emi_calculator) {
                    Intent intent = new Intent(MainActivity.this, EMICalculatorActivity.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.currency_converter) {
                    Intent intent = new Intent(MainActivity.this, CurrencyConverterActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.History) {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    intent.putStringArrayListExtra("HISTORY", new ArrayList<>(calculationHistory));
                    startActivity(intent);
                }
                else if (itemId == R.id.stock_profit_calculator) {
                    Intent intent = new Intent(MainActivity.this, StockProfitCalculator.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.action_unit_converter) {
                    Intent intent = new Intent(MainActivity.this, UnitConverterActivity.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.Scientific_calculator) {
                    Intent intent = new Intent(MainActivity.this, ScientificCalculatorActivity.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.menu_bmi_calculator) {
                    startActivity(new Intent(MainActivity.this, BMICalculatorActivity.class));
                }
                else if (itemId == R.id.action_discount_check) {
                    startActivity(new Intent(MainActivity.this, DiscountCheckActivity.class));
                }
                else if (itemId == R.id.action_number_system) {
                    startActivity(new Intent(MainActivity.this, NumberSystemActivity.class));
                }
                else if (itemId == R.id.menu_calories_calculator) {
                    startActivity(new Intent(MainActivity.this,CaloriesCalculatorActivity.class));
                }
                else if (itemId == R.id.Geometry) {
                    startActivity(new Intent(MainActivity.this,GeometryCalculatorActivity.class));
                }
                else if (itemId == R.id.TAX) {
                    startActivity(new Intent(MainActivity.this,TaxCalculatorActivity.class));
                }

                return false;
            }

        });

        // Show the PopupMenu
        popupMenu.show();
    }

    // Helper method to assign the onClick listeners to buttons
    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        if (buttonText.equals("History")) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putStringArrayListExtra("HISTORY", new ArrayList<>(calculationHistory));
            startActivity(intent);
            return;
        }

        // Other button operations...
        if (buttonText.equals("AC")) {
            // Clear only the current calculation display
            solutionTv.setText("");
            resultTv.setText("0");
            return; // Important: do not clear history
        }
        if (buttonText.equals("=")) {
            // Save calculation to history
            String calculation = dataToCalculate + " = " + resultTv.getText().toString();
            calculationHistory.add(calculation);

            // Save to SharedPreferences
            saveCalculationHistory();

            solutionTv.setText(resultTv.getText());
            return;
        }

        if (buttonText.equals("C")) {
            // Remove the last character from the expression when "C" is pressed
            if (dataToCalculate.length() > 0) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
            solutionTv.setText(dataToCalculate);

            // If the expression is empty, reset the result
            if (dataToCalculate.isEmpty()) {
                resultTv.setText("0");
            } else {
                // Evaluate the new expression
                String finalResult = getResult(dataToCalculate);
                if (!finalResult.equals("Err")) {
                    resultTv.setText(finalResult);
                }
            }
            return;
        }

        if (buttonText.equals("x²")) {
            // Append "²" to the expression
            dataToCalculate = dataToCalculate + "²";
            solutionTv.setText(dataToCalculate);

            // Compute square result
            double number = Double.parseDouble(resultTv.getText().toString());
            double result = number * number;
            resultTv.setText(String.valueOf(result));
            return;
        } else if (buttonText.equals("x³")) {
            // Append "³" to the expression
            dataToCalculate = dataToCalculate + "³";
            solutionTv.setText(dataToCalculate);

            // Compute cube result
            double number = Double.parseDouble(resultTv.getText().toString());
            double result = number * number * number;
            resultTv.setText(String.valueOf(result));
            return;
        } else if (buttonText.equals("fact")) {
            // Append "!" to the expression
            dataToCalculate = dataToCalculate + "!";
            solutionTv.setText(dataToCalculate);

            // Compute factorial result
            int number = Integer.parseInt(resultTv.getText().toString());
            if (number < 0) {
                resultTv.setText("Err");
            } else {
                long result = factorial(number);
                resultTv.setText(String.valueOf(result));
            }
            return;
        } else {
            // Append the button text to the current expression
            dataToCalculate = dataToCalculate + buttonText;
        }

        solutionTv.setText(dataToCalculate);

        // Evaluate the expression and show result
        String finalResult = getResult(dataToCalculate);
        if (!finalResult.equals("Err")) {
            resultTv.setText(finalResult);
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // Helper method to calculate factorial
    private long factorial(int number) {
        if (number == 0) {
            return 1;
        }
        long result = 1;
        for (int i = 1; i <= number; i++) {
            result *= i;
        }
        return result;
    }

    // Method to evaluate the expression and return the result
    String getResult(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        } catch (Exception e) {
            return "Err";
        }
    }



}

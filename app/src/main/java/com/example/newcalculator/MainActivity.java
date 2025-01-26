package com.example.newcalculator;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        assignId(Currency_Converter, R.id.button_currency_convert);
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

        // Currency Converter button - launch the CurrencyConverterActivity
        if (buttonText.equals("Currency Converter")) {
            Intent intent = new Intent(MainActivity.this, CurrencyConverterActivity.class);
            startActivity(intent);
        }

        // Other button operations...
        if (buttonText.equals("AC")) {
            // Reset everything when "AC" is pressed
            solutionTv.setText("");
            resultTv.setText("0");
            return;
        }

        if (buttonText.equals("=")) {
            // Update solutionTv with the result when "=" is pressed
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
            // Square function
            double number = Double.parseDouble(resultTv.getText().toString());
            double result = number * number;
            resultTv.setText(String.valueOf(result));
            return;
        } else if (buttonText.equals("x³")) {
            // Cube function
            double number = Double.parseDouble(resultTv.getText().toString());
            double result = number * number * number;
            resultTv.setText(String.valueOf(result));
            return;
        } else if (buttonText.equals("fact")) {
            // Factorial function
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
package com.example.newcalculator;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ScientificCalculatorActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "CalculatorHistory";
    private static final String HISTORY_KEY = "calculationHistory";
    private LinkedList<String> calculationHistory = new LinkedList<>();

    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, bdot, bpi, button_equals,
            bmin, bplus, bdiv, bmul, bminus, blog, bac, bc, bb1, bb2, bln,
            bsin, bcos, btan, binv, bsqrt, bsquare, bfact, bmod;

    TextView tvsec, tvmain;
    String pi = "3.14159265";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientific_calculator);

        loadCalculationHistory();

        // Initialize all buttons
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        b0 = findViewById(R.id.b0);
        bac = findViewById(R.id.bac);
        bc = findViewById(R.id.bc);
        bb1 = findViewById(R.id.bb1);
        bb2 = findViewById(R.id.bb2);
        bpi = findViewById(R.id.bpi);
        bdot = findViewById(R.id.bdot);
        button_equals = findViewById(R.id.bequal);
        bplus = findViewById(R.id.bplus);
        bminus = findViewById(R.id.bminus);
        bmul = findViewById(R.id.bmul);
        bdiv = findViewById(R.id.bdiv);
        binv = findViewById(R.id.binv);
        bmod = findViewById(R.id.bmod);
        bsqrt = findViewById(R.id.bsqrt);
        bsquare = findViewById(R.id.bsquare);
        bfact = findViewById(R.id.bfact);
        bln = findViewById(R.id.bln);
        bsin = findViewById(R.id.bsin);
        bcos = findViewById(R.id.bcos);
        btan = findViewById(R.id.btan);
        blog = findViewById(R.id.blog);

        tvmain = findViewById(R.id.tvmain);
        tvsec = findViewById(R.id.tvsec);

        // Set click listeners for all buttons
        setupButtonListeners();

        // Setup menu icon
        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }
    View.OnClickListener numberClickListener = v -> {
        Button button = (Button) v;
        String currentText = tvmain.getText().toString();
        String newText = currentText + button.getText().toString();

        try {
            double num = Double.parseDouble(newText);

            if (newText.length() > 12) { // If the number is too long, use scientific notation
                newText = String.format("%e", num);
            }

            tvmain.setText(newText);
        } catch (NumberFormatException e) {
            // If the input is not a valid number, just show as-is
            tvmain.setText(currentText + button.getText().toString());
        }
    };

    private void setupButtonListeners() {


        // Number buttons
        b1.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "1"));
        b2.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "2"));
        b3.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "3"));
        b4.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "4"));
        b5.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "5"));
        b6.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "6"));
        b7.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "7"));
        b8.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "8"));
        b9.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "9"));
        b0.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "0"));

        // Dot button
        bdot.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "."));

        // Clear buttons
        bac.setOnClickListener(v -> {
            tvmain.setText("");
            tvsec.setText("");
        });

        // Backspace button
        bc.setOnClickListener(v -> {
            String val = tvmain.getText().toString();
            if (!val.isEmpty()) {
                val = val.substring(0, val.length() - 1);
                tvmain.setText(val);
            }
        });

        // Operator buttons
        bplus.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "+"));
        bminus.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "-"));
        bdiv.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "÷"));
        bmul.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "×"));

        // Modulo button
        bmod.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "%"));


        bb1.setOnClickListener(v -> tvmain.setText(tvmain.getText() + "("));
        bb2.setOnClickListener(v -> tvmain.setText(tvmain.getText() + ")"));

        bpi.setOnClickListener(v -> {
            tvsec.setText(bpi.getText());
            tvmain.setText(tvmain.getText() + pi);
        });

        // SIN FUNCTION
        bsin.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    double result = Math.sin(Math.toRadians(value));
                    tvsec.append("sin(" + input + ") ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// COS FUNCTION
        bcos.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    double result = Math.cos(Math.toRadians(value));
                    tvsec.append("cos(" + input + ") ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// TAN FUNCTION (Handles Undefined Cases)
        btan.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    if (value % 90 == 0 && (value / 90) % 2 != 0) {
                        tvmain.setText("Undefined");
                        tvsec.append("tan(" + input + ") ");
                    } else {
                        double result = Math.tan(Math.toRadians(value));
                        tvsec.append("tan(" + input + ") ");
                        tvmain.setText(String.valueOf(result));
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// INVERSE FUNCTION (1/x)
        binv.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    if (value == 0) {
                        Toast.makeText(ScientificCalculatorActivity.this, "Cannot divide by zero!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = 1 / value;
                    tvsec.append("1 / " + input + " ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// FACTORIAL FUNCTION (x!)
        bfact.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    int val = Integer.parseInt(input);
                    if (val < 0) {
                        Toast.makeText(ScientificCalculatorActivity.this, "Factorial of negative numbers is not defined!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BigInteger fact = factorial(val);
                    tvsec.append(input + "! ");
                    tvmain.setText(String.valueOf(fact));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// SQUARE FUNCTION (x²)
        bsquare.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    double result = value * value;
                    tvsec.append(input + "² ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// NATURAL LOG (ln)
        bln.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    if (value <= 0) {
                        Toast.makeText(ScientificCalculatorActivity.this, "ln(x) is undefined for x ≤ 0!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = Math.log(value);
                    tvsec.append("ln(" + input + ") ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// LOG FUNCTION (Base 10)
        blog.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    if (value <= 0) {
                        Toast.makeText(ScientificCalculatorActivity.this, "log(x) is undefined for x ≤ 0!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = Math.log10(value);
                    tvsec.append("log(" + input + ") ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// SQUARE ROOT FUNCTION (√)
        bsqrt.setOnClickListener(v -> {
            String input = tvmain.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    double value = Double.parseDouble(input);
                    if (value < 0) {
                        Toast.makeText(ScientificCalculatorActivity.this, "Square root of negative numbers is not defined!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = Math.sqrt(value);
                    tvsec.append("√(" + input + ") ");
                    tvmain.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    Toast.makeText(ScientificCalculatorActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

// PI BUTTON (π)
        bpi.setOnClickListener(v -> {
            tvsec.append("π ");
            tvmain.setText(String.valueOf(Math.PI));
        });


        // Equals button
        button_equals.setOnClickListener(v -> {
            String val = tvmain.getText().toString();
            // Replace special characters with standard mathematical operators
            String replacedstr = val.replace('×', '*').replace('÷', '/').replace('%', '%');

            try {
                double result = eval(replacedstr);

                if (Double.isNaN(result)) {
                    tvmain.setText("Error");
                } else {
                    String formattedResult;
                    if (Math.abs(result) >= 1e8 || Math.abs(result) < 1e-5) {
                        // Convert to scientific notation if number is too large/small
                        formattedResult = String.format("%e", result);
                    } else {
                        // Limit decimal places to 8 digits
                        formattedResult = String.format("%.8f", result);
                        formattedResult = formattedResult.replaceAll("0+$", "").replaceAll("\\.$", ""); // Trim trailing zeros
                    }

                    tvmain.setText(formattedResult);
                    tvsec.setText(val);
                    saveCalculation(val + " = " + formattedResult);
                }
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });

    }

    private void loadCalculationHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String historyJson = prefs.getString(HISTORY_KEY, null);

        if (historyJson != null) {
            try {
                // Convert JSON string back to LinkedList
                Type listType = new TypeToken<LinkedList<String>>() {
                }.getType();
                calculationHistory = new Gson().fromJson(historyJson, listType);
            } catch (Exception e) {
                calculationHistory = new LinkedList<>();
            }
        } else {
            calculationHistory = new LinkedList<>();
        }
    }

    private void saveCalculation(String calculation) {
        calculationHistory.addFirst(calculation);

        // Keep only the last 50 calculations
        if (calculationHistory.size() > 50) {
            calculationHistory.removeLast();
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String historyJson = new Gson().toJson(calculationHistory);
        editor.putString(HISTORY_KEY, historyJson);
        editor.apply();
    }

    public void clearHistoryPermanently() {
        calculationHistory.clear();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(HISTORY_KEY);
        editor.apply();

        Toast.makeText(this, "Calculation History Cleared", Toast.LENGTH_SHORT).show();
    }

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
                    Intent intent = new Intent(ScientificCalculatorActivity.this, EMICalculatorActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.menu_bmi_calculator) {
                    Toast.makeText(ScientificCalculatorActivity.this, "BMI Calculator Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.currency_converter) {
                    Intent intent = new Intent(ScientificCalculatorActivity.this, CurrencyConverterActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.History) {
                    Intent intent = new Intent(ScientificCalculatorActivity.this, HistoryActivity.class);
                    intent.putStringArrayListExtra("HISTORY", new ArrayList<>(calculationHistory));
                    startActivity(intent);
                } else if (itemId == R.id.stock_profit_calculator) {
                    Intent intent = new Intent(ScientificCalculatorActivity.this, StockProfitCalculator.class);
                    startActivity(intent);
                } else if (itemId == R.id.action_unit_converter) {
                    Intent intent = new Intent(ScientificCalculatorActivity.this, UnitConverterActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.menu_bmi_calculator) {
                    startActivity(new Intent(ScientificCalculatorActivity.this, BMICalculatorActivity.class));
                }
                else if (itemId == R.id.action_discount_check) {
                    startActivity(new Intent(ScientificCalculatorActivity.this, DiscountCheckActivity.class));
                }
                else if (itemId == R.id.action_number_system) {
                    startActivity(new Intent(ScientificCalculatorActivity.this, NumberSystemActivity.class));
                }
                else if (itemId == R.id.menu_calories_calculator) {
                    startActivity(new Intent(ScientificCalculatorActivity.this,CaloriesCalculatorActivity.class));
                }else if (itemId == R.id.TAX) {
                    startActivity(new Intent(ScientificCalculatorActivity.this,TaxCalculatorActivity.class));
                }

                return false;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }
    private void displayResult(double result) {
        String resultText;

        // Convert large/small numbers to scientific notation
        if (Math.abs(result) >= 1e8 || Math.abs(result) < 1e-5) {
            resultText = String.format("%.8e", result);
        } else {
            resultText = String.format("%.8f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
        }

        // Adjust text size dynamically based on length
        if (resultText.length() > 10) {
            tvmain.setTextSize(24); // Reduce text size for large numbers
        } else {
            tvmain.setTextSize(32); // Normal text size
        }

        // Display formatted result
        tvmain.setText(resultText);

        // Auto-scroll to the bottom
        final ScrollView scrollView = findViewById(R.id.scrollView);
        tvmain.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    // Factorial function
    static BigInteger factorial(int n) {
        if (n == 0 || n == 1)
            return BigInteger.ONE;
        BigInteger fact = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }

    // Existing eval method with modulo support
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // Addition
                    else if (eat('-')) x -= parseTerm(); // Subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // Multiplication
                    else if (eat('/')) x /= parseFactor(); // Division
                    else if (eat('%')) x %= parseFactor(); // Modulo
                    else return x;
                }
            }


            double parseFactor() {
                if (eat('+')) return parseFactor(); // Unary plus
                if (eat('-')) return -parseFactor(); // Unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // Parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // Numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // Functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        case "log":
                            x = Math.log10(x);
                            break;
                        case "ln":
                            x = Math.log(x);
                            break;
                        case "exp":
                            x = Math.exp(x);
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('²')) x = x * x; // Square
                if (eat('³')) x = x * x * x; // Cube
                if (eat('!')) x = factorial((int) x).doubleValue(); // Factorial

                return x;
            }
        }.parse();
    }
}
package com.example.newcalculator;

import java.text.DecimalFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import android.view.MotionEvent;
import android.widget.Button;

public class StockProfitCalculator extends AppCompatActivity {



    private EditText buyPrice, sellPrice, quantity;
    private Spinner tradeTypeSpinner;
    private Button calculateButton;
    private TextView turnover, brokerage, stt, exchangeTurnoverCharge, gst, sebiCharges, stampDuty, totalCharges, pointsToBreakeven, netPnL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_profit_calculator);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
        // Initialize views
        buyPrice = findViewById(R.id.buyPrice);
        sellPrice = findViewById(R.id.sellPrice);
        quantity = findViewById(R.id.quantity);
        tradeTypeSpinner = findViewById(R.id.tradeTypeSpinner);
        calculateButton = findViewById(R.id.calculateButton);

        turnover = findViewById(R.id.turnover);
        brokerage = findViewById(R.id.brokerage);
        stt = findViewById(R.id.stt);
        exchangeTurnoverCharge = findViewById(R.id.exchangeTurnoverCharge);
        gst = findViewById(R.id.gst);
        sebiCharges = findViewById(R.id.sebiCharges);
        stampDuty = findViewById(R.id.stampDuty);
        totalCharges = findViewById(R.id.totalCharges);
        pointsToBreakeven = findViewById(R.id.pointsToBreakeven);
        netPnL = findViewById(R.id.netPnL);

        calculateButton.setOnClickListener(v -> calculateProfit());
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

    private void calculateProfit() {
        double buy = Double.parseDouble(buyPrice.getText().toString());
        double sell = Double.parseDouble(sellPrice.getText().toString());
        int qty = Integer.parseInt(quantity.getText().toString());
        String tradeType = tradeTypeSpinner.getSelectedItem().toString();

        // Calculate turnover
        double turnoverValue = (buy + sell) * qty;
        turnover.setText("Turnover: " + formatNumber(turnoverValue));

        // Calculate charges based on trade type
        double brokerageValue = 0;
        double sttValue = 0;
        double exchangeTurnoverChargeValue = 0;
        double gstValue = 0;
        double sebiChargesValue = 0;
        double stampDutyValue = 0;

        switch (tradeType) {
            case "Intraday Equity":
                brokerageValue = Math.min(40, 0.0003 * turnoverValue); // 0.03% or ₹20, whichever is lower
                sttValue = 0.00025 * sell * qty; // 0.025% of sell value
                exchangeTurnoverChargeValue = 0.0000325 * turnoverValue; // 0.00325% of turnover
                gstValue = 0.18 * (brokerageValue + exchangeTurnoverChargeValue); // 18% of (brokerage + exchange turnover charge)
                sebiChargesValue = 0.000001 * turnoverValue; // 0.0001% of turnover
                stampDutyValue = 0.00003 * buy * qty; // 0.003% of buy value
                break;

            case "Delivery Equity":
                brokerageValue = 0; // No brokerage for delivery trades
                sttValue = 0.0018 * sell * qty; // 0.1% of sell value
                exchangeTurnoverChargeValue = 0.0000325 * turnoverValue; // 0.00325% of turnover
                gstValue = 0.18 * (brokerageValue + exchangeTurnoverChargeValue); // 18% of (brokerage + exchange turnover charge)
                sebiChargesValue = 0.000001 * turnoverValue; // 0.0001% of turnover
                stampDutyValue = 0.00003 * buy * qty; // 0.003% of buy value
                break;

            // Add more cases for other trade types
        }

        // Display charges
        brokerage.setText("Brokerage: " + formatNumber(brokerageValue));
        stt.setText("STT Total: " + formatNumber(sttValue));
        exchangeTurnoverCharge.setText("Exchange Turnover Charge: " + formatNumber(exchangeTurnoverChargeValue));
        gst.setText("GST: " + formatNumber(gstValue));
        sebiCharges.setText("SEBI Charges: " + formatNumber(sebiChargesValue));
        stampDuty.setText("Stamp Duty: " + formatNumber(stampDutyValue));

        // Calculate Total Charges
        double totalChargesValue = brokerageValue + sttValue + exchangeTurnoverChargeValue + gstValue + sebiChargesValue + stampDutyValue;
        totalCharges.setText("Total Tax and Charges: " + formatNumber(totalChargesValue));

        // Calculate Points to Breakeven
        double pointsToBreakevenValue = totalChargesValue / qty;
        pointsToBreakeven.setText("Points to Breakeven: " + formatNumber(pointsToBreakevenValue));

        // Calculate Net P&L
        double netPnLValue = (sell - buy) * qty - totalChargesValue;
        netPnL.setText("Net P&L: " + formatNumber(netPnLValue));
    }

    // Helper method to format numbers
    private String formatNumber(double value) {
        DecimalFormat df = new DecimalFormat("#.##"); // Rounds to 2 decimal places
        return df.format(value);
    }
}

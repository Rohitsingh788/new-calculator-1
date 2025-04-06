package com.example.newcalculator;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EMICalculatorActivity extends AppCompatActivity {
    private EditText principalEditText, interestRateEditText;
    private NumberPicker yearsPicker, monthsPicker;
    private Button calculateButton;
    private TextView resultTextView;
    private SoundPool soundPool;
    private int scrollSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_calculator);
        ImageView backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish()); // Close this activity and go back
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

        // Initialize views
        principalEditText = findViewById(R.id.principalEditText);
        interestRateEditText = findViewById(R.id.interestRateEditText);
        yearsPicker = findViewById(R.id.yearsPicker);
        monthsPicker = findViewById(R.id.monthsPicker);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Setup NumberPickers
        yearsPicker.setMinValue(0);
        yearsPicker.setMaxValue(30);
        monthsPicker.setMinValue(0);
        monthsPicker.setMaxValue(11);

        // Initialize SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        // Load the scrolling sound from res/raw
        scrollSoundId = soundPool.load(this, R.raw.scroll, 1);

        // Play sound when NumberPicker is scrolled
        NumberPicker.OnValueChangeListener soundListener = (picker, oldVal, newVal) -> playScrollSound();
        yearsPicker.setOnValueChangedListener(soundListener);
        monthsPicker.setOnValueChangedListener(soundListener);

        calculateButton.setOnClickListener(v -> calculateEMI());
    }

    private void playScrollSound() {
        if (scrollSoundId != 0) {
            soundPool.play(scrollSoundId, 1, 1, 0, 0, 1);
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateEMI() {
        // Get input values
        String principalStr = principalEditText.getText().toString().trim();
        String interestRateStr = interestRateEditText.getText().toString().trim();
        int years = yearsPicker.getValue();
        int months = monthsPicker.getValue();
        int totalMonths = (years * 12) + months;

        try {
            double principal = Double.parseDouble(principalStr);
            double interestRate = Double.parseDouble(interestRateStr);

            if (totalMonths <= 0) {
                resultTextView.setText("Tenure must be at least 1 month.");
                return;
            }

            // Convert interest rate to monthly rate
            double monthlyRate = interestRate / 12 / 100;

            // Calculate EMI
            double emi = principal * monthlyRate *
                    (Math.pow(1 + monthlyRate, totalMonths)) /
                    (Math.pow(1 + monthlyRate, totalMonths) - 1);

            double totalPayment = emi * totalMonths;
            double totalInterest = totalPayment - principal;

            // Display results
            String resultText = String.format(
                    "EMI Calculation Results:\n\n" +
                            "Loan Tenure: %d Years %d Months\n" +
                            "Monthly EMI: ₹%.2f\n" +
                            "Total Payment: ₹%.2f\n" +
                            "Total Interest: ₹%.2f",
                    years, months, emi, totalPayment, totalInterest
            );

            resultTextView.setText(resultText);

        } catch (NumberFormatException e) {
            resultTextView.setText("Please enter valid numbers.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}

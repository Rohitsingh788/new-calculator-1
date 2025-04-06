package com.example.newcalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

public class HistoryActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "CalculatorHistory";
    private static final String HISTORY_KEY = "calculationHistory";

    private LinkedList<String> calculationHistory;
    private ArrayAdapter<String> adapter;
    private ListView historyListView;
    private TextView emptyHistoryTextView;
    private Button clearHistoryButton;
    private Button deleteSelectedButton;

    private ArrayList<Integer> selectedPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ImageView backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish()); // Close the activity when back arrow is clicked

        // Initialize views
        historyListView = findViewById(R.id.history_list_view);
        emptyHistoryTextView = findViewById(R.id.empty_history_text_view);
        clearHistoryButton = findViewById(R.id.clear_history_button);
        deleteSelectedButton = findViewById(R.id.delete_selected_button);

        // Load history
        loadCalculationHistory();

        // Setup ListView
        setupHistoryListView();

        // Setup buttons
        setupButtons();
    }

    private void loadCalculationHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String historyJson = prefs.getString(HISTORY_KEY, null);

        if (historyJson != null) {
            try {
                Type listType = new TypeToken<LinkedList<String>>() {}.getType();
                calculationHistory = new Gson().fromJson(historyJson, listType);
            } catch (Exception e) {
                calculationHistory = new LinkedList<>();
            }
        } else {
            calculationHistory = new LinkedList<>();
        }

        updateEmptyHistoryVisibility();
    }

    private void setupHistoryListView() {
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                calculationHistory
        );
        historyListView.setAdapter(adapter);
        historyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (historyListView.isItemChecked(position)) {
                    selectedPositions.add(position);
                } else {
                    selectedPositions.remove(Integer.valueOf(position));
                }
                updateDeleteSelectedButtonState();
            }
        });
    }

    private void setupButtons() {
        clearHistoryButton.setOnClickListener(v -> {
            clearHistoryPermanently();
        });

        deleteSelectedButton.setOnClickListener(v -> {
            deleteSelectedHistoryEntries();
        });

        updateDeleteSelectedButtonState();
    }

    private void updateDeleteSelectedButtonState() {
        deleteSelectedButton.setEnabled(!selectedPositions.isEmpty());
    }

    private void deleteSelectedHistoryEntries() {
        // Sort positions in descending order to avoid index shifting
        selectedPositions.sort((a, b) -> b.compareTo(a));

        for (Integer position : selectedPositions) {
            calculationHistory.remove(position.intValue());
        }

        saveCalculationHistory();

        // Clear selection and update UI
        selectedPositions.clear();
        adapter.notifyDataSetChanged();
        historyListView.clearChoices();
        updateEmptyHistoryVisibility();
        updateDeleteSelectedButtonState();

        Toast.makeText(this, "Selected entries deleted", Toast.LENGTH_SHORT).show();
    }

    private void clearHistoryPermanently() {
        calculationHistory.clear();
        saveCalculationHistory();
        adapter.notifyDataSetChanged();
        updateEmptyHistoryVisibility();
        selectedPositions.clear();
        updateDeleteSelectedButtonState();
        Toast.makeText(this, "Calculation History Cleared", Toast.LENGTH_SHORT).show();
    }

    private void saveCalculationHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String historyJson = new Gson().toJson(calculationHistory);
        editor.putString(HISTORY_KEY, historyJson);
        editor.apply();
    }

    private void updateEmptyHistoryVisibility() {
        emptyHistoryTextView.setVisibility(
                calculationHistory.isEmpty() ? View.VISIBLE : View.GONE
        );
        historyListView.setVisibility(
                calculationHistory.isEmpty() ? View.GONE : View.VISIBLE
        );
    }
}
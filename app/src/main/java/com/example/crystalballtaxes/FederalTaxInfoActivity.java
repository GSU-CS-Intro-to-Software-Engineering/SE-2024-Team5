package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class FederalTaxInfoActivity extends AppCompatActivity {
    private static final String TAG = "FederalTaxInfoActivity";

    private DatabaseHelper db;
    private EditText annualIncomeInput;
    private EditText taxCreditsInput;
    private EditText aboveLineDeductionsInput;
    private EditText itemizedDeductionsInput;
    private Button calculateButton;

    private long userId = -1;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_federal_tax_info);

        // initialize database helper
        db = new DatabaseHelper(this);

        // get user ID from intent
        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Log.e(TAG, "No user ID provided");
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // initialize UI elements
        initializeViews();

        // set up currency formatter
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        // load existing tax info if available
        loadExistingTaxInfo();

        // set up input formatting
        setupCurrencyFormatting();

        // set up calculate button
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveTaxInfo();
                    goToTaxResults();
                }
            }
        });
    }

    private void initializeViews() {
        annualIncomeInput = findViewById(R.id.annualIncomeTxtF3);
        taxCreditsInput = findViewById(R.id.taxCreditsTxtF);
        aboveLineDeductionsInput = findViewById(R.id.ablDeductionsTxtF);
        itemizedDeductionsInput = findViewById(R.id.annualIncomeTxtF4);
        calculateButton = findViewById(R.id.button2);
    }

    private void loadExistingTaxInfo() {
        Map<String, String> taxInfo = db.getUserTaxInfo(userId);

        if (!taxInfo.isEmpty()) {
            try {
                // set existing values if they exist
                if (taxInfo.get("income") != null) {
                    double income = Double.parseDouble(taxInfo.get("income"));
                    annualIncomeInput.setText(currencyFormatter.format(income));
                }

                if (taxInfo.get("tax_credits") != null) {
                    double credits = Double.parseDouble(taxInfo.get("tax_credits"));
                    taxCreditsInput.setText(currencyFormatter.format(credits));
                }

                if (taxInfo.get("above_line_deductions") != null) {
                    double aboveLine = Double.parseDouble(taxInfo.get("above_line_deductions"));
                    aboveLineDeductionsInput.setText(currencyFormatter.format(aboveLine));
                }

                if (taxInfo.get("itemized_deductions") != null) {
                    double itemized = Double.parseDouble(taxInfo.get("itemized_deductions"));
                    itemizedDeductionsInput.setText(currencyFormatter.format(itemized));
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing existing tax info: " + e.getMessage());
            }
        }
    }

    //allows for currency formatting when the user is inputting their income
    private void setupCurrencyFormatting() {
        TextWatcher currencyWatcher = new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                if (!TextUtils.isEmpty(s)) {
                    isUpdating = true;
                    String str = s.toString();
                    // remove all formatting characters
                    String cleanString = str.replaceAll("[$,.]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString);
                        // convert to dollars
                        if (cleanString.length() > 2) {
                            parsed = parsed / 100.0;
                        } else {
                            parsed = parsed / 100.0;
                        }
                        String formatted = currencyFormatter.format(parsed);
                        s.replace(0, s.length(), formatted);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error formatting currency: " + e.getMessage());
                    }
                    isUpdating = false;
                }
            }
        };

        annualIncomeInput.addTextChangedListener(currencyWatcher);
        taxCreditsInput.addTextChangedListener(currencyWatcher);
        aboveLineDeductionsInput.addTextChangedListener(currencyWatcher);
        itemizedDeductionsInput.addTextChangedListener(currencyWatcher);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(annualIncomeInput.getText())) {
            annualIncomeInput.setError("Annual income is required");
            annualIncomeInput.requestFocus();
            return false;
        }

        try {
            parseAmount(annualIncomeInput.getText().toString());
            if (!TextUtils.isEmpty(taxCreditsInput.getText())) {
                parseAmount(taxCreditsInput.getText().toString());
            }
            if (!TextUtils.isEmpty(aboveLineDeductionsInput.getText())) {
                parseAmount(aboveLineDeductionsInput.getText().toString());
            }
            if (!TextUtils.isEmpty(itemizedDeductionsInput.getText())) {
                parseAmount(itemizedDeductionsInput.getText().toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid currency amounts", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private double parseAmount(String amount) {
        return Double.parseDouble(amount.replaceAll("[$,]", ""));
    }

    private void saveTaxInfo() {
        try {
            double income = parseAmount(annualIncomeInput.getText().toString());
            double taxCredits = TextUtils.isEmpty(taxCreditsInput.getText()) ? 0 :
                    parseAmount(taxCreditsInput.getText().toString());
            double aboveLineDeductions = TextUtils.isEmpty(aboveLineDeductionsInput.getText()) ? 0 :
                    parseAmount(aboveLineDeductionsInput.getText().toString());
            double itemizedDeductions = TextUtils.isEmpty(itemizedDeductionsInput.getText()) ? 0 :
                    parseAmount(itemizedDeductionsInput.getText().toString());

            // update database with new values
            boolean success = true;
            success &= db.updateIncome(userId, String.valueOf(income));
            success &= db.updateTaxCredits(userId, (int)taxCredits);
            success &= db.updateAboveLineDeductions(userId, String.valueOf(aboveLineDeductions));
            success &= db.updateItemizedDeductions(userId, String.valueOf(itemizedDeductions));

            if (success) {
                Toast.makeText(this, "Tax information saved successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Tax info saved successfully for user " + userId);
            } else {
                Toast.makeText(this, "Error saving some tax information", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving tax info: " + e.getMessage());
            Toast.makeText(this, "Error saving tax information", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToTaxResults() {
        Intent intent = new Intent(this, TaxResultsActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
}
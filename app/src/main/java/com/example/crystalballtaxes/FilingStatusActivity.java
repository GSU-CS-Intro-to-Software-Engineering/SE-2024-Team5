package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class FilingStatusActivity extends AppCompatActivity {
    private RadioButton singleRBtn;
    private RadioButton marriedJointBtn;
    private RadioButton marriedFiledSepRBtn;
    private RadioButton headOfHouseRBtn;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing_status);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        nextBtn = findViewById(R.id.nextBtn);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                singleRBtn = findViewById(R.id.singleRBtn);
                marriedJointBtn = findViewById(R.id.marriedJointBtn);
                marriedFiledSepRBtn = findViewById(R.id.marriedFiledSepRBtn);
                headOfHouseRBtn = findViewById(R.id.headOfHouseRBtn);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilingStatusActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
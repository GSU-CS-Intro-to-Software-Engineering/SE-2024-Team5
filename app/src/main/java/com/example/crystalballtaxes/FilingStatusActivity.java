package com.example.crystalballtaxes;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class FilingStatusActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton singleRBtn;
    private RadioButton marriedJointBtn;
    private RadioButton marriedFiledSepRBtn;
    private RadioButton headOfHouseRBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing_status);

        radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                singleRBtn = findViewById(R.id.singleRBtn);
                marriedJointBtn = findViewById(R.id.marriedJointBtn);
                marriedFiledSepRBtn = findViewById(R.id.marriedFiledSepRBtn);
                headOfHouseRBtn = findViewById(R.id.headOfHouseRBtn);
            }
        });

    }
}
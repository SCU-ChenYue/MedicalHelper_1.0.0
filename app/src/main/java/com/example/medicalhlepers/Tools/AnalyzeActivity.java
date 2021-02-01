package com.example.medicalhlepers.Tools;

import android.content.Context;
import android.content.Intent;

import com.example.medicalhlepers.BasicActivity.HospitalMessage;

public class AnalyzeActivity {
    public static void chooseActivity(Context context, String input) {
        if(input.contains("预约")) {
            Intent intent = new Intent(context, HospitalMessage.class);
            context.startActivity(intent);
        }
    }
}

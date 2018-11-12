package com.jbyrnes.petrecordkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EditPetHistory extends AppCompatActivity {

    private Intent intent;
    private PetHistoryCard historyCard;

    String nameExtra;
    long tableIdExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet_history);

        intent = getIntent();
        nameExtra = intent.getStringExtra(MainActivity.EXTRA_NAME);
        tableIdExtra = intent.getLongExtra(MainActivity.EXTRA_ID, 0);
    }
}
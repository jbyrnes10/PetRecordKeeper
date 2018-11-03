package com.jbyrnes.petrecordkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EditPetActivity extends AppCompatActivity {
    private ImageView petImageView;
    private EditText petNameText;
    private Spinner speciesSpinner;
    private EditText birthDateText;
    private Intent intent;
    private PetCard editCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        try {
            petImageView = findViewById(R.id.pet_photo_view);
            petNameText = findViewById(R.id.pet_name_input);
            speciesSpinner = findViewById(R.id.species_spinner);
            birthDateText = findViewById(R.id.birthDate_input);

            Button updatePetButton = findViewById(R.id.update_button);
            Button removePetButton = findViewById(R.id.delete_button);

            intent = getIntent();
            String nameExtra = intent.getStringExtra(MainActivity.EXTRA_NAME);
            long tableIdExtra = intent.getLongExtra(MainActivity.EXTRA_ID, 0);

            //hit db here
            PetCardData cardData = new PetCardData(this);
            editCard = cardData.getSingleCard(nameExtra, tableIdExtra);

            petNameText.setText(nameExtra);
            petImageView.setImageBitmap(editCard.getPicture());

            String[] speciesValues = getResources().getStringArray(R.array.species_array);
            String savedSpeciesValue = editCard.getSpecies();

            int speciesArrayIndex = 0;
            for (int i=0; i<speciesValues.length; i++) {
                if (speciesValues[i].equals(savedSpeciesValue)) {
                    speciesArrayIndex = i;
                    break;
                }
            }

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.species_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            speciesSpinner.setAdapter(adapter);

            speciesSpinner.setSelection(speciesArrayIndex);
            birthDateText.setText(editCard.getBirthDate());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
//        nameEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    // update initialTextView
//                    initialTextView.setText("");
//                } else if (s.length() >= 1) {
//                    // initialTextView set to first letter of nameEditText and update name stringExtra
//                    initialTextView.setText(String.valueOf(s.charAt(0)));
//                    intent.putExtra(SampleMaterialActivity.EXTRA_UPDATE, true);
//                }
//            }

//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

//        updatePetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // must not be zero otherwise do not finish activity and report Toast message
//                String text = initialTextView.getText().toString().trim();
//                if (TextUtils.isEmpty(text)) {
//                    Toast.makeText(getApplicationContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
//                } else {
//                    intent.putExtra(SampleMaterialActivity.EXTRA_UPDATE, true);
//                    intent.putExtra(SampleMaterialActivity.EXTRA_NAME, String.valueOf(nameEditText.getText()));
//                    intent.putExtra(SampleMaterialActivity.EXTRA_INITIAL, String.valueOf(nameEditText.getText().charAt(0)));
//
//                    setResult(RESULT_OK, intent);
//                    supportFinishAfterTransition();
//                }
//            }
//        });
//
//        removePetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent.putExtra(SampleMaterialActivity.EXTRA_DELETE, true);
//
//                setResult(RESULT_OK, intent);
//                supportFinishAfterTransition();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

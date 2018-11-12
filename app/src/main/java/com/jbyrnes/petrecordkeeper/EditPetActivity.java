package com.jbyrnes.petrecordkeeper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class EditPetActivity extends AppCompatActivity {
    private ImageView petImageView;
    private EditText petNameText;
    private Spinner speciesSpinner;
    private EditText birthDateText;
    private TextView hiddenId;
    private Intent intent;
    private PetCard editCard;

    String nameExtra;
    long tableIdExtra;

    Button updatePetButton;
    Button removePetButton;
    ImageView photoView;
    String imageString;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        try {
            petImageView = findViewById(R.id.pet_photo_view);
            petNameText = findViewById(R.id.pet_name_input);
            speciesSpinner = findViewById(R.id.species_spinner);
            birthDateText = findViewById(R.id.birthDate_input);
            hiddenId = findViewById(R.id.table_id);

            updatePetButton = findViewById(R.id.update_button);
            removePetButton = findViewById(R.id.delete_button);

            intent = getIntent();
            nameExtra = intent.getStringExtra(MainActivity.EXTRA_NAME);
            tableIdExtra = intent.getLongExtra(MainActivity.EXTRA_ID, 0);

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

        Button launchCameraButton = findViewById(R.id.launch_camera_button);
        photoView = findViewById(R.id.pet_photo_view);

        launchCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });

        Button launchGalleryButton = findViewById(R.id.add_photo_button);

        launchGalleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMG);
            }
        });

        updatePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String petName = petNameText.getText().toString().trim();
                String tableId = hiddenId.getText().toString();

                if (TextUtils.isEmpty(petName)) {
                    Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra(MainActivity.EXTRA_NAME, String.valueOf(petName));
                    intent.putExtra(MainActivity.EXTRA_ID, String.valueOf(tableId));

                    setResult(RESULT_OK, intent);
                    supportFinishAfterTransition();
                }
            }
        });

        removePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                //EditText petName = findViewById(R.id.pet_name_input);
                //TextView petId = findViewById(R.id.table_id);

//                ContentValues values = new ContentValues();
//                values.put(PetDatabase.COLUMN_ID, Long.parseLong(petId.getText().toString()));
//                values.put(PetDatabase.PET_NAME, petName.getText().toString().trim());
                //String columnName = MainActivity.EXTRA_NAME;
                //String columnId =MainActivity.EXTRA_ID;
                //String columnId = petId.getText().toString();
                //String columnName = petName.getText().toString().trim();

                //String name = nameExtra;
                //String tableId = Long.toString(tableIdExtra);
                //String whereClause = PetDatabase.COLUMN_ID + "=? AND " + PetDatabase.PET_NAME + "=?";

                db.beginTransaction();

            try {
                db.execSQL("DELETE FROM " + PetDatabase.TABLE_PET_PROFILES + " WHERE ID=" + tableIdExtra + " AND NAME='" + nameExtra + "'");
                //db.delete(PetDatabase.TABLE_PET_PROFILES,  whereClause, new String[] {nameExtra, tableId});
                db.setTransactionSuccessful();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                db.endTransaction();
                Intent mainActivity = new Intent(EditPetActivity.this, MainActivity.class);
                startActivity(mainActivity);
            }
        }});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                    && data != null) {
                //Picture was taken
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                photoView.setImageBitmap(bitmap);

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = findViewById(R.id.pet_photo_view);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imageString));

            } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
                //Image was selected from gallery
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver()
                        .query(selectedImage, filePathColumn, null, null,
                                null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = findViewById(R.id.pet_photo_view);
                // Set the Image in ImageView after decoding the String
                imgView.setImageURI(selectedImage);

            } else {
                Toast.makeText(this, "Please select an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
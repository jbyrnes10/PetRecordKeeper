package com.jbyrnes.petrecordkeeper;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddPetActivity extends AppCompatActivity {

    ImageView photoView;
    String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        Spinner spinner = (Spinner) findViewById(R.id.species_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.species_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final EditText birthDateText = (EditText) findViewById(R.id.birthDate_input);
        birthDateText.setInputType(InputType.TYPE_NULL);
        birthDateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(AddPetActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                birthDateText.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        Button launchCameraButton = (Button) findViewById(R.id.launch_camera_button);
        photoView = findViewById(R.id.pet_photo_view);

        launchCameraButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                 startActivityForResult(intent,0);
             }
         });

        Button addPetButton = (Button) findViewById(R.id.add_pet_button);
        addPetButton.setOnClickListener(new View.OnClickListener() {

            EditText petName = (EditText) findViewById(R.id.pet_name_input);
            Spinner petSpecies = (Spinner) findViewById(R.id.species_spinner);
            EditText birthDate = (EditText) findViewById(R.id.birthDate_input);

            public void onClick(View v) {
                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ImageView imageView = (ImageView) findViewById(R.id.pet_photo_view);
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutput);
                byte[] byteArrayImage = byteArrayOutput.toByteArray();

                ContentValues values = new ContentValues();
                values.put(PetDatabase.PET_NAME, petName.getText().toString());
                values.put(PetDatabase.SPECIES, petSpecies.getSelectedItem().toString());
                values.put(PetDatabase.BIRTH_DATE, birthDate.getText().toString());
                values.put(PetDatabase.PICTURE, byteArrayImage);

                long newRowId = db.insert(PetDatabase.TABLE_PET_PROFILES, null, values);
            }
        });
    }

    private static int RESULT_LOAD_IMG = 1;

    public void launchGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        photoView.setImageBitmap(bitmap);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

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
                ImageView imgView = (ImageView) findViewById(R.id.pet_photo_view);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imageString));

            } else {
                Toast.makeText(this, "Please select an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_LONG)
                    .show();
        }

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void launchCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }




}
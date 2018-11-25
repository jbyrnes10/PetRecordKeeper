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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddPetActivity extends BaseActivity {

    TextView imageLabel;
    ImageView photoView;
    String imageString;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        String titleText = getResources().getString(R.string.add_pet);
        setTitle(titleText);

        Spinner spinner = findViewById(R.id.species_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.species_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final EditText birthDateText = findViewById(R.id.birthDate_input);
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

        Button launchCameraButton = findViewById(R.id.launch_camera_button);
        photoView = findViewById(R.id.pet_photo_view);
        imageLabel = findViewById(R.id.add_image_label);

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

        Button addPetButton = findViewById(R.id.add_pet_button);
        addPetButton.setOnClickListener(new View.OnClickListener() {

            EditText petName = (EditText) findViewById(R.id.pet_name_input);
            Spinner petSpecies = (Spinner) findViewById(R.id.species_spinner);
            EditText birthDate = (EditText) findViewById(R.id.birthDate_input);

            public void onClick(View v) {
                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ImageView imageView = findViewById(R.id.pet_photo_view);
                if (imageView.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please insert an image", Toast.LENGTH_LONG).show();
                    return;
                }

                String petNameText = petName.getText().toString();
                if (petNameText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a name for your pet", Toast.LENGTH_LONG).show();
                    return;
                }

                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                float bitmapRatio = (float)width / (float) height;
                if (bitmapRatio > 1) {
                    width = 500;
                    height = (int) (width / bitmapRatio);
                } else {
                    height = 500;
                    width = (int) (height * bitmapRatio);
                }
                Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap, width, height, true);
                ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutput);
                byte[] byteArrayImage = byteArrayOutput.toByteArray();

                String birthDateText = null;
                if (birthDate.getText() != null) {
                    birthDateText = birthDate.getText().toString();
                }

                ContentValues values = new ContentValues();
                values.put(PetDatabase.PET_NAME, petName.getText().toString().trim());
                values.put(PetDatabase.SPECIES, petSpecies.getSelectedItem().toString());
                values.put(PetDatabase.BIRTH_DATE, birthDateText);
                values.put(PetDatabase.PICTURE, byteArrayImage);

                long insertResult = db.insert(PetDatabase.TABLE_PET_PROFILES, null, values);

                if (insertResult != -1) {
                    Toast.makeText(getApplicationContext(), "Pet successfully added!",
                            Toast.LENGTH_LONG).show();

                    Intent redirect = new Intent(getApplicationContext(), PetHistoryList.class);
                    redirect.putExtra(MainActivity.EXTRA_NAME, petName.getText().toString().trim());
                    redirect.putExtra(MainActivity.EXTRA_ID, insertResult);
                    startActivity(redirect);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
                //Picture was taken
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                ImageView imgView = findViewById(R.id.pet_photo_view);
                imgView.setImageBitmap(bitmap);
                imageLabel.setVisibility(View.GONE);
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
                imageLabel.setVisibility(View.GONE);
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
package com.jbyrnes.petrecordkeeper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class EditPetActivity extends BaseActivity {
    TextView imageLabel;
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

            String titleText = getResources().getString(R.string.edit_profile) + " for " + nameExtra;
            setTitle(titleText);

            //hit db here
            PetCardData cardData = new PetCardData(this);
            editCard = cardData.getSingleCard(nameExtra, tableIdExtra);

            petNameText.setText(nameExtra);

            imageLabel = findViewById(R.id.add_image_label);
            if (editCard.getPicture() != null) {
                petImageView.setImageBitmap(editCard.getPicture());
                imageLabel.setVisibility(View.GONE);
            }

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

        birthDateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(EditPetActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                birthDateText.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        updatePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String petName = petNameText.getText().toString().trim();
                String tableId = Long.toString(tableIdExtra);
                String birthDate = birthDateText.getText().toString();
                String species = speciesSpinner.getSelectedItem().toString();

                petImageView = findViewById(R.id.pet_photo_view);
                if (petImageView.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please insert an image", Toast.LENGTH_LONG).show();
                    return;
                }

                if (petName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a name for your pet", Toast.LENGTH_LONG).show();
                    return;
                }

                byte[] byteArrayImage = null;
                if (petImageView.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    float bitmapRatio = (float) width / (float) height;
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
                    byteArrayImage = byteArrayOutput.toByteArray();
                }

                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(PetDatabase.PET_NAME, petName);
                values.put(PetDatabase.BIRTH_DATE, birthDate);
                values.put(PetDatabase.SPECIES, species);
                values.put(PetDatabase.PICTURE, byteArrayImage);

                try {
                    db.beginTransaction();
                    db.update(PetDatabase.TABLE_PET_PROFILES, values, "ID=?", new String[] {tableId});
                    db.setTransactionSuccessful();

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    db.endTransaction();
                    db.close();

                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                }
            }
        });

        removePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditPetActivity.this);
                alertBuilder.setMessage("Are you sure you want to remove this pet?");
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                try {
                                    db.beginTransaction();
                                    db.execSQL("DELETE FROM " + PetDatabase.TABLE_PET_PROFILES + " WHERE ID=" + tableIdExtra + " AND NAME='" + nameExtra + "'");
                                    db.setTransactionSuccessful();
                                } catch (Exception ex) {
                                    System.out.println(ex.getMessage());
                                } finally {
                                    db.endTransaction();
                                    Intent mainActivity = new Intent(EditPetActivity.this, MainActivity.class);
                                    startActivity(mainActivity);
                                }
                            }
                        });

                alertBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                alertBuilder.show();
        }});
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
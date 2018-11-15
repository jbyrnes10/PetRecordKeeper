package com.jbyrnes.petrecordkeeper;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPetHistory extends AppCompatActivity {

    private Intent intent;
    private PetHistoryCard historyCard;
    String nameExtra;
    long tableIdExtra;
    ImageView photoView;
    String imageString;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;
    //public static final String EXTRA_NAME = "name";
    //public static final String EXTRA_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_pet_history);

            intent = getIntent();
            nameExtra = intent.getStringExtra(PetHistoryList.EXTRA_NAME);
            tableIdExtra = intent.getLongExtra(PetHistoryList.EXTRA_ID, 0);

            String titleText = getResources().getString(R.string.add_new_note) + " " + nameExtra;
            setTitle(titleText);

            final EditText noteDateText = findViewById(R.id.note_date);
            noteDateText.setInputType(InputType.TYPE_NULL);
            noteDateText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);

                    DatePickerDialog picker = new DatePickerDialog(AddPetHistory.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    noteDateText.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                }
            });

            Button launchCameraButton = findViewById(R.id.launch_camera_button);
            photoView = findViewById(R.id.vet_receipt_view);

            launchCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            });

            Button launchGalleryButton = findViewById(R.id.add_photo_button);

            launchGalleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMG);
                }
            });
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        Button addNoteButton = findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(new View.OnClickListener() {

            EditText noteName = (EditText) findViewById(R.id.note_name);
            EditText noteText = (EditText) findViewById(R.id.note_text);
            EditText noteDate = (EditText) findViewById(R.id.note_date);

            public void onClick(View v) {
                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                photoView = findViewById(R.id.vet_receipt_view);
                byte[] byteArrayImage = null;
                if (photoView.getDrawable() != null) {
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

                long dbNoteDate;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = sdf.parse(noteDate.getText().toString());
                    dbNoteDate = date.getTime();

                    ContentValues values = new ContentValues();
                    values.put(PetDatabase.PET_NAME, noteName.getText().toString().trim());
                    values.put(PetDatabase.NOTE_DATE, dbNoteDate);
                    values.put(PetDatabase.NOTE_TEXT, noteText.getText().toString());
                    values.put(PetDatabase.PET_PROFILE_FK, tableIdExtra);
                    values.put(PetDatabase.PICTURE, byteArrayImage);

                    long insertResult = db.insert(PetDatabase.TABLE_HISTORY_LIST, null, values);

                    if (insertResult != -1) {
                        Toast.makeText(getApplicationContext(), "Note successfully added!",
                                Toast.LENGTH_LONG).show();

                        Intent redirect = new Intent(getApplicationContext(), PetHistoryList.class);
                        redirect.putExtra(MainActivity.EXTRA_NAME, nameExtra);
                        redirect.putExtra(MainActivity.EXTRA_ID, tableIdExtra);
                        startActivity(redirect);
                    }

                } catch (ParseException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(),
                            Toast.LENGTH_LONG).show();
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
                ImageView imgView = findViewById(R.id.vet_receipt_view);
                imgView.setImageBitmap(bitmap);
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
                ImageView imgView = findViewById(R.id.vet_receipt_view);
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

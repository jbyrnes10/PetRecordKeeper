package com.jbyrnes.petrecordkeeper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
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

public class EditPetHistory extends BaseActivity {

    private TextView imageLabel;
    private ImageView noteImageView;
    private EditText noteNameText;
    private EditText noteDateText;
    private EditText noteText;
    private Intent intent;
    private PetHistoryCard editCard;

    String nameExtra;
    long tableIdExtra;

    Button updateNoteButton;
    Button removeNoteButton;
    String imageString;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_pet_history);

            intent = getIntent();
            nameExtra = intent.getStringExtra(PetHistoryList.EXTRA_NAME);
            tableIdExtra = intent.getLongExtra(PetHistoryList.EXTRA_ID, 0);

            String titleText = getResources().getString(R.string.edit_saved_note) + " " + nameExtra;
            setTitle(titleText);

            noteImageView = findViewById(R.id.vet_receipt_view);
            noteNameText = findViewById(R.id.note_name);
            noteDateText = findViewById(R.id.note_date);
            noteText = findViewById(R.id.note_text);
            imageLabel = findViewById(R.id.add_image_label);

            //hit db here
            PetCardData cardData = new PetCardData(this);
            editCard = cardData.getSingleHistoryCardById(tableIdExtra);

            noteNameText.setText(editCard.getName());
            if (editCard.pictureExists()) {
                imageLabel.setVisibility(View.GONE);
                noteImageView.setImageBitmap(editCard.getPicture());
            }
            noteText.setText(editCard.getNoteText());

            String dateString = DateFormat.format("MM/dd/yyyy", new Date(editCard.getNoteDate())).toString();
            noteDateText.setText(dateString);

            noteDateText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);

                    DatePickerDialog picker = new DatePickerDialog(EditPetHistory.this,
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

        noteDateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(EditPetHistory.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                noteDateText.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        updateNoteButton = findViewById(R.id.update_button);
        updateNoteButton.setOnClickListener(new View.OnClickListener() {

            EditText noteName = (EditText) findViewById(R.id.note_name);
            EditText noteText = (EditText) findViewById(R.id.note_text);
            EditText noteDate = (EditText) findViewById(R.id.note_date);

            public void onClick(View v) {
                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                noteImageView = findViewById(R.id.vet_receipt_view);
                byte[] byteArrayImage = null;
                if (noteImageView.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) noteImageView.getDrawable()).getBitmap();

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
                    String noteDateText = noteDate.getText().toString();
                    if (noteDateText.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a date for your note", Toast.LENGTH_LONG).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = sdf.parse(noteDateText);
                    dbNoteDate = date.getTime();

                    String noteNameText = noteName.getText().toString().trim();
                    if (noteNameText.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a name for your note", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String noteMultilineText = noteText.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(PetDatabase.PET_NAME, noteNameText);
                    values.put(PetDatabase.NOTE_DATE, dbNoteDate);
                    values.put(PetDatabase.NOTE_TEXT, noteMultilineText);
                    values.put(PetDatabase.PICTURE, byteArrayImage);

                    db.update(PetDatabase.TABLE_HISTORY_LIST, values, "ID=?", new String[] {Long.toString(tableIdExtra)});
                    db.close();

                    Intent redirect = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(redirect);

                } catch (ParseException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        removeNoteButton = findViewById(R.id.delete_button);
        removeNoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditPetHistory.this);
                alertBuilder.setMessage("Are you sure you want to delete this note?");
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                PetDatabase databaseHelper = new PetDatabase(getBaseContext());
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                try {
                                    db.delete(PetDatabase.TABLE_HISTORY_LIST, "ID=?", new String[] {Long.toString(tableIdExtra)});
                                    db.close();

                                    Intent redirect = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(redirect);

                                } catch (Exception ex) {
                                    Toast.makeText(getApplicationContext(), ex.getMessage(),
                                            Toast.LENGTH_LONG).show();
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
                noteImageView.setImageBitmap(bitmap);
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
                // Set the Image in ImageView after decoding the String
                noteImageView.setImageURI(selectedImage);
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
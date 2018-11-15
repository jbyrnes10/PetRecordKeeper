package com.jbyrnes.petrecordkeeper;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PetHistoryCardActivity extends AppCompatActivity  {

   private ImageView noteImageView;
   private TextView noteText;
   private Intent intent;
   private PetHistoryCard viewCard;

   String nameExtra;
   long tableIdExtra;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      try {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_pet_history_card);

         intent = getIntent();
         nameExtra = intent.getStringExtra(PetHistoryList.EXTRA_NAME);
         tableIdExtra = intent.getLongExtra(PetHistoryList.EXTRA_ID, 0);

         //hit db here
         PetCardData cardData = new PetCardData(this);
         viewCard = cardData.getSingleHistoryCardById(tableIdExtra);

         String dateString = DateFormat.format("MM/dd/yyyy", new Date(viewCard.getNoteDate())).toString();

         String titleText = viewCard.getName() + " (" + dateString + ")";
         setTitle(titleText);

         noteImageView = findViewById(R.id.vet_receipt_view);
         noteText = findViewById(R.id.note_text);

         noteText.setText(viewCard.getNoteText());
         noteImageView.setImageBitmap(viewCard.getPicture());

      } catch (Exception ex) {
         System.out.print(ex.getMessage());
      }
   }
}

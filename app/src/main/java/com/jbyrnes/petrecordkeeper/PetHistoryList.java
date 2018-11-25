package com.jbyrnes.petrecordkeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class PetHistoryList extends BaseActivity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_FK = "fk";
    public static final String TRANSITION_NAME = "name_transition";
    public static final String TRANSITION_ID = "id_transition";
    public static final String TRANSITION_FK = "fk_transition";

    private ArrayList<PetHistoryCard> cardList = new ArrayList<>();
    private PetCardData cardData = new PetCardData(this);
    private RecyclerView recyclerView;
    private PetHistoryCardAdapter adapter;
    private Intent intent;
    String nameExtra;
    long tableIdExtra;
    private FloatingActionButton fab;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pet_history_list);

            recyclerView = findViewById(R.id.history_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            intent = getIntent();
            nameExtra = intent.getStringExtra(MainActivity.EXTRA_NAME);
            tableIdExtra = intent.getLongExtra(MainActivity.EXTRA_ID, 0);

            String titleText = getResources().getString(R.string.history_list_title) + " " + nameExtra;
            setTitle(titleText);

            new GetHistoryCardsList().execute();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        fab = findViewById(R.id.fab_add_history);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Pair<View, String> pair = Pair.create(v.findViewById(R.id.fab_add_history), "fab_transition");

                    ActivityOptionsCompat options;
                    Activity act = PetHistoryList.this;
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, pair);

                    Intent transitionIntent = new Intent(act, AddPetHistory.class);
                    transitionIntent.putExtra(PetHistoryList.EXTRA_NAME, nameExtra);
                    transitionIntent.putExtra(PetHistoryList.EXTRA_ID, tableIdExtra);
                    act.startActivityForResult(transitionIntent, adapter.getItemCount(), options.toBundle());
                } catch (Exception ex) {
                    System.out.print(ex.getMessage());
                }
            }
        });
    }

    public class GetHistoryCardsList extends AsyncTask<Void, Void, ArrayList<PetHistoryCard>> {
        @Override
        protected ArrayList<PetHistoryCard> doInBackground(Void... params) {
            cardData.open();
            cardList = cardData.getAllHistoryCards(tableIdExtra);

//            if (cardList.size() == 0) {
//                PetHistoryCard card = new PetHistoryCard();
//                card.setName("Mollie");
//                cardList.add(card);
//                cardData.create(card);
//            }
            return cardList;
        }

        @Override
        protected void onPostExecute(ArrayList<PetHistoryCard> cards) {
            super.onPostExecute(cards);
            try {
                adapter = new PetHistoryCardAdapter(PetHistoryList.this, cardList, cardData);
                recyclerView.setAdapter(adapter);
            } catch (Exception ex) {
                System.out.print(ex.getMessage());
            }
        }
    }
}
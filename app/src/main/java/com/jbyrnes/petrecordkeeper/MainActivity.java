package com.jbyrnes.petrecordkeeper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    public static final String TRANSITION_NAME = "name_transition";
    public static final String TRANSITION_ID = "id_transition";

    private Toolbar toolbar;
    private ArrayList<PetCard> cardList = new ArrayList<>();
    private PetCardData cardData = new PetCardData(this);
    private RecyclerView recyclerView;
    private PetCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetCardsListTask().execute();

        toolbar = findViewById(R.id.pet_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_add_pet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View, String> pair = Pair.create(v.findViewById(R.id.fab_add_pet), "fab_transition");

                ActivityOptionsCompat options;
                Activity act = MainActivity.this;
                options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, pair);

                Intent transitionIntent = new Intent(act, AddPetActivity.class);
                act.startActivityForResult(transitionIntent, adapter.getItemCount(), options.toBundle());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch (item.getItemId()) {
            case R.id.profile:
                return true;
            case R.id.add_pet:
                startActivity(new Intent(this, AddPetActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetCardsListTask extends AsyncTask<Void, Void, ArrayList<PetCard>> {
        @Override
        protected ArrayList<PetCard> doInBackground(Void... params) {
            cardData.open();
            cardList = cardData.getAll();
            if (cardList.size() == 0) {
                PetCard card = new PetCard();
                card.setName("Mollie");
                //card.setColorResource(colors[i]);
                cardList.add(card);
                cardData.create(card);
            }
            return cardList;
        }

        @Override
        protected void onPostExecute(ArrayList<PetCard> cards) {
            super.onPostExecute(cards);
            adapter = new PetCardAdapter(MainActivity.this, cardList, cardData);
            recyclerView.setAdapter(adapter);
        }
    }
}
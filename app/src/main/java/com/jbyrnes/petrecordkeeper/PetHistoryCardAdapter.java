package com.jbyrnes.petrecordkeeper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PetHistoryCardAdapter extends RecyclerView.Adapter<PetHistoryCardAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PetHistoryCard> cardList;
    public PetCardData cardData;

    public PetHistoryCardAdapter(Context context, ArrayList<PetHistoryCard> cardsList, PetCardData cardsData) {
        this.context = context;
        this.cardList = cardsList;
        this.cardData = cardsData;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String name = cardList.get(position).getName();
        Long foreignKey = cardList.get(position).getPetProfileFK();
        //Long primaryKey = cardList.get(position).getId();

        TextView nameTextView = viewHolder.note_name;
        TextView tableId = viewHolder.note_table_id;
        TextView tableFk = viewHolder.profile_table_id;

        nameTextView.setText(name);
        tableId.setText(Integer.toString(position));
        //tableId.setText(Long.toString(primaryKey));
        tableFk.setText(Long.toString(foreignKey));
    }

    @Override
    public void onViewDetachedFromWindow(PetHistoryCardAdapter.ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);
        viewHolder.itemView.clearAnimation();
    }

    @Override
    public void onViewAttachedToWindow(PetHistoryCardAdapter.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        animateCircularReveal(viewHolder.itemView);
    }

    public void animateCircularReveal(View view) {
        int centerX = 0;
        int centerY = 0;
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        view.setVisibility(View.VISIBLE);
        animation.start();
    }

    public void animateCircularDelete(final View view, final int list_position) {
        int centerX = view.getWidth();
        int centerY = view.getHeight();
        int startRadius = view.getWidth();
        int endRadius = 0;
        Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                view.setVisibility(View.INVISIBLE);
                PetHistoryCard card = new PetHistoryCard();
                card.setId(getItemId(list_position));
                card.setPosition(list_position);
                new PetHistoryCardAdapter.DeleteCardTask().execute(card);
            }
        });
        animation.start();
    }

    public void addCard(String name, int color) {
        PetHistoryCard card = new PetHistoryCard();
        card.setName(name);
        new PetHistoryCardAdapter.CreateCardTask().execute(card);
    }

    public void updateCard(String name, int list_position) {
        PetHistoryCard card = new PetHistoryCard();
        card.setName(name);
        card.setId(getItemId(list_position));
        card.setPosition(list_position);
        new PetHistoryCardAdapter.UpdateCardTask().execute(card);
    }

    public void deleteCard(View view, int list_position) {
        animateCircularDelete(view, list_position);
    }

    @Override
    public int getItemCount() {
        if (cardList.isEmpty()) {
            return 0;
        } else {
            return cardList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return cardList.get(position).getId();
    }

    @Override
    public PetHistoryCardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(viewGroup.getContext());
        View v = li.inflate(R.layout.history_card_layout, viewGroup, false);
        return new PetHistoryCardAdapter.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView note_name;
        private TextView note_table_id;
        private TextView profile_table_id;

        public ViewHolder(View v) {
            super(v);
            note_name = v.findViewById(R.id.history_name);
            note_table_id = v.findViewById(R.id.history_table_id);
            profile_table_id = v.findViewById(R.id.profile_table_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair<View, String> p1 = Pair.create((View) note_name, PetHistoryList.TRANSITION_NAME);
                    Pair<View, String> p2 = Pair.create((View) note_name, PetHistoryList.TRANSITION_ID);
                    Pair<View, String> p3 = Pair.create((View) note_name, PetHistoryList.TRANSITION_FK);

                    AppCompatActivity act = (AppCompatActivity) context;
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, p1, p2, p3);

                    int requestCode = getAdapterPosition();
                    long id = cardList.get(requestCode).getId();
                    String name = cardList.get(requestCode).getName();
                    long fk = cardList.get(requestCode).getPetProfileFK();

                    try {
                        Intent transitionIntent = new Intent(context, PetHistoryCardActivity.class);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_NAME, name);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_ID, id);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_FK, fk);
                        ((AppCompatActivity) context).startActivityForResult(transitionIntent, requestCode, options.toBundle());
                    }
                    catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });

            Button editProfileButton = v.findViewById(R.id.edit_pet_history_button);
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair<View, String> p1 = Pair.create((View) note_name, PetHistoryList.TRANSITION_NAME);
                    Pair<View, String> p2 = Pair.create((View) note_name, PetHistoryList.TRANSITION_ID);
                    Pair<View, String> p3 = Pair.create((View) note_name, PetHistoryList.TRANSITION_FK);

                    AppCompatActivity act = (AppCompatActivity) context;
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, p1, p2, p3);

                    int requestCode = getAdapterPosition();
                    long id = cardList.get(requestCode).getId();
                    String name = cardList.get(requestCode).getName();
                    long fk = cardList.get(requestCode).getPetProfileFK();

                    try {
                        Intent transitionIntent = new Intent(context, EditPetHistory.class);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_NAME, name);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_ID, id);
                        transitionIntent.putExtra(PetHistoryList.EXTRA_FK, fk);
                        ((AppCompatActivity) context).startActivityForResult(transitionIntent, requestCode, options.toBundle());
                    }
                    catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

            });
        }
    }

    private class CreateCardTask extends AsyncTask<PetHistoryCard, Void, PetHistoryCard> {
        @Override
        protected PetHistoryCard doInBackground(PetHistoryCard... cards) {
            cardData.create(cards[0]);
            cardList.add(cards[0]);
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetHistoryCard card) {
            super.onPostExecute(card);
            //((MainActivity) context).doSmoothScroll(getItemCount() - 1);
            notifyItemInserted(getItemCount());
        }
    }

    private class UpdateCardTask extends AsyncTask<PetHistoryCard, Void, PetHistoryCard> {
        @Override
        protected PetHistoryCard doInBackground(PetHistoryCard... cards) {
            cardList.get(cards[0].getPosition()).setName(cards[0].getName());
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetHistoryCard card) {
            super.onPostExecute(card);
            notifyItemChanged(card.getPosition());
        }
    }

    private class DeleteCardTask extends AsyncTask<PetHistoryCard, Void, PetHistoryCard> {
        @Override
        protected PetHistoryCard doInBackground(PetHistoryCard... cards) {
            //cardData.delete(cards[0].getId());
            cardList.remove(cards[0].getPosition());
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetHistoryCard card) {
            super.onPostExecute(card);
            notifyItemRemoved(card.getPosition());
        }
    }
}

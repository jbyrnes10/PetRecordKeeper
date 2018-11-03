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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PetCard> cardList;
    public PetCardData cardData;

    public PetCardAdapter(Context context, ArrayList<PetCard> cardsList, PetCardData cardsData) {
        this.context = context;
        this.cardList = cardsList;
        this.cardData = cardsData;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String name = cardList.get(position).getName();
        TextView nameTextView = viewHolder.name;
        TextView tableId = viewHolder.table_id;

        nameTextView.setText(name);
        tableId.setText(Integer.toString(position));

        ImageView image = viewHolder.pet_image;
        image.setImageBitmap(cardList.get(position).getPicture());
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);
        viewHolder.itemView.clearAnimation();
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder viewHolder) {
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
                PetCard card = new PetCard();
                card.setId(getItemId(list_position));
                card.setPosition(list_position);
                new DeleteCardTask().execute(card);
            }
        });
        animation.start();
    }

    public void addCard(String name, int color) {
        PetCard card = new PetCard();
        card.setName(name);
        //card.setColorResource(color);
        new CreateCardTask().execute(card);
    }

    public void updateCard(String name, int list_position) {
        PetCard card = new PetCard();
        card.setName(name);
        card.setId(getItemId(list_position));
        card.setPosition(list_position);
        new UpdateCardTask().execute(card);
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(viewGroup.getContext());
        View v = li.inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView pet_image;
        private TextView table_id;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            pet_image = v.findViewById(R.id.pet_image);
            table_id = v.findViewById(R.id.table_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair<View, String> p1 = Pair.create((View) name, MainActivity.TRANSITION_NAME);
                    Pair<View, String> p2 = Pair.create((View) name, MainActivity.TRANSITION_ID);
//                    Pair<View, String> p3 = Pair.create((View) deleteButton, MainActivity.TRANSITION_DELETE_BUTTON);

                    ActivityOptionsCompat options;
                    AppCompatActivity act = (AppCompatActivity) context;
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, p1, p2);

                    int requestCode = getAdapterPosition();
                    long id = cardList.get(requestCode).getId();
                    String name = cardList.get(requestCode).getName();
                    //int color = cardList.get(requestCode).getColorResource();

                    try {
                        Intent transitionIntent = new Intent(context, EditPetActivity.class);
                        transitionIntent.putExtra(MainActivity.EXTRA_NAME, name);
                        transitionIntent.putExtra(MainActivity.EXTRA_ID, id);
                        ((AppCompatActivity) context).startActivityForResult(transitionIntent, requestCode, options.toBundle());
                    }
                    catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

            });
        }
    }

    private class CreateCardTask extends AsyncTask<PetCard, Void, PetCard> {
        @Override
        protected PetCard doInBackground(PetCard... cards) {
            cardData.create(cards[0]);
            cardList.add(cards[0]);
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetCard card) {
            super.onPostExecute(card);
            //((MainActivity) context).doSmoothScroll(getItemCount() - 1);
            notifyItemInserted(getItemCount());
        }
    }

    private class UpdateCardTask extends AsyncTask<PetCard, Void, PetCard> {
        @Override
        protected PetCard doInBackground(PetCard... cards) {
            //cardData.update(cards[0].getId(), cards[0].getName());
            cardList.get(cards[0].getPosition()).setName(cards[0].getName());
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetCard card) {
            super.onPostExecute(card);
            notifyItemChanged(card.getPosition());
        }
    }

    private class DeleteCardTask extends AsyncTask<PetCard, Void, PetCard> {
        @Override
        protected PetCard doInBackground(PetCard... cards) {
            //cardData.delete(cards[0].getId());
            cardList.remove(cards[0].getPosition());
            return cards[0];
        }

        @Override
        protected void onPostExecute(PetCard card) {
            super.onPostExecute(card);
            notifyItemRemoved(card.getPosition());
        }
    }
}
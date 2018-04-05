package com.example.android.findbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

/**
 * Created by Gurchani on 3/12/2018.
 */

public class GreenAdapter extends RecyclerView.Adapter<GreenAdapter.drinkViewHolder> {
    private int numberOfItems;
    final private listItemClickListener mOnClickListener;

    public GreenAdapter(int itemsQuantity, listItemClickListener listener) {
        numberOfItems = itemsQuantity;
        mOnClickListener = listener;
    }

    @Override
    public drinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutforItem = R.layout.drink_resource_file;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutforItem, parent, false);
        drinkViewHolder drinkViewHolder = new drinkViewHolder(view);
        return drinkViewHolder;
    }
    public interface listItemClickListener {
            void onListItemClick(int clickedItemIndex);
    }

    @Override
    public void onBindViewHolder(drinkViewHolder holder, int position) {
        holder.bind(position, "Beer");
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }


    class drinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CheckedTextView nameOfdrink;

        public drinkViewHolder(View itemView) {
            super(itemView);

            nameOfdrink = itemView.findViewById(R.id.NameofDrink);
        }

        void bind(int price, String nameOfDrink) {

            nameOfdrink.setText(nameOfDrink);

        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}

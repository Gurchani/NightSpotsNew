package com.example.android.findbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gurchani on 3/12/2018.
 */

public class GreenAdapter extends RecyclerView.Adapter<GreenAdapter.drinkViewHolder> {
    private int numberOfItems;

    public GreenAdapter(int itemsQuantity) {
        numberOfItems = itemsQuantity;
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

    @Override
    public void onBindViewHolder(drinkViewHolder holder, int position) {
        holder.bind(position, "Beer");
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }


    class drinkViewHolder extends RecyclerView.ViewHolder {
        TextView priceOfdrink;
        TextView nameOfdrink;

        public drinkViewHolder(View itemView) {
            super(itemView);
            priceOfdrink = itemView.findViewById(R.id.textPlacementofPrice);
            nameOfdrink = itemView.findViewById(R.id.NameofDrink);
        }

        void bind(int price, String nameOfDrink) {
            priceOfdrink.setText(String.valueOf(price));
            nameOfdrink.setText(nameOfDrink);

        }
    }
}

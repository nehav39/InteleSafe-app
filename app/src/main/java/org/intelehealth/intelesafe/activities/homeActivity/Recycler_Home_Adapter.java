package org.intelehealth.intelesafe.activities.homeActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.intelehealth.intelesafe.R;

import java.util.ArrayList;

public class Recycler_Home_Adapter extends RecyclerView.Adapter<Recycler_Home_Adapter.MyViewHolder> {

    ArrayList<Day_Date> arrayList;

    public Recycler_Home_Adapter(ArrayList<Day_Date> recycler_arraylist) {
        this.arrayList = recycler_arraylist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_recycler_view, viewGroup, false);
        return new MyViewHolder(v, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        myViewHolder.day_text.setText(arrayList.get(position).getDay());
        myViewHolder.date_text.setText(arrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return  arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView day_text;
        TextView date_text;
        ImageView check_image;
        Context context;


        public MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cardview_recycler);
            this.day_text = itemView.findViewById(R.id.recycler_day_textview);
            this.date_text = itemView.findViewById(R.id.recycler_date_textview);
            this.check_image = itemView.findViewById(R.id.recycler_tick_imageview);
            this.context = context;
        }

        @Override
        public void onClick(View view) {

        }
    }
}

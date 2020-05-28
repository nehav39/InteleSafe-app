package org.intelehealth.intelesafe.activities.CardGuidelines;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.Webview;

import java.util.List;

/**
 * Created by Prajwal Waingankar
 * on 28-May-20.
 * Github: prajwalmw
 */


public class RecyclerAdapter_CardGuidelines extends
        RecyclerView.Adapter<RecyclerAdapter_CardGuidelines.MyViewModel> {
    private Context context;
    List<Model_CardGuidelines> model_cardGuidelines;
    String ppe = "";

    public RecyclerAdapter_CardGuidelines(Context context, List<Model_CardGuidelines> model_cardGuidelines, String ppe_1)
    {
        this.context = context;
        this.model_cardGuidelines = model_cardGuidelines;
        this.ppe = ppe_1;
    }

    @NonNull
    @Override
    public MyViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_guidelines_recyclerlayout, parent, false);

        return new MyViewModel(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewModel holder, int position) {
        holder.title_textview.setText(model_cardGuidelines.get(position).getTitle());
        holder.description_textview.setText(model_cardGuidelines.get(position).getDescription());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Webview.class);
                i.putExtra("Webview" + position + ppe, 1);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return model_cardGuidelines.size();
    }

    public class MyViewModel extends RecyclerView.ViewHolder{
        TextView title_textview;
        TextView description_textview;
        CardView cardView;

        public MyViewModel(@NonNull View itemView)
        {
            super(itemView);
            this.title_textview = itemView.findViewById(R.id.card_g_title);
            this.description_textview = itemView.findViewById(R.id.card_g_descr);
            this.cardView = itemView.findViewById(R.id.cardview_g);

        }
    }
}

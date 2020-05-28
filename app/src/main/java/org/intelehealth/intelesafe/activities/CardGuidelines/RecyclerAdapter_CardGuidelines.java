package org.intelehealth.intelesafe.activities.CardGuidelines;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Prajwal Waingankar
 * on 28-May-20.
 * Github: prajwalmw
 */


public class RecyclerAdapter_CardGuidelines extends RecyclerView.Adapter<RecyclerAdapter_CardGuidelines.MyViewModel> {

    public RecyclerAdapter_CardGuidelines(Context context, List<Model_CardGuidelines> model_cardGuidelines) {
    }

    @NonNull
    @Override
    public MyViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewModel holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewModel extends RecyclerView.ViewHolder{

        public MyViewModel(@NonNull View itemView) {
            super(itemView);
        }
    }
}

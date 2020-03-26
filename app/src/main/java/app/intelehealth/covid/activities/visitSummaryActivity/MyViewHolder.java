package app.intelehealth.covid.activities.visitSummaryActivity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import app.intelehealth.covid.R;


/**
 * Created by dell on 6/30/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public MyViewHolder(View view)
    {
        super(view);
        imageView = view.findViewById(R.id.imageView_medical);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}

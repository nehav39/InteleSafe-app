package org.intelehealth.swasthyasamparkp.activities.HealthFacility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.swasthyasamparkp.R;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
* Created By: Prajwal Waingankar on 30-Jun-21
* Github: prajwalmw
* Email: prajwalwaingankar@gmail.com
*/

public class MedicalFacility_Adapter extends RecyclerView.Adapter<MedicalFacility_Adapter.MedicalFacility_ViewHolder> {
private ArrayList<MedicalFacility_DataModel> dataModels;
private Context context;

    public MedicalFacility_Adapter(ArrayList<MedicalFacility_DataModel> dataModel, Context context) {
        this.dataModels = dataModel;
        this.context = context;
    }

/*    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resourceID, parent, false);
        }

        MedicalFacility_DataModel medicalFacility_dataModel = dataModels.get(position);

        TextView title = convertView.findViewById(R.id.title);
        TextView address = convertView.findViewById(R.id.address);
        TextView phoneno = convertView.findViewById(R.id.phoneno);
        ImageButton imageButton = convertView.findViewById(R.id.map_icon);

        title.setText(medicalFacility_dataModel.getTitle());
        address.setText(medicalFacility_dataModel.getAddress());
        phoneno.setText(medicalFacility_dataModel.getPhoneno());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Open google maps...
            }
        });

        return convertView;
    }*/

    @Override
    public MedicalFacility_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_medical_facility, parent, false);
        MedicalFacility_ViewHolder medicalFacility_viewHolder = new MedicalFacility_ViewHolder(view);
        return medicalFacility_viewHolder;
    }

    @Override
    public void onBindViewHolder(MedicalFacility_Adapter.MedicalFacility_ViewHolder holder, int position) {
        MedicalFacility_DataModel medicalFacility_dataModel = dataModels.get(position);

        holder.title.setText(medicalFacility_dataModel.getTitle());
        holder.address.setText(medicalFacility_dataModel.getAddress());

        holder.phoneno.setText(medicalFacility_dataModel.getPhoneno());
        holder.phoneno.setPaintFlags(holder.phoneno.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.mapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("url", medicalFacility_dataModel.getMap_url());
                context.startActivity(i);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }


    public class MedicalFacility_ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView address;
        private TextView phoneno;
        private ImageButton mapLink;

        public MedicalFacility_ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
            phoneno = itemView.findViewById(R.id.phoneno);
            mapLink = itemView.findViewById(R.id.map_icon);
        }
    }
}

package org.intelehealth.intelesafe.activities.homeActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;

public class Recycler_Home_Adapter extends RecyclerView.Adapter<Recycler_Home_Adapter.MyViewHolder> {

    ArrayList<Day_Date> arrayList;
    private Context mcontext;
    SessionManager sessionManager;

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

       StringBuilder stringBuilder = new StringBuilder(arrayList.get(position).getDate());
                       int a1 = stringBuilder.indexOf("T");
        myViewHolder.date_text.setText(stringBuilder.substring(0, a1));

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        String query = "SELECT v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND " +
                "v.patientuuid = ? AND v.startdate=?";
        String[] data = {sessionManager.getPersionUUID(), arrayList.get(position).getDate()};



        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            String message = "skdjs";
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(mcontext);
                alertdialogBuilder.setTitle("Today's Check-in");

                final Cursor cursor = db.rawQuery(query, data);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {

                                message = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                                alertdialogBuilder.setMessage(message);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }while (cursor.moveToNext());
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }

               // alertdialogBuilder.setMessage("");

                alertdialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // moveTaskToBack(true);
                        // finish();
                    }
                });
                //alertdialogBuilder.setNegativeButton(R.string.generic_no, null);

                AlertDialog alertDialog = alertdialogBuilder.create();
                alertDialog.show();

                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
                positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                negativeButton.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
                negativeButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            }
        });

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

       // Context context;


        public MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cardview_recycler);
            this.day_text = itemView.findViewById(R.id.recycler_day_textview);
            this.date_text = itemView.findViewById(R.id.recycler_date_textview);
            this.check_image = itemView.findViewById(R.id.recycler_tick_imageview);
            mcontext = context;
            sessionManager = new SessionManager(mcontext);

           // this.date_text.getText();

        }

        @Override
        public void onClick(View view) {

        }
    }
}

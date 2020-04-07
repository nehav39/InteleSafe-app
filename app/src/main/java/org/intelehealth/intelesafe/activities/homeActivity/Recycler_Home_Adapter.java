package org.intelehealth.intelesafe.activities.homeActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 */

public class Recycler_Home_Adapter extends RecyclerView.Adapter<Recycler_Home_Adapter.MyViewHolder> {

    ArrayList<Day_Date> arrayList;
    ArrayList<String> stringArrayList_date;
    private Context mcontext;
    SessionManager sessionManager;

    public Recycler_Home_Adapter(Context context, ArrayList<Day_Date> recycler_arraylist, ArrayList<String> array_og_date) {
        this.arrayList = recycler_arraylist;
        this.mcontext = context;
        this.stringArrayList_date = array_og_date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_recycler_view, viewGroup, false);
        return new MyViewHolder(v, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        //SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /*    try {
            Date first_visit = currentDate.parse(arrayList.get(position).getDate());
            Date array_visit = currentDate.parse(arrayList.get(position+1).getDate());

            if((array_visit.getTime() > first_visit.getTime() || arrayList.size() > 0) &&
            array_visit.getTime() != first_visit.getTime())
            //25 after 26 -> true. --- 26 before 25 -> true.
            {
                myViewHolder.day_text.setText(arrayList.get(position).getDay());
                StringBuilder stringBuilder = new StringBuilder(arrayList.get(position).getDate());
                int a1 = stringBuilder.indexOf("T");
                myViewHolder.date_text.setText(stringBuilder.substring(0, a1));
                Log.d("GG","GG: "+stringBuilder.substring(0, a1));
                Log.d("GG","GG_1: "+myViewHolder.date_text.getText().toString());
            }
            else
            {
                Toast.makeText(mcontext, "Hello", Toast.LENGTH_SHORT).show();
                myViewHolder.cardView.setVisibility(View.GONE);
            }

        }
        catch (Exception e)
        {

        }*/


        myViewHolder.day_text.setText(arrayList.get(position).getDay());
//        StringBuilder stringBuilder = new StringBuilder(arrayList.get(position).getDate());
//        int a1 = stringBuilder.indexOf("T");
        myViewHolder.date_text.setText(arrayList.get(position).getDate());
        Log.d("GG", "GG: " + arrayList.get(position).getDate());
        Log.d("GG", "GG_1: " + myViewHolder.date_text.getText().toString());


        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        String query = "SELECT v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND " +
                "v.patientuuid = ? AND v.startdate LIKE ? ";
        String[] data = {sessionManager.getPersionUUID(), arrayList.get(position).getDate() + "%"};
        String dd = sessionManager.getPersionUUID();
        Log.d("JJJ", "JJ: " + dd);

        String message = "skdjs";
        ArrayList<String> array_message = new ArrayList<>();
        int counter = 0;

        final Cursor cursor = db.rawQuery(query, data);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {

                        message = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                        StringBuilder stringBuilder = new StringBuilder(message);
                        int a1 = stringBuilder.indexOf("T");
                        String de = stringBuilder.substring(0, a1);

                        array_message.add(counter, de);

                        counter++;
                        // alertdialogBuilder.setMessage(message);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(mcontext);
                alertdialogBuilder.setTitle("Today's Check-in");


                View customView = LayoutInflater.from(mcontext).inflate(R.layout.custom_dialog_layout, null);
                alertdialogBuilder.setView(customView);

                LinearLayout visit_list_view = customView.findViewById(R.id.visit_list_view);

                Log.e("VENU: ",array_message.toString());
                StringBuilder stringBuilder_2 = new StringBuilder();
                for (int i = 0; i < array_message.size(); i++) {
                    stringBuilder_2.append("Visit no." + (i + 1) + "-\t\t" + array_message.get(i));
                    //stringBuilder_2.append("\n");
                    TextView visitText = new TextView(mcontext);
                    visitText.setText("Visit no." + (i + 1) + "-\t\t" + array_message.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 8, 0, 8);
                    visitText.setLayoutParams(params);
                    visitText.setPadding(5, 10, 5, 10);
                    visitText.setTextSize(14);
                    visitText.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
                    Typeface typeface = ResourcesCompat.getFont(mcontext, R.font.lato_regular);
                    visitText.setTypeface(typeface);
                    visitText.setPaintFlags(visitText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    visit_list_view.addView(visitText);
                    visitText.setTag(i);
                    visitText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int pos = (int) view.getTag();
                            ((HomeActivity) mcontext).pastVisits(pos);
                        }
                    });

//                    ((HomeActivity) context).pastVisits(position);
                }
                // alertdialogBuilder.setMessage(stringBuilder_2.toString());
              /*  SpannableString ss = new SpannableString(stringBuilder_2.toString());
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {

                       // startActivity(new Intent(MyActivity.this, NextActivity.class));
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                alertdialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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


    private void setAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mcontext);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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

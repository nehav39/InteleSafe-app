package org.intelehealth.intelesafe.activities.homeActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.intelesafe.BuildConfig;
import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.appointments.AppointmentsActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 */

public class Recycler_Home_Adapter extends RecyclerView.Adapter<Recycler_Home_Adapter.MyViewHolder> {

    ArrayList<Day_Date> arrayList;
    ArrayList<String> stringArrayList_date;
    private Context mcontext;
    SessionManager sessionManager;
    AlertDialog.Builder alertdialogBuilder;
    private boolean self;

    public Recycler_Home_Adapter(Context context, ArrayList<Day_Date> recycler_arraylist, ArrayList<String> array_og_date, boolean self) {
        this.arrayList = recycler_arraylist;
        this.mcontext = context;
        this.stringArrayList_date = array_og_date;
        this.self = self;
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
//        StringBuilder stringBuilder = new StringBuilder(arrayList.get(position).getDate());
//        int a1 = stringBuilder.indexOf("T");
        myViewHolder.date_text.setText(arrayList.get(position).getDate());
        Log.d("GG", "GG: " + arrayList.get(position).getDate());
        Log.d("GG", "GG_1: " + myViewHolder.date_text.getText().toString());

        myViewHolder.titlePart2TextView.setText(arrayList.get(position).getDate());
        if (self) {
            myViewHolder.titlePart1TextView.setText(mcontext.getString(R.string.self_assessment_label));
            myViewHolder.backgroundCardView.setCardBackgroundColor(mcontext.getResources().getColor(R.color.orange2));
            myViewHolder.foregroundCardView.setCardBackgroundColor(mcontext.getResources().getColor(R.color.red_light_1));
            myViewHolder.prescriptionLayout.setVisibility(View.GONE);
//            myViewHolder.descriptionTextView.setText(mcontext.getString(R.string.click_here_view_details));
            String physicalExamStr = arrayList.get(position).physicalExamValue.replaceAll("<b>General exams: </b>", "<b>Summary: </b>");
            myViewHolder.descriptionTextView.setText(Html.fromHtml(physicalExamStr));
        } else {
            myViewHolder.titlePart1TextView.setText(mcontext.getString(R.string.doctors_visits_label));
            myViewHolder.backgroundCardView.setCardBackgroundColor(mcontext.getResources().getColor(R.color.blue_11));
            myViewHolder.foregroundCardView.setCardBackgroundColor(mcontext.getResources().getColor(R.color.blue_light_1));
            myViewHolder.prescriptionLayout.setVisibility(View.VISIBLE);
            if (arrayList.get(position).hasPrescription) {
                myViewHolder.check_image.setVisibility(View.VISIBLE);
                myViewHolder.prescriptionStatusTextView.setText(mcontext.getString(R.string.click_here_to_download_prescription));

            } else {
                myViewHolder.check_image.setVisibility(View.GONE);
                myViewHolder.prescriptionStatusTextView.setText(mcontext.getString(R.string.waiting_for_doctor_s_prescription));
            }
//            myViewHolder.descriptionTextView.setText(mcontext.getString(R.string.note_for_doctors_visits));
            myViewHolder.descriptionTextView.setText(Html.fromHtml(String.format("%s%s", "<b>Teleconsultation for: </b><br>", arrayList.get(position).currentComplaintValue)));
        }


        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
       /* String query =
                "SELECT  DISTINCT tbl_visit.uuid, tbl_visit.startdate FROM tbl_visit JOIN tbl_encounter ON  tbl_encounter.visituuid == tbl_visit.uuid JOIN tbl_obs ON tbl_obs.encounteruuid== tbl_encounter.uuid" +
                " WHERE  tbl_visit.startdate IS NOT NULL AND  " +
                "tbl_visit.patientuuid = ? AND tbl_visit.startdate LIKE ? ";*/
        String query = "SELECT DISTINCT v.uuid, v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND (v.issubmitted == 1 OR v.enddate IS NOT NULL) AND " +
                "v.patientuuid = ? AND v.startdate LIKE ? ";
        String[] data = {sessionManager.getPersionUUID(), arrayList.get(position).getDate() + "%"};
        String dd = sessionManager.getPersionUUID();
        Log.d("JJJ", "JJ: " + dd);

        String message = "skdjs";
        ArrayList<String> array_message = new ArrayList<>();
        ArrayList<String> uuid_array = new ArrayList<>();
        int counter = 0;

        final Cursor cursor = db.rawQuery(query, data);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        String uuid = cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
                        Cursor cursor1 = db.rawQuery("select * from tbl_obs as o where o.conceptuuid = '3edb0e09-9135-481e-b8f0-07a26fa9a5ce' and o.encounteruuid IN (select e.uuid from tbl_encounter as e where e.visituuid = ?)", new String[]{uuid});
                        if (cursor1 != null && cursor1.moveToFirst()) {
                            cursor1.close();
                            if (self)
                                continue;
                        } else {
                            cursor1.close();
                            if (!self)
                                continue;
                        }

                        message = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                        StringBuilder stringBuilder = new StringBuilder(message);
                        int a1 = stringBuilder.indexOf("T");
                        String de = stringBuilder.substring(0, a1);

                        array_message.add(counter, de);


                        uuid_array.add(counter, uuid);

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

        Log.v("TAG", array_message + "");

        myViewHolder.foregroundCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (self)
                    return;

                Day_Date day_date = arrayList.get(position);
                String Url = BuildConfig.BASE_URL +
                        "preApi/i.jsp?v=" +
                        day_date.visitUid + "&pid=" + day_date.openmrsId;

                Intent download_intent = new Intent(Intent.ACTION_VIEW);
                download_intent.setData(Uri.parse(Url));
                mcontext.startActivity(download_intent);
                Log.d("url", "url: "+ Url);

                /*alertdialogBuilder = new AlertDialog.Builder(mcontext);
                alertdialogBuilder.setTitle(R.string.today_checkin);


                View customView = LayoutInflater.from(mcontext).inflate(R.layout.custom_dialog_layout, null);
                alertdialogBuilder.setView(customView);

                LinearLayout visit_list_view = customView.findViewById(R.id.visit_list_view);

                alertdialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // moveTaskToBack(true);
                        // finish();
                    }
                });

                AlertDialog alertDialog = alertdialogBuilder.create();
                alertDialog.show();

                Log.e("VENU: ", array_message.toString());
                StringBuilder stringBuilder_2 = new StringBuilder();

                for (int i = 0; i < array_message.size(); i++) {
                    stringBuilder_2.append(R.string.visit + (i + 1) + "-\t\t" + array_message.get(i));
                    //stringBuilder_2.append("\n");
                    TextView visitText = new TextView(mcontext);
//                    visitText.setText( "Visit no." + (i + 1) + "-\t\t" + array_message.get(i));
                    visitText.setText("Visit no." + (i + 1) + "-\t\t View Details");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (mcontext.getResources().getBoolean(R.bool.isTab)) {
                        params.setMargins(0, 12, 0, 12);
                        visitText.setLayoutParams(params);
                        visitText.setPadding(9, 14, 9, 14);
                        visitText.setTextSize(20);
                    } else {
                        params.setMargins(0, 8, 0, 8);
                        visitText.setLayoutParams(params);
                        visitText.setPadding(5, 10, 5, 10);
                        visitText.setTextSize(14);
                    }
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
                            ((AppointmentsActivity) mcontext).pastVisits(pos, array_message.get(pos), self, uuid_array.get(pos));
                            alertDialog.dismiss();
                        }
                    });

//                    ((HomeActivity) context).pastVisits(position);
                }
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
                positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                negativeButton.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
                negativeButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);*/
            }
        });

        if (arrayList.get(position).hasPrescription) {
            myViewHolder.check_image.setImageResource(R.drawable.prescription_new_icon);
        } else {
            myViewHolder.check_image.setImageDrawable(null);
        }


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
        TextView titlePart1TextView, titlePart2TextView, descriptionTextView, prescriptionStatusTextView;
        CardView backgroundCardView, foregroundCardView;
        RelativeLayout prescriptionLayout;


        public MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            titlePart1TextView = itemView.findViewById(R.id.title_label_1_tv);
            titlePart2TextView = itemView.findViewById(R.id.title_label_2_tv);
            descriptionTextView = itemView.findViewById(R.id.note_tv);
            prescriptionStatusTextView = itemView.findViewById(R.id.prescription_status_tv);

            backgroundCardView = itemView.findViewById(R.id.cardview_recycler_1);
            foregroundCardView = itemView.findViewById(R.id.cardview_recycler);

            prescriptionLayout = itemView.findViewById(R.id.prescription_layout);

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

package org.intelehealth.swasthyasamparkp.activities.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.additionalDocumentsActivity.AdditionalDocumentViewHolder;
import org.intelehealth.swasthyasamparkp.activities.cameraActivity.CameraActivity;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.app.IntelehealthApplication;
import org.intelehealth.swasthyasamparkp.database.dao.ImagesDAO;
import org.intelehealth.swasthyasamparkp.database.dao.ObsDAO;
import org.intelehealth.swasthyasamparkp.database.dao.PatientsDAO;
import org.intelehealth.swasthyasamparkp.models.DocumentObject;
import org.intelehealth.swasthyasamparkp.models.ObsImageModel.ObsJsonResponse;
import org.intelehealth.swasthyasamparkp.models.ObsImageModel.ObsPushDTO;
import org.intelehealth.swasthyasamparkp.models.UserProfileData;
import org.intelehealth.swasthyasamparkp.models.dto.ObsDTO;
import org.intelehealth.swasthyasamparkp.models.dto.PatientDTO;
import org.intelehealth.swasthyasamparkp.models.dto.ResponseDTO;
import org.intelehealth.swasthyasamparkp.models.patientImageModelRequest.PatientProfile;
import org.intelehealth.swasthyasamparkp.utilities.BitmapUtils;
import org.intelehealth.swasthyasamparkp.utilities.DownloadFilesUtils;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.NetworkConnection;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.intelehealth.swasthyasamparkp.utilities.StringUtils;
import org.intelehealth.swasthyasamparkp.utilities.UrlModifiers;
import org.intelehealth.swasthyasamparkp.utilities.UuidDictionary;
import org.intelehealth.swasthyasamparkp.utilities.exception.DAOException;
import org.intelehealth.swasthyasamparkp.widget.materialprogressbar.CustomProgressDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_PROFILE_PHOTO = 100;
    private static final int PICK_ADDITIONAL_DOC = 101;
    private static final String TYPE_PROFILE = "PP";
    private ImageView ivProfile;
    List<ObsPushDTO> obsPushDTOList;
    private String mCurrentPhotoPath;
    private String dummyEncounterUuid, obsUid;
//    private ArrayList<String> additionalImages = new ArrayList<>();
    private ArrayList<DocumentObject> documentObjects;
    private RadioGroup rgStayType, rgAllergies, rgMedications, rgAlcohol, rgSmokeTobacco, rgPostCovidConsulting;
    private CheckBox cbDiabetes, cbHypertension, cbHeartDiseases, cbCancer, cbAsthma, cbKidneyDisorder, cbOthers;
    private CheckBox cbDiabetesFamily, cbHypertensionFamily, cbHeartDiseasesFamily, cbCancerFamily, cbAsthmaFamily, cbKidneyDisorderFamily, cbOthersFamily;
    private EditText et_stay_days, et_Complaint, etOthers, etOthersFamily, et_admission_date, et_tested_positive_date;
    private CustomProgressDialog customProgressDialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    private AdditionalDocumentAdapter recyclerViewAdapter;
    private SessionManager sessionManager;
    private Context context;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = ProfileActivity.this;
        sessionManager = new SessionManager(this);
        dummyEncounterUuid = sessionManager.getDummyEncounterUidForProfile();
        obsUid = sessionManager.getObsUidForProfile();
        customProgressDialog = new CustomProgressDialog(this);
        ArrayList<File> fileList = getDocuments();
        documentObjects = new ArrayList<>();
        for (File file : fileList)
            documentObjects.add(new DocumentObject(file.getName(), file.getAbsolutePath()));

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new AdditionalDocumentAdapter(this, documentObjects, AppConstants.IMAGE_PATH);
        recyclerView.setAdapter(recyclerViewAdapter);

        SessionManager sessionManager = new SessionManager(this);
        PatientsDAO patientsDAO = new PatientsDAO();
        PatientDTO patient = patientsDAO.getPatient(sessionManager.getPersionUUID());
        int age = getAge(patient.getDateofbirth());
        TextView tvNameAge = findViewById(R.id.tvNameAge);
        TextView tvMobile = findViewById(R.id.tvMobile);
        TextView tvAddress = findViewById(R.id.tvAddress);
        et_tested_positive_date = findViewById(R.id.et_tested_positive_date);
        String attributeValue = patientsDAO.getAttributeValue(patient.getUuid(), "ecdaadb6-14a0-4ed9-b5b7-cfed87b44b87");
        et_tested_positive_date.setText(attributeValue);
        et_tested_positive_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = {
                        getString(R.string.enter_number_of_days), getString(R.string.enter_date)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View viewInflated = LayoutInflater.from(context).inflate(R.layout.input_tested_positive, findViewById(android.R.id.content), false);
                            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                            builder.setView(viewInflated);
                            builder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    et_tested_positive_date.setError(null);
                                    //passes number of days to this function to calculate the actual date...
                                    if (!input.getText().toString().isEmpty() || !input.getText().toString().equals("")) {
                                        String date = getDatefromDays(Integer.parseInt(input.getText().toString()));
                                        et_tested_positive_date.setText(date);
                                    } else {
                                        //do nothing close the dialog...
                                    }
                                    //  et_tested_positive_date.setText(input.getText().toString());
                                }
                            });
                            builder.show();
                            input.requestFocus();
                        } else {
                            Calendar instance = Calendar.getInstance();
                            datePickerDialog = new DatePickerDialog(context,
                                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            et_tested_positive_date.setError(null);
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTimeInMillis(0);
                                            cal.set(year, monthOfYear, dayOfMonth);
                                            Date date = cal.getTime();
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                                            et_tested_positive_date.setText(simpleDateFormat.format(date));
                                        }
                                    }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            //This will set the maxDate to Today only...
                            datePickerDialog.show();
                        }
                    }
                });
                builder.show();
            }
        });

        et_admission_date = findViewById(R.id.et_admission_date);
        et_admission_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar instance = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                et_tested_positive_date.setError(null);
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(0);
                                cal.set(year, monthOfYear, dayOfMonth);
                                Date date = cal.getTime();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                                et_admission_date.setText(simpleDateFormat.format(date));
                            }
                        }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                //This will set the maxDate to Today only...
                datePickerDialog.show();
            }
        });

        et_stay_days = findViewById(R.id.et_stay_days);
        et_Complaint = findViewById(R.id.et_Complaint);
        etOthers = findViewById(R.id.et_Others);

        rgStayType = findViewById(R.id.rgStayType);
        rgMedications = findViewById(R.id.rgMedications);
        rgAllergies = findViewById(R.id.rgAllergies);
        rgAlcohol = findViewById(R.id.rgAlcohol);
        rgSmokeTobacco = findViewById(R.id.rgSmokeTobacco);
        rgPostCovidConsulting = findViewById(R.id.rgPostCovidConsulting);

        cbDiabetes = findViewById(R.id.cbDiabetes);
        cbHypertension = findViewById(R.id.cbHypertension);
        cbHeartDiseases = findViewById(R.id.cbHeartDiseases);
        cbCancer = findViewById(R.id.cbCancer);
        cbAsthma = findViewById(R.id.cbAsthma);
        cbKidneyDisorder = findViewById(R.id.cbKidneyDisorder);
        cbOthers = findViewById(R.id.cbOthers);


        etOthersFamily = findViewById(R.id.et_OthersFamily);
        cbDiabetesFamily = findViewById(R.id.cbDiabetesFamily);
        cbHypertensionFamily = findViewById(R.id.cbHypertensionFamily);
        cbHeartDiseasesFamily = findViewById(R.id.cbHeartDiseasesFamily);
        cbCancerFamily = findViewById(R.id.cbCancerFamily);
        cbAsthmaFamily = findViewById(R.id.cbAsthmaFamily);
        cbKidneyDisorderFamily = findViewById(R.id.cbKidneyDisorderFamily);
        cbOthersFamily = findViewById(R.id.cbOthersFamily);

        tvNameAge.setText(String.format(Locale.ROOT, "%s %s, %d", patient.getFirstname(), patient.getLastname(), age));
        tvAddress.setText(String.format(Locale.ROOT, "%s, %s", patient.getCityvillage(), patient.getStateprovince()));
        if(TextUtils.isEmpty(patient.getPhonenumber())) {
            tvMobile.setVisibility(View.GONE);
        } else {
            tvMobile.setText(patient.getPhonenumber());
        }

        ivProfile = findViewById(R.id.profile_photo);
        findViewById(R.id.btn_edit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PICK_PROFILE_PHOTO);
            }
        });

        setProfilePhoto(patient.getPatientPhoto());

        findViewById(R.id.fab_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validations
                if(cbOthers.isChecked() && TextUtils.isEmpty(etOthers.getText())) {
                    etOthers.setError(getString(R.string.error_field_required));
                    etOthers.requestFocus();
                    Toast.makeText(ProfileActivity.this, R.string.please_fill_your_details, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cbOthersFamily.isChecked() && TextUtils.isEmpty(etOthersFamily.getText())) {
                    etOthersFamily.setError(getString(R.string.error_field_required));
                    etOthers.requestFocus();
                    Toast.makeText(ProfileActivity.this, R.string.please_fill_your_details, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rgPostCovidConsulting.getCheckedRadioButtonId() == R.id.rbPostCovidConsultingYes && TextUtils.isEmpty(et_Complaint.getText())) {
                    et_Complaint.setError(getString(R.string.error_field_required));
                    et_Complaint.requestFocus();
                    Toast.makeText(ProfileActivity.this, R.string.please_fill_your_details, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!NetworkConnection.isOnline(context)) {
                    Toast.makeText(ProfileActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
                    return;
                }

                updateProfile();
            }
        });

        setData();
    }

    private void updateProfile() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            //updateImageDatabase(TYPE_PROFILE, StringUtils.getFileNameWithoutExtension(photo));
            insertPatientProfileImages(mCurrentPhotoPath, sessionManager.getPersionUUID());
            patientProfileImagesPush();
        }

        if (documentObjects.size() > 0) {
            for (DocumentObject additionalImage : documentObjects) {
                if (additionalImage.isNew)
                    updateImageDatabase(StringUtils.getFileNameWithoutExtension(new File(additionalImage.getDocumentPhoto())));
            }
        }

        setProfileObsData();
    }

    private void setProfilePhoto(String photoPath) {
        if (!TextUtils.isEmpty(photoPath) && new File(photoPath).exists()) {
            Glide.get(this).clearMemory();
            Glide.with(ProfileActivity.this)
                    .load(photoPath)
                    .thumbnail(0.3f)
                    .centerCrop()
                    .circleCrop()
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivProfile);
        } else {
            profilePicDownload(sessionManager.getPersionUUID());
        }
    }

    private ArrayList<File> getDocuments() {
        ImagesDAO imagesDAO = new ImagesDAO();
        ArrayList<String> fileuuidList = new ArrayList<String>();
        ArrayList<File> fileList = new ArrayList<File>();
        try {
            fileuuidList = imagesDAO.getAdditionalDocuments(UuidDictionary.PATIENT_PROFILE_AD);
            for (String fileuuid : fileuuidList) {
                String filename = AppConstants.IMAGE_PATH + fileuuid + ".jpg";
                File file = new File(filename);
                if (file.exists()) {
                    fileList.add(file);
                } else  {
                    try {
                        file.createNewFile();
                        fileList.add(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (DAOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    private int getAge(String dobString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            date = sdf.parse(dobString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadDocument(View view) {
        /*Intent cameraIntent = new Intent(this, CameraActivity.class);
        String imageName = UUID.randomUUID().toString();
        cameraIntent.putExtra(CameraActivity.SET_IMAGE_NAME, imageName);
        cameraIntent.putExtra(CameraActivity.SET_IMAGE_PATH, AppConstants.IMAGE_PATH);
        startActivityForResult(cameraIntent, CameraActivity.TAKE_IMAGE);*/
        selectImage(PICK_ADDITIONAL_DOC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_ADDITIONAL_DOC) {
            if (resultCode == RESULT_OK) {
                String mCurrentPhotoPath = data.getStringExtra("RESULT");
                if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                    mCurrentPhotoPath = getImagePathFromData(data);
                    String finalImageName = UUID.randomUUID().toString();
                    final String finalFilePath = AppConstants.IMAGE_PATH + finalImageName + ".jpg";
                    BitmapUtils.copyFile(mCurrentPhotoPath, finalFilePath);
                    compressImageAndSave(finalFilePath);
                    mCurrentPhotoPath = finalFilePath;
                } else {
                    saveImage(mCurrentPhotoPath);
                }
                File photo = new File(mCurrentPhotoPath);
                if (photo.exists()) {
                    DocumentObject doc = new DocumentObject(photo.getName(), mCurrentPhotoPath);
                    doc.isNew = true;
                    recyclerViewAdapter.add(doc);
                }
            }
        } else if (requestCode == PICK_PROFILE_PHOTO) {
            if (resultCode == RESULT_OK) {
                mCurrentPhotoPath = data.getStringExtra("RESULT");
                if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                    mCurrentPhotoPath = getImagePathFromData(data);
                    final String finalFilePath = AppConstants.IMAGE_PATH + sessionManager.getPersionUUID() + ".jpg";
                    BitmapUtils.copyFile(mCurrentPhotoPath, finalFilePath);
                    compressImageAndSave(finalFilePath);
                    mCurrentPhotoPath = finalFilePath;
                }
                File photo = new File(mCurrentPhotoPath);
                if (photo.exists()) {
                    setProfilePhoto(mCurrentPhotoPath);
                }
            }
        }
    }

    private String getImagePathFromData(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        //Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        Log.v("path", picturePath + "");
        return picturePath;
    }

    static class AdditionalDocumentAdapter extends RecyclerView.Adapter<AdditionalDocumentViewHolder> {

        int screen_height;
        int screen_width;

        private List<DocumentObject> documentList = new ArrayList<>();
        private Context context;
        private String filePath;
        ImagesDAO imagesDAO = new ImagesDAO();
        private SessionManager sessionManager;
        private final String TAG = org.intelehealth.swasthyasamparkp.activities.additionalDocumentsActivity.AdditionalDocumentAdapter.class.getSimpleName();

        public AdditionalDocumentAdapter(Context context, List<DocumentObject> documentList, String filePath) {
            this.documentList = documentList;
            this.context = context;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screen_height = displayMetrics.heightPixels;
            screen_width = displayMetrics.widthPixels;
            this.filePath = filePath;
            sessionManager = new SessionManager(context);
        }

        @Override
        public AdditionalDocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_additional_doc, null);
            AdditionalDocumentViewHolder rcv = new AdditionalDocumentViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final AdditionalDocumentViewHolder holder, final int position) {

            DocumentObject documentObject = documentList.get(position);
            holder.getDocumentNameTextView().setText(documentObject.getDocumentName());

            final File image = new File(documentObject.getDocumentPhoto());
            String url = new UrlModifiers().obsImageUrl(StringUtils.getFileNameWithoutExtension(image));
            if (documentObject.isNew) {
                Glide.with(context)
                        .load(image)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .thumbnail(0.1f)
                        .into(holder.getDocumentPhotoImageView());
            } else {
                GlideUrl glideUrl = new GlideUrl(url,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Basic " + sessionManager.getEncoded())
                                .build());
                Glide.with(context)
                        .load(glideUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .thumbnail(0.1f)
                        .into(holder.getDocumentPhotoImageView());
            }

            holder.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayImage(image, url);
                }
            });

            if (documentObject.isNew) {
                holder.getDeleteDocumentImageView().setVisibility(View.VISIBLE);
            } else {
                holder.getDeleteDocumentImageView().setVisibility(View.GONE);
            }

            holder.getDeleteDocumentImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (image.exists()) image.delete();
                    documentList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, documentList.size());
                    String imageName = holder.getDocumentNameTextView().getText().toString();

                    try {
                        imagesDAO.deleteImageFromDatabase(StringUtils.getFileNameWithoutExtensionString(imageName));
                    } catch (DAOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
            });
        }

        public void add(DocumentObject doc) {
            boolean bool = documentList.add(doc);
            if (bool) Log.d(TAG, "add: Item added to list");
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return this.documentList.size();
        }


        public void displayImage(final File file, String url) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);


            final AlertDialog dialog = builder.create();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.image_confirmation_dialog, null);
            dialog.setView(dialogLayout);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    ImageView imageView = dialog.findViewById(R.id.confirmationImageView);
                    final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                    if (imageView != null) {
                        if (file.exists() && file.length() > 0) {
                            Glide.with(context)
                                    .load(file)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .thumbnail(0.1f)
                                    .into(imageView);
                        } else {
                            GlideUrl glideUrl = new GlideUrl(url,
                                    new LazyHeaders.Builder()
                                            .addHeader("Authorization", "Basic " + sessionManager.getEncoded())
                                            .build());
                            Glide.with(context)
                                    .load(glideUrl)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            if (progressBar != null) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            if (progressBar != null) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            return false;
                                        }
                                    })
                                    .override(screen_width, screen_height)
                                    .into(imageView);
                        }
                    }
                }
            });

            dialog.show();

        }
    }

    private void selectImage(int requestCode) {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.additional_doc_image_picker_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent cameraIntent = new Intent(ProfileActivity.this, CameraActivity.class);
                    String imageName = UUID.randomUUID().toString();
                    if (requestCode == PICK_PROFILE_PHOTO) {
                        imageName = sessionManager.getPersionUUID();
                    }
                    cameraIntent.putExtra(CameraActivity.SET_IMAGE_NAME, imageName);
                    cameraIntent.putExtra(CameraActivity.SET_IMAGE_PATH, AppConstants.IMAGE_PATH);
                    startActivityForResult(cameraIntent, requestCode);

                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, requestCode);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public boolean updatePatientProfileImages(String imagepath, String patientuuid) throws DAOException {
        boolean isUpdated = false;
        long isupdate = 0;
        SQLiteDatabase localdb = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        localdb.beginTransaction();
        ContentValues contentValues = new ContentValues();
        String whereclause = "patientuuid = ? AND image_type = ?";
        try {
            contentValues.put("patientuuid", patientuuid);
            contentValues.put("image_path", imagepath);
            contentValues.put("obs_time_date", AppConstants.dateAndTimeUtils.currentDateTime());
            contentValues.put("sync", "false");
            isupdate = localdb.update("tbl_image_records", contentValues, whereclause, new String[]{patientuuid, "PP"});
            if (isupdate != 0)
                isUpdated = true;
            localdb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            isUpdated = false;
            throw new DAOException(e);
        } finally {
            localdb.endTransaction();

        }
        if (isupdate == 0)
            isUpdated = insertPatientProfileImages(imagepath, patientuuid);
        return isUpdated;
    }

    public boolean insertPatientProfileImages(String imagepath, String patientUuid) {
        boolean isInserted = false;
        SQLiteDatabase localdb = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        localdb.beginTransaction();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put("uuid", patientUuid);
            contentValues.put("patientuuid", patientUuid);
            contentValues.put("visituuid", "");
            contentValues.put("image_path", imagepath);
            contentValues.put("image_type", "PP");
            contentValues.put("obs_time_date", AppConstants.dateAndTimeUtils.currentDateTime());
            contentValues.put("sync", "false");
            localdb.insertWithOnConflict("tbl_image_records", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            isInserted = true;
            localdb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            isInserted = false;
//            throw new DAOException(e);
        } finally {
            localdb.endTransaction();

        }
        return isInserted;
    }


    public boolean patientProfileImagesPush() {
        String encoded = sessionManager.getEncoded();
        Gson gson = new Gson();
        UrlModifiers urlModifiers = new UrlModifiers();
        ImagesDAO imagesDAO = new ImagesDAO();
        String url = urlModifiers.setPatientProfileImageUrl();
        List<PatientProfile> patientProfiles = new ArrayList<>();
        try {
            patientProfiles = imagesDAO.getPatientProfileUnsyncedImages();
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        Logger.logD("url", url);
        for (PatientProfile p : patientProfiles) {
            Single<ResponseBody> personProfilePicUpload = AppConstants.apiInterface.PERSON_PROFILE_PIC_UPLOAD(url, "Basic " + encoded, p);
            personProfilePicUpload.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Logger.logD(ProfileActivity.class.getSimpleName(), "success" + responseBody);
                            try {
                                imagesDAO.updateUnsyncedPatientProfile(p.getPerson(), "PP");
                            } catch (DAOException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                            }
//                            AppConstants.notificationUtils.DownloadDone("Patient Profile", "Uploaded Patient Profile", 4, IntelehealthApplication.getAppContext());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.logD(ProfileActivity.class.getSimpleName(), "Onerror " + e.getMessage());
//                            AppConstants.notificationUtils.DownloadDone("Patient Profile", "Error Uploading Patient Profile", 4, IntelehealthApplication.getAppContext());
                        }
                    });
        }
        /*sessionManager.setPullSyncFinished(true);
        IntelehealthApplication.getAppContext().sendBroadcast(new Intent(AppConstants.SYNC_INTENT_ACTION)
                .putExtra(AppConstants.SYNC_INTENT_DATA_KEY, AppConstants.SYNC_PATIENT_PROFILE_IMAGE_PUSH_DONE));*/
        return true;
    }

    public void profilePicDownload(String patientUuid) {
        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.patientProfileImageUrl(patientUuid);
        Logger.logD(ProfileActivity.class.getSimpleName(), "profileimage url" + url);
        Observable<ResponseBody> profilePicDownload = AppConstants.apiInterface.PERSON_PROFILE_PIC_DOWNLOAD(url, "Basic " + sessionManager.getEncoded());
        profilePicDownload.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody file) {
                        DownloadFilesUtils downloadFilesUtils = new DownloadFilesUtils();
                        downloadFilesUtils.saveToDisk(file, patientUuid);
                        Logger.logD(ProfileActivity.class.getSimpleName(), file.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.logD(ProfileActivity.class.getSimpleName(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {
//                        Logger.logD(ProfileActivity.class.getSimpleName(), "complete" + patient_new.getPatient_photo());
                        PatientsDAO patientsDAO = new PatientsDAO();
                        boolean updated = false;
                        try {
                            updated = patientsDAO.updatePatientPhoto(patientUuid, AppConstants.IMAGE_PATH + patientUuid + ".jpg");
                        } catch (DAOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                        if (updated) {
                            Glide.with(ProfileActivity.this)
                                    .load(AppConstants.IMAGE_PATH + patientUuid + ".jpg")
                                    .thumbnail(0.3f)
                                    .centerCrop()
                                    .circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(ivProfile);
                        }
                        ImagesDAO imagesDAO = new ImagesDAO();
                        boolean isImageDownloaded = false;
                        try {
                            isImageDownloaded = imagesDAO.insertPatientProfileImages(AppConstants.IMAGE_PATH + patientUuid + ".jpg", patientUuid);
                        } catch (DAOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
//                        if (isImageDownloaded)
//                            AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_image_download_notifi), "" + patient_new.getFirst_name() + "" + patient_new.getLast_name() + "'s Image Download Incomplete.", 4, getApplication());
//                        else
//                            AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_image_download_notifi), "" + patient_new.getFirst_name() + "" + patient_new.getLast_name() + "'s Image Download Incomplete.", 4, getApplication());
                    }
                });
    }

    private Handler mBackgroundHandler;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    void compressImageAndSave(final String filePath) {
        getBackgroundHandler().post(new Runnable() {
            @Override
            public void run() {
                boolean flag = BitmapUtils.fileCompressed(filePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (true) {
                            saveImage(filePath);
                        } else
                            Toast.makeText(ProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void saveImage(String picturePath) {
        Log.v("AdditionalDocuments", "picturePath = " + picturePath);
        File photo = new File(picturePath);
        if (photo.exists()) {
            try {

                long length = photo.length();
                length = length / 1024;
                Log.e("------->>>>", length + "");
            } catch (Exception e) {
                System.out.println("File not found : " + e.getMessage() + e);
            }

            String fileNameWithoutExtension = StringUtils.getFileNameWithoutExtension(photo);
//            additionalImages.add(fileNameWithoutExtension);
//            updateImageDatabase(fileNameWithoutExtension);
        }
    }

    private void updateImageDatabase(String imageuuid) {
        ImagesDAO imagesDAO = new ImagesDAO();
        try {
            //obs image... CONCEPT ID FOR PATIENT DOC>>>
            Log.v("HOME", "HOME_UUID: " + sessionManager.getPersionUUID());
            imagesDAO.insertObsImageDatabase(imageuuid, dummyEncounterUuid, UuidDictionary.PATIENT_PROFILE_AD);
            obsPushDTOList = imagesDAO.getObsUnsyncedImages_novisit(sessionManager.getPersionUUID(), dummyEncounterUuid, imageuuid);
            obsImagesPush();
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public boolean obsImagesPush() {

        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String encoded = sessionManager.getEncoded();
        Gson gson = new Gson();
        UrlModifiers urlModifiers = new UrlModifiers();
        ImagesDAO imagesDAO = new ImagesDAO();
        String url = urlModifiers.setObsImageUrl();
        List<ObsPushDTO> obsImageJsons = new ArrayList<>();
        //  try {
        // obsImageJsons = imagesDAO.getObsUnsyncedImages();
        obsImageJsons = obsPushDTOList;
        Log.e(TAG, "image request model_ppp" + gson.toJson(obsImageJsons));
      /*  } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }*/

        int i = 0;
        for (ObsPushDTO p : obsImageJsons) {
            //pass it like this
            File file = null;
            file = new File(AppConstants.IMAGE_PATH + p.getUuid() + ".jpg");
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/json"), file);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Observable<ObsJsonResponse> obsJsonResponseObservable = AppConstants.apiInterface.OBS_JSON_RESPONSE_OBSERVABLE
                    (url, "Basic " + encoded, body, p);
            obsJsonResponseObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<ObsJsonResponse>() {
                        @Override
                        public void onNext(ObsJsonResponse obsJsonResponse) {
                            Logger.logD(TAG, "success" + obsJsonResponse);

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.logD(TAG, "Onerror " + e.getMessage());
                            //                            AppConstants.notificationUtils.DownloadDone("Patient Profile", "Error Uploading Patient Profile", IntelehealthApplication.getAppContext());
                        }

                        @Override
                        public void onComplete() {
                            Logger.logD(TAG, "success");
                            try {
                                imagesDAO.updateUnsyncedObsImages(p.getUuid());
                            } catch (DAOException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                            }
                        }
                    });

            pushProfileDocumentObsData(p);
        }
        sessionManager.setPushSyncFinished(true);
        IntelehealthApplication.getAppContext().sendBroadcast(new Intent(AppConstants.SYNC_INTENT_ACTION)
                .putExtra(AppConstants.SYNC_INTENT_DATA_KEY, AppConstants.SYNC_OBS_IMAGE_PUSH_DONE));
//        AppConstants.notificationUtils.DownloadDone("Patient Profile", "Completed Uploading Patient Profile", 4, IntelehealthApplication.getAppContext());
        return true;
    }

    private boolean setProfileObsData() {
        String value = getObsValue();
        ObsDAO obsDAO = new ObsDAO();
        ObsDTO obsDTO = new ObsDTO();
        obsDTO.setUuid(obsUid);
        obsDTO.setConceptuuid(UuidDictionary.PATIENT_PROFILE_MED_HISTORY);
        obsDTO.setEncounteruuid(dummyEncounterUuid);
        obsDTO.setCreator(sessionManager.getCreatorID());
        obsDTO.setValue(StringUtils.getValue(value));
        boolean isInserted = false;
        try {
            isInserted = obsDAO.insertObsWithUid(obsDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.getPatientProfileHistoryUrl();
        UserProfileData userProfileData = new UserProfileData(sessionManager.getPersionUUID(), sessionManager.getCreatorID(), obsDTO.getConceptuuid(), obsDTO.getValue());
        Single<ResponseDTO> personProfilePicUpload = AppConstants.apiInterface.USER_PROFILE(url, "Basic " + sessionManager.getEncoded(), userProfileData);
        customProgressDialog.show();
        personProfilePicUpload.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ResponseDTO>() {
                    @Override
                    public void onSuccess(ResponseDTO responseBody) {
                        Logger.logD(ProfileActivity.class.getSimpleName(), "success" + responseBody);
                        customProgressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.logD(ProfileActivity.class.getSimpleName(), "Onerror " + e.getMessage());
                        customProgressDialog.dismiss();
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });


        return isInserted;
    }

    private void pushProfileDocumentObsData(ObsPushDTO obsPushDTO) {
        ObsDTO obsDTO = new ObsDTO();
        obsDTO.setUuid(obsPushDTO.getUuid());
        obsDTO.setConceptuuid(obsPushDTO.getConcept());
        obsDTO.setEncounteruuid(obsPushDTO.getEncounter());
        obsDTO.setCreator(sessionManager.getCreatorID());
        obsDTO.setValue("");
        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.getPatientProfileHistoryUrl();
        UserProfileData userProfileData = new UserProfileData(sessionManager.getPersionUUID(), sessionManager.getCreatorID(), obsDTO.getConceptuuid(), obsDTO.getValue());
        Single<ResponseDTO> personProfilePicUpload = AppConstants.apiInterface.USER_PROFILE(url, "Basic " + sessionManager.getEncoded(), userProfileData);
        customProgressDialog.show();
        personProfilePicUpload.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<ResponseDTO>() {
                @Override
                public void onSuccess(ResponseDTO responseBody) {
                    Logger.logD(ProfileActivity.class.getSimpleName(), "success" + responseBody);
                    customProgressDialog.dismiss();
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    Logger.logD(ProfileActivity.class.getSimpleName(), "Onerror " + e.getMessage());
                    customProgressDialog.dismiss();
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }

    private void selectRadioGroup(RadioGroup group, String value) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.getText().equals(value)) {
                childAt.setChecked(true);
                break;
            }
        }
    }

    private void selectBooleanRadioGroup(RadioGroup group, Boolean value) {
        RadioButton childAt = (RadioButton) group.getChildAt(value ? 0 : 1);
        childAt.setChecked(true);
    }

    private void selectCheckbox(List<String> selectionList, CheckBox checkBox) {
        for (String s : selectionList) {
            if (s.contains(checkBox.getText().toString())) {
                checkBox.setChecked(true);
            }
        }
    }

    void setData() {
        try {
            ObsDAO obsDAO = new ObsDAO();
            ObsDTO obsProfileDto = obsDAO.getObsProfileDto(UuidDictionary.PATIENT_PROFILE_MED_HISTORY);
            if (TextUtils.isEmpty(obsProfileDto.getValue()))
                return;
            obsUid = obsProfileDto.getUuid();
            Map<String, Object> map = StringUtils.jsonToMap(StringUtils.jsonToObject(obsProfileDto.getValue()));
            HashMap<String, Object> covidHistory = (HashMap<String, Object>) map.get("covid_history");
            if (covidHistory != null) {
                String date_days_tested_positive = (String) covidHistory.get("date_days_tested_positive");
                String date_of_admission = (String) covidHistory.get("date_of_admission");
                String stay_in_hospital_days = (String) covidHistory.get("stay_in_hospital_days");
                String stay_type = (String) covidHistory.get("stay_type");
                if (TextUtils.isEmpty(et_tested_positive_date.getText())) {
                    et_tested_positive_date.setText(date_days_tested_positive);
                }
                et_admission_date.setText(date_of_admission);
                et_stay_days.setText(stay_in_hospital_days);
                selectRadioGroup(rgStayType, stay_type);
            }

            HashMap<String, Object> pastHistory = (HashMap<String, Object>) map.get("past_history");
            if (pastHistory != null) {
                Boolean taking_medicine = (Boolean) pastHistory.get("taking_medicine");
                Boolean allergies = (Boolean) pastHistory.get("allergies");
                Boolean consume_alcohol = (Boolean) pastHistory.get("consume_alcohol");
                Boolean consume_tobacco = (Boolean) pastHistory.get("consume_tobacco");
                List<String> medical_conditions = (List<String>) pastHistory.get("medical_conditions");
                List<String> family_history_disease = (List<String>) pastHistory.get("family_history_disease");
                selectBooleanRadioGroup(rgMedications, taking_medicine);
                selectBooleanRadioGroup(rgAllergies, allergies);
                selectBooleanRadioGroup(rgAlcohol, consume_alcohol);
                selectBooleanRadioGroup(rgSmokeTobacco, consume_tobacco);

                selectCheckbox(medical_conditions, cbDiabetes);
                selectCheckbox(medical_conditions, cbHypertension);
                selectCheckbox(medical_conditions, cbHeartDiseases);
                selectCheckbox(medical_conditions, cbCancer);
                selectCheckbox(medical_conditions, cbAsthma);
                selectCheckbox(medical_conditions, cbKidneyDisorder);
                selectCheckbox(medical_conditions, cbOthers);
                /*cbOthers.post(new Runnable() {
                    @Override
                    public void run() {*/
                        if (cbOthers.isChecked()) {
                            for (String medical_condition : medical_conditions) {
                                if (medical_condition.contains(cbOthers.getText().toString())) {
                                    etOthers.setText(medical_condition.split("\\|")[1]);
                                    break;
                                }
                            }
                        }
//                    }
//                });

                selectCheckbox(family_history_disease, cbDiabetesFamily);
                selectCheckbox(family_history_disease, cbHypertensionFamily);
                selectCheckbox(family_history_disease, cbHeartDiseasesFamily);
                selectCheckbox(family_history_disease, cbCancerFamily);
                selectCheckbox(family_history_disease, cbAsthmaFamily);
                selectCheckbox(family_history_disease, cbKidneyDisorderFamily);
                selectCheckbox(family_history_disease, cbOthersFamily);
                /*cbOthersFamily.post(new Runnable() {
                    @Override
                    public void run() {*/
                        if (cbOthersFamily.isChecked()) {
                            for (String history : family_history_disease) {
                                if (history.contains(cbOthersFamily.getText().toString())) {
                                    etOthersFamily.setText(history.split("\\|")[1]);
                                    break;
                                }
                            }
                        }
//                    }
//                });
            }

            HashMap<String, Object> postCovidCare = (HashMap<String, Object>) map.get("post_covid_care");
            if (postCovidCare != null) {
                Boolean consulting_doctor = (Boolean) postCovidCare.get("consulting_doctor");
                String complaint = (String) postCovidCare.get("complaint");
                selectBooleanRadioGroup(rgPostCovidConsulting, consulting_doctor);
                if (consulting_doctor != null && consulting_doctor) {
                    et_Complaint.setText(complaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Resources englishResources;
    private String getObsValue() {
        englishResources = StringUtils.getLocalizedResources(this, Locale.ENGLISH);
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> covidHistory = new HashMap<>();
        covidHistory.put("date_days_tested_positive", et_tested_positive_date.getText().toString());
        covidHistory.put("date_of_admission", et_admission_date.getText().toString());
        covidHistory.put("stay_in_hospital_days", et_stay_days.getText().toString());
        RadioButton rbStayType = findViewById(rgStayType.getCheckedRadioButtonId());
        if (rbStayType != null) {
            String text = rbStayType.getText().toString();
            if (rbStayType.getId() == R.id.rbIcu) {
                text = englishResources.getString(R.string.icu);
            } else if (rbStayType.getId() == R.id.rbOxygenTherapy) {
                text = englishResources.getString(R.string.oxygen_therapy);
            } else if (rbStayType.getId() == R.id.rbVentilator) {
                text = englishResources.getString(R.string.ventilator);
            } else if (rbStayType.getId() == R.id.rbWithout_Oxygen_Therapy) {
                text = englishResources.getString(R.string.without_oxygen_therapy);
            }
//            covidHistory.put("stay_type", rbStayType.getText().toString());
            covidHistory.put("stay_type", text);
        }
        data.put("covid_history", covidHistory);

        Map<String, Object> pastHistory = new HashMap<>();
        List<String> medicalConditions = new ArrayList<>(); //will be array of selected checkbox values
        if (cbDiabetes.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.diabetes));
        }
        if (cbHypertension.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.hypertension));
        }
        if (cbHeartDiseases.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.heart_diseases));
        }
        if (cbCancer.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.cancer));
        }
        if (cbAsthma.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.asthma));
        }
        if (cbKidneyDisorder.isChecked()) {
            medicalConditions.add(englishResources.getString(R.string.kidney_disorder));
        }
        if (cbOthers.isChecked()) {
            medicalConditions.add(String.format("%s|%s", englishResources.getString(R.string.others), etOthers.getText().toString()));
        }
        pastHistory.put("medical_conditions", medicalConditions);
        pastHistory.put("taking_medicine", rgMedications.getCheckedRadioButtonId() == R.id.rbMedicationsYes);

        List<String> familyHistoryDisease = new ArrayList<>(); //will be array of selected checkbox values
        if (cbDiabetesFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.diabetes));
        }
        if (cbHypertensionFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.hypertension));
        }
        if (cbHeartDiseasesFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.heart_diseases));
        }
        if (cbCancerFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.cancer));
        }
        if (cbAsthmaFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.asthma));
        }
        if (cbKidneyDisorderFamily.isChecked()) {
            familyHistoryDisease.add(englishResources.getString(R.string.kidney_disorder));
        }
        if (cbOthersFamily.isChecked()) {
            familyHistoryDisease.add(String.format("%s|%s", englishResources.getString(R.string.others), etOthersFamily.getText().toString()));
        }
        pastHistory.put("family_history_disease", familyHistoryDisease);

        pastHistory.put("allergies", rgAllergies.getCheckedRadioButtonId() == R.id.rbAllergiesYes);
        pastHistory.put("consume_alcohol", rgAlcohol.getCheckedRadioButtonId() == R.id.rbAlcoholYes);
        pastHistory.put("consume_tobacco", rgSmokeTobacco.getCheckedRadioButtonId() == R.id.rbSmokeTobaccoYes);
        data.put("past_history", pastHistory);

        Map<String, Object> postCovidCare = new HashMap<>();
        postCovidCare.put("consulting_doctor", rgPostCovidConsulting.getCheckedRadioButtonId() == R.id.rbPostCovidConsultingYes);
        postCovidCare.put("complaint", et_Complaint.getText().toString());
        data.put("post_covid_care", postCovidCare);
        String json = new Gson().toJson(data);
        return json;
    }

    private String getDatefromDays(int dateString) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy",
                Locale.ENGLISH);
        //number of days before date...
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -dateString);
        date = simpleDateFormat.format(calendar.getTime());
        Log.v("time", "todays date: " + date);
        //number of days calculation...

        return date;
    }
}
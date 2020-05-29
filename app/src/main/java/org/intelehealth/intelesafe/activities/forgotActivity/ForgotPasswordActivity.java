package org.intelehealth.intelesafe.activities.forgotActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.intelehealth.intelesafe.R;

import java.util.regex.Pattern;


public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText et_email, et_pass, et_cpass, otp_1, otp_2, otp_3, otp_4;
    Button submit;
    LinearLayout layout1, layout2, layout3;
    int visiblelayout = 1;
    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        String val = ""+((int)(Math.random()*9000)+1000);
        Log.e("value",val);
        initView();
    }

    private void initView() {
        layout1  = (LinearLayout)findViewById(R.id.layout1);
        layout2  = (LinearLayout)findViewById(R.id.layout2);
        layout3  = (LinearLayout)findViewById(R.id.layout3);
        et_email = (EditText)findViewById(R.id.et_email);
        et_pass  = (EditText)findViewById(R.id.et_password);
        et_cpass = (EditText)findViewById(R.id.et_confirm_password);
        otp_1    = (EditText)findViewById(R.id.otp_1);
        otp_2    = (EditText)findViewById(R.id.otp_2);
        otp_3    = (EditText)findViewById(R.id.otp_3);
        otp_4    = (EditText)findViewById(R.id.otp_4);
        submit   = (Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(this);
        otp_1.addTextChangedListener(this);
        otp_2.addTextChangedListener(this);
        otp_3.addTextChangedListener(this);
        otp_4.addTextChangedListener(this);
    }

    private void setVisibility() {
        visiblelayout = visiblelayout+1;
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
      if(visiblelayout==2)
            layout2.setVisibility(View.VISIBLE);
        else if(visiblelayout==3)
            layout3.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                if(visiblelayout == 1) {
                    if (et_email.getText().toString().isEmpty()) {
                        et_email.setError(getString(R.string.error_field_required));
                    } else {
                        if (et_email.getText().toString().trim().matches(emailPattern)) {
                            setVisibility();
                        } else {
                            et_email.setError(getString(R.string.error_invalid_email_address));
                        }
                    }
                }else if(visiblelayout==2) {
                    setVisibility();
                }else if(visiblelayout==3){
                    String password  = et_pass.getText().toString();
                    String cPassword = et_cpass.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        et_pass.setError(getString(R.string.error_field_required));
                        et_pass.requestFocus();
                        return;
                    }

                    if (password.length() < 8) {
                        et_pass.setError(getString(R.string.password_must_eight));
                        et_pass.requestFocus();
                        return;
                    }

                    if (!UpperCasePatten.matcher(password).find()) {
                        et_pass.setError(getString(R.string.upper_case_validation));
                        et_pass.requestFocus();
                        return;
                    }
                    if (!lowerCasePatten.matcher(password).find()) {
                        et_pass.setError(getString(R.string.password_validation_two));
                        et_pass.requestFocus();
                        return;
                    }
                    if (!digitCasePatten.matcher(password).find()) {
                        et_pass.setError(getString(R.string.password_validation));
                        et_pass.requestFocus();
                        return;
                    }

                    if (cPassword.equalsIgnoreCase("")) {
                        et_cpass.setError(getString(R.string.error_field_required));
                        et_cpass.requestFocus();
                        return;
                    }

                    if (!cPassword.equals(password)) {
                        et_cpass.setError(getString(R.string.confirm_password_is_mismatched));
                        et_cpass.requestFocus();
                        return;
                    }
                    finish();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditText text = (EditText)getCurrentFocus();
        if (text != null && text.length() > 0)
        {
            View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
            if (next != null)
                next.requestFocus();

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}

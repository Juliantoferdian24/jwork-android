package com.panikga.jwork_android.unused;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.panikga.jwork_android.ui.HomeActivity;
import com.panikga.jwork_android.request.JobBatalRequest;
import com.panikga.jwork_android.request.JobFetchRequest;
import com.panikga.jwork_android.request.JobSelesaiRequest;
import com.panikga.jwork_android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelesaiJobActivity extends AppCompatActivity {
    TextView tvInvoiceId, tvJobseekerName, tvInvoiceDate, tvPaymentType, tvInvoiceStatus, tvReferralCode, tvJobName, tvJobFee, tvTotalFee;
    TextView staticReferralCode, staticJobseeker, staticInvoiceDate, staticJob, staticInvoiceStatus, staticPayType, staticTotalFee;
    Button btnCancel, btnFinish;

    String jobseekerName, jobName, invoiceDate, referralCode, paymentType;
    int jobseekerId, jobFee, totalFee, currentInvoiceId, adminFee, discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_job);

        tvInvoiceId = findViewById(R.id.invoice_id);
        tvJobseekerName = findViewById(R.id.jobseeker_name);
        tvInvoiceDate = findViewById(R.id.invoice_date);
        tvPaymentType = findViewById(R.id.payment_type);
        tvInvoiceStatus = findViewById(R.id.invoice_status);
        tvReferralCode = findViewById(R.id.referral_code);
        tvJobName = findViewById(R.id.job_name);
        tvJobFee = findViewById(R.id.job_fee);
        tvTotalFee = findViewById(R.id.total_fee);
        staticJobseeker = findViewById(R.id.static_jobseeker);
        staticInvoiceDate = findViewById(R.id.st_invoice_date);
        staticPayType = findViewById(R.id.staticPaymentType);
        staticJob = findViewById(R.id.static_job);
        staticInvoiceStatus = findViewById(R.id.static_invoice_status);
        staticReferralCode = findViewById(R.id.static_referral_code);
        staticTotalFee = findViewById(R.id.static_total_fee);

        btnCancel = findViewById(R.id.btnCancel);
        btnFinish = findViewById(R.id.btnFinish);

        tvInvoiceId.setText("There are no ongoing orders");
        tvReferralCode.setVisibility(View.GONE);
        staticReferralCode.setVisibility(View.GONE);
        staticJobseeker.setVisibility(View.GONE);
        staticInvoiceDate.setVisibility(View.GONE);
        staticJob.setVisibility(View.GONE);
        staticInvoiceStatus.setVisibility(View.GONE);
        staticPayType.setVisibility(View.GONE);
        staticTotalFee.setVisibility(View.GONE);
        tvJobseekerName.setVisibility(View.GONE);
        tvInvoiceDate.setVisibility(View.GONE);
        tvPaymentType.setVisibility(View.GONE);
        tvInvoiceStatus.setVisibility(View.GONE);
        tvJobName.setVisibility(View.GONE);
        tvJobFee.setVisibility(View.GONE);
        tvTotalFee.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jobseekerName = extras.getString("jobseekerName");
            jobseekerId = extras.getInt("jobseekerId");
            currentInvoiceId = extras.getInt("currentInvoiceId");
        }

        runOnUiThread(this::fetchJob);

        Log.d("currentInvoiceId", String.valueOf(currentInvoiceId));

        btnCancel.setOnClickListener(view -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.equals(null)) {
                        Toast.makeText(SelesaiJobActivity.this, "This invoice is cancelled", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SelesaiJobActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("jobseekerId", jobseekerId);
                        intent.putExtra("jobseekerName", jobseekerName);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiJobActivity.this);
                    builder.setMessage("Please try again").create().show();
                }
            };

            JobBatalRequest request = new JobBatalRequest(String.valueOf(currentInvoiceId), "Cancelled", responseListener);
            RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
            queue.add(request);
        });

        btnFinish.setOnClickListener(view -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.equals(null)) {
                        Toast.makeText(SelesaiJobActivity.this, "This invoice is finished", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SelesaiJobActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("jobseekerId", jobseekerId);
                        intent.putExtra("jobseekerName", jobseekerName);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiJobActivity.this);
                    builder.setMessage("Operation Failed! Please try again").create().show();
                }
            };
            JobSelesaiRequest request = new JobSelesaiRequest(String.valueOf(currentInvoiceId), "Finished", responseListener);
            RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
            queue.add(request);
        });
    }

    public void fetchJob() {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONArray jsonResponse = new JSONArray(response);
                if (jsonResponse != null) {
                    Toast.makeText(SelesaiJobActivity.this, "Bank Payment", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject invoice = jsonResponse.getJSONObject(i);
                        JSONArray jobs = invoice.getJSONArray("jobs");
                        String invoiceStatus = invoice.getString("invoiceStatus");
                        if (invoiceStatus.equals("OnGoing")) {
                            for (int j = 0; j < jobs.length(); j++) {
                                JSONObject job = jobs.getJSONObject(j);
                                jobName = job.getString("name");
                                jobFee = job.getInt("fee");
                                tvJobName.setText(jobName);
                                tvJobFee.setText("Rp. " + jobFee);
                            }

                            staticJobseeker.setVisibility(View.VISIBLE);
                            staticInvoiceDate.setVisibility(View.VISIBLE);
                            staticJob.setVisibility(View.VISIBLE);
                            staticInvoiceStatus.setVisibility(View.VISIBLE);
                            staticPayType.setVisibility(View.VISIBLE);
                            staticTotalFee.setVisibility(View.VISIBLE);
                            tvJobseekerName.setVisibility(View.VISIBLE);
                            tvInvoiceDate.setVisibility(View.VISIBLE);
                            tvPaymentType.setVisibility(View.VISIBLE);
                            tvInvoiceStatus.setVisibility(View.VISIBLE);
                            tvJobName.setVisibility(View.VISIBLE);
                            tvJobFee.setVisibility(View.VISIBLE);
                            tvTotalFee.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.VISIBLE);
                            btnFinish.setVisibility(View.VISIBLE);

                            tvInvoiceId.setText("Invoice ID: " + currentInvoiceId);
                            tvJobseekerName.setText(jobseekerName);
                            tvInvoiceStatus.setText(invoiceStatus);

                            invoiceDate = invoice.getString("date");
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                            Date date = inputFormat.parse(invoiceDate);
                            Locale indonesia = new Locale("in", "ID");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", indonesia);
                            invoiceDate = outputFormat.format(date);

                            tvInvoiceDate.setText(invoiceDate);
                            paymentType = invoice.getString("paymentType");
                            tvPaymentType.setText(paymentType);
                            totalFee = invoice.getInt("totalFee");
                            tvTotalFee.setText("Rp. " + totalFee);

                            switch (paymentType) {
                                case "BankPayment":
                                    adminFee = invoice.getInt("adminFee");
                                    tvReferralCode.setVisibility(View.GONE);
                                    staticReferralCode.setVisibility(View.GONE);
                                    break;
                                case "EWalletPayment":
                                    JSONObject referral = invoice.getJSONObject("referral");
                                    referralCode = referral.getString("code");
                                    if (referral.isNull("code")) {
                                        tvReferralCode.setVisibility(View.GONE);
                                        staticReferralCode.setVisibility(View.GONE);
                                    } else {
                                        discount = referral.getInt("discount");
                                        tvReferralCode.setVisibility(View.VISIBLE);
                                        staticReferralCode.setVisibility(View.VISIBLE);
                                        tvReferralCode.setText(referralCode);
                                    }
                                    break;
                            }
                        } else {
                            tvInvoiceId.setText("There are no ongoing orders");
                            staticReferralCode.setVisibility(View.GONE);
                            staticJobseeker.setVisibility(View.GONE);
                            staticInvoiceDate.setVisibility(View.GONE);
                            staticJob.setVisibility(View.GONE);
                            staticInvoiceStatus.setVisibility(View.GONE);
                            staticPayType.setVisibility(View.GONE);
                            staticTotalFee.setVisibility(View.GONE);
                            tvJobseekerName.setVisibility(View.GONE);
                            tvInvoiceDate.setVisibility(View.GONE);
                            tvPaymentType.setVisibility(View.GONE);
                            tvInvoiceStatus.setVisibility(View.GONE);
                            tvReferralCode.setVisibility(View.GONE);
                            tvJobName.setVisibility(View.GONE);
                            tvJobFee.setVisibility(View.GONE);
                            tvTotalFee.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.GONE);
                        }
                    }

                }
            } catch (JSONException | ParseException e) {
                Toast.makeText(SelesaiJobActivity.this, "" + currentInvoiceId, Toast.LENGTH_SHORT).show();
            }
        };

        JobFetchRequest request = new JobFetchRequest(Integer.toString(jobseekerId), responseListener);
        RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
        queue.add(request);
    }

    public void hideDisplay() {
        tvInvoiceId.setText("There are no ongoing orders");
        tvReferralCode.setVisibility(View.GONE);
        staticReferralCode.setVisibility(View.GONE);
        staticJobseeker.setVisibility(View.GONE);
        staticInvoiceDate.setVisibility(View.GONE);
        staticJob.setVisibility(View.GONE);
        staticInvoiceStatus.setVisibility(View.GONE);
        staticPayType.setVisibility(View.GONE);
        staticTotalFee.setVisibility(View.GONE);
        tvJobseekerName.setVisibility(View.GONE);
        tvInvoiceDate.setVisibility(View.GONE);
        tvPaymentType.setVisibility(View.GONE);
        tvInvoiceStatus.setVisibility(View.GONE);
        tvJobName.setVisibility(View.GONE);
        tvJobFee.setVisibility(View.GONE);
        tvTotalFee.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
    }
}
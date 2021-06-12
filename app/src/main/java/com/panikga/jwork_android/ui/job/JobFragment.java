package com.panikga.jwork_android.ui.job;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class JobFragment extends Fragment {

    TextView tvInvoiceId, tvJobseekerName, tvInvoiceDate, tvPaymentType, tvInvoiceStatus, tvReferralCode, tvJobName, tvJobFee, tvTotalFee;
    TextView staticReferralCode, staticJobseeker, staticInvoiceDate, staticJob, staticInvoiceStatus, staticPayType, staticTotalFee;
    Button btnCancel, btnFinish;

    String jobseekerName, jobName, invoiceDate, referralCode, paymentType;
    int jobseekerId, jobFee, totalFee, currentInvoiceId, adminFee, discount;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_job, container, false);
        tvInvoiceId = root.findViewById(R.id.invoice_id);
        tvJobseekerName = root.findViewById(R.id.jobseeker_name);
        tvInvoiceDate = root.findViewById(R.id.invoice_date);
        tvPaymentType = root.findViewById(R.id.payment_type);
        tvInvoiceStatus = root.findViewById(R.id.invoice_status);
        tvReferralCode = root.findViewById(R.id.referral_code);
        tvJobName = root.findViewById(R.id.job_name);
        tvJobFee = root.findViewById(R.id.job_fee);
        tvTotalFee = root.findViewById(R.id.total_fee);
        staticJobseeker = root.findViewById(R.id.static_jobseeker);
        staticInvoiceDate = root.findViewById(R.id.st_invoice_date);
        staticPayType = root.findViewById(R.id.staticPaymentType);
        staticJob = root.findViewById(R.id.static_job);
        staticInvoiceStatus = root.findViewById(R.id.static_invoice_status);
        staticReferralCode = root.findViewById(R.id.static_referral_code);
        staticTotalFee = root.findViewById(R.id.static_total_fee);

        btnCancel = root.findViewById(R.id.btnCancel);
        btnFinish = root.findViewById(R.id.btnFinish);

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

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            jobseekerId = extras.getInt("jobseekerId");
            jobseekerName = extras.getString("jobseekerName");
        }
        getActivity().runOnUiThread(this::fetchJob);

        Log.d("currentInvoiceId", String.valueOf(currentInvoiceId));

        btnCancel.setOnClickListener(view -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.equals(null)) {
                        Toast.makeText(getContext(), "This invoice is cancelled", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("jobseekerId", jobseekerId);
                        intent.putExtra("jobseekerName", jobseekerName);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Please try again").create().show();
                }
            };

            JobBatalRequest request = new JobBatalRequest(String.valueOf(currentInvoiceId), "Cancelled", responseListener);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(request);
        });

        btnFinish.setOnClickListener(view -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.equals(null)) {
                        Toast.makeText(getContext(), "This invoice is finished", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("jobseekerId", jobseekerId);
                        intent.putExtra("jobseekerName", jobseekerName);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Operation Failed! Please try again").create().show();
                }
            };
            JobSelesaiRequest request = new JobSelesaiRequest(String.valueOf(currentInvoiceId), "Finished", responseListener);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(request);
        });

        return root;
    }

    public void fetchJob() {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONArray jsonResponse = new JSONArray(response);
                if (jsonResponse != null) {
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject invoice = jsonResponse.getJSONObject(i);
                        JSONArray jobs = invoice.getJSONArray("jobs");
                        currentInvoiceId = invoice.getInt("id");
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
                Toast.makeText(getContext(), "" + currentInvoiceId, Toast.LENGTH_SHORT).show();
            }
        };

        JobFetchRequest request = new JobFetchRequest(Integer.toString(jobseekerId), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}
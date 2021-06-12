package com.panikga.jwork_android.unused;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.panikga.jwork_android.ui.home.ApplyJobActivity;
import com.panikga.jwork_android.model.Job;
import com.panikga.jwork_android.request.JobFetchRequest;
import com.panikga.jwork_android.model.Location;
import com.panikga.jwork_android.ui.home.HomeListAdapter;
import com.panikga.jwork_android.request.MenuRequest;
import com.panikga.jwork_android.R;
import com.panikga.jwork_android.model.Recruiter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> jobIdList = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMapping = new HashMap<>();


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    Button btnAppliedJob;
    private static int jobseekerId;
    private static String jobseekerName;
    private static int currentInvoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jobseekerId = extras.getInt("jobseekerId");
            jobseekerName = extras.getString("jobseekerName");
        }

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        btnAppliedJob = findViewById(R.id.btnAppliedJob);

        runOnUiThread(() -> refreshList());

        expListView.setOnChildClickListener((expandableListView, view, i, i1, l) -> {
            Intent intent = new Intent(MainActivity.this, ApplyJobActivity.class);
            int jobId = childMapping.get(listRecruiter.get(i)).get(i1).getId();
            String jobName = childMapping.get(listRecruiter.get(i)).get(i1).getName();
            String jobCategory = childMapping.get(listRecruiter.get(i)).get(i1).getCategory();
            int jobFee = childMapping.get(listRecruiter.get(i)).get(i1).getFee();

            intent.putExtra("job_id", jobId);
            intent.putExtra("job_name", jobName);
            intent.putExtra("job_category", jobCategory);
            intent.putExtra("job_fee", jobFee);

            intent.putExtra("jobseekerId", jobseekerId);

            startActivity(intent);
            return true;
        });

        btnAppliedJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchJob();
            }
        });
    }

    protected void refreshList() {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONArray jsonResponse = new JSONArray(response);
                if (jsonResponse != null) {
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject job = jsonResponse.getJSONObject(i);
                        JSONObject recruiter = job.getJSONObject("recruiter");
                        JSONObject location = recruiter.getJSONObject("location");

                        String city = location.getString("city");
                        String province = location.getString("province");
                        String description = location.getString("description");

                        Location location1 = new Location(province, city, description);

                        int recruiterId = recruiter.getInt("id");
                        String recruiterName = recruiter.getString("name");
                        String recruiterEmail = recruiter.getString("email");
                        String recruiterPhoneNumber = recruiter.getString("phoneNumber");

                        Recruiter newRecruiter = new Recruiter(recruiterId, recruiterName, recruiterEmail, recruiterPhoneNumber, location1);
                        if (listRecruiter.size() > 0) {
                            boolean success = true;
                            for (Recruiter rec : listRecruiter)
                                if (rec.getId() == newRecruiter.getId())
                                    success = false;
                            if (success) {
                                listRecruiter.add(newRecruiter);
                            }
                        } else {
                            listRecruiter.add(newRecruiter);
                        }

                        int jobId = job.getInt("id");
                        int jobFee = job.getInt("fee");
                        String jobName = job.getString("name");
                        String jobCategory = job.getString("category");

                        Job newJob = new Job(jobId, jobName, newRecruiter, jobFee, jobCategory);
                        jobIdList.add(newJob);

                        for (Recruiter sel : listRecruiter) {
                            ArrayList<Job> temp = new ArrayList<>();
                            for (Job jobs : jobIdList) {
                                if (jobs.getRecruiter().getName().equals(sel.getName()) || jobs.getRecruiter().getEmail().equals(sel.getEmail()) || jobs.getRecruiter().getPhoneNumber().equals(sel.getPhoneNumber())) {
                                    temp.add(jobs);
                                }
                            }
                            childMapping.put(sel, temp);
                        }
                    }
                    listAdapter = new HomeListAdapter(MainActivity.this, listRecruiter, childMapping);
                    expListView.setAdapter(listAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);
    }

    private void fetchJob() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray != null) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject invoice = jsonArray.getJSONObject(i);
                            currentInvoiceId = invoice.getInt("id");
                        }
                        Intent intent = new Intent(MainActivity.this, SelesaiJobActivity.class);
                        intent.putExtra("jobseekerId", jobseekerId);
                        intent.putExtra("currentInvoiceId", currentInvoiceId);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Invoice is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        };

        JobFetchRequest request = new JobFetchRequest(Integer.toString(jobseekerId), responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

}
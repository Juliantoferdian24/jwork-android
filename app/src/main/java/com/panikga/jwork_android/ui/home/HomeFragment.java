package com.panikga.jwork_android.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.panikga.jwork_android.model.Job;
import com.panikga.jwork_android.model.Location;
import com.panikga.jwork_android.request.MenuRequest;
import com.panikga.jwork_android.R;
import com.panikga.jwork_android.model.Recruiter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> jobIdList = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMapping = new HashMap<>();


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    private static int jobseekerId;
    private static String jobseekerName;

    TextView name;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            jobseekerId = extras.getInt("jobseekerId");
            jobseekerName = extras.getString("jobseekerName");
        }

        expListView = root.findViewById(R.id.lvExp);
        name = root.findViewById(R.id.name);
        name.setText(jobseekerName);

        getActivity().runOnUiThread(() -> refreshList());

        expListView.setOnChildClickListener((expandableListView, view, i, i1, l) -> {
            Intent intent = new Intent(getContext(), ApplyJobActivity.class);
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


        return root;
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
                    listAdapter = new HomeListAdapter(getContext(), listRecruiter, childMapping);
                    expListView.setAdapter(listAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(menuRequest);
    }
}
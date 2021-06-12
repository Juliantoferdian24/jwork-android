package com.panikga.jwork_android.request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class JobFetchRequest extends StringRequest {

    private static final String URL = "http://192.168.1.116:8080/invoice/jobseeker/";
    private final Map<String, String> params;

    public JobFetchRequest(String jobseekerId, Response.Listener<String> listener) {
        super(Method.GET, URL + jobseekerId, listener, null);
        params = new HashMap<>();
        Log.d("", "JobFetchRequest: " + jobseekerId);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

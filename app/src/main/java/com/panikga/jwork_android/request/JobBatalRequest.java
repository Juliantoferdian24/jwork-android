package com.panikga.jwork_android.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class JobBatalRequest extends StringRequest {

    private static final String URL = "http://192.168.1.116:8080/invoice/invoiceStatus/";
    private final Map<String, String> params;

    public JobBatalRequest(String id, String status, Response.Listener<String> listener) {
        super(Method.PUT, URL + id, listener, null);
        params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


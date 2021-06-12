package com.panikga.jwork_android.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MenuRequest extends StringRequest {
    private static final String URL = "http://192.168.1.116:8080/job";
    private final Map<String, String> params;

    public MenuRequest(Response.Listener<String> listener) {
        super(Method.GET, URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}

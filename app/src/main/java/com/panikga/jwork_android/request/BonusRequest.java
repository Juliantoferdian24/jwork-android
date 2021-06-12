package com.panikga.jwork_android.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BonusRequest extends StringRequest {
    private static final String URL = "http://192.168.1.116:8080/bonus/";
    private final Map<String, String> params;

    public BonusRequest(String refCode, Response.Listener<String> listener) {
        super(Method.GET, URL + refCode, listener, null);
        params = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}


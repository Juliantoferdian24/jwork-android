package com.panikga.jwork_android.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ApplyJobRequest extends StringRequest {
    private static final String URL_Ewallet = "http://192.168.1.116:8080/invoice/createEWalletPayment";
    private static final String URL_Bank = "http://192.168.1.116:8080/invoice/createBankPayment";
    private final Map<String, String> params;

    public ApplyJobRequest(String jobList, String jobseekerId, String refferalCode, Response.Listener<String> listener) {
        super(Method.POST, URL_Ewallet, listener, null);
        params = new HashMap<>();
        params.put("jobIdList", jobList);
        params.put("jobseekerId", jobseekerId);
        params.put("referralCode", refferalCode);
    }

    public ApplyJobRequest(String jobList, String jobseekerId, Response.Listener<String> listener) {
        super(Method.POST, URL_Bank, listener, null);
        params = new HashMap<>();
        params.put("jobIdList", jobList);
        params.put("jobseekerId", jobseekerId);
        params.put("adminFee", "5000");
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}


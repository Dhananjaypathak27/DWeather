package com.xparticle.dweather;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VollySinglenton {
    private static VollySinglenton mInstance;
    private RequestQueue mRequestQueue;

    public VollySinglenton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VollySinglenton getInstance(Context context){
        if(mInstance == null){
            mInstance = new VollySinglenton(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

}

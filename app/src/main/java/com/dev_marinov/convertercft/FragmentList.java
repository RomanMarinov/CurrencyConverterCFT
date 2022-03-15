package com.dev_marinov.convertercft;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.AsyncHttpClient;

import cz.msebera.android.httpclient.Header;

public class FragmentList extends Fragment {
// установка ааптера и кнопки отмены

    View frag;
    RecyclerView rvList;
    AdapterList adapterList;
    TextView tvCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frag = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = frag.findViewById(R.id.rvList);
        tvCancel = frag.findViewById(R.id.tvCancel);

        adapterList = new AdapterList(getActivity(), ((MainActivity)getActivity()).arrayList);
        rvList.setAdapter(adapterList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        return frag;
    }
}
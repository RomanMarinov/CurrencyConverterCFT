package com.dev_marinov.convertercft;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterList extends RecyclerView.Adapter<AdapterList.Holder> {
    Context context;
    ArrayList<ObjectListValute> arrayList;

    Intent intent;

    public AdapterList(Context context, ArrayList<ObjectListValute> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_list, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ObjectListValute objectListValute = arrayList.get(position);

        if(objectListValute != null)
        {
            holder.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_1));
            holder.clCurrencyMain.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_1));
            holder.tvCurrency.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_1));
            holder.tvKeyValuteName.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_1));

            holder.tvCurrency.setText(objectListValute.nameValute);
            holder.tvKeyValuteName.setText(objectListValute.keyValuteName);
        }
        // клик на любую валюту, передача ее во fragmentCalc и переход назад
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).flipCard("backStack");
                holder.cardView.setBackgroundResource(R.drawable.button_turn_off);
                intent = new Intent(FragmentCalc.FIRST_ACTION);
                intent.putExtra("KeyValuteName", holder.tvKeyValuteName.getText().toString());
                context.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e("3333", "-arrayList.size()-" + arrayList.size());
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        CardView cardView;
        ConstraintLayout clCurrencyMain;
        TextView tvCurrency, tvKeyValuteName;

        public Holder(View view) {
            super(view);
            clCurrencyMain = view.findViewById(R.id.clCurrencyMain);
            tvCurrency = view.findViewById(R.id.tvCurrency);
            tvKeyValuteName = view.findViewById(R.id.tvKeyValuteName);
            cardView = view.findViewById(R.id.cardView);
        }
    }

}

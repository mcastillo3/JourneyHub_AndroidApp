package com.android.mauro_castillo_d424_capstone.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> mExcursions;
    private final Context CONTEXT;
    private final LayoutInflater INFLATER;

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionItemView;
        private final TextView excursionItemView2;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textView2);
            excursionItemView2 = itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Excursion current = mExcursions.get(position);
                    Intent intent = new Intent(CONTEXT, ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionId());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("date", current.getExcursionDate());
                    intent.putExtra("vacID", current.getVacationId());
                    CONTEXT.startActivity(intent);
                }
            });
        }
    }

    public ExcursionAdapter(Context CONTEXT) {
        INFLATER = LayoutInflater.from(CONTEXT);
        this.CONTEXT = CONTEXT;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = INFLATER.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            String name = current.getExcursionName();
            int vacID = current.getVacationId();
            holder.excursionItemView.setText(name);
            holder.excursionItemView2.setText(Integer.toString(vacID));
        } else {
            holder.excursionItemView.setText("No excursion name");
            holder.excursionItemView.setText("No vacation id");
        }
    }

    @Override
    public int getItemCount() {
        if (mExcursions != null) {
            return mExcursions.size();
        } else return 0;
    }

    public void setExcursions (List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }
}

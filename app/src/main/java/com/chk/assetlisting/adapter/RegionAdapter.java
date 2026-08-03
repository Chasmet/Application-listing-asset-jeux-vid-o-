package com.chk.assetlisting.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chk.assetlisting.R;
import com.chk.assetlisting.model.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    public interface Listener {
        void onClick(Region region);
        void onLongClick(Region region);
    }

    private final Listener listener;
    private final List<Region> items = new ArrayList<>();

    public RegionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Region> regions) {
        items.clear();
        if (regions != null) items.addAll(regions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_region, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Region region = items.get(position);
        holder.name.setText(region.getName());
        holder.type.setText(TextUtils.isEmpty(region.getType()) ? "Région" : region.getType());
        holder.stats.setText(region.getIntegratedCount() + " / " + region.getAssetCount() + " intégrés");

        holder.itemView.setOnClickListener(v -> listener.onClick(region));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(region);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView type;
        final TextView stats;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            type = itemView.findViewById(R.id.textType);
            stats = itemView.findViewById(R.id.textStats);
        }
    }
}

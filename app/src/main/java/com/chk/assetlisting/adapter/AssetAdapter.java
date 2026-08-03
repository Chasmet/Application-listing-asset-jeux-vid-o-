package com.chk.assetlisting.adapter;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.chk.assetlisting.R;
import com.chk.assetlisting.model.Asset;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {
    public interface Listener { void onClick(Asset asset); void onLongClick(Asset asset); void onValidate(Asset asset); }
    private final Listener listener; private final List<Asset> items = new ArrayList<>();
    public AssetAdapter(Listener listener) { this.listener = listener; }
    public void submitList(List<Asset> assets) { items.clear(); if (assets != null) items.addAll(assets); notifyDataSetChanged(); }
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false)); }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asset asset=items.get(position); holder.name.setText(asset.getName()); String category=TextUtils.isEmpty(asset.getCategory())?"Autre":asset.getCategory(); String region=TextUtils.isEmpty(asset.getRegionName())?"Sans région":asset.getRegionName(); holder.meta.setText(category+" · "+region); bindStatus(holder, asset.getStatus()); holder.image.setImageResource(R.drawable.ic_image);
        if (!TextUtils.isEmpty(asset.getMainImageUri())) try { holder.image.setImageURI(Uri.parse(asset.getMainImageUri())); } catch (Exception ignored) { holder.image.setImageResource(R.drawable.ic_image); }
        int tint=asset.getStatus()==Asset.STATUS_INTEGRATED?ContextCompat.getColor(holder.itemView.getContext(),R.color.primary_dark):ContextCompat.getColor(holder.itemView.getContext(),R.color.primary); holder.validate.setBackgroundTintList(ColorStateList.valueOf(tint)); holder.itemView.setOnClickListener(v->listener.onClick(asset)); holder.itemView.setOnLongClickListener(v->{listener.onLongClick(asset);return true;}); holder.validate.setOnClickListener(v->listener.onValidate(asset));
    }
    private void bindStatus(ViewHolder holder,int status){ if(status==Asset.STATUS_INTEGRATED){holder.status.setText("✓ INTÉGRÉ");holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.status_integrated));holder.status.setBackgroundResource(R.drawable.bg_status_integrated);}else if(status==Asset.STATUS_CREATED){holder.status.setText("ASSET CRÉÉ");holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.status_created));holder.status.setBackgroundResource(R.drawable.bg_status_created);}else{holder.status.setText("À CRÉER");holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.text_secondary));holder.status.setBackgroundResource(R.drawable.bg_status_todo);}}
    @Override public int getItemCount(){return items.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{final ImageView image;final TextView name;final TextView meta;final TextView status;final FloatingActionButton validate;ViewHolder(@NonNull View itemView){super(itemView);image=itemView.findViewById(R.id.imageAsset);name=itemView.findViewById(R.id.textName);meta=itemView.findViewById(R.id.textMeta);status=itemView.findViewById(R.id.textStatus);validate=itemView.findViewById(R.id.buttonValidate);}}
}

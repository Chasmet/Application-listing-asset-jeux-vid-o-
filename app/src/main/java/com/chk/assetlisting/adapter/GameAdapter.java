package com.chk.assetlisting.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chk.assetlisting.R;
import com.chk.assetlisting.model.Game;
import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    public interface Listener { void onClick(Game game); void onLongClick(Game game); }
    private final Listener listener;
    private final List<Game> items = new ArrayList<>();
    public GameAdapter(Listener listener) { this.listener = listener; }
    public void submitList(List<Game> games) { items.clear(); if (games != null) items.addAll(games); notifyDataSetChanged(); }
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false)); }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = items.get(position);
        holder.name.setText(game.getName());
        holder.description.setText(TextUtils.isEmpty(game.getDescription()) ? "Aucune description" : game.getDescription());
        holder.stats.setText(game.getIntegratedCount() + " / " + game.getAssetCount() + " assets intégrés · " + game.getProgressPercent() + "%");
        holder.progress.setProgress(game.getProgressPercent());
        holder.cover.setImageResource(R.drawable.ic_image);
        if (!TextUtils.isEmpty(game.getCoverUri())) try { holder.cover.setImageURI(Uri.parse(game.getCoverUri())); } catch (Exception ignored) { holder.cover.setImageResource(R.drawable.ic_image); }
        holder.itemView.setOnClickListener(v -> listener.onClick(game));
        holder.itemView.setOnLongClickListener(v -> { listener.onLongClick(game); return true; });
    }
    @Override public int getItemCount() { return items.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView cover; final TextView name; final TextView description; final TextView stats; final ProgressBar progress;
        ViewHolder(@NonNull View itemView) { super(itemView); cover=itemView.findViewById(R.id.imageCover); name=itemView.findViewById(R.id.textName); description=itemView.findViewById(R.id.textDescription); stats=itemView.findViewById(R.id.textStats); progress=itemView.findViewById(R.id.progress); }
    }
}

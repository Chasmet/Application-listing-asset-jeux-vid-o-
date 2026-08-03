package com.chk.assetlisting.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chk.assetlisting.R;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
    private final List<String> uris;
    public ImagePreviewAdapter(List<String> uris){this.uris=uris;}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview,parent,false));}
    @Override public void onBindViewHolder(@NonNull ViewHolder holder,int position){String uri=uris.get(position);holder.image.setImageResource(R.drawable.ic_image);try{holder.image.setImageURI(Uri.parse(uri));}catch(Exception ignored){holder.image.setImageResource(R.drawable.ic_image);}holder.remove.setOnClickListener(v->{int p=holder.getBindingAdapterPosition();if(p!=RecyclerView.NO_POSITION){uris.remove(p);notifyItemRemoved(p);}});}
    @Override public int getItemCount(){return uris.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{final ImageView image;final TextView remove;ViewHolder(@NonNull View itemView){super(itemView);image=itemView.findViewById(R.id.imagePreview);remove=itemView.findViewById(R.id.buttonRemove);}}
}

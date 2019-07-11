package com.fudicia.usupply1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapterFavourites extends RecyclerView.Adapter<ImageAdapterFavourites.ImageViewHolder> {

    private Context mContext;
    private List<UploadAnnons> mUploads;
    private OnItemClickListener mListener;
    private UploadAnnons uploadCurrent;

    public ImageAdapterFavourites(Context context, List<UploadAnnons> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageAdapterFavourites.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.ad_item, viewGroup, false);

        return new ImageAdapterFavourites.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapterFavourites.ImageViewHolder imageViewHolder, int i) {
        uploadCurrent = mUploads.get(i);
        imageViewHolder.textViewTitle.setText(uploadCurrent.getProgram());

        imageViewHolder.textViewChatterCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chatters, 0, 0, 0);
        imageViewHolder.textViewChatterCount.setText(uploadCurrent.getChatters());

        String time_channel = "Kl. " + uploadCurrent.getTime() + "   " + uploadCurrent.getChannel();
        imageViewHolder.textViewTime.setText(time_channel);

        if (!uploadCurrent.getUrl().equals("")) {
            Picasso.with(mContext)
                    .load(uploadCurrent.getUrl())
                    .fit()
                    .centerCrop()
                    .into(imageViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewTitle;
        TextView textViewTime;
        public TextView textViewChatterCount;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewTime = itemView.findViewById(R.id.text_view_timestamp);
            textViewChatterCount = itemView.findViewById(R.id.text_view_chatters);

            imageView = itemView.findViewById(R.id.image_view_upload);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Välj ett alternativ");
            MenuItem favo = menu.add(Menu.NONE, 1, 1, "Ta bort från bokmärken");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Radera chatt");
            favo.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (mListener != null) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:

                            String chatId = mUploads.get(position).getChatId();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

                            FavouritesFragment.updateData2 = true;
                            ref.child(chatId).child("saved").child(user.getUid()).removeValue();
                            notifyDataSetChanged();

                            Toast.makeText(mContext, "Chatten borttagen från bokmärken", Toast.LENGTH_SHORT).show();

                            return true;
                        case 2:
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String creatorId = mUploads.get(position).getCreator();
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Admin");
                            String adminId = ref2.child("W6gVK2tXu8ge2c90taVyghvY6WX2").getKey();
                            String adminId2 = ref2.child("VIqpArQKBAdzfCQ5sa7Ezt6pKiU2").getKey();

                            if(userId.equals(creatorId) || userId.equals(adminId) || userId.equals(adminId2)) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Radera chatt")
                                        .setMessage("Är du säker på att du vill radera denna chatt? Detta besult går inte ångra.")
                                        .setPositiveButton("Ja radera chatt", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("Chats");
                                                String chatId0 = mUploads.get(position).getChatId();

                                                FavouritesFragment.updateData2 = true;
                                                ref0.child(chatId0).removeValue();

                                                //mUploads.remove(position);
                                                //notifyItemRemoved(position);

                                                Toast.makeText(mContext, "Chatten raderad", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Nej", null).show();

                            }else{
                                Toast.makeText(mContext, "Du kan bara radera chattar du skapat själv", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
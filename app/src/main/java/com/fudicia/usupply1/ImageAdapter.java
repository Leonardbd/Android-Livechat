package com.fudicia.usupply1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private  List<UploadAnnons> mUploads;
    private OnItemClickListener mListener;
    private UploadAnnons uploadCurrent;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ImageAdapter(Context context, List<UploadAnnons> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == VIEW_TYPE_ITEM){
            final View v = LayoutInflater.from(mContext).inflate(R.layout.ad_item, viewGroup, false);
            return new ImageViewHolder(v);
        }else {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;

            uploadCurrent = mUploads.get(i);
            imageViewHolder.textViewTitle.setText(uploadCurrent.getProgram());

            imageViewHolder.textViewChatterCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chatters, 0, 0, 0);
            imageViewHolder.textViewChatterCount.setText(uploadCurrent.getChatters());

            String time_channel = "Kl. " + uploadCurrent.getTime() + "   " + uploadCurrent.getChannel();
            imageViewHolder.textViewTime.setText(time_channel);

            if(!uploadCurrent.getUrl().equals("")) {
                Picasso.with(mContext)
                        .load(uploadCurrent.getUrl())
                        .fit()
                        .centerCrop()
                        .into(imageViewHolder.imageView);

            }else{
                imageViewHolder.imageView.setImageResource(android.R.color.transparent);
            }
        }else if (viewHolder instanceof  LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @Override
    public int getItemViewType(int i){
        return mUploads.get(i) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
            if(mListener != null)
            {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Välj ett alternativ");
            MenuItem favo = menu.add(Menu.NONE, 1, 1, "Lägg till i bokmärken");
            favo.setOnMenuItemClickListener(this);
            MenuItem hide = menu.add(Menu.NONE, 2, 2, "Dölj från flöde");
            hide.setOnMenuItemClickListener(this);

            if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                MenuItem delete = menu.add(Menu.NONE, 3, 3, "Radera chatt");
                delete.setOnMenuItemClickListener(this);
            }

            MenuItem report = menu.add(Menu.NONE, 4, 4 ,"Anmäl chatt");
            report.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(mListener != null)
            {
                final int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {

                        case 1:
                            String chatId = mUploads.get(position).getChatId();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                                ref.child(chatId).child("saved").child(user.getUid()).setValue(user.getDisplayName());
                                Toast.makeText(mContext, "Chatten tillagd under bokmärken", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "Du måste vara inloggad för att lägga till bokmärken", Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case 2:
                            final FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();

                            if (user2 != null) {
                                if (mUploads.get(position).getCreator().equals(user2.getUid())) {
                                    Toast.makeText(mContext, "Du kan inte dölja en chatt du själv har skapat", Toast.LENGTH_SHORT).show();
                                    return true;
                                } else {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Dölj chatt")
                                            .setMessage("Om du döljer denna chatt kommer du inte längre kunna se den i flödet. Vill du fortsätta?")
                                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String chatId2 = mUploads.get(position).getChatId();

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

                                                    HomeFragment.updateData = true;
                                                    NewHomeFragment.updateData = true;
                                                    ref.child(chatId2).child("hidden").child(user2.getUid()).setValue(user2.getDisplayName());
                                                    //notifyDataSetChanged();

                                                    Toast.makeText(mContext, "Chatten är nu dold", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("Nej", null).show();
                                    return true;
                                }
                            }else{
                                Toast.makeText(mContext, "Du måste logga in för att kunna dölja chatten", Toast.LENGTH_SHORT).show();
                                return true;
                            }

                        case 3:
                            FirebaseUser user0 = FirebaseAuth.getInstance().getCurrentUser();
                            if(user0 != null) {

                                String userId = user0.getUid();
                                String creatorId = mUploads.get(position).getCreator();
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Admin");
                                String adminId = ref2.child("W6gVK2tXu8ge2c90taVyghvY6WX2").getKey();
                                String adminId2 = ref2.child("VIqpArQKBAdzfCQ5sa7Ezt6pKiU2").getKey();

                                if (userId.equals(creatorId) || userId.equals(adminId) || userId.equals(adminId2)) {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Radera chatt")
                                            .setMessage("Är du säker på att du vill radera denna chatt? Detta besult går inte ångra.")
                                            .setPositiveButton("Ja radera chatt", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("Chats");
                                                    String chatId0 = mUploads.get(position).getChatId();

                                                    HomeFragment.updateData = true;
                                                    NewHomeFragment.updateData = true;
                                                    ref0.child(chatId0).removeValue();

                                                    //mUploads.remove(position);

                                                    Toast.makeText(mContext, "Chatten raderad", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("Nej", null).show();

                                } else {
                                    Toast.makeText(mContext, "Du kan bara radera chattar du skapat själv", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(mContext, "Du är inte inloggad", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case 4:
                            Toast.makeText(mContext, "Chatt anmäld", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                }
            }
            return false;
        }
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


}

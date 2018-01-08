package com.drivecom.nearby;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private SparseArray<InternalUserModel> nearbyUsers;
    private Set<String> selectedIds;
    private Integer emptySlots;
    private int highlightedItemsNumber = 0;
    private OnItemHighlightListener onItemHighlightListener;

    public NearbyListAdapter(Context context) {
        this.context = context;
        this.nearbyUsers = new SparseArray<>();
        this.selectedIds = new HashSet<>();
    }

    public NearbyListAdapter(Context context, OnItemHighlightListener onItemHighlightListener, Integer emptySlots) {
        this.context = context;
        this.onItemHighlightListener = onItemHighlightListener;
        this.emptySlots = emptySlots;
        this.nearbyUsers = new SparseArray<>();
        this.selectedIds = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_nearby_list, parent, false);
        return new NearbyListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        setCardViewBackground(holder);
        ((NearbyListItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emptySlots != null) {
                    InternalUserModel userIth = nearbyUsers.get(nearbyUsers.keyAt(position));
                    if (isItemSelected(userIth.getId()) || (!isItemSelected(userIth.getId()) && highlightedItemsNumber < emptySlots)) {
                        updateItem(holder);
                    }
                } else {
                    updateItem(holder);
                }
            }
        });
        ((NearbyListItemViewHolder) holder).userName.setText(nearbyUsers.get(holder.getAdapterPosition()).getName());
        ((NearbyListItemViewHolder) holder).userRegistrationId.setText(nearbyUsers.get(holder.getAdapterPosition()).getRegistrationId());
    }

    private boolean isItemSelected(String id) {
        return selectedIds.contains(id);
    }

    private void updateItem(RecyclerView.ViewHolder holder) {
        String currentItemId = nearbyUsers.get(holder.getAdapterPosition()).getId();
        if (isItemSelected(currentItemId)) {
            selectedIds.remove(currentItemId);
        } else {
            selectedIds.add(currentItemId);
        }
        setCardViewBackground(holder);
        if (emptySlots != null) {
            if (isItemSelected(currentItemId)) {
                highlightedItemsNumber++;
            } else {
                highlightedItemsNumber--;
            }
            onItemHighlightListener.onItemHighlight(emptySlots - highlightedItemsNumber);
        }
    }

    private void setCardViewBackground(RecyclerView.ViewHolder holder) {
        if (isItemSelected(nearbyUsers.get(holder.getAdapterPosition()).getId())) {
            ((NearbyListItemViewHolder) holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            ((NearbyListItemViewHolder) holder).cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardview_dark_background));
        }
    }

    @Override
    public int getItemCount() {
        return nearbyUsers.size();
    }


    public ArrayList<InternalUserModel> getSelectedUsers() {
        ArrayList<InternalUserModel> selectedUsers = new ArrayList<>();
        for (int i = 0; i < nearbyUsers.size(); i++) {
            if (isItemSelected(nearbyUsers.get(i).getId())) {
                selectedUsers.add(nearbyUsers.get(i));
            }
        }
        return selectedUsers;
    }

    public void addItem(int position, InternalUserModel internalUserModel) {
        nearbyUsers.put(position, internalUserModel);
        notifyItemInserted(nearbyUsers.indexOfKey(position));
    }

    public void updateItem(int position, InternalUserModel internalUserModel) {
        nearbyUsers.put(position, internalUserModel);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        nearbyUsers.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAdapters() {
        this.nearbyUsers = new SparseArray<>();
        this.selectedIds = new HashSet<>();
        notifyDataSetChanged();
    }

    public SparseArray<InternalUserModel> getItems() {
        return nearbyUsers;
    }

    public int getItemPositionById(String itemId) {
        for (int i = 0; i < nearbyUsers.size(); i++) {
            int key = nearbyUsers.keyAt(i);
            if (nearbyUsers.get(key) != null && itemId.equals(nearbyUsers.get(key).getId())) {
                return key;
            }
        }
        return -1;
    }

    public interface OnItemHighlightListener {
        void onItemHighlight(int itemsLeft);
    }

    class NearbyListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nearby_item_card_view)
        protected CardView cardView;
        @BindView(R.id.nearby_item_user_name)
        protected TextView userName;
        @BindView(R.id.nearby_item_registration_id)
        protected TextView userRegistrationId;

        public NearbyListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

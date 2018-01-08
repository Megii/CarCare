package com.drivecom.notifications;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.models.GroupModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvitationsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GroupModel> invitations;
    private ArrayList<Boolean> wasClickedList;
    private NotificationsFragmentInterface fragment;

    public InvitationsAdapter(Context context, NotificationsFragmentInterface fragment) {
        this.context = context;
        this.fragment = fragment;
        invitations = new ArrayList<>();
        wasClickedList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return invitations.size();
    }

    @Override
    public GroupModel getItem(int position) {
        return invitations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        InvitationsItemViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_notifications_invitation, parent, false);
            viewHolder = new InvitationsItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (InvitationsItemViewHolder) convertView.getTag();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.onInvitationClicked(position, invitations.get(position));
            }
        });
        viewHolder.nameText.setText(invitations.get(position).name);
        if (wasClickedList.get(position)) {
            viewHolder.nameText.setTextColor(ContextCompat.getColor(context, R.color.textSecondaryDarkBackground));
        } else {
            viewHolder.nameText.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryDarkBackground));
        }
        if (position == invitations.size()) {
            viewHolder.divider.setVisibility(View.GONE);
        } else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    public void addInvitation(GroupModel invitation, boolean wasClicked) {
        invitations.add(0, invitation);
        wasClickedList.add(0, wasClicked);
        notifyDataSetChanged();
    }

    public void setWasItemClicked(int position) {
        wasClickedList.set(position, true);
        notifyDataSetChanged();
    }

    public void deleteInvitation(String groupId) {
        int position = getGroupById(groupId);
        if (position != -1) {
            invitations.remove(position);
            wasClickedList.remove(position);
            notifyDataSetChanged();
        }
    }

    public int getGroupById(String groupId) {
        int position = -1;
        for (int i = 0; i < invitations.size(); i++) {
            if (groupId.equals(invitations.get(i).groupId)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void clearAdapter() {
        wasClickedList = new ArrayList<>();
        invitations = new ArrayList<>();
        notifyDataSetChanged();
    }

    class InvitationsItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_notifications_invitation_name_text)
        protected TextView nameText;
        @BindView(R.id.item_notifications_invitations_divider)
        protected View divider;

        public InvitationsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

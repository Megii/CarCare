package com.drivecom.notifications;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.utils.InternalMessageModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InternalMessageModel> messages;
    private ArrayList<Boolean> wasClickedList;
    private NotificationsFragmentInterface listener;

    public MessagesAdapter(Context context, NotificationsFragmentInterface listener) {
        this.context = context;
        this.listener = listener;
        messages = new ArrayList<>();
        wasClickedList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public InternalMessageModel getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MessagesItemViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_notifications_messages, parent, false);
            viewHolder = new MessagesItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (MessagesItemViewHolder) convertView.getTag();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageClicked(position, messages.get(position).getFrom(), messages.get(position).getUrl());
            }
        });
        viewHolder.nameText.setText(messages.get(position).getFrom());
        if (wasClickedList.get(position)) {
            viewHolder.nameText.setTextColor(ContextCompat.getColor(context, R.color.textSecondaryDarkBackground));
        } else {
            viewHolder.nameText.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryDarkBackground));
        }
        if (position == messages.size()) {
            viewHolder.divider.setVisibility(View.GONE);
        } else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void addMessage(InternalMessageModel message, boolean wasClicked) {
        messages.add(0, message);
        wasClickedList.add(0, wasClicked);
        notifyDataSetChanged();
    }

    public void setWasItemClicked(int position) {
        wasClickedList.set(position, true);
        notifyDataSetChanged();
    }

    public void clearAdapter() {
        wasClickedList = new ArrayList<>();
        messages = new ArrayList<>();
        notifyDataSetChanged();
    }

    class MessagesItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_notifications_messages_name_text)
        protected TextView nameText;
        @BindView(R.id.item_notifications_messages_icon)
        protected ImageView imageView;
        @BindView(R.id.item_notifications_messages_divider)
        protected View divider;

        public MessagesItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

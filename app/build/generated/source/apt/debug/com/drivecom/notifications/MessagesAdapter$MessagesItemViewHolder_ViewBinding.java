// Generated code from Butter Knife. Do not modify!
package com.drivecom.notifications;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MessagesAdapter$MessagesItemViewHolder_ViewBinding implements Unbinder {
  private MessagesAdapter.MessagesItemViewHolder target;

  @UiThread
  public MessagesAdapter$MessagesItemViewHolder_ViewBinding(MessagesAdapter.MessagesItemViewHolder target,
      View source) {
    this.target = target;

    target.nameText = Utils.findRequiredViewAsType(source, R.id.item_notifications_messages_name_text, "field 'nameText'", TextView.class);
    target.imageView = Utils.findRequiredViewAsType(source, R.id.item_notifications_messages_icon, "field 'imageView'", ImageView.class);
    target.divider = Utils.findRequiredView(source, R.id.item_notifications_messages_divider, "field 'divider'");
  }

  @Override
  @CallSuper
  public void unbind() {
    MessagesAdapter.MessagesItemViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.nameText = null;
    target.imageView = null;
    target.divider = null;
  }
}

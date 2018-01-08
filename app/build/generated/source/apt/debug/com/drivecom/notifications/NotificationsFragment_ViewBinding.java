// Generated code from Butter Knife. Do not modify!
package com.drivecom.notifications;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NotificationsFragment_ViewBinding implements Unbinder {
  private NotificationsFragment target;

  @UiThread
  public NotificationsFragment_ViewBinding(NotificationsFragment target, View source) {
    this.target = target;

    target.rootLayout = Utils.findRequiredViewAsType(source, R.id.fragment_notifications_root_layout, "field 'rootLayout'", CoordinatorLayout.class);
    target.invitationsListView = Utils.findRequiredViewAsType(source, R.id.fragment_notifications_invitations_recycler_view, "field 'invitationsListView'", ListView.class);
    target.messagesListView = Utils.findRequiredViewAsType(source, R.id.fragment_notifications_messages_recycler_view, "field 'messagesListView'", ListView.class);
    target.invitationsHeader = Utils.findRequiredView(source, R.id.fragment_notifications_invitations_header, "field 'invitationsHeader'");
    target.messagesHeader = Utils.findRequiredView(source, R.id.fragment_notifications_messages_header, "field 'messagesHeader'");
    target.divider = Utils.findRequiredView(source, R.id.fragment_notifications_divider, "field 'divider'");
    target.emptyMessageText = Utils.findRequiredView(source, R.id.fragment_notifications_empty_message_text, "field 'emptyMessageText'");
  }

  @Override
  @CallSuper
  public void unbind() {
    NotificationsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rootLayout = null;
    target.invitationsListView = null;
    target.messagesListView = null;
    target.invitationsHeader = null;
    target.messagesHeader = null;
    target.divider = null;
    target.emptyMessageText = null;
  }
}

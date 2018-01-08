// Generated code from Butter Knife. Do not modify!
package com.drivecom.notifications;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class InvitationsAdapter$InvitationsItemViewHolder_ViewBinding implements Unbinder {
  private InvitationsAdapter.InvitationsItemViewHolder target;

  @UiThread
  public InvitationsAdapter$InvitationsItemViewHolder_ViewBinding(InvitationsAdapter.InvitationsItemViewHolder target,
      View source) {
    this.target = target;

    target.nameText = Utils.findRequiredViewAsType(source, R.id.item_notifications_invitation_name_text, "field 'nameText'", TextView.class);
    target.divider = Utils.findRequiredView(source, R.id.item_notifications_invitations_divider, "field 'divider'");
  }

  @Override
  @CallSuper
  public void unbind() {
    InvitationsAdapter.InvitationsItemViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.nameText = null;
    target.divider = null;
  }
}

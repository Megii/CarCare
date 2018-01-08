// Generated code from Butter Knife. Do not modify!
package com.drivecom.nearby;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NearbyListAdapter$NearbyListItemViewHolder_ViewBinding implements Unbinder {
  private NearbyListAdapter.NearbyListItemViewHolder target;

  @UiThread
  public NearbyListAdapter$NearbyListItemViewHolder_ViewBinding(NearbyListAdapter.NearbyListItemViewHolder target,
      View source) {
    this.target = target;

    target.cardView = Utils.findRequiredViewAsType(source, R.id.nearby_item_card_view, "field 'cardView'", CardView.class);
    target.userName = Utils.findRequiredViewAsType(source, R.id.nearby_item_user_name, "field 'userName'", TextView.class);
    target.userRegistrationId = Utils.findRequiredViewAsType(source, R.id.nearby_item_registration_id, "field 'userRegistrationId'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NearbyListAdapter.NearbyListItemViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cardView = null;
    target.userName = null;
    target.userRegistrationId = null;
  }
}

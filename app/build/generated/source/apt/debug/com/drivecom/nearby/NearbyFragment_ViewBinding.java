// Generated code from Butter Knife. Do not modify!
package com.drivecom.nearby;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NearbyFragment_ViewBinding implements Unbinder {
  private NearbyFragment target;

  @UiThread
  public NearbyFragment_ViewBinding(NearbyFragment target, View source) {
    this.target = target;

    target.rootLayout = Utils.findRequiredViewAsType(source, R.id.fragment_nearby_root_layout, "field 'rootLayout'", CoordinatorLayout.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.fragment_nearby_recycler_view, "field 'recyclerView'", RecyclerView.class);
    target.fab = Utils.findRequiredViewAsType(source, R.id.fragment_nearby_fab, "field 'fab'", FloatingActionButton.class);
    target.emptyMessage = Utils.findRequiredView(source, R.id.fragment_nearby_empty_message_text, "field 'emptyMessage'");
  }

  @Override
  @CallSuper
  public void unbind() {
    NearbyFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rootLayout = null;
    target.recyclerView = null;
    target.fab = null;
    target.emptyMessage = null;
  }
}

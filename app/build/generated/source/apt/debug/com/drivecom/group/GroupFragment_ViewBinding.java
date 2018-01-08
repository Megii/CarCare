// Generated code from Butter Knife. Do not modify!
package com.drivecom.group;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GroupFragment_ViewBinding implements Unbinder {
  private GroupFragment target;

  @UiThread
  public GroupFragment_ViewBinding(GroupFragment target, View source) {
    this.target = target;

    target.rootLayout = Utils.findRequiredViewAsType(source, R.id.fragment_group_root_layout, "field 'rootLayout'", CoordinatorLayout.class);
    target.fab = Utils.findRequiredView(source, R.id.fragment_group_fab, "field 'fab'");
  }

  @Override
  @CallSuper
  public void unbind() {
    GroupFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rootLayout = null;
    target.fab = null;
  }
}

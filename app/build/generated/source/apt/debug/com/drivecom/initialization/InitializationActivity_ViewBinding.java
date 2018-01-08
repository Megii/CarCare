// Generated code from Butter Knife. Do not modify!
package com.drivecom.initialization;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.drivecom.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class InitializationActivity_ViewBinding implements Unbinder {
  private InitializationActivity target;

  @UiThread
  public InitializationActivity_ViewBinding(InitializationActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public InitializationActivity_ViewBinding(InitializationActivity target, View source) {
    this.target = target;

    target.rootLayout = Utils.findRequiredView(source, R.id.initialization_root_layout, "field 'rootLayout'");
    target.loginLayout = Utils.findRequiredView(source, R.id.initialization_login_layout, "field 'loginLayout'");
    target.registrationLayout = Utils.findRequiredView(source, R.id.initialization_registration_layout, "field 'registrationLayout'");
    target.loginEmailEditText = Utils.findRequiredViewAsType(source, R.id.initialization_login_email_edit_text, "field 'loginEmailEditText'", EditText.class);
    target.loginPasswordEditText = Utils.findRequiredViewAsType(source, R.id.initialization_login_password_edit_text, "field 'loginPasswordEditText'", EditText.class);
    target.loginButton = Utils.findRequiredView(source, R.id.initialization_login_button, "field 'loginButton'");
    target.loginButtonText = Utils.findRequiredViewAsType(source, R.id.initialization_login_button_text, "field 'loginButtonText'", TextView.class);
    target.smallRegisterButton = Utils.findRequiredView(source, R.id.initialization_login_small_register_card_view, "field 'smallRegisterButton'");
    target.registrationScrollView = Utils.findRequiredViewAsType(source, R.id.initialization_registration_scroll_view, "field 'registrationScrollView'", ScrollView.class);
    target.registrationEmailEditText = Utils.findRequiredViewAsType(source, R.id.initialization_registration_email_edit_text, "field 'registrationEmailEditText'", EditText.class);
    target.registrationPasswordEditText = Utils.findRequiredViewAsType(source, R.id.initialization_registration_password_edit_text, "field 'registrationPasswordEditText'", EditText.class);
    target.registrationPasswordConfirmationEditText = Utils.findRequiredViewAsType(source, R.id.initialization_registration_password_confirmation_edit_text, "field 'registrationPasswordConfirmationEditText'", EditText.class);
    target.registrationPasswordConfirmationTextInputLayout = Utils.findRequiredViewAsType(source, R.id.initialization_registration_password_confirmation_text_input, "field 'registrationPasswordConfirmationTextInputLayout'", TextInputLayout.class);
    target.registrationNameEditText = Utils.findRequiredViewAsType(source, R.id.initialization_registration_name_edit_text, "field 'registrationNameEditText'", EditText.class);
    target.registrationNumberEditText = Utils.findRequiredViewAsType(source, R.id.initialization_registration_registration_number_edit_text, "field 'registrationNumberEditText'", EditText.class);
    target.registerButton = Utils.findRequiredView(source, R.id.initialization_register_button, "field 'registerButton'");
    target.registerButtonText = Utils.findRequiredViewAsType(source, R.id.initialization_register_button_text, "field 'registerButtonText'", TextView.class);
    target.smallLoginButton = Utils.findRequiredView(source, R.id.initialization_registration_small_login_card_view, "field 'smallLoginButton'");
    target.progressLayout = Utils.findRequiredView(source, R.id.initialization_progress_layout, "field 'progressLayout'");
    target.progressBar = Utils.findRequiredView(source, R.id.initialization_progress_bar, "field 'progressBar'");
    target.progressText = Utils.findRequiredViewAsType(source, R.id.initialization_progress_text, "field 'progressText'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    InitializationActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rootLayout = null;
    target.loginLayout = null;
    target.registrationLayout = null;
    target.loginEmailEditText = null;
    target.loginPasswordEditText = null;
    target.loginButton = null;
    target.loginButtonText = null;
    target.smallRegisterButton = null;
    target.registrationScrollView = null;
    target.registrationEmailEditText = null;
    target.registrationPasswordEditText = null;
    target.registrationPasswordConfirmationEditText = null;
    target.registrationPasswordConfirmationTextInputLayout = null;
    target.registrationNameEditText = null;
    target.registrationNumberEditText = null;
    target.registerButton = null;
    target.registerButtonText = null;
    target.smallLoginButton = null;
    target.progressLayout = null;
    target.progressBar = null;
    target.progressText = null;
  }
}

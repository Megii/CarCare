package com.drivecom.debug;

import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drivecom.R;
import com.drivecom.models.CoordinatesModel;
import com.drivecom.models.UserModel;
import com.drivecom.models.VoiceMessageModel;
import com.drivecom.remote.UserDataProvider;
import com.drivecom.remote.UserDataListener;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.message.FirebaseMessageManager;
import com.drivecom.remote.message.MessageBroadcastReceiver;
import com.drivecom.remote.message.MessageManager;
import com.drivecom.utils.LocationPointHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class ApiDebugActivity extends AppCompatActivity implements UserDataListener, LocationPointHelper.LocationPointCallback {

    EditText email;
    EditText passwd;
    EditText model;
    EditText numer;
    EditText coords;
    EditText nearby;
    Button button;

    private UserModel myUserModel;
    private FirebaseUserDataProvider firebaseUserDataProvider;
    private MessageManager messageManager;
    private MessageBroadcastReceiver messageReceiver;
    private LocationPointHelper locationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_debug);

        locationHelper = new LocationPointHelper(this);
        setupDatabase();
        setupViews();
        messageReceiver = new MessageBroadcastReceiver() {
            @Override
            public void onMessageReceived(String messageId) {
                log("onMessageReceived: " + messageId);
                messageManager.getMessageById(messageId, new MessageManager.GetMessageCallback() {
                    @Override
                    public void onMessageSuccess(String messageId, VoiceMessageModel message) {
                        log("onMessageSuccess: " + message.url);
                        Toast.makeText(ApiDebugActivity.this, "Message received!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMessageFailure(String messageId, Exception ex) {
                        log("onMessageFailure: " + ex.getMessage());
                    }
                });
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(MessageBroadcastReceiver.MESSAGE_ACTION));
    }

    private void setupViews() {
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.passwd);
        model = findViewById(R.id.model);
        numer = findViewById(R.id.numer);
        coords = findViewById(R.id.coords);
        nearby = findViewById(R.id.nearby);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                if (firebaseUserDataProvider.isUserLoggedIn()) {
                    updateMyUserModel();
                    firebaseUserDataProvider.updateCurrentUserData(myUserModel, ApiDebugActivity.this);
                } else {
                    firebaseUserDataProvider.registerUser(email.getText().toString(), passwd.getText().toString(), ApiDebugActivity.this);
                }*/
                ArrayList<UserModel> to = new ArrayList<>(1);
                to.add(myUserModel);
                new FirebaseMessageManager().sendVoiceMessage(
                        "temp1497366311673.aac",
                        "https://firebasestorage.googleapis.com/v0/b/projektcarcare.appspot.com/o/voice%2Ftemp1497366311673.aac?alt=media&token=e5c39ae3-5c52-441c-8f13-3ce7bf65fcae",
                        to,
                        new MessageManager.SendMessageCallback() {
                            @Override
                            public void onMessageSendingSuccess(String messageId) {
                                Toast.makeText(ApiDebugActivity.this, "Message sent succesfully! " + messageId, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onMessageSendingFailure(Exception ex) {
                                Toast.makeText(ApiDebugActivity.this, "Message sending failure!" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        updateViewState();
    }

    private void updateViewState() {
        if (firebaseUserDataProvider.isUserLoggedIn()) {
            button.setText("Update data");
        } else {
            button.setText("registerUser");
        }
    }

    private void updateMyUserModel() {
        myUserModel.model = model.getText().toString();
        myUserModel.nr = numer.getText().toString();
        myUserModel.coords = getCoordsFromText();
    }

    private CoordinatesModel getCoordsFromText() {
        try {
            String raw = coords.getText().toString();
            String[] split = raw.split(" ");
            CoordinatesModel model = new CoordinatesModel();
            /*model.lat = Double.valueOf(split[0]);
            model.lon = Double.valueOf(split[1]);*/
            return model;
        } catch (Throwable t) {
            return null;
        }
    }

    private void setupDatabase() {
        firebaseUserDataProvider = new FirebaseUserDataProvider();
        if (firebaseUserDataProvider.isUserLoggedIn()) {
            firebaseUserDataProvider.getCurrentUserData(this);
        }
        messageManager = new FirebaseMessageManager();
    }

    private void updateViews() {
        log("updateViews");
        if (myUserModel == null) {
            model.setText("");
            numer.setText("");
            coords.setText("");
            nearby.setText("");
        } else {
            model.setText(myUserModel.model);
            numer.setText(myUserModel.nr);
            try {
                coords.setText(myUserModel.coords.lat + " " + myUserModel.coords.lon);
            } catch (NullPointerException ex) {
                coords.setText("");
            }
            try {
                nearby.setText("" + myUserModel.nearby.size());
            } catch (NullPointerException ex) {
                nearby.setText("");
            }
        }
    }

    @Override
    public void onRegistrationSuccess() {
        log("onRegistrationSuccess");
        myUserModel = new UserModel();
        updateMyUserModel();
        firebaseUserDataProvider.updateCurrentUserData(myUserModel, new UserDataProvider.UpdateUserDataCallback() {
            @Override
            public void onUserDataUpdateSuccess() {
                firebaseUserDataProvider.getCurrentUserData(ApiDebugActivity.this);
                checkForFcmToken();
            }

            @Override
            public void onUserDataUpdateFailure(Exception e) {
                Toast.makeText(ApiDebugActivity.this, "Registration.updateCurrentUserData failure:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRegistrationFailure(Exception e) {
        Toast.makeText(this, "Registration failure:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void checkForFcmToken() {
        log("checkForFcmToken started");
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token == null) {
            log("checkForFcmToken token=null");
        } else if (!token.equals(myUserModel.token)) {
            log("checkForFcmToken token changed");
            myUserModel.token = token;
            firebaseUserDataProvider.updateCurrentUserToken(token);
        }
    }

    @Override
    public void onNearbyElementAdded(String elementKey, UserModel newNearbyUser) {
        Toast.makeText(this, "Nearby added!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNearbyElementChanged(String elementKey, UserModel changedNearbyUser) {
        Toast.makeText(this, "Nearby changed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNearbyElementRemoved(final String elementKey, final String itemId) {
        Toast.makeText(this, "Nearby removed!", Toast.LENGTH_SHORT).show();
    }

    private void log(String message) {
        Log.println(Log.ASSERT, "MainView", message);
    }

    @Override
    public void onLoginSuccess() {
        firebaseUserDataProvider.getCurrentUserData(this);
    }

    @Override
    public void onLoginFailure(Exception e) {
        Toast.makeText(this, "Login failure:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserDataSuccess(String email, UserModel userModel) {
        log("onCurrentUserData");
        this.myUserModel = userModel;
        this.email.setText(email);
        updateViews();
        checkForFcmToken();
        firebaseUserDataProvider.subscribeNearbyChanges(this);
        locationHelper.startListening(5000, this);
    }

    @Override
    public void onUserDataFailure(Exception e) {
        Toast.makeText(this, "UserData failure:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserDataUpdateSuccess() {
        Toast.makeText(this, "Update data success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserDataUpdateFailure(Exception e) {
        Toast.makeText(this, "UpdateData failure:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        locationHelper.stopListening();
        super.onPause();
    }

    @Override
    public void onLocationReceived(Location location) {
/*        Toast.makeText(ApiDebugActivity.this,
                "" + location.getLatitude() + " " + location.getLongitude(),
                Toast.LENGTH_SHORT).show();*/
        firebaseUserDataProvider.updateCurrentUserCoords(new CoordinatesModel(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onLocationError(Throwable t) {
        Toast.makeText(ApiDebugActivity.this,
                "Location error: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }
}

package com.drivecom.remote.message;

import android.support.annotation.NonNull;
import android.util.Log;

import com.drivecom.models.UserModel;
import com.drivecom.models.VoiceMessageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseMessageManager implements MessageManager {
    private final DatabaseReference reference;

    public FirebaseMessageManager() {
        reference = FirebaseDatabase.getInstance().getReference().child("voices");
    }

    @Override
    public void sendVoiceMessage(String audioFilename, String audioUrl, List<UserModel> to, final SendMessageCallback callback) {
        log("sendVoiceMessage started!");
        String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not logged in!");
        }
        VoiceMessageModel messageModel = new VoiceMessageModel(userId, getTokenList(to),
                audioFilename, audioUrl);
        final String messageId = "" + System.currentTimeMillis();
        reference
                .child(messageId)
                .setValue(messageModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            log("sendVoiceMessage success!");
                            callback.onMessageSendingSuccess(messageId);
                        } else {
                            log("sendVoiceMessage failure!");
                            callback.onMessageSendingFailure(task.getException());
                        }
                    }
                });
    }

    private List<String> getTokenList(List<UserModel> users) {
        List<String> tokens = new ArrayList<>();
        for (UserModel model : users) {
            if (model.token != null) {
                tokens.add(model.token);
            }
        }
        return tokens;
    }

    private String getUserId() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public void getMessageById(final String messageId, final GetMessageCallback callback) {
        reference.child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log("getCurrentUserData success!");
                VoiceMessageModel value = dataSnapshot.getValue(VoiceMessageModel.class);
                callback.onMessageSuccess(messageId, value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("getCurrentUserData failed!");
                callback.onMessageFailure(messageId, databaseError.toException());
            }
        });
    }

    private void log(String message) {
        Log.println(Log.ASSERT, "MessageData", message);
    }
}

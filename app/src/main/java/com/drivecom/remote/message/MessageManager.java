package com.drivecom.remote.message;

import com.drivecom.models.UserModel;
import com.drivecom.models.VoiceMessageModel;

import java.util.List;

public interface MessageManager {
    void sendVoiceMessage(String audioFilename, String audioUrl, List<UserModel> to, SendMessageCallback callback);
    void getMessageById(String messageId, GetMessageCallback callback);

    interface SendMessageCallback {
        void onMessageSendingSuccess(String messageId);
        void onMessageSendingFailure(Exception ex);
    }

    interface GetMessageCallback {
        void onMessageSuccess(String messageId, VoiceMessageModel message);
        void onMessageFailure(String messageId, Exception ex);
    }
}

package com.google.firebase.project.blooddonor.messaging;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chat {
    public String senderName;
    public String receiverName;
    public String senderUid;
    public String receiverUid;
    public String message;

    public Chat() {
        // Needed for Firebase
    }

    /*
        public Chat(String name, String message, String uid) {
            mName = name;
            mMessage = message;
            mUid = uid;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public String getUid() {
            return mUid;
        }

        public void setUid(String uid) {
            mUid = uid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Chat chat = (Chat) o;

            return mUid.equals(chat.mUid)
                    && (mName == null ? chat.mName == null : mName.equals(chat.mName))
                    && (mMessage == null ? chat.mMessage == null : mMessage.equals(chat.mMessage));
        }

        @Override
        public int hashCode() {
            int result = mName == null ? 0 : mName.hashCode();
            result = 31 * result + (mMessage == null ? 0 : mMessage.hashCode());
            result = 31 * result + mUid.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Chat{" +
                    "mName='" + mName + '\'' +
                    ", mMessage='" + mMessage + '\'' +
                    ", mUid='" + mUid + '\'' +
                    '}';
        }*/
    public Chat(String senderName, String receiverName, String senderUid, String receiverUid, String message) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

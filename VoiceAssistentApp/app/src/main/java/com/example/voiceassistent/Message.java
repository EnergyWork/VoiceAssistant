package com.example.voiceassistent;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Message implements Parcelable {
    public String text;
    public Date date;
    public Boolean isSend;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YYYY");

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }
    public Message(MessageEntity message) throws ParseException {
        this.text = message.text;
        this.date = (new SimpleDateFormat("HH:mm dd.MM.YYYY")).parse(message.date);
        this.isSend = (message.isSend == 1);
    }
    protected Message(Parcel in) {
        text = in.readString();
        isSend = (in.readByte() == 1);
        try {
            date =  dateFormat.parse(Objects.requireNonNull(in.readString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeByte((byte) (isSend ? 1 : 0));
        dest.writeString(dateFormat.format(date));
    }
}

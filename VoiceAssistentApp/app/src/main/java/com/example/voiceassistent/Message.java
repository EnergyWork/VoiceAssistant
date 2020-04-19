package com.example.voiceassistent;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Message implements Parcelable {
    public String text;
    public Date date;
    public Boolean isSend;
    private DateFormat dateFormat = new SimpleDateFormat("KK:mm", new Locale("rus"));

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }
    protected Message(Parcel in) {
        text = in.readString();
        byte tmpIsSend = in.readByte();
        isSend = tmpIsSend == 1;
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

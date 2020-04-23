package com.example.voiceassistent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected Button sendButton;
    protected EditText questionField;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected MessageListAdapter messageListAdapter;
    protected SharedPreferences sPref;
    public static final String APP_PREFERENCE = "mysettings";
    private boolean isLight = true;
    private String THEME = "THEME";

    DBHelper dbHelper;
    SQLiteDatabase database;

    protected void OnSend() {
        String question = questionField.getText().toString();
        try {
            AI.getAnswer(question, answer -> {
                messageListAdapter.messageList.add(new Message(question, true));
                messageListAdapter.messageList.add(new Message(answer, false));
                messageListAdapter.notifyDataSetChanged();
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
                questionField.setText("");
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);
        if (isLight) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        Log.i("LOG", "onCreate");
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        questionField = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();

        chatMessageList.setLayoutManager((new LinearLayoutManager(this)));
        chatMessageList.setAdapter(messageListAdapter);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_MESSAGES,
                null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int messageIndex = cursor.getColumnIndex(DBHelper.FIELD_MESSAGE);
            int dateIndex    = cursor.getColumnIndex(DBHelper.FIELD_DATE);
            int sendIndex    = cursor.getColumnIndex(DBHelper.FIELD_SEND);
            do {
                MessageEntity messageEntity = new MessageEntity(
                        cursor.getString(messageIndex),
                        cursor.getString(dateIndex),
                        cursor.getInt(sendIndex));
                try {
                    Message message = new Message(messageEntity);
                    messageListAdapter.messageList.add(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        sendButton.setOnClickListener(view -> OnSend());

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(new Locale("ru"));
            }
        });
    }
    //Добавление менюшки
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Обработка нажатий на пункты менюшки
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.day_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isLight = true;
                break;
            case R.id.night_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isLight = false;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Сохрание состояние при повороте
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) messageListAdapter.messageList);
        Log.i("LOG", "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }
    //Восстановление созраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("LOG", "onRestoreInstanceState");
        messageListAdapter.messageList = savedInstanceState.getParcelableArrayList("list");
        messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LOG", "onStop");
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();
        database.delete(DBHelper.TABLE_MESSAGES, null, null);
        for (Message message : messageListAdapter.messageList) {
            MessageEntity messageEntity = new MessageEntity(message);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, messageEntity.text);
            contentValues.put(DBHelper.FIELD_SEND, messageEntity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, messageEntity.date);
            database.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LOG", "onStart");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LOG", "onPause");
    }
    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
        Log.i("LOG", "onDestroy");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("LOG", "onRestart");
    }
}

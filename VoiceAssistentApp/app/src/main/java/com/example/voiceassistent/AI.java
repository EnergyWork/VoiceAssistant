package com.example.voiceassistent;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class AI {
    public static Map<String, String> phrases = new HashMap<String, String>() {{
        put("привет", "Привет!");
        put("как дела", "Всё отлично, как сам?");
        put("чем занимаешься", "Общаюсь");
        put("какой сегодня день", whatDayToday());
        put("который час", whatTime());
        put("какой день недели", whatDayOfWeek());
        put("сколько дней до зачета", whatDaysToTest());
        }};

    static private String whatDayToday() {
        Date curDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(curDate);
    }
    static private String whatTime() {
        Date curDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("KK:mm", Locale.getDefault());
        return timeFormat.format(curDate);
    }
    static private String whatDayOfWeek() {
        Date curDate = new Date();
        Calendar dow = Calendar.getInstance();
        dow.setFirstDayOfWeek(Calendar.MONDAY);
        dow.setTime(curDate);
        int dayOfWeek = dow.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1: return "Воскресенье";
            case 2: return "Понедельник";
            case 3: return "Вторник";
            case 4: return "Среда";
            case 5: return "Четверг";
            case 6: return "Пятница";
            case 7: return "Суббота";
        }
        return "Я не знаю, какой сегодня день";
    }
    static private String whatDaysToTest() {
        Calendar theDay = Calendar.getInstance();
        theDay.set(Calendar.DAY_OF_MONTH, 27);
        theDay.set(Calendar.MONTH, 3);
        theDay.set(Calendar.YEAR, 2020);
        Calendar today = Calendar.getInstance();
        long delta = theDay.getTimeInMillis() - today.getTimeInMillis();
        return (delta / (24 * 60 * 60 * 1000)) + " дней до зачета";
    }
    public static String getGradusEnding(int n) {
        n = (n < 0) ? -n : n;
        return (n % 100 >= 11 && n % 100 <= 19) || n % 10 == 0 || n % 10 > 4 ? "ов" : n % 10 == 1 ? "" : "a";
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    static void getAnswer(String question, final Consumer<String> callback) {
        question = question.toLowerCase();

        //final List<String> ans = phrases.keySet()
        //        .stream()
        //        .filter(question::contains)
        //        .map(t -> phrases.get(t))
        //        .collect(Collectors.toList());
        //Collections.reverse(ans);

        Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(question);
        if (matcher.find()) {
            String cityName = matcher.group(1);
            ForecastToString.getForecast(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    //ans.add(s);
                    //callback.accept(String.join(", ", ans));
                    callback.accept(s);
                }
            });
        }
        for(Map.Entry<String, String> item : phrases.entrySet()) {
            if (question.contains(item.getKey())) {
                //ans.add(item.getValue());
                callback.accept(item.getValue());
            }
        }
        //callback.accept(String.join(", ", ans));
        //callback.accept("Вопрос не поняла...");
    }
}

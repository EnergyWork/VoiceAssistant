package com.example.voiceassistent;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.service.autofill.FieldClassification;

import androidx.annotation.RequiresApi;

import java.io.CharArrayReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
    private static String modify(String date) {
        String[] mas = date.split(" ");
        Matcher matcher = Pattern.compile("0\\d").matcher(mas[0]);
        if (matcher.find()) {
            return date.substring(1);
        } else {
            return date;
        }
    }
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String question) throws ParseException {
        Date tmp;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY", dateFormatSymbols);
        String tmpQuestion = question.replace("праздник ", "");;
        String[] dates = tmpQuestion.split(",");
        for (int i = 0; i < dates.length; i++) {
            if (dates[i].contains("вчера")) {
                calendar.add(Calendar.DAY_OF_YEAR, - 1);
                tmp = calendar.getTime();
                dates[i] = sdf.format(tmp) + ",";
                calendar.add(Calendar.DAY_OF_YEAR, + 1);
            } else if (dates[i].contains("сегодня")) {
                tmp = calendar.getTime();
                dates[i] = sdf.format(tmp) + ",";
            } else if (dates[i].contains("завтра")) {
                calendar.add(Calendar.DAY_OF_YEAR, + 1);
                tmp = calendar.getTime();
                dates[i] = sdf.format(tmp);
                calendar.add(Calendar.DAY_OF_YEAR, - 1);
            } else {
                String pattern = "\\d{1,2}\\.\\d{1,2}\\.\\d{4}";
                Matcher matcher = Pattern.compile(pattern).matcher(dates[i]);
                if (matcher.find()) {
                    dates[i] = sdf.format(
                            Objects.requireNonNull(new SimpleDateFormat("dd.MM.yyyy").
                                    parse(dates[i].substring(matcher.start(), matcher.end()))));
                    dates[i] = modify(dates[i]);
                }
            }
        }
        return String.join(",", dates);
    }
    private static DateFormatSymbols dateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };
    //@RequiresApi(api = Build.VERSION_CODES.N)
    static void getAnswer(String question, final Consumer<String> callback) throws ParseException {
        question = question.toLowerCase();

        if (question.contains("перевод")) {
            final String number = question.replaceAll("[^0-9\\+]", "");
            ConvertNumberToString.getConvertNumber(number, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    callback.accept(s);
                }
            });
        }

        if (question.contains("праздник")) {
            String date = getDate(question);
            /*new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    String result = "";
                    for (String str : strings) {
                        try {
                            result += " " + str + ": " + ParsingHtmlService.getHolyday(str) + "\n";
                        }
                        catch (IOException e) {
                            result += " " + str + "Не могу ответь :(";
                        }
                    }
                    return result;
                }
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    callback.accept(res);
                }
            }.execute(date.split(","));*/

            String[] strings = date.split(",");

            Observable.fromCallable(() -> {
                String result = "";
                for (String str : strings) {
                    try {
                        result += " " + str + ": " + ParsingHtmlService.getHolyday(str) + "\n";
                    } catch (IOException e) {
                        result += " " + str + ": Не могу ответь :(";
                    }
                }
                return result;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((result) -> { callback.accept(result); });
        }

        Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(question);
        if (matcher.find()) {
            String cityName = matcher.group(1);
            ForecastToString.getForecast(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    callback.accept(s);
                }
            });
        }
        for(Map.Entry<String, String> item : phrases.entrySet()) {
            if (question.contains(item.getKey())) {
                callback.accept(item.getValue());
            }
        }
    }
}

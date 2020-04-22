package com.example.voiceassistent;

import android.os.strictmode.ServiceConnectionLeakedViolation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2020";

    public static String getHolyday(String date) throws IOException {
        if (date.equals("")) {
            return "Неверная дата!";
        }
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        Elements div_next_phase = document.select("div.next_phase");
        ListIterator<Element> iter = div_next_phase.listIterator();
        String holidaysStr = "";
        while(iter.hasNext()) {
            String test = ((Element)iter.next().childNode(0)).text();
            if (test.contains(date)) {
                Element el = (Element)iter.previous().childNode(1);
                Elements holidays = el.select("a");
                if (!holidays.isEmpty()) {
                    for (Element holiday : holidays) {
                        holidaysStr += holiday.text() + "; ";
                    }
                }
                return holidaysStr == "" ? "Праздников нет" : holidaysStr;
            }
        }
        return "";
    }
}

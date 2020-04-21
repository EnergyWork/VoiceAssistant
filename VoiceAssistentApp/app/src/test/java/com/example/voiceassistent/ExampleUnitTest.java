package com.example.voiceassistent;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.example.voiceassistent.ParsingHtmlService.getHolyday;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test1() throws IOException {
        String a = ParsingHtmlService.getHolyday("3 января 2020");
        assertEquals(a, "Новогодние каникулы; День рождения соломинки для коктейлей; ");
    }
    @Test
    public void test2() throws ParseException {
        String test = AI.getDate("праздник 21 апреля 2020, 1.1.2020");
        assertEquals(test, "21 апреля 2020, 1 января 2020 ");
    }
}
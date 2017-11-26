package com.forum.harsh.forumpoc;

import org.junit.Test;

import okhttp3.OkHttpClient;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void url_isCorrect() {
        assertEquals("http://toasterjeep.com/api.php?action=attachment-view&attachment_id=1234&visitor_id=3398&token=b409e1b5a2975ac8ded5b622ad85229f79ee99e3",
                OkHttpHelper.createUrl("toasterjeep.com", 3398, "b409e1b5a2975ac8ded5b622ad85229f79ee99e3").toString());
    }
}
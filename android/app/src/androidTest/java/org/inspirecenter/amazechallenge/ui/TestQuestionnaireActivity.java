package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.inspirecenter.amazechallenge.R;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Nearchos Paspallis
 */
@RunWith(AndroidJUnit4.class)
public class TestQuestionnaireActivity {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        final String json = "{ \"hello\": \"world\" }";
        new QuestionnaireActivity.SubmitQuestionnaireAsyncTask(appContext, json).execute();
        assertEquals("", "");
    }
}
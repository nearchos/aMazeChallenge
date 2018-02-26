package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.GsonBuilder;

import org.inspirecenter.amazechallenge.Installation;
import org.inspirecenter.amazechallenge.model.questionnaire.QuestionEntry;
import org.inspirecenter.amazechallenge.model.questionnaire.QuestionnaireEntry;
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

        final QuestionEntry [] questionEntries = new QuestionEntry [] {
                new QuestionEntry("hello", "world"),
                new QuestionEntry("hello2", "world2")
        };
        final QuestionnaireEntry questionnaireEntry = new QuestionnaireEntry(Installation.id(appContext), 0L, questionEntries);
        final String json = new GsonBuilder().setPrettyPrinting().create().toJson(questionnaireEntry);
        System.out.println("json: " + json);
        new QuestionnaireActivity.SubmitQuestionnaireAsyncTask(appContext, json).execute();
        System.out.println("SubmitQuestionnaireAsyncTask...");
        assertEquals("", "");
    }
}
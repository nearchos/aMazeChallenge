package org.inspirecenter.amazechallenge.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.amazechallenge.Installation;
import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.questionnaire.DichotomousResponse;
import org.inspirecenter.amazechallenge.model.questionnaire.LikertResponse;
import org.inspirecenter.amazechallenge.model.questionnaire.QuestionEntry;
import org.inspirecenter.amazechallenge.model.questionnaire.QuestionnaireEntry;

import static org.inspirecenter.amazechallenge.ui.MainActivity.setLanguage;

public class QuestionnaireActivity extends AppCompatActivity {

    //Views:
    private Button skipButton;
    private Button submitButton;

    private RatingBar question_1_ratingBar;

    private RadioButton question2_no;
    private RadioButton question2_maybe;
    private RadioButton question2_yes;

    private RadioButton question3_high;
    private RadioButton question3_medium;
    private RadioButton question3_low;

    private RadioButton question4_veryHigh;
    private RadioButton question4_high;
    private RadioButton question4_medium;
    private RadioButton question4_low;
    private RadioButton question4_veryLow;

    private RadioButton question5_high;
    private RadioButton question5_medium;
    private RadioButton question5_low;

    private RadioButton question6_no;
    private RadioButton question6_notsure;
    private RadioButton question6_yes;

    private RadioButton question7_veryHigh;
    private RadioButton question7_high;
    private RadioButton question7_medium;
    private RadioButton question7_low;
    private RadioButton question7_veryLow;

    private RadioButton question8_no;
    private RadioButton question8_maybe;
    private RadioButton question8_yes;

    private EditText question9_answer;
    private EditText question10_answer;

    //Response values:
    float question1Response = -1;
    DichotomousResponse question2Response = null;
    int question3Response = -1;
    LikertResponse question4Response = null;
    int question5Response = -1;
    DichotomousResponse question6Response = null;
    LikertResponse question7Response = null;
    DichotomousResponse question8Response = null;
    String question9Response = null;
    String question10Response = null;

    @Override
    public void onBackPressed() {
        //DO NOTHING - Do not allow player to return to the game unless they skip or finish the questionnaire.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLanguage(this);
        setContentView(R.layout.activity_questionnaire);

        //Controls:
        question_1_ratingBar = findViewById(R.id.answer_1);
        question2_no = findViewById(R.id.question_2_no);
        question2_maybe = findViewById(R.id.question_2_maybe);
        question2_yes = findViewById(R.id.question_2_yes);
        question3_high = findViewById(R.id.q3_high);
        question3_medium = findViewById(R.id.q3_medium);
        question3_low = findViewById(R.id.q3_low);
        question4_veryHigh = findViewById(R.id.q4_veryhigh);
        question4_high = findViewById(R.id.q4_high);
        question4_medium = findViewById(R.id.q4_medium);
        question4_low = findViewById(R.id.q4_low);
        question4_veryLow = findViewById(R.id.q4_verylow);
        question5_high = findViewById(R.id.q5_high);
        question5_medium = findViewById(R.id.q5_medium);
        question5_low = findViewById(R.id.q5_low);
        question6_no = findViewById(R.id.question_6_no);
        question6_notsure = findViewById(R.id.question_6_notsure);
        question6_yes = findViewById(R.id.question_6_yes);
        question7_veryHigh = findViewById(R.id.q7_veryhigh);
        question7_high = findViewById(R.id.q7_high);
        question7_medium = findViewById(R.id.q7_medium);
        question7_low = findViewById(R.id.q7_low);
        question7_veryLow = findViewById(R.id.q7_verylow);
        question8_no = findViewById(R.id.q8_low);
        question8_maybe = findViewById(R.id.q8_medium);
        question8_yes = findViewById(R.id.q8_high);
        question9_answer = findViewById(R.id.question9_answer);
        question10_answer = findViewById(R.id.question10_answer);

        submitButton = findViewById(R.id.submit_button);
        skipButton = findViewById(R.id.skip_button);


        //--LISTENERS:

        //Q1:
        question_1_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                question1Response = v;
            }
        });

        //Q2:
        question2_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.NO;
            }
        });

        question2_maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.MAYBE;
            }
        });

        question2_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.YES;
            }
        });

        //Q3:
        question3_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question3Response = 10;
            }
        });

        question3_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question3Response = 5;
            }
        });

        question3_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question3Response = 0;
            }
        });

        //Q4:
        question4_veryHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question4Response = LikertResponse.VERY_POSITIVE;
            }
        });

        question4_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question4Response = LikertResponse.POSITIVE;
            }
        });

        question4_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question4Response = LikertResponse.NEUTRAL;
            }
        });

        question4_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question4Response = LikertResponse.NEGATIVE;
            }
        });

        question4_veryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question4Response = LikertResponse.VERY_NEGATIVE;
            }
        });

        //Q5:
        question5_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question5Response = 10;
            }
        });

        question5_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question5Response = 5;
            }
        });

        question5_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question5Response = 0;
            }
        });

        //Q6:
        question6_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.NO;
            }
        });

        question6_notsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.MAYBE;
            }
        });

        question6_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.YES;
            }
        });

        //Q7:
        question7_veryHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question7Response = LikertResponse.VERY_POSITIVE;
            }
        });

        question7_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question7Response = LikertResponse.POSITIVE;
            }
        });

        question7_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question7Response = LikertResponse.NEUTRAL;
            }
        });

        question7_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question7Response = LikertResponse.NEGATIVE;
            }
        });

        question7_veryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question7Response = LikertResponse.VERY_NEGATIVE;
            }
        });

        //Q8:
        question8_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question8Response = DichotomousResponse.NO;
            }
        });

        question8_maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question8Response = DichotomousResponse.MAYBE;
            }
        });

        question8_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question8Response = DichotomousResponse.YES;
            }
        });

        //Q9:
        question9_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) question9Response = null;
                else question9Response = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        //Q10:
        question10_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) question10Response = null;
                else question10Response = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        question_1_ratingBar.setFocusable(true);
        question_1_ratingBar.setFocusableInTouchMode(true);
        question2_yes.setFocusable(true);
        question2_yes.setFocusableInTouchMode(true);
        question3_high.setFocusable(true);
        question3_high.setFocusableInTouchMode(true);
        question4_veryHigh.setFocusable(true);
        question4_veryHigh.setFocusableInTouchMode(true);
        question5_high.setFocusable(true);
        question5_high.setFocusableInTouchMode(true);
        question6_yes.setFocusable(true);
        question6_yes.setFocusableInTouchMode(true);
        question7_veryHigh.setFocusable(true);
        question7_veryHigh.setFocusableInTouchMode(true);
        question8_yes.setFocusable(true);
        question8_yes.setFocusableInTouchMode(true);
        question9_answer.setFocusable(true);
        question9_answer.setFocusableInTouchMode(true);
        question10_answer.setFocusable(true);
        question10_answer.setFocusableInTouchMode(true);

        //Submit Button:
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswers();
            }
        });

        //Skip Button:
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void submitAnswers() {

        if (checkAnswers()) {

            System.out.println("Answers:");
            System.out.println("Q1: " + question1Response);
            System.out.println("Q2: " + question2Response);
            System.out.println("Q3: " + question3Response);
            System.out.println("Q4: " + question4Response);
            System.out.println("Q5: " + question5Response);
            System.out.println("Q6: " + question6Response);
            System.out.println("Q7: " + question7Response);
            System.out.println("Q8: " + question8Response);
            System.out.println("Q9: " + question9Response);
            System.out.println("Q10: " + question10Response);

            //TODO - Server submission

            // first convert to JSON
            final QuestionEntry[] questionEntries = {
                    new QuestionEntry("Q1", Float.toString(question1Response)),
                    new QuestionEntry("Q2", question2Response.toString()),
                    new QuestionEntry("Q3", Integer.toString(question3Response)),
                    new QuestionEntry("Q4", question4Response.toString()),
                    new QuestionEntry("Q5", Integer.toString(question5Response)),
                    new QuestionEntry("Q6", question6Response.toString()),
                    new QuestionEntry("Q7", question7Response.toString()),
                    new QuestionEntry("Q8", question8Response.toString()),
                    new QuestionEntry("Q9", question9Response),
                    new QuestionEntry("Q10", question10Response)
            };
            final long challengeId = 0L; // todo this must be set (e.g. passed from the calling avtivity via the intent)
            final QuestionnaireEntry questionnaireEntry = new QuestionnaireEntry(Installation.id(this), challengeId, questionEntries);
            final String json = new Gson().toJson(questionnaireEntry);

            // todo use a standard asynctask to submit the JSON as a post to /api/submit-questionnaire?magic=...
            // see submit-code asynctask for an example on submitting via POST
        }
        else Toast.makeText(this, R.string.invalid_questionnaire_response, Toast.LENGTH_LONG).show();
    }

    private boolean checkAnswers() {

        //Q1
        if (question1Response < 0) {
            question_1_ratingBar.requestFocus();
            return false;
        }

        //Q2
        if (question2Response == null) {
            question2_yes.setError(getString(R.string.no_response));
            question2_yes.requestFocus();
            return false;
        } else question2_yes.setError(null);

        //Q3
        if (question3Response < 0) {
            question3_high.setError(getString(R.string.no_response));
            question3_high.requestFocus();
            return false;
        } else question3_high.setError(null);

        //Q4
        if (question4Response == null) {
            question4_veryHigh.setError(getString(R.string.no_response));
            question4_veryHigh.requestFocus();
            return false;
        } else question4_veryHigh.setError(null);

        //Q5
        if (question5Response < 0) {
            question5_high.setError(getString(R.string.no_response));
            question5_high.requestFocus();
            return false;
        } else question5_high.setError(null);

        //Q6
        if (question6Response == null) {
            question6_yes.setError(getString(R.string.no_response));
            question6_yes.requestFocus();
            return false;
        } else question6_yes.setError(null);

        //Q7
        if (question7Response == null) {
            question7_veryHigh.setError(getString(R.string.no_response));
            question7_veryHigh.requestFocus();
            return false;
        } else question7_veryHigh.setError(null);

        //Q7
        if (question8Response == null) {
            question8_yes.setError(getString(R.string.no_response));
            question8_yes.requestFocus();
            return false;
        } else question8_yes.setError(null);

        //Q9
        if (question9Response == null) {
            question9_answer.setError(getString(R.string.no_response));
            question9_answer.requestFocus();
            return false;
        } else question9_answer.setError(null);

        //Q10
        if (question10Response == null) {
            question10_answer.setError(getString(R.string.no_response));
            question10_answer.requestFocus();
            return false;
        } else question10_answer.setError(null);

        return true;
    }

}

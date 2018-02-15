package org.inspirecenter.amazechallenge.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.questionnaire.DichotomousResponse;
import org.inspirecenter.amazechallenge.model.questionnaire.LikertResponse;

public class QuestionnaireActivity extends AppCompatActivity {

    private Button skipButton;
    private Button submitButton;
    private RatingBar question_1_ratingBar;
    private RadioGroup question2_group;
    private RadioButton question_2_radio_yes;
    private RadioButton question_2_radio_no;
    private RadioButton question_2_radio_maybe;
    private RatingBar question_3_ratingBar;
    private RatingBar question_4_ratingBar;
    private SeekBar question_5_seekbar;
    private RadioGroup question6_group;
    private RadioButton question_6_radio_yes;
    private RadioButton question_6_radio_no;
    private RadioButton question_6_radio_notSure;

    float question1Response = 0;
    DichotomousResponse question2Response = null;
    float question3Response = 0;
    float question4Response = 0;
    int question5Response = 0;
    DichotomousResponse question6Response = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_questionnaire);

        //Controls:
        question_1_ratingBar = findViewById(R.id.answer_1);
        question2_group = findViewById(R.id.question_2_radiogroup);
        question_2_radio_yes = findViewById(R.id.question_2_yes);
        question_2_radio_no = findViewById(R.id.question_2_no);
        question_2_radio_maybe = findViewById(R.id.question_2_maybe);
        question_3_ratingBar = findViewById(R.id.answer_3);
        question_4_ratingBar = findViewById(R.id.answer_4);
        question_5_seekbar = findViewById(R.id.answer_5);
        question6_group = findViewById(R.id.question_6_radiogroup);
        question_6_radio_yes = findViewById(R.id.question_6_yes);
        question_6_radio_no = findViewById(R.id.question_6_no);
        question_6_radio_notSure = findViewById(R.id.question_6_notsure);
        submitButton = findViewById(R.id.submit_button);
        skipButton = findViewById(R.id.skip_button);

        //Question 1 Controls:
        question_1_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                question1Response = v;
            }
        });

        //Question 2 Controls:
        question_2_radio_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.YES;
            }
        });
        question_2_radio_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.NO;
            }
        });
        question_2_radio_maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question2Response = DichotomousResponse.MAYBE;
            }
        });

        //Question 3 Controls:
        question_3_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                question3Response = v;
            }
        });

        //Question 4 Controls:
        question_4_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                question4Response = v;
            }
        });

        //Question 5 Controls:
        question_5_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                question5Response = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Question 6 Controls:
        question_6_radio_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.YES;
            }
        });
        question_6_radio_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.NO;
            }
        });
        question_6_radio_notSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question6Response = DichotomousResponse.MAYBE;
            }
        });


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

            //TODO - Server submission
        }
        else Toast.makeText(this, R.string.invalid_questionnaire_response, Toast.LENGTH_LONG).show();
    }

    private boolean checkAnswers() {
        if (question2Response == null) {
            question_2_radio_maybe.setError(getString(R.string.no_response));
            question_2_radio_maybe.requestFocus();
            return false;
        } else question_2_radio_maybe.setError(null);

        if (question6Response == null) {
            question_6_radio_notSure.setError(getString(R.string.no_response));
            question_6_radio_notSure.requestFocus();
            return false;
        } else question_6_radio_notSure.setError(null);

        return true;
    }

}

package fit5120.lookout;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Text;

import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.Random;

public class RuleQuiz extends MainActivity {
    TextView question,score,qnumber;
    Button submitButton;
    ProgressBar progressBar;
    ArrayList<QuestionModel> questionModelArrayList;
    TextView[] optionA;
    String[] optionArray;
    int currentPosition = 0;
    int numberOfCorrectAnswer = 0;
    int presscount = 0;
    int index = 0;
    int prevIndex = 0;
    Typeface typeface;
    String ans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_quiz);
        setNavigationViewListener();

        question = (TextView) findViewById(R.id.textQuestion);

        score = (TextView) findViewById(R.id.score);
        qnumber = (TextView) findViewById(R.id.qnumber);
        submitButton = (Button) findViewById(R.id.submitButton);
        questionModelArrayList = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        optionA = new TextView[]{
                findViewById(R.id.option1),
                findViewById(R.id.option2),
                findViewById(R.id.option3),
                findViewById(R.id.option4)
        };
        setUpQuestion();
        setUpData();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    public String[] shuffleArray(String[] ar){
        int index;
        String temp;
        Random random = new Random();
        for(int i = ar.length - 1; i > 0 ; i--) {
            index = random.nextInt(i + 1);
            temp = ar[index];
            ar[index] = ar[i];
            ar[i] = temp;
        }
        return ar;
    }

    public void checkAnswer(){
        typeface = ResourcesCompat.getFont(this, R.font.fredoka_one);

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(RuleQuiz.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("Right Asswer")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        currentPosition ++;
                        setUpData();
                        sweetAlertDialog.dismiss();
                    }
                });

        SweetAlertDialog sDialog = new SweetAlertDialog(RuleQuiz.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Wrong Answer")
                .setContentText("The right answer is : "+questionModelArrayList.get(currentPosition).getAnswer())
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        currentPosition ++;
                        setUpData();
                    }
                });

        if(ans.equalsIgnoreCase(questionModelArrayList.get(currentPosition).getAnswer())){
            numberOfCorrectAnswer ++;
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setCanceledOnTouchOutside(false);
            sweetAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                    TextView subText = (TextView) alertDialog.findViewById(R.id.content_text);
                    Button con_button = (Button) alertDialog.findViewById(R.id.confirm_button);
                    text.setTypeface(typeface);
                    subText.setTypeface(typeface);
                    con_button.setTypeface(typeface);
                }
            });
            sweetAlertDialog.show();
        }
        else {
            sDialog.setCancelable(false);
            sDialog.setCanceledOnTouchOutside(false);
            sDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                    TextView subText = (TextView) alertDialog.findViewById(R.id.content_text);
                    Button con_button = (Button) alertDialog.findViewById(R.id.confirm_button);
                    text.setTypeface(typeface);
                    subText.setTypeface(typeface);
                    con_button.setTypeface(typeface);
                }
            });

            sDialog.show();
        }
        int x = ((currentPosition+1) * 100) / questionModelArrayList.size();
        progressBar.setProgress(x);
    }

    public void setUpQuestion(){
        questionModelArrayList.add(new QuestionModel("You should stop when the signal light becomes?","Red",new String[]{"Red","Black","Green","Yellow"}));
        questionModelArrayList.add(new QuestionModel("When there are pedestrians crossing the road, do you need to give way to them?","Of course I need to give the way.",new String[]{"Of course I need to give the way.","No, I should go to work quickly.","I don'know","May be"}));
        questionModelArrayList.add(new QuestionModel("In built-up areas where there are no signs, the speed limit is? ","108",new String[]{"10 km/h","50 km/h","70 km/h","70 m/h"}));
        questionModelArrayList.add(new QuestionModel("Can you take a bike on a bus or tram?","Unless it is a folding bike",new String[]{"I think I can","if there is less people","Unless it is a folding bike","Of course not"}));
        questionModelArrayList.add(new QuestionModel("Can you park your bike if there is a ‘No Parking’ sign or similar?","NO",new String[]{"I can if my bicycle is worthless","Park but watch for Police","Park but keep an eye","NO"}));
    }

    public void setUpData(){
        presscount = 0;
        ans = new String();
        if(questionModelArrayList.size()>currentPosition) {
            question.setText(questionModelArrayList.get(currentPosition).getQuestionString());
            optionArray = shuffleArray(questionModelArrayList.get(currentPosition).getOptionArray());
            for (index = 0; index < optionArray.length;index++){
                optionA[index].setTag(index);
                optionA[index].setText(optionArray[index]);
                optionA[index].setTextColor(getColor(R.color.colorPrimaryDark));
                optionA[index].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        presscount++;
                        prevIndex = index;
                        index = (int)v.getTag();
                        if (presscount < 2) {
                            optionA[index].setTextColor(getColor(R.color.slide_active));
                            ans = (String)optionA[index].getText();
                        }
                        else{
                            optionA[prevIndex].setTextColor(getColor(R.color.colorPrimaryDark));
                            index = (int)v.getTag();
                            optionA[index].setTextColor(getColor(R.color.slide_active));
                            ans = (String)optionA[index].getText();
                        }
                    }
                });
            }
            score.setText("Score :" + numberOfCorrectAnswer + "/" + questionModelArrayList.size());
            qnumber.setText("Question No : " + (currentPosition + 1));
        }
        else{
            typeface = ResourcesCompat.getFont(this, R.font.fredoka_one);
            SweetAlertDialog qEnd =  new SweetAlertDialog(RuleQuiz.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("You have successfully completed the quiz")
                    .setContentText("Your score is : "+ numberOfCorrectAnswer + "/" + questionModelArrayList.size())
                    .setConfirmText("Restart")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            currentPosition = 0;
                            numberOfCorrectAnswer = 0;
                            progressBar.setProgress(0);
                            setUpData();
                        }
                    })
                    .setCancelText("Close")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            finish();
                        }
                    });

            qEnd.setCancelable(false);
            qEnd.setCanceledOnTouchOutside(false);
            qEnd.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                    TextView subText = (TextView) alertDialog.findViewById(R.id.content_text);
                    Button con_button = (Button) alertDialog.findViewById(R.id.confirm_button);
                    Button can_button = (Button) alertDialog.findViewById(R.id.cancel_button);
                    text.setTypeface(typeface);
                    subText.setTypeface(typeface);
                    con_button.setTypeface(typeface);
                    can_button.setTypeface(typeface);
                }
            });
            qEnd.show();
        }
    }
}

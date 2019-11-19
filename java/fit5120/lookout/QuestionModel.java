package fit5120.lookout;

public class QuestionModel {

    public QuestionModel(String questionString, String answer,String[] optionArray) {
        QuestionString = questionString;
        Answer = answer;
        this.optionArray = optionArray;
    }

    public String getQuestionString() {
        return QuestionString;
    }

    public void setQuestionString(String questionString) {
        QuestionString = questionString;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public void setOption(String[] optionArray){
        this.optionArray = optionArray;
    }

    public String[] getOptionArray(){
        return optionArray;
    }

    private String QuestionString;
    private String Answer;
    private String[] optionArray;
}

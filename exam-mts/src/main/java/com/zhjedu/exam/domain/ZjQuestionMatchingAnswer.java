package com.zhjedu.exam.domain;



/**
 * ZjQuestionMatchingAnswer generated by MyEclipse - Hibernate Tools
 */

public class ZjQuestionMatchingAnswer  implements java.io.Serializable {


    // Fields    

     private String id;
     private String question;
     private String answercontext;
     private String viewflag;

    // Constructors

    public String getViewflag() {
		return viewflag;
	}

	public void setViewflag(String viewflag) {
		this.viewflag = viewflag;
	}

	/** default constructor */
    public ZjQuestionMatchingAnswer() {
    }

	/** minimal constructor */
    public ZjQuestionMatchingAnswer(String question) {
        this.question = question;
    }
    
    /** full constructor */
    public ZjQuestionMatchingAnswer(String question, String answercontext) {
        this.question = question;
        this.answercontext = answercontext;
    }

   
    // Property accessors

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswercontext() {
        return this.answercontext;
    }
    
    public void setAnswercontext(String answercontext) {
        this.answercontext = answercontext;
    }
   








}
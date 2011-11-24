package firstapp.wirinun;

import java.io.Serializable;
import java.util.Date;

public class Word implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7519081602875919037L;
    private Long id;
    private String english = "";
    private String hungarian = "";
    private String englishSentence = "";
    private long lessonId = 0;
    //	    private Lesson lessonName;
    private int noAsked = 0;
    private int noKnown = 0;
    private boolean known = false;
    private Date createdAt;

    public Word() {
        super();

        createdAt = new java.util.Date();
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getEnglishSentence() {
        return englishSentence;
    }

    public void setEnglishSentence(String englishSentence) {
        this.englishSentence = englishSentence;
    }

    public String getHungarian() {
        return hungarian;
    }

    public void setHungarian(String hungarian) {
        this.hungarian = hungarian;
    }


    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public int getNoAsked() {
        return noAsked;
    }

    public void setNoAsked(int noAsked) {
        this.noAsked = noAsked;
    }

    public int getNoKnown() {
        return noKnown;
    }

    public void setNoKnown(int noKnown) {
        this.noKnown = noKnown;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("english : ");
        sb.append(getEnglish());
        sb.appendCodePoint(13);
        sb.append("hungarian : ");
        sb.append(getHungarian());
        sb.appendCodePoint(13);
        sb.append("english sentence : ");
        sb.append(getEnglishSentence());
        sb.appendCodePoint(13);
        sb.appendCodePoint(13);


        return sb.toString();

    }
    /*
    public boolean isParentLesson(Lesson root) {
    
    Lesson l = this.getLessonName();
    
    
    while (l != null) {
    if (root.getId().longValue() == l.getId().longValue()) {
    return true;
    }
    l = l.getParent();
    
    }
    return false;
    
    }
    
    public Lesson getLessonName() {
    return lessonName;
    }
    
    public void setLessonName(Lesson lessonName) {
    this.lessonName = lessonName;
    }
    
    public String getLessonString() {
    
    if (getLessonName() != null) {
    
    return getLessonName().getLessonString();
    } else {
    return new String("");
    }
    }
     */
}

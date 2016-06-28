package com.nlp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Jyoti Bhosale on 6/25/16.
 */
@XmlRootElement(name = "sentences")
@XmlAccessorType(XmlAccessType.FIELD)
public class Text {

    @XmlElement(name = "sentence")
    private String sentence;

    @XmlElement(name = "noun")
    private List<String> noun = null;

    @XmlElement(name = "docId")
    private String docId;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<String> getNoun() {
        return noun;
    }

    public void setNoun(List<String> noun) {
        this.noun = noun;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}

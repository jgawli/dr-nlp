package com.nlp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * SentenceXml is a XML representation of Output that has list of sentences separated by sentence boundary
 * with doc name which is thefile name that sentence belong to along with list of Proper Nouns in each sentence.
 *
 * Created by Jyoti Bhosale on 6/25/16.
 */
@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.FIELD)
public class SentenceXml {

    @XmlElement(name = "sentences")
    private List<Text> sentences;

    public List<Text> getSentences() {
        return sentences;
    }

    public void setSentences(List<Text> sentences) {
        this.sentences = sentences;
    }
}

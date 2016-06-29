package com.nlp;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Jyoti Bhosale on 6/27/16.
 */
public class NamedEntityFinderTest {

    NamedEntityFinder finder = new NamedEntityFinder();

    @Test
    public void getSentenceByBoundary() throws IOException {
        List<String> sentenceBoundary = finder.getSentenceBoundary("nlp_data.txt");
        assertNotNull(sentenceBoundary);
        assertEquals("5 sentence boundaries were not found", sentenceBoundary.size(), 5);
    }

    @Test
    public void tokenizeWords() throws IOException {
        List<String> extractedWords = finder.getWordTokenizer("nlp_data.txt");
        assertNotNull(extractedWords);
        assertEquals("131 sentence boundaries were not found", extractedWords.size(), 131);
    }


    @Test
    public void findNamedEntities() throws IOException, JAXBException {
        SentenceXml xml = finder.findNamedEntities("nlp_data.txt");
        assertNotNull(xml);
        assertEquals("5 sentence boundaries were not found", xml.getSentences().size(), 5);
        String firstSentence = xml.getSentences().get(0).getSentence();
        assertEquals("First sentence didn't matched the expected sentence boundary", firstSentence,
                "The term \"First World War\" was first used in September 1914 by the German philosopher Ernst Haeckel, who claimed that \"there is no doubt that the course and character of the feared 'European War' ... will become the first world war in the full sense of the word.\"\n" +
                        "\n");
        assertEquals("2 nouns were not identified as expected from first sentence", xml.getSentences().get(0).getNoun().size(), 2);
    }

    @Test
    public void processConcurrentFiles() throws Exception {
        List<Future<SentenceXml>> xml = finder.processAllFilesForNLP(10);
        assertNotNull(xml);
        assertEquals("10 files were not processed", xml.size(), 10);
        SentenceXml sentenceXmlFirstFile = xml.get(0).get();
        assertEquals("4 sentences were not found in first SentenceXml as expected",
                sentenceXmlFirstFile.getSentences().size(), 4);
        Text firstSentence = sentenceXmlFirstFile.getSentences().get(0);
        assertEquals("2 nouns were not found in first sentence as expected", firstSentence.getNoun().size(), 2);
        assertEquals("First file name processed is incorrect", firstSentence.getDocId(), "d01.txt");

    }

}

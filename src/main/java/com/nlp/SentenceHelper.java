package com.nlp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * Created by Jyoti Bhosale on 6/23/16.
 */
public class SentenceHelper {

    List<String> markBoundaries(String target, BreakIterator iterator) {

        List<String> sentenceList = new ArrayList<>();
        StringBuffer markers = new StringBuffer();
        markers.setLength(target.length() + 1);
        for (int k = 0; k < markers.length(); k++) {
            markers.setCharAt(k,' ');
        }

        iterator.setText(target);
        int boundary = iterator.first();

        while (boundary != BreakIterator.DONE) {
            markers.setCharAt(boundary,'^');
            String sentence = target.substring(boundary, iterator.next());
            sentenceList.add(sentence);

            boundary = iterator.next();
        }

        System.out.println(target);
        System.out.println(markers);
        return sentenceList;
    }

    static List<String> extractWords(String target, BreakIterator wordIterator) {

        wordIterator.setText(target);
        int start = wordIterator.first();
        int end = wordIterator.next();
        List<String> wordList = new ArrayList<>();

        while (end != BreakIterator.DONE) {
            String word = target.substring(start,end);
            if (Character.isLetterOrDigit(word.charAt(0))) {
                System.out.println(word);
                wordList.add(word);
            }
            start = end;
            end = wordIterator.next();
        }
        return wordList;
    }


    private static List<Integer> listPositions(String target, BreakIterator iterator) {
        iterator.setText(target);
        int boundary = iterator.first();

        List<Integer> boundaries = new ArrayList<>();
        while (boundary != BreakIterator.DONE) {
            System.out.println(boundary);
            boundary = iterator.next();
            boundaries.add(boundary);
        }
        return boundaries;
    }

    public static List<String> getSentenceByBoundary(String text, BreakIterator iterator) {
        List<String> sentenceList = new ArrayList<>();
        List<Integer> boundaries = listPositions(text, iterator);

        boundaries.add(0, 0);
        int length = boundaries.size();
        boundaries.remove(length - 1);
        System.out.println(boundaries);
        for (int i = 0; i < boundaries.size() - 1; i++) {
            String sentence = text.substring(boundaries.get(i), boundaries.get(i+1));
            sentenceList.add(sentence);
        }
        System.out.println(sentenceList);
        return sentenceList;
    }



    public static void getXmlRepresentation(SentenceXml xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SentenceXml.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //Marshal the SentenceXml list in console
        jaxbMarshaller.marshal(xml, System.out);
    }

}

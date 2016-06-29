package com.nlp;


import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * This class reads text from a file and print sentence boundaries, tokenizes the words in it,
 * finds NamedEntities from each sentence and represent them XML format.
 *
 * Created by Jyoti Bhosale on 6/22/16.
 */
public class NamedEntityFinder {

    static Locale currentLocale = new Locale("en","US");
    private SentenceHelper sentenceHelper = new SentenceHelper();

    /**
     * Marks sentence boundary below String with ^ symbol
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public List<String> markSentenceBoundary(String fileName) throws IOException {
        String textString = readFileAndConvertToString(fileName);

        BreakIterator sentenceIterator =
                BreakIterator.getSentenceInstance(currentLocale);
        return sentenceHelper.markBoundaries(textString, sentenceIterator);
    }

    /**
     * Gets list of Sentence string separated by boundaries read from a File
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String> getSentenceBoundary(File file) throws IOException {

        InputStream inputStream = new FileInputStream(file);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        BreakIterator sentenceIterator =
                BreakIterator.getSentenceInstance(currentLocale);
        return SentenceHelper.getSentenceByBoundary(textBuilder.toString(), sentenceIterator);
    }


    /**
     * Gets list of Sentence string separated by boundaries read from a File by fileName
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public List<String> getSentenceBoundary(String fileName) throws IOException {
        String textString = readFileAndConvertToString(fileName);

        BreakIterator sentenceIterator =
                BreakIterator.getSentenceInstance(currentLocale);
        return SentenceHelper.getSentenceByBoundary(textString, sentenceIterator);
    }

    private static String readFileAndConvertToString(String fileName) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);

        return convertInputStreamToString(inputStream);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }


    /**
     * Get list of words tokenized read from a file by fileName
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public List<String> getWordTokenizer(String fileName) throws IOException {
        String textString = readFileAndConvertToString(fileName);
        BreakIterator wordIterator =
                BreakIterator.getWordInstance(currentLocale);
        return SentenceHelper.extractWords(textString, wordIterator);
    }


    private static List<String> getNounList(String fileName) throws IOException {

        String textString = readFileAndConvertToString(fileName);
        String textStr[] = textString.split("\\r\\n|\\n|\\r");
        return Arrays.asList(textStr);
    }

    /**
     * Find all proper nouns in a given sentence that is separated by it's boundary and form SentenceXml object with sentence
     * and list of proper nouns in that sentence.
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public SentenceXml findNamedEntities(String fileName) throws IOException, JAXBException {

        List<String> sentenceList = getSentenceBoundary(fileName);
        List<String> nounList = getNounList("NER.txt");

        List<Text> texts = new ArrayList<>();
        for (String sentence : sentenceList) {
            List<String> nouns = new ArrayList<>();

            for (String word : nounList) {
                if (sentence.contains(word)) {
                    nouns.add(word);
                }
            }
            Text text = new Text();
            text.setSentence(sentence);
            text.setNoun(nouns);
            text.setDocId(fileName);
            texts.add(text);
        }
        SentenceXml sentenceXml = new SentenceXml();
        sentenceXml.setSentences(texts);
        SentenceHelper.getXmlRepresentation(sentenceXml);

        return sentenceXml;

    }

    /**
     * Find all proper nouns in a given sentence that is separated by it's boundary and form SentenceXml object with sentence
     * and list of proper nouns in that sentence.
     *
     * @param file
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public static SentenceXml findNamedEntitiesByFile(File file) throws IOException, JAXBException {

        List<String> sentenceList = getSentenceBoundary(file);
        List<String> nounList = getNounList("NER.txt");

        List<Text> texts = new ArrayList<>();
        for (String sentence : sentenceList) {
            List<String> nouns = new ArrayList<>();

            for (String word : nounList) {
                if (sentence.contains(word)) {
                    nouns.add(word);
                }
            }
            Text text = new Text();
            text.setSentence(sentence);
            text.setNoun(nouns);
            text.setDocId(file.getName());
            texts.add(text);
        }
        SentenceXml sentenceXml = new SentenceXml();
        sentenceXml.setSentences(texts);
        SentenceHelper.getXmlRepresentation(sentenceXml);

        return sentenceXml;

    }

    private Callable<SentenceXml> findNamedEntities(File file) throws IOException, JAXBException {
        return () -> findNamedEntitiesByFile(file);
    }


    /**
     * Extract all files in zip into user directory
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void unzipFileIntoDirectory() throws IOException, InterruptedException {

        try {

            URL path = ClassLoader.getSystemResource("nlp_data.zip");
            ZipFile zipFile = new ZipFile(path.getPath());

            Enumeration files = zipFile.entries();

            while (files.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) files.nextElement();

                final String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
                System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
                        name, size, compressedSize);

                File file = new File(name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                InputStream is = zipFile.getInputStream(zipEntry);

                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }


                is.close();
                fos.close();

            }

            zipFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * It reads zip file, extracts it and process them concurrently where it read each file, get sentences by their boundaries
     * and find proper nouns in them and form XML representation.
     *
     * @param noOfThreads
     * @throws Exception
     */
    public List<Future<SentenceXml>> processAllFilesForNLP(int noOfThreads)
            throws Exception
    {
        unzipFileIntoDirectory();
        String currentDir = System.getProperty("user.dir");
        String folderPath = currentDir + File.separator + "nlp_data";

        List<File> filesInFolder = Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        int count = filesInFolder.size();
        List<Callable<SentenceXml>> tasks = new ArrayList<>(count);
        for(File file : filesInFolder)
            tasks.add(findNamedEntities(file));
        ExecutorService es = Executors.newFixedThreadPool(noOfThreads);

        List<Future<SentenceXml>> results = es.invokeAll(tasks);

        es.shutdown();

        return results;

    }

}

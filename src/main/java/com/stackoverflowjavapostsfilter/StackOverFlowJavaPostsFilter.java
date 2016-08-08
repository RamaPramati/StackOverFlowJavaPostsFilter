package com.stackoverflowjavapostsfilter;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Gets all java posts which are having "how" in their title from stackoverflow.com
 *
 * Created by ramakrishnas on 28/7/16.
 */
public class StackOverFlowJavaPostsFilter {

    private static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilter.class.getName());
    private static Properties applicationProperties = new Properties();
    static PrintWriter javaPostsWriter;

    public static void main(String args[]) {
        try {
            applicationProperties.load(new FileInputStream("application.properties"));
            javaPostsWriter = new PrintWriter(applicationProperties.getProperty("filteredPostsFilePath"));

            File stackOverFlowPostsFolder = new File(applicationProperties.getProperty("stackOverFlowPostsDirectory"));
            File[] postsFiles = stackOverFlowPostsFolder.listFiles();

            DefaultHandler handler = new StackOverFlowPostsParser();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();

            if (postsFiles == null) {
                LOGGER.info("No files in the given directory");
                return;
            }

            for (File postsFile : postsFiles) {
                LOGGER.info("Processing file " + postsFile);
                parser.parse(postsFile, handler);
            }

            JavaPostsFileWriter.writePostsWithRequiredAnswersToFile();
        } catch (SAXException saxException) {
            LOGGER.info("Failed to parse xml..." + saxException.getMessage());
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.info("File not found..." + fileNotFoundException.getMessage());
        } catch (ParserConfigurationException parserConfigurationException) {
            LOGGER.info("Failure in parser configuration...." + parserConfigurationException.getMessage());
        } catch (IOException ioException) {
            LOGGER.info("IOException" + ioException.getMessage());
        } finally {
            javaPostsWriter.close();
        }
    }
}

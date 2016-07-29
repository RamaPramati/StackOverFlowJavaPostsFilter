import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Created by ramakrishnas on 28/7/16.
 */
public class StackOverFlowJavaPostsFilter {

    public static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilter.class.getName());

    public static void main(String args[]) {

        PrintWriter javaPostsWriter = null;
        try {
            javaPostsWriter = new PrintWriter("/home/ramakrishnas/Desktop/StackOverFlowJavaPostsFilter/" +
                    "StackOverFlowJavaPosts.txt");
            File stackOverFlowPostsFolder = new File("/home/ramakrishnas/Desktop/StackOverFlowPosts");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File[] postsFiles = stackOverFlowPostsFolder.listFiles();
            for (int postsFilesIndex = 0; postsFilesIndex < postsFiles.length; postsFilesIndex++) {

                Document postsDocument = dBuilder.parse(postsFiles[postsFilesIndex]);
                postsDocument.getDocumentElement().normalize();
                NodeList postsDetails = postsDocument.getElementsByTagName("row");
                for (int postDetailsIndex = 0; postDetailsIndex < postsDetails.getLength(); postDetailsIndex++) {

                    Node post = postsDetails.item(postDetailsIndex);
                    if (post.getNodeType() == Node.ELEMENT_NODE) {
                        Element postElement = (Element) post;
                        String postTags = postElement.getAttribute("Tags").toString();
                        String postTitle = postElement.getAttribute("Title").toString();
                        if (postTags.contains("<java>") && (postTitle.toLowerCase().contains("how")))
                            javaPostsWriter.println("PostId: " + postElement.getAttribute("Id") + "\n" + "Title : " +
                                    postElement.getAttribute("Title").toString() + "\n");
                    }
                }
            }
        } catch (SAXException saxException) {
            LOGGER.info("Failed to parse xml..." + saxException.getMessage());
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.info("File not found..." + fileNotFoundException.getMessage());
        } catch (ParserConfigurationException parserConfigurationException) {
            LOGGER.info("Failure in parser configuration...." + parserConfigurationException.getMessage());
        } catch (IOException ioException) {
            LOGGER.info("" + ioException.getMessage());
        } finally {
            javaPostsWriter.close();
        }
    }
}

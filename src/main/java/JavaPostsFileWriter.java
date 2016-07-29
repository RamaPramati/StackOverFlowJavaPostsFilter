import org.w3c.dom.Element;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by ramakrishnas on 29/7/16.
 */
public class JavaPostsFileWriter {

    public static void writeToFile(Element postElement, PrintWriter javaPostsWriter) throws IOException {
        javaPostsWriter.println("PostId: " + postElement.getAttribute("Id") + "\n" + "Title : " +
                postElement.getAttribute("Title").toString() + "\n");
    }
}

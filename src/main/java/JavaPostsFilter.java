import org.w3c.dom.Element;

/**
 * Created by ramakrishnas on 29/7/16.
 */
public class JavaPostsFilter {
    public boolean isJavaPost(Element postElement) {
        String postTags = postElement.getAttribute("Tags").toString();
        String postTitle = postElement.getAttribute("Title").toString();
        if(postTags.contains("<java>") && (postTitle.toLowerCase().contains("how")))
            return true;
        else
            return false;
    }
}

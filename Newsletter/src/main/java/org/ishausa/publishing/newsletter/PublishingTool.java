package org.ishausa.publishing.newsletter;

import com.google.common.base.Throwables;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;
import spark.Request;
import spark.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;


/**
 * Created by tosri on 12/3/2016.
 */
public class PublishingTool {
    private static final Logger log = Logger.getLogger(PublishingTool.class.getName());

    private static final NewsletterCreator NEWSLETTER_CREATOR = new NewsletterCreator();

    public static void main(final String[] args) {
        get("/", (req, res) -> SoyRenderer.INSTANCE.render(SoyRenderer.Template.INDEX));

        post("/", PublishingTool::handlePost);

        exception(Exception.class, ((exception, request, response) -> {
            log.info("Exception: " + exception + " stack: " + Throwables.getStackTraceAsString(exception));
        }));
    }

    private static String handlePost(final Request request, final Response response) throws Exception {
        final String content = request.body();

        // Build the Newsletter from the paramsMap by building out each section
        final Newsletter newsletter = NEWSLETTER_CREATOR.parseToNewsletter(content);

        // Dump the html content of the Newsletter for wordpress
        final PrintWriter printWriter = new PrintWriter(response.raw().getOutputStream());
        newsletter.printForWordpress(printWriter);

        // Dump it for email
        newsletter.printForEmail(printWriter);

        // for debugging
        final StringWriter buffer = new StringWriter();
        newsletter.printForWordpress(new PrintWriter(buffer));
        log.info("Wordpress Contents: " + buffer);

        return buffer.toString();
    }
}

package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import javax.annotation.concurrent.Immutable;
import java.io.PrintWriter;

/**
 * An independent news item that belongs to a particular section.
 *
 * Created by tosri on 12/4/2016.
 */
@Immutable
public class Item {
    private final String title;
    private final String htmlContent;
    private final String htmlSummary;

    public Item(final String title,
                final String htmlContent,
                final String htmlSummary) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.htmlSummary = htmlSummary;
    }

    void printForWordpress(final PrintWriter writer, final boolean isAccordion) {
        if (isAccordion) {
            writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.Template.SPOILER,
                    ImmutableMap.of("title", title, "htmlContent", htmlContent)));
        } else {
            writer.println(htmlContent);
        }
    }

    void printForEmail(final PrintWriter writer) {
        writer.println(htmlSummary);
    }
}

package org.ishausa.publishing.newsletter;

import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tosri on 12/4/2016.
 */
public class Newsletter {
    private final List<Section> sections = new LinkedList<>();

    void addSection(final Section section) {
        sections.add(section);
    }

    void printForWordpress(final PrintWriter writer) {
        writer.println("[su_accordion]");

        for (final Section section : sections) {
            section.printForWordpress(writer);
        }

        writer.println("[/su_accordion]");

        writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.Template.FOOTER, new HashMap<>()));
    }

    void printForEmail(final PrintWriter writer) {
        for (final Section section : sections) {
            section.printForEmail(writer);
        }
    }
}

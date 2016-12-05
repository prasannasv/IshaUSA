package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Broad category of items in a newsletter.
 * Created by tosri on 12/4/2016.
 */
public class Section {
    private final StandardSection sectionType;
    private final List<Item> items = new LinkedList<>();

    public Section(final StandardSection sectionType) {
        this.sectionType = sectionType;
    }

    public void addItem(final Item item) {
        items.add(item);
    }

    public void printForWordpress(final PrintWriter writer) {
        writeSectionTitle(writer);
        for (final Item item : items) {
            item.printForWordpress(writer, sectionType.isAccordion());
        }
    }

    private void writeSectionTitle(final PrintWriter writer) {
        writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.Template.SECTION_HEADING,
                ImmutableMap.of("anchorId", sectionType.getAnchorId(),
                        "heading", sectionType.getHeading())));
    }

    public void printForEmail(final PrintWriter writer) {
        for (final Item item : items) {
            item.printForEmail(writer);
        }
    }
}

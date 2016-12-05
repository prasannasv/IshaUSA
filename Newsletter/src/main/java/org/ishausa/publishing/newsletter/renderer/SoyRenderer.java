package org.ishausa.publishing.newsletter.renderer;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.util.Map;

/**
 * Created by tosri on 12/3/2016.
 */
public class SoyRenderer {

    public enum Template {
        INDEX,
        SECTION_HEADING,
        SPOILER,
        VIDEO_YOUTUBE,
        VIDEO_FACEBOOK,
        FOOTER;

        @Override
        public String toString() {
            return "." + name().toLowerCase();
        }
    }

    public static final SoyRenderer INSTANCE = new SoyRenderer();

    private final SoyTofu serviceTofu;
    private final SoyTofu wordpressTofu;

    private SoyRenderer() {
        final SoyFileSet sfs = SoyFileSet.builder()
                .add(new File("./src/main/webapp/template/publishing_tool.soy"))
                .add(new File("./src/main/webapp/template/wordpress.soy"))
                .build();
        serviceTofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter");
        wordpressTofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter.wordpress");
    }

    public String render(final Template template) {
        return serviceTofu.newRenderer(template.toString()).render();
    }

    public String renderWordpress(final Template template, final Map<String, ?> data) {
        return wordpressTofu.newRenderer(template.toString()).setData(data).render();
    }
}

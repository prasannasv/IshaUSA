package org.ishausa.publishing.newsletter.renderer;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;

/**
 * Created by tosri on 12/3/2016.
 */
public class SoyRenderer {

    public enum Template {
        INDEX,
    }

    private final SoyTofu tofu;

    public SoyRenderer() {
        final SoyFileSet sfs = SoyFileSet.builder()
                .add(new File("./src/main/webapp/template/publishing_tool.soy"))
                .build();
        tofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter");
    }

    public String render(final Template template) {
        return tofu.newRenderer("." + template.toString().toLowerCase()).render();
    }
}

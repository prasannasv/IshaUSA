package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by tosri on 12/4/2016.
 */
public class VideoLinks {
    static Section createSection(final String videoLink) {
        if (videoLink.contains("youtube")) {
            return createYoutubeVideoSection(videoLink);
        } else if (videoLink.contains("facebook")) {
            return createFacebookVideoSection(videoLink);
        }
        throw new IllegalArgumentException("Don't know how to parse given videoLink: " + videoLink);
    }

    private static Section createYoutubeVideoSection(final String link) {
        final String videoId = link.split("=")[1];

        final String htmlContent = SoyRenderer.INSTANCE.renderWordpress(
                SoyRenderer.Template.VIDEO_YOUTUBE, ImmutableMap.of("videoId", videoId));

        final Item item = new Item("Unused", htmlContent, htmlContent);
        final Section section = new Section(StandardSection.VIDEO);
        section.addItem(item);

        return section;
    }

    private static Section createFacebookVideoSection(final String videoLink) {
        try {
            final String htmlContent = SoyRenderer.INSTANCE.renderWordpress(
                    SoyRenderer.Template.VIDEO_FACEBOOK, ImmutableMap.of("encodedUrl", URLEncoder.encode(videoLink, "UTF-8")));

            final Item item = new Item("Unused", htmlContent, htmlContent);
            final Section section = new Section(StandardSection.VIDEO);
            section.addItem(item);

            return section;
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}

package org.ishausa.publishing.newsletter;

import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by tosri on 12/4/2016.
 */
class PostBodyParser {
    private static final Logger log = Logger.getLogger(PostBodyParser.class.getName());

    Newsletter parseToNewsletter(final String content) throws Exception {
        final Newsletter newsletter = new Newsletter();

        final Map<String, String> paramsMap = parseContentToMap(content);

        addVideo(newsletter, paramsMap);

        for (final StandardSection sectionType : StandardSection.values()) {
            addSection(newsletter, sectionType, paramsMap);
        }

        return newsletter;
    }

    private void addVideo(final Newsletter newsletter, final Map<String, String> params) {
        final String videoLink = params.get(StandardSection.VIDEO.toString().toLowerCase());

        newsletter.addSection(VideoLinks.createSection(videoLink));
    }

    private void addSection(final Newsletter newsletter,
                            final StandardSection sectionType,
                            final Map<String, String> params) {
        final String queryParamPrefix = sectionType.toString().toLowerCase() + "-";
        final String titleParam = queryParamPrefix + "title";
        final String fullContentParam = queryParamPrefix + "full";
        final String summaryContentParam = queryParamPrefix + "summary";

        addSection(newsletter, sectionType, params.get(titleParam), params.get(fullContentParam), params.get(summaryContentParam));
    }

    private void addSection(final Newsletter newsletter,
                            final StandardSection sectionType,
                            final String title,
                            final String fullContent,
                            final String summaryContent) {

        if (fullContent != null && title != null) {
            newsletter.addSection(createSection(sectionType, title, fullContent, summaryContent));
        }
    }

    private Section createSection(final StandardSection sectionType,
                                  final String title,
                                  final String htmlContent,
                                  @Nullable final String htmlSummary) {
        final Section section = new Section(sectionType);

        section.addItem(new Item(title, htmlContent, htmlSummary));

        return section;
    }

    //TODO: Handle multiple items in a single section
    private Map<String, String> parseContentToMap(final String postBody) throws Exception {
        final Map<String, String> paramsMap = new HashMap<>();

        for (final String keyValuePair : postBody.split("&")) {
            final String[] parts = keyValuePair.split("=");
            if (parts.length == 2) {
                paramsMap.put(parts[0], URLDecoder.decode(parts[1], "UTF-8"));
            } else {
                paramsMap.put(parts[0], "");
            }
        }

        return paramsMap;
    }
}

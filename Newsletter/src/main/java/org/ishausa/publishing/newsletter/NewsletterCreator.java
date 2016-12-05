package org.ishausa.publishing.newsletter;

import com.google.common.base.Strings;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by tosri on 12/4/2016.
 */
class NewsletterCreator {
    private static final Logger log = Logger.getLogger(NewsletterCreator.class.getName());

    Newsletter parseToNewsletter(final String content) throws Exception {
        log.info("parsing content: " + content);

        final Newsletter newsletter = new Newsletter();

        final Map<String, List<String>> paramsMap = parseContentToMap(content);

        addVideo(newsletter, paramsMap);

        for (final StandardSection sectionType : StandardSection.values()) {
            addSection(newsletter, sectionType, paramsMap);
        }

        return newsletter;
    }

    private void addVideo(final Newsletter newsletter, final Map<String, List<String>> params) {
        final String videoLink = params.get(StandardSection.VIDEO.toString().toLowerCase()).get(0);

        newsletter.addSection(VideoLinks.createSection(videoLink));
    }

    private void addSection(final Newsletter newsletter,
                            final StandardSection sectionType,
                            final Map<String, List<String>> params) {
        final String queryParamPrefix = sectionType.toString().toLowerCase() + "-";
        final String titleParam = queryParamPrefix + "title";
        final String fullContentParam = queryParamPrefix + "full";
        final String summaryContentParam = queryParamPrefix + "summary";

        final Section section = new Section(sectionType);
        addItems(section, params.get(titleParam), params.get(fullContentParam), params.get(summaryContentParam));
        if (!section.isEmpty()) {
            newsletter.addSection(section);
        }
    }

    private void addItems(final Section section,
                          final List<String> titles,
                          final List<String> fullContents,
                          final List<String> summaryContents) {

        if (fullContents != null && titles != null) {
            for (int i = 0; i < titles.size(); ++i) {
                final String title = titles.get(i);
                final String fullContent = fullContents.get(i);
                final String summaryContent = summaryContents != null ? summaryContents.get(i) : "";

                if (!Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(fullContent)) {
                    section.addItem(new Item(title, fullContent, summaryContent));
                }
            }
        }
    }

    private Map<String, List<String>> parseContentToMap(final String postBody) throws Exception {
        final Map<String, List<String>> paramsMap = new HashMap<>();

        for (final String keyValuePair : postBody.split("&")) {
            final String[] parts = keyValuePair.split("=");
            if (!paramsMap.containsKey(parts[0])) {
                paramsMap.put(parts[0], new ArrayList<>());
            }
            if (parts.length == 2) {
                paramsMap.get(parts[0]).add(URLDecoder.decode(parts[1], "UTF-8"));
            } else {
                paramsMap.get(parts[0]).add("");
            }
        }

        return paramsMap;
    }
}

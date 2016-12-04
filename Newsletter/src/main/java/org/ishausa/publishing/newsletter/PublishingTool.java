package org.ishausa.publishing.newsletter;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringEscapeUtils;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;
import spark.Request;
import spark.Response;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by tosri on 12/3/2016.
 */
public class PublishingTool {
    private static final Logger log = Logger.getLogger(PublishingTool.class.getName());
    private static final SoyRenderer renderer = new SoyRenderer();

    enum QueryParam {
        VIDEO_LINK("video"),
        ADIYOGI_FULL("adiyogi-full"),
        ARTICLE_FULL("article-full"),
        WEEK_IN_PHOTOS("photos-full"),
        NEWS_ONE_TITLE("news1-title"),
        NEWS_ONE_CONTENT("news1-full"),
        NEWS_TWO_TITLE("news1-title"),
        NEWS_TWO_CONTENT("news1-full"),
        IN_THE_NEWS("in-the-news-full"),
        VOLUNTEERING_OPPORTUNITIES("volunteer-full"),
        III_PROGRAMS("iii-programs-full"),
        US_CA_PROGRAMS("us-ca-programs-full"),
        LIVE_IN_CONSECRATED_SPACE("consecrated-programs-full"),
        INTERNATIONAL_SCHEDULE("international-programs-full"),
        SHARING("sharing-full");

        private final String paramName;

        QueryParam(final String paramName) {
            this.paramName = paramName;
        }
    }

    public static void main(final String[] args) {
        get("/", (req, res) -> renderer.render(SoyRenderer.Template.INDEX));
        post("/", (req, res) -> handlePost(req, res));
        exception(Exception.class, ((exception, request, response) -> {
            log.info("Exception: " + exception + " stack: " + Throwables.getStackTraceAsString(exception));
        }));
    }

    private static String handlePost(final Request request, final Response response) throws Exception {
        final String content = request.body();
        final Map<String, String> paramsMap = paraseContentToMap(content);
        log.info("Content parsed as map: " + paramsMap);

        for (final QueryParam param : QueryParam.values()) {
            log.info("param: " + param.toString() + ", value: " + paramsMap.get(param.paramName));
        }
        return paramsMap.toString();
    }

    private static Map<String, String> paraseContentToMap(final String postBody) throws Exception {
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

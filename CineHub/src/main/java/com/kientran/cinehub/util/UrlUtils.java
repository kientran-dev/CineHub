package com.kientran.cinehub.util;

public class UrlUtils {
    public static String fixTmdbUrl(String url) {
        if (url == null) {
            return null;
        }
        // Replace media.themoviedb.org with image.tmdb.org to bypass Vietnam ISP block
        if (url.contains("media.themoviedb.org")) {
            return url.replace("media.themoviedb.org", "image.tmdb.org");
        }
        // Just in case there are other themoviedb.org URLs
        if (url.contains("themoviedb.org") && !url.contains("image.tmdb.org")) {
            return url.replace("themoviedb.org", "tmdb.org");
        }
        return url;
    }
}

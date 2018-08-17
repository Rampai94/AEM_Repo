package com.aem.training.core.helpers;

import java.util.Map;

import javax.jcr.Session;

import com.day.cq.search.result.SearchResult;

public interface JCRQueryBuilder {

    /**
     * Gets the results.
     * 
     * @param map
     *            the map
     * @param session
     *            the session
     * @return the results
     */
    SearchResult getResults(Map<String, String> map, Session session);

    /**
     * Gets the results.
     * 
     * @param map
     *            the map
     * @param session
     *            the session
     * @param startIndex
     *            the start index
     * @param endIndex
     *            the end index
     * @return the results
     */

    SearchResult getResults(Map<String, String> map, Session session,
            long startIndex, long endIndex);

    /**
     * Checks if is resource avail in current page.
     * 
     * @param resourcePath
     *            the resource path
     * @param currentPagePath
     *            the current page path
     * @param session
     *            the session
     * @return true, if is resource avail in current page
     */

    boolean isResourceAvailInCurrentPage(String resourcePath,
            String currentPagePath, Session session);

    /**
     * This method will return all aem pages routes
     * 
     * @param angularSiteHomePath
     * @param session
     * @return result of all aem pages routes
     */

    public SearchResult getAEMRoutes(String angularSiteHomePath, Session session);

}

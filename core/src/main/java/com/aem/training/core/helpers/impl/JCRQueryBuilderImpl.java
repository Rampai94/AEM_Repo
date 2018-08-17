package com.aem.training.core.helpers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.training.core.constants.GlobalConstants;
import com.aem.training.core.helpers.JCRQueryBuilder;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(immediate = true, enabled = true, name = "com.aem.tmobile.core.helpers.impl.JCRQueryBuilderImpl", service = JCRQueryBuilder.class)
public class JCRQueryBuilderImpl implements JCRQueryBuilder {

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JCRQueryBuilderImpl.class);

    /** The query builder. */
    @Reference
    private QueryBuilder queryBuilder;

    /**
     * This method will execute the query and will give you a result object
     * 
     * {@inheritDoc}
     */
    public SearchResult getResults(Map<String, String> map, Session session) {

        Query query = createQuery(map, session);
        return executeQuery(query);
    }

    /**
     * This method will execute the query with start index and end index and
     * will give you a result object
     * 
     * {@inheritDoc}
     */
    public SearchResult getResults(Map<String, String> map, Session session,
            long startIndex, long endIndex) {

        Query query = createQuery(map, session);
        query.setStart(startIndex);
        query.setHitsPerPage(endIndex);
        return executeQuery(query);
    }

    /**
     * Gets the result nodes.
     * 
     * @param aSearchResult
     *            the a search result
     * @return the result nodes
     * @throws RepositoryException
     *             the repository exception
     */
    public List<Node> getResultNodes(SearchResult searchResult)
            throws RepositoryException {
        List<Node> nodeList = new ArrayList<Node>();

        for (Hit hit : searchResult.getHits()) {
            try {
                Node parentNode = hit.getNode();
                nodeList.add(parentNode);
            } catch (ItemNotFoundException itemNotFoundException) {
                LOGGER.error(
                        "Item Not Found Exception during getting node from hit :: {}",
                        itemNotFoundException);
            }
        }
        return nodeList;
    }

    /**
     * Creates the query.
     * 
     * @param map
     *            the map
     * @param session
     *            the session
     * @return the query
     */
    private Query createQuery(Map<String, String> map, Session session) {
        return this.queryBuilder.createQuery(PredicateGroup.create(map),
                session);
    }

    /**
     * Execute query.
     * 
     * @param query
     *            the query
     * @return the search result
     */
    private SearchResult executeQuery(Query query) {
        return query.getResult();
    }

    /**
     * This method is used to check is component is available on to the page or
     * not if it available on to the page will send true otherwise false.
     * 
     * @param currentPagePath
     *            the current page path
     * @param resourcePath
     *            the resource path
     * @param session
     *            the session
     * @return true, if is resource avail in current page
     */
    public boolean isResourceAvailInCurrentPage(String currentPagePath,
            String resourcePath, Session session) {
        boolean isAvailable = false;
        long totalMatches = 0;
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(currentPagePath);
        strBuild.append(GlobalConstants.SLASH_FORWARD);
        strBuild.append(JcrConstants.JCR_CONTENT);
        map.put(GlobalConstants.PREDICATE_PATH, strBuild.toString());
        map.put(GlobalConstants.PROPERTY, GlobalConstants.RESOURCE_TYPE);
        map.put(GlobalConstants.PREDICATE_PROPERTY_VALUE, resourcePath);
        SearchResult searchResult = getResults(map, session);
        totalMatches = searchResult.getTotalMatches();
        if (totalMatches > 0) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public SearchResult getAEMRoutes(String angularSiteHomePath, Session session) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("path", angularSiteHomePath);
        map.put("type", "cq:Page");
        map.put("p.limit", "-1");
        return getResults(map, session);
    }

}

package com.aem.training.core.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.training.core.helpers.JCRQueryBuilder;
import com.aem.training.core.service.ResourceResolverService;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "= Download Assets Service",
/*		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "AEMMaven13/components/structure/page",
		"sling.servlet.selectors=" + "assets",
		"sling.servlet.extensions=" + "json",*/
		"sling.servlet.paths=" + "/bin/Assets"})
public class DownloadAssets extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger
	 */
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverService resourceResolverService;

	@Reference
	private JCRQueryBuilder queryBuilder;

	private Session session;

	@Override
	protected void doGet(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) {
		try {
			ResourceResolver resourceResolver = resourceResolverService
					.getResourceResolver("datawrite");
			session = resourceResolver.adaptTo(Session.class);

			// Map to store query description
			Map<String, String> map = new HashMap<>();
			map.put("type", "dam:Asset");
			map.put("path", "/content/dam/we-retail/en/activities/biking");

			// Invoke the query using helper class
			SearchResult result = queryBuilder.getResults(map, session);

			// write out to the AEM Log file
			LOGGER.info("Search Results: " + result.getTotalMatches());

			// Create a MAP to store results
			Map<String, InputStream> dataMap = new HashMap<>();

			// iterating over the results
			for (Hit hit : result.getHits()) {

				// Convert the HIT to an asset - each asset will be placed into
				// a ZIP for downloading
				String path = hit.getPath();
				Resource rs = resourceResolver.getResource(path);
				Asset asset = rs.adaptTo(Asset.class);

				// We have the File Name and the inputstream
				InputStream data = asset.getOriginal().getStream();
				String name = asset.getName();

				// Add to map
				dataMap.put(name, data); // key is fileName and value is
											// inputStream - this will all be
											// placed in ZIP file
			}

			// ZIP up the AEM DAM Assets
			byte[] zip = zipFiles(dataMap);

			ServletOutputStream sos = response.getOutputStream();

			response.setContentType("application/zip");
			response.setHeader("Content-Disposition",
					"attachment;filename=dam.zip");

			// Write bytes to tmp file.
			sos.write(zip);
			sos.flush();
			LOGGER.info("The ZIP is sent");

		} catch (LoginException | RepositoryException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info("Exception in DownloadAssets :: doGet :: {}", e);
		}

	}

	/**
	 * Create the ZIP with AEM DAM Assets.
	 */
	private byte[] zipFiles(Map data) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		byte bytes[] = new byte[2048];
		Iterator<Map.Entry<String, InputStream>> entries = data.entrySet()
				.iterator();

		while (entries.hasNext()) {
			Map.Entry<String, InputStream> entry = entries.next();

			String fileName = (String) entry.getKey();
			InputStream is1 = (InputStream) entry.getValue();

			BufferedInputStream bis = new BufferedInputStream(is1);

			// populate the next entry of the ZIP with the AEM DAM asset
			zos.putNextEntry(new ZipEntry(fileName));

			int bytesRead;
			while ((bytesRead = bis.read(bytes)) != -1) {
				zos.write(bytes, 0, bytesRead);

			}
			zos.closeEntry();
			bis.close();
			is1.close();

		}

		zos.flush();
		baos.flush();
		zos.close();
		baos.close();

		return baos.toByteArray();
	}

}

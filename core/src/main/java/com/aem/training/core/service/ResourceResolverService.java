package com.aem.training.core.service;

import javax.jcr.Session;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverService {
	public ResourceResolver getResourceResolver(String serviceName) throws LoginException;
	public void close(ResourceResolver resourceResolver);	
	public void close(Session session);
}

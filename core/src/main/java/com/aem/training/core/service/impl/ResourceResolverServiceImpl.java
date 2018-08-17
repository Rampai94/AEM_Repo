package com.aem.training.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.training.core.service.ResourceResolverService;

@Component(immediate = true, service = ResourceResolverService.class)
public class ResourceResolverServiceImpl implements ResourceResolverService {
	
	@Reference
	private ResourceResolverFactory resolverFactory;

	public ResourceResolver getResourceResolver(String serviceName)
			throws LoginException {
		Map<String, Object> param = new HashMap<>();
		param.put(ResourceResolverFactory.SUBSERVICE, serviceName);
		return resolverFactory.getServiceResourceResolver(param);
	}

	public void close(ResourceResolver resourceResolver) {
		if (resourceResolver != null && resourceResolver.isLive()) {
			try {
				resourceResolver.close();
				resourceResolver = null;
			} catch (Exception exception) {
			}
		}
	}
	    
	public void close(Session session) {
    	if (session != null && session.isLive()) {
            session.logout();
            session = null;
        }
    }
}


package org.idempiere.hybrid.search.event;

import org.adempiere.base.AnnotationBasedEventManager;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class HybridSearchAnnotationBasedEventManager extends AnnotationBasedEventManager {

	@Override
	public String[] getPackages() {
		return new String[] {"org.idempiere.hybrid.search.event"};
	}

}

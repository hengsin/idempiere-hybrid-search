package org.idempiere.hybrid.search.event;

import org.adempiere.base.IServiceHolder;
import org.adempiere.base.Service;
import org.adempiere.base.annotation.EventTopicDelegate;
import org.adempiere.base.annotation.ModelEventTopic;
import org.adempiere.base.event.annotations.ModelEventDelegate;
import org.adempiere.base.event.annotations.po.PostCreate;
import org.adempiere.base.event.annotations.po.PostDelete;
import org.adempiere.base.event.annotations.po.PostUpdate;
import org.compiere.model.MSearchDefinition;
import org.osgi.service.event.Event;

@EventTopicDelegate
@ModelEventTopic(modelClass = MSearchDefinition.class)
public class MSearchDefinitionEventDelegate extends ModelEventDelegate<MSearchDefinition> {

	public MSearchDefinitionEventDelegate(MSearchDefinition po, Event event) {
		super(po, event);
	}

	@PostCreate
	public void onPostCreate() {
		MSearchDefinition searchDefinition = getModel();
		IServiceHolder<HybridSearchEventHandler> serviceHolder = Service.locator().locate(HybridSearchEventHandler.class);
		HybridSearchEventHandler eventHandler = serviceHolder.getService();
		if (eventHandler != null)
			eventHandler.postAdd(searchDefinition);
	}
	
	@PostUpdate
	public void onPostUpdate() {
		MSearchDefinition searchDefinition = getModel();
		IServiceHolder<HybridSearchEventHandler> serviceHolder = Service.locator().locate(HybridSearchEventHandler.class);
		HybridSearchEventHandler eventHandler = serviceHolder.getService();
		if (eventHandler != null)
			eventHandler.postChange(searchDefinition);
	}
	
	@PostDelete
	public void onPostDelete() {
		MSearchDefinition searchDefinition = getModel();
		IServiceHolder<HybridSearchEventHandler> serviceHolder = Service.locator().locate(HybridSearchEventHandler.class);
		HybridSearchEventHandler eventHandler = serviceHolder.getService();
		if (eventHandler != null)
			eventHandler.postDelete(searchDefinition);
	}
}

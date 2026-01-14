package org.idempiere.hybrid.search.event;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.compiere.Adempiere;
import org.compiere.model.MColumn;
import org.compiere.model.MSearchDefinition;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.ServerStateChangeEvent;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.idempiere.hybrid.search.model.I_HYS_SearchIndex;
import org.idempiere.hybrid.search.model.MSearchColumn;
import org.idempiere.hybrid.search.model.MSearchIndex;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Event handler for hybrid search index management.
 * 
 * @author hengsin
 */
@Component(service = { HybridSearchEventHandler.class, EventHandler.class }, immediate = true)
public class HybridSearchEventHandler extends AbstractEventHandler {

	@SuppressWarnings("unused")
	private static final CLogger log = CLogger.getCLogger(HybridSearchEventHandler.class);
	private List<MSearchDefinition> searchDefinitions;

	@Override
	protected void doHandleEvent(Event event) {
		PO po = getPO(event);
		String type = event.getTopic();

		if (searchDefinitions == null || searchDefinitions.size() == 0)
			return;

		for (MSearchDefinition msd : searchDefinitions) {
			if (msd.getAD_Table_ID() != po.get_Table_ID())
				continue;
			if (type.equals(IEventTopics.PO_POST_CREATE)) {
				handleAdd(msd, po);
			} else if (type.equals(IEventTopics.PO_POST_UPADTE)) {
				handleChange(msd, po);
			} else if (type.equals(IEventTopics.PO_POST_DELETE)) {
				handleDelete(msd, po);
			}
		}
	}

	private void handleAdd(MSearchDefinition msd, PO po) {
		int id = po.get_ID();
		if (id > 0 && po.get_KeyColumns().length == 1) {
			if (!Util.isEmpty(msd.getQuery(), true)) {			
				String whereClause = po.get_KeyColumns()[0] + "=? AND (" + msd.getQuery() + ")";
				int count = new Query(Env.getCtx(), po.get_TableName(), whereClause, null)
						.setParameters(id)
						.count();
				if (count == 0)
					return;
			}
		} else {
			return;
		}
		
		ArrayList<String> languages = Env.getLoginLanguages();
		for (String lang : languages) {
			MSearchIndex index = new MSearchIndex(po.getCtx(), 0, po.get_TrxName());
			index.setAD_SearchDefinition_ID(msd.getAD_SearchDefinition_ID());
			index.setAD_Table_ID(po.get_Table_ID());
			index.setRecord_ID(po.get_ID());
			index.setAD_Language(lang);
			index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Pending);
			index.saveEx();
		}
	}

	private void handleChange(MSearchDefinition msd, PO po) {
		MSearchColumn[] columns = MSearchColumn.getColumns(msd.getAD_SearchDefinition_ID(), po.get_TrxName());
		boolean match = false;
		for (MSearchColumn col : columns) {
			if (po.is_ValueChanged(MColumn.getColumnName(Env.getCtx(), col.getAD_Column_ID()))) {
				match = true;
				break;
			}
		}

		if (!match)
			return;

		int id = po.get_ID();
		if (id > 0 && po.get_KeyColumns().length == 1) {
			if (!Util.isEmpty(msd.getQuery(), true)) {			
				String whereClause = po.get_KeyColumns()[0] + "=? AND (" + msd.getQuery() + ")";
				int count = new Query(Env.getCtx(), po.get_TableName(), whereClause, null)
						.setParameters(id)
						.count();
				if (count == 0)
					return;
			}
		} else {
			return;
		}
		
		ArrayList<String> languages = Env.getLoginLanguages();
		for (String lang : languages) {
			MSearchIndex index = new Query(po.getCtx(), I_HYS_SearchIndex.Table_Name,
					I_HYS_SearchIndex.COLUMNNAME_AD_SearchDefinition_ID + "=? AND " +
							I_HYS_SearchIndex.COLUMNNAME_Record_ID + "=? AND " +
							I_HYS_SearchIndex.COLUMNNAME_AD_Language + "=?",
					po.get_TrxName())
					.setParameters(msd.getAD_SearchDefinition_ID(), po.get_ID(), lang)
					.first();

			if (index == null) {
				index = new MSearchIndex(po.getCtx(), 0, po.get_TrxName());
				index.setAD_SearchDefinition_ID(msd.getAD_SearchDefinition_ID());
				index.setAD_Table_ID(po.get_Table_ID());
				index.setRecord_ID(po.get_ID());
				index.setAD_Language(lang);
			}

			index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Pending);
			index.saveEx();
		}
	}

	private void handleDelete(MSearchDefinition msd, PO po) {
		List<MSearchIndex> list = new Query(po.getCtx(), I_HYS_SearchIndex.Table_Name,
				I_HYS_SearchIndex.COLUMNNAME_AD_SearchDefinition_ID + "=? AND " +
						I_HYS_SearchIndex.COLUMNNAME_Record_ID + "=?",
				po.get_TrxName())
				.setParameters(msd.getAD_SearchDefinition_ID(), po.get_ID())
				.list();

		for (MSearchIndex index : list) {
			index.deleteEx(true);
		}
	}

	@Override
	@Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
	public void bindEventManager(IEventManager eventManager) {
		super.bindEventManager(eventManager);
	}

	@Override
	public void unbindEventManager(IEventManager eventManager) {
		super.unbindEventManager(eventManager);
	}

	@Override
	protected void initialize() {
		if (Adempiere.isStarted())
			doInitialize();
		else
			Adempiere.addServerStateChangeListener(e ->  {
				if (e.getEventType() == ServerStateChangeEvent.SERVER_START)
					doInitialize();
			});
	}

	private void doInitialize() {
		searchDefinitions = new Query(Env.getCtx(), MSearchDefinition.Table_Name,
				MSearchDefinition.COLUMNNAME_SearchType + "=?", null)
				.setParameters(MSearchIndex.SEARCHTYPE_HYBRID)
				.setOnlyActiveRecords(true)
				.list();
		
		initializeEventRegistration();
	}

	private void initializeEventRegistration() {
		eventManager.unregister(this);
		
		List<Integer> tableIds = new ArrayList<>();
		for (MSearchDefinition msd : searchDefinitions) {
			if (tableIds.contains(msd.getAD_Table_ID()))
				continue;
			tableIds.add(msd.getAD_Table_ID());
			registerEvents(msd);
		}
	}

	private void registerEvents(MSearchDefinition msd) {
		registerTableEvent(MTable.getTableName(Env.getCtx(), msd.getAD_Table_ID()), IEventTopics.PO_POST_CREATE);
		registerTableEvent(MTable.getTableName(Env.getCtx(), msd.getAD_Table_ID()), IEventTopics.PO_POST_UPADTE);
		registerTableEvent(MTable.getTableName(Env.getCtx(), msd.getAD_Table_ID()), IEventTopics.PO_POST_DELETE);
	}

	public void postAdd(MSearchDefinition msd) {
		if (msd.getSearchType() == null || !msd.getSearchType().equals(MSearchIndex.SEARCHTYPE_HYBRID))
			return;
		
		if (searchDefinitions == null) {
			searchDefinitions = new ArrayList<>();
		}
		searchDefinitions.add(msd);
		for (MSearchDefinition existingMsd : searchDefinitions) {
			if (existingMsd.getAD_Table_ID() == msd.getAD_Table_ID()) {
				// already registered
				return;
			}
		}
		
		registerEvents(msd);
	}
	
	public void postDelete(MSearchDefinition msd) {
		if (searchDefinitions == null || searchDefinitions.size() == 0)
			return;

		boolean removed = searchDefinitions.removeIf(def -> def.getAD_SearchDefinition_ID() == msd.getAD_SearchDefinition_ID());
		// there's no api to unregister specific table event, so re-initialize all
		if (removed)
			initializeEventRegistration();
	}
	
	public void postChange(MSearchDefinition msd) {
		if (msd.getSearchType() == null || !msd.getSearchType().equals(MSearchIndex.SEARCHTYPE_HYBRID)) {
			postDelete(msd);
			return;
		}
		
		if (searchDefinitions == null) {
			searchDefinitions = new ArrayList<>();
		}
		
		boolean found = searchDefinitions.stream().anyMatch(def -> def.getAD_SearchDefinition_ID() == msd.getAD_SearchDefinition_ID());
		if (!found)
			postAdd(msd);
	}
}

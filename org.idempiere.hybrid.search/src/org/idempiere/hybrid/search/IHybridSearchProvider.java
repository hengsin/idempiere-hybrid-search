package org.idempiere.hybrid.search;

import org.adempiere.base.search.ISearchProvider;
import org.idempiere.hybrid.search.model.MSearchIndex;

/**
 * Interface for search providers that support hybrid indexing.
 * @author hengsin
 */
public interface IHybridSearchProvider extends ISearchProvider {

	public static final String MAX_RESULT_HYBRID_SEARCH = "MAX_RESULT_HYBRID_SEARCH";
	
	/**
	 * Update the index for the given search index record.
	 * @param index
	 */
	public void updateIndex(MSearchIndex index);
}

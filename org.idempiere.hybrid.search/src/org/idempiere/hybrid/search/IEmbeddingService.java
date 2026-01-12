/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - hengsin                         								   *
 **********************************************************************/
package org.idempiere.hybrid.search;

import java.util.List;

import dev.langchain4j.data.segment.TextSegment;

/**
 * Interface for services that generate vector embeddings from text.
 */
public interface IEmbeddingService {

    /**
     * Generate embedding vector for the given text.
     * 
     * @param text text to embed
     * @return float array representing the embedding vector
     * @throws Exception if generation fails
     */
    public float[] generateEmbedding(String text) throws Exception;

    /**
     * @return logical name of the provider (e.g. "openai", "ollama", "pgai")
     */
    public String getName();
    
    public List<TextSegment> splitToSegments(String content);
}

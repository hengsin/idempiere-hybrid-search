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
package org.idempiere.hybrid.search.onnx;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.idempiere.hybrid.search.IEmbeddingService;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.model.embedding.onnx.PoolingMode;

/**
 * Implementation of {@link IEmbeddingService} that uses an ONNX model
 * loaded from the bundle's model folder.
 * 
 * @author hengsin
 */
@Component(service = IEmbeddingService.class, immediate = true, property = {"service.ranking:Integer=10", "database=All"})
public class OnnxEmbeddingService implements IEmbeddingService {

    private CustomOnnxEmbeddingModel model;
	private HuggingFaceTokenizer huggingFaceTokenizer;

    @Activate
    public void activate(ComponentContext context) throws Exception {
        Bundle bundle = context.getBundleContext().getBundle();
        
        String modelDir = "lib/models/gemma/";
        
        Path modelPath = extractBundleResource(bundle, modelDir, "model_quantized.onnx");
        extractBundleResource(bundle, modelDir, "model_quantized.onnx_data");
        Path tokenizerPath = extractBundleResource(bundle, modelDir, "tokenizer.json");
        extractBundleResource(bundle, modelDir, "config.json");

        model = new CustomOnnxEmbeddingModel(modelPath.toAbsolutePath().toString(), tokenizerPath.toAbsolutePath().toString(), PoolingMode.MEAN);
        huggingFaceTokenizer = new HuggingFaceTokenizer();
    }

    private Path extractBundleResource(Bundle bundle, String dir, String fileName) throws Exception {
        String fullPath = dir + fileName;
        URL url = bundle.getEntry(fullPath);
        if (url == null) throw new RuntimeException("Resource not found: " + fullPath);

        // Use a path relative to the bundle's data storage        
        File file = bundle.getDataFile(fullPath);
        Path parentPath = Paths.get(file.getParent());
        parentPath.toFile().mkdirs();
        if (!file.exists()) {
	        try (InputStream in = url.openStream()) {
	           Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        }
    	}
        return file.toPath();
    }

    @Override
    public float[] generateEmbedding(String content) throws Exception {
    	Embedding embedding = model.embed(content).content();
        return embedding.vector();
    }

    @Override
    public String getName() {
        return "onnx";
    }

	@Override
	public List<TextSegment> splitToSegments(String content) {		
		DocumentSplitter splitter = DocumentSplitters.recursive(
			    300, // Smaller chunks for Gemma 300M
			    50,  // 10% overlap
			    huggingFaceTokenizer
			);
		Document document = Document.from(content);
		return splitter.split(document);				
	}
}

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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import dev.langchain4j.model.embedding.onnx.OnnxBertBiEncoder;
import dev.langchain4j.model.embedding.onnx.PoolingMode;

public class CustomOnnxBertBiEncoder extends OnnxBertBiEncoder {

	public CustomOnnxBertBiEncoder(String model, String tokenizer, PoolingMode poolingMode) {
		this(OrtEnvironment.getEnvironment(), model, tokenizer, poolingMode);
	}

	private CustomOnnxBertBiEncoder(OrtEnvironment env, String model, String tokenizer, PoolingMode poolingMode) {
		super(env, createSession(env, model), getInputStream(tokenizer), poolingMode);
	}

	private static OrtSession createSession(OrtEnvironment env, String modelPath) {
		try {
			return env.createSession(modelPath, new OrtSession.SessionOptions());
		} catch (OrtException e) {
			throw new RuntimeException(e);
		}
	}

	private static InputStream getInputStream(String path) {
		try {
			return Files.newInputStream(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public float[] embedInternal(String text) {
		try {
			Method embedMethod = OnnxBertBiEncoder.class.getDeclaredMethod("embed", String.class);
			embedMethod.setAccessible(true);
			Object result = embedMethod.invoke(this, text);
			
			Class<?> resultClass = result.getClass();
			Field embeddingField = resultClass.getDeclaredField("embedding");
			embeddingField.setAccessible(true);
			return (float[]) embeddingField.get(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
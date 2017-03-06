/*
 * Copyright 2014 Cyril Deba
 * http://www.scriptico.com
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.scriptico.beaconlocator.data.dto.enums;

/**
 * Enumeration class for the beacon activity content type
 * 
 * @author Cyril Deba
 * @version 1.0
 */
public enum ContentType {

    IMAGE("Image"), VIDEO("Video"), WEB_URL("Web URL"), APP_URL("App URL");

    private String label;

    private ContentType(String label) {
	this.label = label;
    }

    @Override
    public String toString() {
	return label;
    }

}

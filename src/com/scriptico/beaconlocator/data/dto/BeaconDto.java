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
package com.scriptico.beaconlocator.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cyril Deba
 * @version 1.0
 *
 */
public class BeaconDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String uuid;
    private Integer majorId;
    private Integer minorId;

    private List<BeaconActionDto> actions = new ArrayList<BeaconActionDto>(0);

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    public Integer getMajorId() {
	return majorId;
    }

    public void setMajorId(int majorId) {
	this.majorId = majorId;
    }

    public Integer getMinorId() {
	return minorId;
    }

    public void setMinorId(int minorId) {
	this.minorId = minorId;
    }

    public void addAction(BeaconActionDto action) {
	actions.add(action);
    }

    public List<BeaconActionDto> getActions() {
	return actions;
    }

    public void setActions(List<BeaconActionDto> actions) {
	if (null == this.actions) {
	    this.actions = actions;
	} else {
	    this.actions.addAll(actions);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((majorId == null) ? 0 : majorId.hashCode());
	result = prime * result + ((minorId == null) ? 0 : minorId.hashCode());
	result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BeaconDto other = (BeaconDto) obj;
	if (majorId == null) {
	    if (other.majorId != null)
		return false;
	} else if (!majorId.equals(other.majorId))
	    return false;
	if (minorId == null) {
	    if (other.minorId != null)
		return false;
	} else if (!minorId.equals(other.minorId))
	    return false;
	if (uuid == null) {
	    if (other.uuid != null)
		return false;
	} else if (!uuid.equals(other.uuid))
	    return false;
	return true;
    }

}

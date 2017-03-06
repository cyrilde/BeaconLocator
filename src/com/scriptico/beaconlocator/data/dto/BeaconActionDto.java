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
import java.sql.Date;

import com.scriptico.beaconlocator.data.dto.enums.ContentType;

/**
 * Data transfer object for the beacon action
 * 
 * @author Cyril Deba
 * @version 1.0
 * 
 * @see BeaconDto
 */
public class BeaconActionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private double distance;

    private Date timeout;
    private Date lastLaunch;

    private ContentType contentType;
    private ContentHolderDto content;

    private String notificationMessage;
    private ContentHolderDto notificationIcon;

    /**
     * Returns the beacon action name
     * 
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Returns the beacon action distance
     * 
     * The distance is optional parameter and indicates the distance in meters
     * when the action for the detected device should be performed. If the
     * distance value is not defined, the action will be performed when the
     * device is detected, and there is no other action with defined distance
     * that fits the found device.
     * 
     * @return the distance
     */
    public double getDistance() {
	return distance;
    }

    /**
     * @param distance
     *            the distance to set
     */
    public void setDistance(double distance) {
	this.distance = distance;
    }

    /**
     * @return the timeout
     */
    public Date getTimeout() {
	return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(Date timeout) {
	this.timeout = timeout;
    }

    /**
     * @return the lastLaunch
     */
    public Date getLastLaunch() {
	return lastLaunch;
    }

    /**
     * @param lastLaunch
     *            the lastLaunch to set
     */
    public void setLastLaunch(Date lastLaunch) {
	this.lastLaunch = lastLaunch;
    }

    /**
     * @return the contentType
     */
    public ContentType getContentType() {
	return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(ContentType contentType) {
	this.contentType = contentType;
    }

    /**
     * @return the content
     */
    public ContentHolderDto getContent() {
	return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(ContentHolderDto content) {
	this.content = content;
    }

    /**
     * @return the contentUrl
     */
    public String getContentUrl() {
	return (null != content) ? content.getUrl() : "";
    }

    /**
     * @return the notificationMessage
     */
    public String getNotificationMessage() {
	return notificationMessage;
    }

    /**
     * @param notificationMessage
     *            the notificationMessage to set
     */
    public void setNotificationMessage(String notificationMessage) {
	this.notificationMessage = notificationMessage;
    }

    /**
     * @return the notificationIcon
     */
    public ContentHolderDto getNotificationIcon() {
	return notificationIcon;
    }

    /**
     * @param notificationIcon
     *            the notificationIcon to set
     */
    public void setNotificationIcon(ContentHolderDto notificationIcon) {
	this.notificationIcon = notificationIcon;
    }

}

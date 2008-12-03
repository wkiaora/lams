/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */

package org.lamsfoundation.lams.tool.mdglos.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;

/**
 * @hibernate.class table="tl_mdglos10_mdlglossary"
 */

public class MdlGlossary implements java.io.Serializable, Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = 4093029387948273849L;

    static Logger log = Logger.getLogger(MdlGlossary.class.getName());

    private static final String CUSTOM_CSV_MAP_PARAM_USER = "user";
    private static final String CUSTOM_CSV_MAP_PARAM_COURSE = "course";
    private static final String CUSTOM_CSV_MAP_PARAM_SECTION = "section";

    // Fields
    /**
     * 
     */
    private Long uid;
    private Date createDate;
    private Date updateDate;
    private boolean defineLater;
    private Long toolContentId;
    private Long extToolContentId;
    private Set<MdlGlossarySession> mdlGlossarySessions;
    private boolean runOffline;
    private boolean contentInUse;
    private String extUsername;
    private String extCourseId;
    private String extSection;

    //*********** NON Persistent fields
    private IToolContentHandler toolContentHandler;

    // Constructors

    public MdlGlossary(Long uid, Date createDate, Date updateDate, boolean defineLater, boolean runOffline,
	    boolean contentInUse, Long toolContentId, Long extToolContentId, String extUsername, String extCourseId,
	    String extSection, Set<MdlGlossarySession> mdlGlossarySessions, IToolContentHandler toolContentHandler) {
	super();
	this.uid = uid;
	this.createDate = createDate;
	this.updateDate = updateDate;
	this.defineLater = defineLater;
	this.toolContentId = toolContentId;
	this.extToolContentId = extToolContentId;
	this.mdlGlossarySessions = mdlGlossarySessions;
	this.toolContentHandler = toolContentHandler;
	this.runOffline = runOffline;
	this.contentInUse = contentInUse;
	this.extCourseId = extCourseId;
	this.extUsername = extUsername;
	this.extSection = extSection;
    }

    /** default constructor */
    public MdlGlossary() {
    }

    // Property accessors
    /**
     * @hibernate.id generator-class="native" type="java.lang.Long" column="uid"
     * 
     */
    public Long getUid() {
	return this.uid;
    }

    public void setUid(Long uid) {
	this.uid = uid;
    }

    /**
     * @hibernate.property column="create_date"
     * 
     */
    public Date getCreateDate() {
	return this.createDate;
    }

    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }

    /**
     * @hibernate.property column="update_date"
     * 
     */
    public Date getUpdateDate() {
	return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
	this.updateDate = updateDate;
    }

    /**
     * @hibernate.property column="define_later" length="1"
     * 
     */
    public boolean isDefineLater() {
	return this.defineLater;
    }

    public void setDefineLater(boolean defineLater) {
	this.defineLater = defineLater;
    }

    /**
     * @hibernate.property column="content_in_use" length="1"
     * 
     */
    public boolean isContentInUse() {
	return contentInUse;
    }

    public void setContentInUse(boolean contentInUse) {
	this.contentInUse = contentInUse;
    }

    /**
     * @hibernate.property column="run_offline" length="1"
     * 
     */
    public boolean isRunOffline() {
	return runOffline;
    }

    public void setRunOffline(boolean runOffline) {
	this.runOffline = runOffline;
    }

    /**
     * @hibernate.property column="tool_content_id" length="20"
     * 
     */
    public Long getToolContentId() {
	return this.toolContentId;
    }

    public void setToolContentId(Long toolContentId) {
	this.toolContentId = toolContentId;
    }

    /**
     * @hibernate.property column="ext_tool_content_id" length="20"
     * 
     */
    public Long getExtToolContentId() {
	return extToolContentId;
    }

    public void setExtToolContentId(Long extToolContentId) {
	this.extToolContentId = extToolContentId;
    }

    /**
     * @hibernate.property column="ext_user_name" length="255"
     * 
     */
    public String getExtUsername() {
	return extUsername;
    }

    public void setExtUsername(String extUsername) {
	this.extUsername = extUsername;
    }

    /**
     * @hibernate.property column="ext_course_id" length="255"
     * 
     */
    public String getExtCourseId() {
	return extCourseId;
    }

    public void setExtCourseId(String extCourseId) {
	this.extCourseId = extCourseId;
    }

    /**
     * @hibernate.property column="ext_section" length="255"
     * 
     */
    public String getExtSection() {
	return extSection;
    }

    public void setExtSection(String extSection) {
	this.extSection = extSection;
    }

    public static long getSerialVersionUID() {
	return serialVersionUID;
    }

    /**
     * @hibernate.set lazy="true" inverse="true" cascade="none"
     * @hibernate.collection-key column="mdlglossary_uid"
     * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.mdglos.model.MdlGlossarySession"
     * 
     */
    public Set<MdlGlossarySession> getMdlGlossarySessions() {
	return mdlGlossarySessions;
    }

    public void setMdlGlossarySessions(Set<MdlGlossarySession> mdlGlossarySessions) {
	this.mdlGlossarySessions = mdlGlossarySessions;
    }

    public IToolContentHandler getToolContentHandler() {
	return toolContentHandler;
    }

    public void setToolContentHandler(IToolContentHandler toolContentHandler) {
	this.toolContentHandler = toolContentHandler;
    }

    /**
     * toString
     * 
     * @return String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();

	buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
	buffer.append("toolContentId").append("='").append(getToolContentId()).append("' ");
	buffer.append("exttoolContentId").append("='").append(getExtToolContentId()).append("' ");
	buffer.append("]");

	return buffer.toString();
    }

    public boolean equals(Object other) {
	if ((this == other))
	    return true;
	if ((other == null))
	    return false;
	if (!(other instanceof MdlGlossary))
	    return false;
	MdlGlossary castOther = (MdlGlossary) other;

	return ((this.getUid() == castOther.getUid()) || (this.getUid() != null && castOther.getUid() != null && this
		.getUid().equals(castOther.getUid())));
    }

    public int hashCode() {
	int result = 17;
	result = 37 * result + (getUid() == null ? 0 : this.getUid().hashCode());
	return result;
    }

    public static MdlGlossary newInstance(MdlGlossary fromContent, Long toContentId,
	    IToolContentHandler mdlGlossaryToolContentHandler) {
	MdlGlossary toContent = new MdlGlossary();
	fromContent.toolContentHandler = mdlGlossaryToolContentHandler;
	toContent = (MdlGlossary) fromContent.clone();
	toContent.setToolContentId(toContentId);
	toContent.setCreateDate(new Date());
	return toContent;
    }

    protected Object clone() {

	MdlGlossary mdlGlossary = null;
	try {
	    mdlGlossary = (MdlGlossary) super.clone();
	    mdlGlossary.setUid(null);
	    mdlGlossary.mdlGlossarySessions = new HashSet<MdlGlossarySession>();

	} catch (CloneNotSupportedException cnse) {
	    log.error("Error cloning " + MdlGlossary.class);
	}
	return mdlGlossary;
    }

    public void setByCustomCSVHashMap(HashMap<String, String> params) {
	this.extUsername = params.get(CUSTOM_CSV_MAP_PARAM_USER);
	this.extCourseId = params.get(CUSTOM_CSV_MAP_PARAM_COURSE);
	this.extSection = params.get(CUSTOM_CSV_MAP_PARAM_SECTION);
    }
}

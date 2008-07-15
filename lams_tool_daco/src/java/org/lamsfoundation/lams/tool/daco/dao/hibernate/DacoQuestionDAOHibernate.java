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
package org.lamsfoundation.lams.tool.daco.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.tool.daco.dao.DacoQuestionDAO;
import org.lamsfoundation.lams.tool.daco.model.DacoQuestion;

public class DacoQuestionDAOHibernate extends BaseDAOHibernate implements DacoQuestionDAO {

	private static final String FIND_BY_CONTENT_UID = "from " + DacoQuestion.class.getName()
			+ " where content_uid = ? order by create_date asc";

	public List getByContentUid(Long dacoUid) {
		return this.getHibernateTemplate().find(DacoQuestionDAOHibernate.FIND_BY_CONTENT_UID, dacoUid);
	}

	public DacoQuestion getByUid(Long dacoQuestionUid) {
		return (DacoQuestion) this.getObject(DacoQuestion.class, dacoQuestionUid);
	}
}
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

package org.lamsfoundation.lams.tool.taskList.service;

import javax.servlet.ServletContext;

import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.taskList.TaskListConstants;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * This class act as the proxy between web layer and service layer. It is
 * designed to decouple the presentation logic and business logic completely.
 * In this way, the presentation tier will no longer be aware of the changes in
 * service layer. Therefore we can feel free to switch the business logic
 * implementation.
 * </p>
 *
 * @author Dapeng.Ni
 */
public class TaskListServiceProxy {

    /**
     * Return the domain service object. It will delegate to the Spring
     * helper method to retrieve the proper bean from Spring bean factory.
     * 
     * @param servletContext
     *            the servletContext for current application
     * @return Shared taskList service object.
     */
    public static final ITaskListService getTaskListService(ServletContext servletContext) {
	return (ITaskListService) TaskListServiceProxy.getTaskListDomainService(servletContext);
    }

    public static final ToolSessionManager getSessionManager(ServletContext servletContext) {
	return (ToolSessionManager) TaskListServiceProxy.getTaskListDomainService(servletContext);
    }

    public static final ToolContentManager getContentManager(ServletContext servletContext) {
	return (ToolContentManager) TaskListServiceProxy.getTaskListDomainService(servletContext);
    }

    private static Object getTaskListDomainService(ServletContext servletContext) {
	WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	return wac.getBean(TaskListConstants.TASKLIST_SERVICE);
    }

}

/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.usermanagement.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.UserOrganisationRole;
import org.lamsfoundation.lams.usermanagement.service.UserManagementService;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * <p>
 * <a href="AdminPreparer.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:fyang@melcoe.mq.edu.au">Fei Yang</a>
 */
public class AdminPreparer {

	public static void prepare(Organisation org, HttpServletRequest request, UserManagementService service){
		UserOrganisationRole userOrgRole = null;
		if(org.getParentOrganisation()!=null){
			userOrgRole = service.getUserOrganisationRole(request.getRemoteUser(),org.getParentOrganisation().getOrganisationId(),Role.ADMIN);	
		}
		List childOrgs = service.getChildOrganisations(org);
		for(int i=0; i<childOrgs.size();i++){
			Organisation childOrg = (Organisation)childOrgs.get(i);
			if(service.getUserOrganisationRole(request.getRemoteUser(),childOrg.getOrganisationId(),Role.ADMIN)==null){
				childOrgs.remove(i);
			}
		}
		if(childOrgs.size()!=0){
			Set childOrganisations = new HashSet();
			childOrganisations.addAll(childOrgs);
			org.setChildOrganisations(childOrganisations);
		}else{
			org.setChildOrganisations(null);
		}
		request.setAttribute(AttributeNames.ADMIN_ORGANISATION,org);
		if(userOrgRole!=null){
			request.setAttribute(AttributeNames.ADMIN_PARENT_ACCESS,"true");
		}
	}
}

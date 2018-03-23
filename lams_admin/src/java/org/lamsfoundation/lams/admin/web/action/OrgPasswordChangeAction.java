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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.admin.web.action;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.lamsfoundation.lams.admin.AdminConstants;
import org.lamsfoundation.lams.admin.service.AdminServiceProxy;
import org.lamsfoundation.lams.events.IEventNotificationService;
import org.lamsfoundation.lams.integration.security.RandomPasswordGenerator;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.HashUtil;
import org.lamsfoundation.lams.util.JsonUtil;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.ValidationUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OrgPasswordChangeAction extends DispatchAction {

    private static Logger log = Logger.getLogger(OrgPasswordChangeAction.class);

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	Integer organisationID = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID);
	DynaActionForm passForm = (DynaActionForm) form;
	passForm.set(AttributeNames.PARAM_ORGANISATION_ID, organisationID);
	IUserManagementService userManagementService = AdminServiceProxy.getService(getServlet().getServletContext());
	Organisation organisation = (Organisation) userManagementService.findById(Organisation.class, organisationID);
	passForm.set(AttributeNames.PARAM_ORGANISATION_NAME, organisation.getName());
	passForm.set("isStaffChange", true);
	passForm.set("isLearnerChange", true);
	passForm.set("staffPass", RandomPasswordGenerator.nextPasswordValidated());
	passForm.set("learnerPass", RandomPasswordGenerator.nextPasswordValidated());

	return mapping.findForward("display");
    }

    public ActionForward generatePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	response.setContentType("text/plain;charset=utf-8");
	response.getWriter().print(RandomPasswordGenerator.nextPasswordValidated());
	return null;
    }

    public ActionForward getGridUsers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	Integer organisationID = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID);
	String role = WebUtil.readStrParam(request, AttributeNames.PARAM_ROLE);

	UserDTO userDTO = getUserDTO();
	Integer currentUserId = userDTO.getUserID();
	if (!AdminServiceProxy.getSecurityService(getServlet().getServletContext()).isSysadmin(currentUserId,
		"get grid users for org password change", false)) {
	    String warning = "User " + currentUserId + " is not a sysadmin";
	    log.warn(warning);
	    response.sendError(HttpServletResponse.SC_FORBIDDEN, warning);
	    return null;
	}

	int page = WebUtil.readIntParam(request, AdminConstants.PARAM_PAGE);
	int rowLimit = WebUtil.readIntParam(request, AdminConstants.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, AdminConstants.PARAM_SORD);
	String sortColumn = WebUtil.readStrParam(request, AdminConstants.PARAM_SIDX, true);

	// fetch staff or learners
	List<UserDTO> users = getUsersByRole(organisationID, role.equalsIgnoreCase("staff"), sortColumn, sortOrder);

	// paging
	int totalPages = 1;
	int totalUsers = users.size();
	if (rowLimit < users.size()) {
	    totalPages = new Double(
		    Math.ceil(new Integer(users.size()).doubleValue() / new Integer(rowLimit).doubleValue()))
			    .intValue();
	    int firstRow = (page - 1) * rowLimit;
	    int lastRow = firstRow + rowLimit;

	    if (lastRow > users.size()) {
		users = users.subList(firstRow, users.size());
	    } else {
		users = users.subList(firstRow, lastRow);
	    }
	}

	ObjectNode resultJSON = JsonNodeFactory.instance.objectNode();
	resultJSON.put(AdminConstants.ELEMENT_PAGE, page);
	resultJSON.put(AdminConstants.ELEMENT_TOTAL, totalPages);
	resultJSON.put(AdminConstants.ELEMENT_RECORDS, totalUsers);

	ArrayNode rowsJSON = JsonNodeFactory.instance.arrayNode();
	// build rows for grid
	for (UserDTO user : users) {
	    ObjectNode rowJSON = JsonNodeFactory.instance.objectNode();
	    rowJSON.put(AdminConstants.ELEMENT_ID, user.getUserID());

	    ArrayNode cellJSON = JsonNodeFactory.instance.arrayNode();
	    cellJSON.add(user.getFirstName() + " " + user.getLastName());
	    cellJSON.add(user.getLogin());
	    cellJSON.add(user.getEmail());

	    rowJSON.set(AdminConstants.ELEMENT_CELL, cellJSON);
	    rowsJSON.add(rowJSON);
	}

	resultJSON.set(AdminConstants.ELEMENT_ROWS, rowsJSON);

	response.setContentType("application/json;charset=utf-8");
	response.getWriter().print(resultJSON.toString());
	return null;
    }

    public ActionForward changePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	UserDTO userDTO = getUserDTO();
	Integer currentUserId = userDTO.getUserID();
	// security check
	if (!AdminServiceProxy.getSecurityService(getServlet().getServletContext()).isSysadmin(currentUserId,
		"org password change", false)) {
	    String warning = "User " + currentUserId + " is not a sysadmin";
	    log.warn(warning);
	    response.sendError(HttpServletResponse.SC_FORBIDDEN, warning);
	    return null;
	}

	DynaActionForm passForm = (DynaActionForm) form;
	Integer organisationID = (Integer) passForm.get(AttributeNames.PARAM_ORGANISATION_ID);
	Boolean email = (Boolean) passForm.get("email");
	Boolean force = (Boolean) passForm.get("force");

	Boolean isStaffChange = (Boolean) passForm.get("isStaffChange");
	Boolean isLearnerChange = (Boolean) passForm.get("isLearnerChange");
	// get data needed for each group
	if (isStaffChange) {
	    ArrayNode excludedStaff = JsonUtil.readArray((String) passForm.get("excludedStaff"));
	    String staffPass = (String) passForm.get("staffPass");
	    Collection<User> users = getUsersByRole(organisationID, true);
	    Collection<Integer> changedUserIDs = changePassword(staffPass, users, excludedStaff, force);
	    if (email && !changedUserIDs.isEmpty()) {
		notifyOnPasswordChange(changedUserIDs, staffPass);
	    }
	}
	if (isLearnerChange) {
	    ArrayNode excludedLearners = JsonUtil.readArray((String) passForm.get("excludedLearners"));
	    String learnerPass = (String) passForm.get("learnerPass");
	    Collection<User> users = getUsersByRole(organisationID, false);
	    Collection<Integer> changedUserIDs = changePassword(learnerPass, users, excludedLearners, force);
	    if (email && !changedUserIDs.isEmpty()) {
		notifyOnPasswordChange(changedUserIDs, learnerPass);
	    }
	}

	request.setAttribute("success", true);
	return mapping.findForward("display");
    }

    private void notifyOnPasswordChange(Collection<Integer> userIDs, String password) {
	MessageService messageService = AdminServiceProxy.getMessageService(getServlet().getServletContext());
	AdminServiceProxy.getEventNotificationService(getServlet().getServletContext()).sendMessage(null,
		userIDs.toArray(new Integer[] {}), IEventNotificationService.DELIVERY_METHOD_MAIL,
		messageService.getMessage("admin.org.password.change.email.subject"),
		messageService.getMessage("admin.org.password.change.email.body", new String[] { password }), false);
    }

    private Set<Integer> changePassword(String password, Collection<User> users, ArrayNode excludedUsers,
	    boolean force) {
	if (!ValidationUtil.isPasswordValueValid(password, password)) {
	    // this should have been picked up by JS validator on the page!
	    throw new InvalidParameterException("Password does not pass validation");
	}
	Set<Integer> changedUserIDs = new TreeSet<Integer>();
	IUserManagementService userManagementService = AdminServiceProxy.getService(getServlet().getServletContext());
	for (User user : users) {
	    boolean excluded = false;
	    // skip excluded (unchecked on the page) users
	    for (int index = 0; index < excludedUsers.size(); index++) {
		Integer excludedUserID = excludedUsers.get(index).asInt();
		if (user.getUserId().equals(excludedUserID)) {
		    excluded = true;
		    break;
		}
	    }
	    if (excluded) {
		continue;
	    }
	    // change password
	    String salt = HashUtil.salt();
	    user.setSalt(salt);
	    user.setPassword(HashUtil.sha256(password, salt));
	    if (force) {
		user.setChangePassword(true);
	    }
	    userManagementService.saveUser(user);
	    changedUserIDs.add(user.getUserId());
	}
	return changedUserIDs;
    }

    /**
     * Get unsorted users for password change
     */
    @SuppressWarnings("unchecked")
    private List<User> getUsersByRole(Integer organisationID, boolean isStaff) {
	IUserManagementService userManagementService = AdminServiceProxy.getService(getServlet().getServletContext());
	Set<User> staff = new HashSet<User>();
	staff.addAll(userManagementService.getUsersFromOrganisationByRole(organisationID, Role.AUTHOR, true));
	staff.addAll(userManagementService.getUsersFromOrganisationByRole(organisationID, Role.MONITOR, true));

	Set<User> users = null;
	if (isStaff) {
	    users = staff;
	} else {
	    users = new HashSet<User>();
	    users.addAll(userManagementService.getUsersFromOrganisationByRole(organisationID, Role.LEARNER, true));
	    users.removeAll(staff);
	}
	return new LinkedList<User>(users);
    }

    /**
     * Gets sorted users for grids
     */
    private List<UserDTO> getUsersByRole(Integer organisationID, boolean isStaff, String sortBy, String sortOrder) {
	IUserManagementService userManagementService = AdminServiceProxy.getService(getServlet().getServletContext());
	List<UserDTO> staff = userManagementService.getAllUsers(organisationID,
		new String[] { Role.AUTHOR, Role.MONITOR }, null, null, sortBy, sortOrder, null);

	List<UserDTO> users = null;
	if (isStaff) {
	    users = staff;
	} else {
	    users = new LinkedList<UserDTO>();
	    users.addAll(userManagementService.getAllUsers(organisationID, new String[] { Role.LEARNER }, null, null,
		    sortBy, sortOrder, null));
	    users.removeAll(staff);
	}
	return users;
    }

    private UserDTO getUserDTO() {
	HttpSession ss = SessionManager.getSession();
	return (UserDTO) ss.getAttribute(AttributeNames.USER);
    }
}
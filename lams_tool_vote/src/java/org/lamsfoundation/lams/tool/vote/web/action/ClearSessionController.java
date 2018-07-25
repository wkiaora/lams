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

package org.lamsfoundation.lams.tool.vote.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.authoring.web.LamsAuthoringFinishController;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class give a chance to clear HttpSession when user save/close authoring page.
 *
 * @author Steve.Ni, Ozgur Demirtas
 */
@Controller
public class ClearSessionController extends LamsAuthoringFinishController implements VoteAppConstants {
    private static Logger logger = Logger.getLogger(ClearSessionController.class.getName());

    @Autowired
    private WebApplicationContext applicationContext;

    @RequestMapping("/clearsession")
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
	super.execute(request, response, applicationContext);
    }

    @Override
    public void clearSession(String customiseSessionID, HttpSession session, ToolAccessMode mode) {
	session.removeAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG);
	if (mode.isAuthor()) {
	    ClearSessionController.logger.debug("In Author mode");
	    session.removeAttribute(customiseSessionID);
	}
    }
}

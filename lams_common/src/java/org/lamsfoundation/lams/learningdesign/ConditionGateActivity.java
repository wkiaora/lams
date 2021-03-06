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

package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.learningdesign.dto.ValidationErrorDTO;
import org.lamsfoundation.lams.learningdesign.strategy.ConditionGateActivityStrategy;
import org.lamsfoundation.lams.tool.SystemTool;
import org.lamsfoundation.lams.util.MessageService;

/**
 * Gate activity that is based on tools' output and conditions.
 *
 * @author Marcin Cieslak
 *
 */
public class ConditionGateActivity extends GateActivity implements Serializable {

    /** full constructor */
    public ConditionGateActivity(Long activityId, Integer id, String description, String title, Integer xcoord,
	    Integer ycoord, Integer orderId, java.util.Date createDateTime, LearningLibrary learningLibrary,
	    Activity parentActivity, Activity libraryActivity, Integer parentUIID, LearningDesign learningDesign,
	    Grouping grouping, Integer activityTypeId, Transition transitionTo, Transition transitionFrom,
	    String languageFile, Boolean stopAfterActivity, Set inputActivities, Integer gateActivityLevelId,
	    SystemTool sysTool, Set branchActivityEntries) {
	super(activityId, id, description, title, xcoord, ycoord, orderId, createDateTime, learningLibrary,
		parentActivity, libraryActivity, parentUIID, learningDesign, grouping, activityTypeId, transitionTo,
		transitionFrom, languageFile, stopAfterActivity, inputActivities, gateActivityLevelId, sysTool,
		branchActivityEntries);
	super.simpleActivityStrategy = new ConditionGateActivityStrategy(this);
    }

    /** default constructor */
    public ConditionGateActivity() {
	super.simpleActivityStrategy = new ConditionGateActivityStrategy(this);
    }

    /** minimal constructor */
    public ConditionGateActivity(Long activityId, java.util.Date createDateTime,
	    org.lamsfoundation.lams.learningdesign.LearningLibrary learningLibrary,
	    org.lamsfoundation.lams.learningdesign.Activity parentActivity,
	    org.lamsfoundation.lams.learningdesign.LearningDesign learningDesign,
	    org.lamsfoundation.lams.learningdesign.Grouping grouping, Integer activityTypeId, Transition transitionTo,
	    Transition transitionFrom, Integer gateActivityLevelId) {
	super(activityId, createDateTime, learningLibrary, parentActivity, learningDesign, grouping, activityTypeId,
		transitionTo, transitionFrom, gateActivityLevelId);
	super.simpleActivityStrategy = new ConditionGateActivityStrategy(this);
    }

    /**
     * Makes a copy of the PermissionGateActivity for authoring, preview and monitoring enviornment
     *
     * @return PermissionGateActivity Returns a deep-copy of the originalActivity
     */
    @Override
    public Activity createCopy(int uiidOffset) {
	ConditionGateActivity newConditionGateActivity = new ConditionGateActivity();
	copyToNewActivity(newConditionGateActivity, uiidOffset);
	newConditionGateActivity.setGateOpen(new Boolean(false));
	newConditionGateActivity.setGateActivityLevelId(this.getGateActivityLevelId());

	if ((this.getBranchActivityEntries() != null) && (this.getBranchActivityEntries().size() > 0)) {
	    newConditionGateActivity.setBranchActivityEntries(new HashSet());
	    Iterator iter = this.getBranchActivityEntries().iterator();
	    while (iter.hasNext()) {
		BranchActivityEntry oldEntry = (BranchActivityEntry) iter.next();
		BranchActivityEntry newEntry = new BranchActivityEntry(null,
			LearningDesign.addOffset(oldEntry.getEntryUIID(), uiidOffset), null, newConditionGateActivity,
			null, oldEntry.getGateOpenWhenConditionMet());
		if (oldEntry.getCondition() != null) {
		    BranchCondition newCondition = oldEntry.getCondition().clone(uiidOffset);
		    newEntry.setCondition(newCondition);

		}
		newConditionGateActivity.getBranchActivityEntries().add(newEntry);
	    }
	}

	return newConditionGateActivity;

    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("activityId", getActivityId()).toString();
    }

    /**
     * @see org.lamsfoundation.lams.util.Nullable#isNull()
     */
    @Override
    public boolean isNull() {
	return false;
    }

    @Override
    public Vector validateActivity(MessageService messageService) {
	Vector listOfValidationErrors = new Vector();

	if ((getInputActivities() == null) || (getInputActivities().size() == 0)) {
	    listOfValidationErrors
		    .add(new ValidationErrorDTO(ValidationErrorDTO.CONDITION_GATE_ACTVITY_TOOLINPUT_ERROR_CODE,
			    messageService.getMessage(ValidationErrorDTO.CONDITION_GATE_ACTVITY_TOOLINPUT),
			    this.getActivityUIID()));
	}

	boolean conditionsExist = false;
	if (getBranchActivityEntries() != null) {
	    for (BranchActivityEntry entry : getBranchActivityEntries()) {
		BranchCondition condition = entry.getCondition();
		if (condition == null) {
		    listOfValidationErrors
			    .add(new ValidationErrorDTO(ValidationErrorDTO.BRANCH_CONDITION_INVALID_ERROR_CODE,
				    messageService.getMessage(ValidationErrorDTO.BRANCH_CONDITION_INVALID),
				    this.getActivityUIID()));
		} else {
		    conditionsExist = true;
		    if (!condition.isValid()) {
			listOfValidationErrors
				.add(new ValidationErrorDTO(ValidationErrorDTO.BRANCH_CONDITION_INVALID_ERROR_CODE,
					messageService.getMessage(ValidationErrorDTO.BRANCH_CONDITION_INVALID),
					this.getActivityUIID()));
		    }
		}
	    }
	}

	if (!conditionsExist) {
	    listOfValidationErrors
		    .add(new ValidationErrorDTO(ValidationErrorDTO.CONDITION_GATE_ACTVITY_CONDITION_ERROR_CODE,
			    messageService.getMessage(ValidationErrorDTO.CONDITION_GATE_ACTVITY_CONDITION),
			    this.getActivityUIID()));
	}
	return listOfValidationErrors;
    }
}
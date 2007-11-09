﻿/***************************************************************************
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
 * ************************************************************************
 */

import org.lamsfoundation.lams.authoring.cv.*;
import org.lamsfoundation.lams.authoring.br.BranchConnector;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.*

import mx.controls.*
import mx.utils.*
import mx.managers.*
import mx.events.*

/*
*
* @author      DC
* @version     0.1
* @comments    Property Inspector for the canvas
* 
*/
class PropertyInspector extends PropertyInspectorControls {
	
	//Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	
	
	/**
	 * Constructor
	 */
 
	public function PropertyInspector(){
		super();
		
		//Set up this class to use the Flash event delegation model
		EventDispatcher.initialize(this);
		
		//let it wait one frame to set up the components.
		MovieClipUtils.doLater(Proxy.create(this,init));
	}
	
	public function init():Void{
		
		_canvasModel = _canvasModel;
		_canvasController = _canvasController;
		_piIsExpended = false;
		_canvasModel.selectedItem = null;
		_canvasModel.setPIHeight(piHeightHide);
		_canvasModel.addEventListener('viewUpdate',this);
		
		for(var i=0; i<=10; i++)
			noSeqAct_cmb.addItem(i);

		clickTarget_mc.onRelease = Proxy.create (this, localOnRelease);
		clickTarget_mc.onReleaseOutside = Proxy.create (this, localOnReleaseOutside);
		
		//set up handlers
		title_txt.addEventListener("focusOut",this);
		desc_txt.addEventListener("focusOut",this);
		runOffline_chk.addEventListener("click",this);
		defineLater_chk.addEventListener("click",this);
		
		numGroups_rdo.addEventListener("click", Delegate.create(this, onGroupingMethodChange));
		numLearners_rdo.addEventListener("click", Delegate.create(this, onGroupingMethodChange));
		groupType_cmb.addEventListener("change", Delegate.create(this, onGroupTypeChange));
		gateType_cmb.addEventListener("change", Delegate.create(this, onGateTypeChange));
		branchType_cmb.addEventListener("change", Delegate.create(this, onBranchTypeChange));
		
		toolActs_cmb.addEventListener("change", Delegate.create(this, onBranchToolInputChange));
		appliedGroupingActivity_cmb.addEventListener("change", Delegate.create(this, onAppliedGroupingChange));
		
		minAct_stp.addEventListener("change", Delegate.create(this, updateOptionalData));
		minAct_stp.addEventListener("focusOut", Delegate.create(this, updateOptionalData));
		maxAct_stp.addEventListener("change", Delegate.create(this, updateOptionalData));
		maxAct_stp.addEventListener("focusOut", Delegate.create(this, updateOptionalData));
		
		noSeqAct_cmb.addEventListener("change", Delegate.create(this, updateOptionalSequenceData));
		
		days_stp.addEventListener("change", Delegate.create(this, onScheduleOffsetChange));
		hours_stp.addEventListener("change", Delegate.create(this, onScheduleOffsetChange));
		mins_stp.addEventListener("change", Delegate.create(this, onScheduleOffsetChange));
		days_stp.addEventListener("focusOut", Delegate.create(this, onScheduleOffsetChange));
		hours_stp.addEventListener("focusOut", Delegate.create(this, onScheduleOffsetChange));
		mins_stp.addEventListener("focusOut", Delegate.create(this, onScheduleOffsetChange));
		endHours_stp.addEventListener("change", Delegate.create(this, onScheduleOffsetChange));
		endMins_stp.addEventListener("change", Delegate.create(this,onScheduleOffsetChange));
		endHours_stp.addEventListener("focusOut", Delegate.create(this,onScheduleOffsetChange));
		endMins_stp.addEventListener("focusOut", Delegate.create(this, onScheduleOffsetChange));
		numGroups_stp.addEventListener("change", Delegate.create(this, updateGroupingMethodData));
		numLearners_stp.addEventListener("change", Delegate.create(this, updateGroupingMethodData));
		numLearners_stp.addEventListener("focusOut", Delegate.create(this, updateGroupingMethodData));
		numGroups_stp.addEventListener("focusOut", Delegate.create(this, updateGroupingMethodData));
		numRandomGroups_stp.addEventListener("change", Delegate.create(this, updateGroupingMethodData));
		numRandomGroups_stp.addEventListener("focusOut", Delegate.create(this, updateGroupingMethodData));
		
		_group_match_btn.addEventListener("click", Delegate.create(this, onGroupMatchClick));
		_group_naming_btn.addEventListener("click", Delegate.create(this, onGroupNamingClick));
		_tool_output_match_btn.addEventListener("click", Delegate.create(this, onConditionMatchClick));
		_conditions_setup_btn.addEventListener("click", Delegate.create(this, onConditionsSetupClick));
		
		_define_monitor_cb.addEventListener("click", Delegate.create(this, onDefineMonitorSelect));
		
		_pi_defaultBranch_cb.addEventListener("click", Delegate.create(this, onDefaultBranchSelect));
		
		this.onEnterFrame = setupLabels;
		
		this.tabChildren = true;
		setTabIndex();
		hideAllSteppers(false);
		
	}
	
	public function localOnRelease():Void{
		
		if (_piIsExpended){
			_piIsExpended = false
			_canvasModel.setPIHeight(piHeightHide);
			showExpand(true);
			
		}else {
			_piIsExpended = true
			_canvasModel.setPIHeight(piHeightFull);
			showExpand(false);
		}
	}
	
	public function isPIExpanded():Boolean{
		return _piIsExpended;
	}
	
	public function piFullHeight():Number{
		return piHeightFull;
	}
	
	
	public function showExpand(e:Boolean){
      
        //show hide the icons
		expand_mc._visible = e;
		collapse_mc._visible = !e;
                
    }
	
	public function localOnReleaseOutside():Void{
		Debugger.log('Release outside so no event has been fired, current state is: ' + _piIsExpended,Debugger.GEN,'localOnReleaseOutside','PropertyInspector');
		
	}
	/**
	 * Recieves update events from the model.
	 * @usage   
	 * @param   event 
	 * @return  
	 */
	public function viewUpdate(event:Object):Void{
		Debugger.log('Recived an Event dispather UPDATE!, updateType:'+event.updateType+', target'+event.target,4,'viewUpdate','PropertyInspector');
		//Update view from info object
      
        var cm:CanvasModel = event.target;
	   
	    switch (event.updateType){
            case 'SELECTED_ITEM' :
                updateItemProperties(cm);
                break;
                   
            default :
		}

	}
	
	/**
	 * Get called when something is selected in the cavas.
	 * Updates the details in the property inspector widgets
	 * Depending on the type of item selected, different controls will be shown
	 * @usage   
	 * @param   cm 
	 * @return  
	 */
	private function updateItemProperties(cm:CanvasModel):Void{
		//try to cast the selected item to see what we have (instance of des not seem to work)
			
		if(CanvasActivity(cm.selectedItem) != null){
			cover_pnl.visible = false;
			
			var ca = CanvasActivity(cm.selectedItem);
			var a:Activity = ca.activity;	
			
			var cao = CanvasOptionalActivity(cm.selectedItem);
			var cap = CanvasParallelActivity(cm.selectedItem);
			
			var caco:ComplexActivity = ComplexActivity(cao.activity);
			var cacp:ComplexActivity = ComplexActivity(cap.activity);
			
			if(a.isGateActivity()) {
				showGroupingControls(false);
				showToolActivityControls(false);
				showGeneralInfo(false);
				showOptionalControls(false);
				showBranchingControls(false);
				showGateControls(true, !a.readOnly);
				showAppliedGroupingControls(false);
				checkEnableGateControls();
				showGateActivityProperties(GateActivity(a));
				showAppliedGroupingProperties(a);
				
				showGeneralControls(true, !a.readOnly);
				
			} else if(a.isBranchingActivity()) {
				var ba:BranchingActivity = BranchingActivity(ca.activity);
				
				cover_pnl.visible = false;
				delimitLine._visible = true;
				
				showBranchingControls(true, !ba.readOnly);
				showGeneralControls(true, !ba.readOnly);
					
				showOptionalControls(false);
				showGeneralInfo(false);
				showToolActivityControls(false);
				showGateControls(false);
				showAppliedGroupingControls(false);
				
				showBranchingActivityProperties(ba);
				showAppliedGroupingProperties(ba);
					
			} else if(a.isGroupActivity()) {
			
			
				showGroupingControls(true, !a.readOnly);
				showGeneralControls(true, !a.readOnly);
				showOptionalControls(false);
				showBranchingControls(false);
				showGeneralInfo(false);
				showRelevantGroupOptions();
				showToolActivityControls(false);
				showGateControls(false);
				showAppliedGroupingControls(false);
				
				populateGroupingProperties(GroupingActivity(a));
				showAppliedGroupingProperties(a);
			
			} else if(a.isOptionalActivity()) {
				
				showGeneralControls(true, !a.readOnly);
				showGroupingControls(false);
				showToolActivityControls(false);
				showGateControls(false);
				showBranchingControls(false);
				showGeneralInfo(false);
				showAppliedGroupingControls(false);
				checkEnableOptionalControls(!a.readOnly);
				populateGroupingProperties(GroupingActivity(caco));
				showAppliedGroupingProperties(caco);
				showOptionalActivityProperties(caco);
				
			} else if(a.isParallelActivity()) {
				
				showOptionalControls(false);
				showGeneralControls(true, !a.readOnly);
				showGeneralInfo(false);
				showGroupingControls(false);
				showBranchingControls(false);
				showToolActivityControls(false);
				showGateControls(false);
				showAppliedGroupingControls(true, !a.readOnly);
				populateGroupingProperties(GroupingActivity(cacp));
				showAppliedGroupingProperties(cacp);
				showParallelActivityProperties(cacp);
				
			} else {
				showOptionalControls(false);
				showGeneralControls(true, !a.readOnly);
				showGroupingControls(false);
				showBranchingControls(false);
				showGeneralInfo(false);
				showAppliedGroupingControls(true, !a.readOnly);
				showToolActivityControls(true, !a.readOnly);
				showGateControls(false);
				showToolActivityProperties(ToolActivity(a));
				showAppliedGroupingProperties(a);
			}
			
			delimitLine._visible = true;
				
			title_txt.text = StringUtils.cleanNull(a.title);
			desc_txt.text = StringUtils.cleanNull(a.description);
			
			showBranchControls(false);
			
		} else if(CanvasOptionalActivity(cm.selectedItem) != null){
			var co = CanvasOptionalActivity(cm.selectedItem);
			var cca:ComplexActivity = ComplexActivity(co.activity);
			
			cover_pnl.visible = false;
			delimitLine._visible = true;
				
			showGeneralControls(true, !co.activity.readOnly);
			showBranchControls(false);
			showGroupingControls(false);
			showBranchingControls(false);
			showToolActivityControls(false);
			showGateControls(false);
			showGeneralInfo(false);
			showAppliedGroupingControls(false);
			checkEnableOptionalControls(!co.activity.readOnly);
			populateGroupingProperties(GroupingActivity(cca));
			showAppliedGroupingProperties(cca);
			showOptionalActivityProperties(cca);
				
			title_txt.text = StringUtils.cleanNull(cca.title);
			desc_txt.text = StringUtils.cleanNull(cca.description);
				
		} else if(CanvasParallelActivity(cm.selectedItem) != null) {
			var co = CanvasParallelActivity(cm.selectedItem);
			var cca:ComplexActivity = ComplexActivity(co.activity);
			
			cover_pnl.visible = false;
			delimitLine._visible = true;
			
			showOptionalControls(false);
			showGeneralControls(true, !co.activity.readOnly);
			showBranchControls(false);
			showGeneralInfo(false);
			showGroupingControls(false);
			showBranchingControls(false);
			
			showToolActivityControls(false);
			showGateControls(false);
			showAppliedGroupingControls(true, !co.activity.readOnly);
			
			populateGroupingProperties(GroupingActivity(cca));
			showAppliedGroupingProperties(cca);
			showParallelActivityProperties(cca);
			
			title_txt.text = StringUtils.cleanNull(cca.title);
			desc_txt.text = StringUtils.cleanNull(cca.description);
			
		} else if(CanvasTransition(cm.selectedItem) != null) {
			var ct = CanvasTransition(cm.selectedItem);
			var t:Transition = ct.transition;
			
			cover_pnl.visible = false;
			delimitLine._visible = false;
			
			showTransitionProperties(t);
			
			showGeneralInfo(false);
			showBranchControls(false);
			showGeneralControls(false);
			showOptionalControls(false);
			showGroupingControls(false);
			showBranchingControls(false);
			showToolActivityControls(false);
			showGateControls(false);
			showAppliedGroupingControls(false);
		
		} else if(BranchConnector(cm.selectedItem) != null) {
			var bc = BranchConnector(cm.selectedItem);
			var branch = bc.branch;
			
			cover_pnl.visible = false;
			delimitLine._visible = false;
			
			showBranchProperties(branch);
			
			showGeneralControls(true, !branch.sequenceActivity.readOnly);
			showBranchControls((Activity(cm.getCanvas().ddm.getActivityByUIID(branch.sequenceActivity.parentUIID)).activityTypeID == Activity.TOOL_BRANCHING_ACTIVITY_TYPE));
			
			showGeneralInfo(false);
			showOptionalControls(false);
			showGroupingControls(false);
			showBranchingControls(false);
			showToolActivityControls(false);
			showGateControls(false);
			showAppliedGroupingControls(false);
			
		} else {
			cover_pnl.visible = false;
			delimitLine._visible = false;
			
			toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> ";
			
			showGeneralInfo(true);
			showBranchControls(false);
			showGroupingControls(false);
			showBranchingControls(false);
			showGeneralControls(false);
			showOptionalControls(false);
			showRelevantGroupOptions();
			showToolActivityControls(false);
			showGateControls(false);
			showAppliedGroupingControls(false);
		}
	}
	
	private function showToolActivityProperties(ta:ToolActivity){
		
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+StringUtils.cleanNull(ta.toolDisplayName);
		runOffline_chk.selected = ta.runOffline;
		defineLater_chk.selected = ta.defineLater;
					
		currentGrouping_lbl.text = "GroupingUIID:"+StringUtils.cleanNull(ta.runOffline.groupingUIID);

	}
	
	private function showGeneralProperties(ta:ToolActivity){
		
	}
	
	private function showOptionalActivityProperties(ca:ComplexActivity){
		if(ca.isSequenceBased) return showOptionalSequenceActivityProperties(ca);
	
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('pi_optional_title');
		
		runOffline_chk.selected = ca.runOffline;
		defineLater_chk.selected = ca.defineLater;
		
		if(ca.minOptions == undefined) {
			minAct_stp.value = 0;
		} else {
			minAct_stp.value = ca.minOptions;
		}
		
		if(ca.maxOptions == undefined) {
			maxAct_stp.value = 0;
		} else {
			maxAct_stp.value = ca.maxOptions;
		}
		
		currentGrouping_lbl.text = "GroupingUIID:"+StringUtils.cleanNull(ca.runOffline.groupingUIID);
	}
	
	private function showOptionalSequenceActivityProperties(ca:ComplexActivity){
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('opt_activity_seq_title');
		
		runOffline_chk.selected = ca.runOffline;
		defineLater_chk.selected = ca.defineLater;
		
		if(ca.noSequences == undefined)
			noSeqAct_cmb.selectedIndex = 0;
		else
			noSeqAct_cmb.selectedIndex = ca.noSequences;
	}
	
	private function updateOptionalData(){
		var oa = _canvasModel.selectedItem.activity;
		var	o = ComplexActivity(oa);
		
		o.minOptions = minAct_stp.value;
		o.maxOptions = maxAct_stp.value;
		
		var newChildren = _canvasModel.getCanvas().ddm.getComplexActivityChildren(oa.activityUIID);
		CanvasOptionalActivity(_canvasModel.selectedItem).updateChildren(newChildren);
		
		setModified();
	}
	
	private function updateOptionalSequenceData(){
		var controller = _canvasModel.activeView.getController();
		if(!controller.isBusy) {
			controller.setBusy()
			
			var oa = _canvasModel.selectedItem.activity;
			var o = ComplexActivity(oa);
			
			if(o.noSequences < noSeqAct_cmb.value) {
				for(var i=0; i<(noSeqAct_cmb.value - o.noSequences); i++)
					_canvasModel.createNewSequenceActivity(oa);
			} else {
				var itemsToRemove:Array = CanvasOptionalActivity(_canvasModel.selectedItem).getLastItems((o.noSequences - noSeqAct_cmb.value));
				Debugger.log("itemsToRemove len: " + itemsToRemove.length,Debugger.CRITICAL, "updateOptionalSequenceData", "PropertyInspector");
				
				for(var i=0; i<itemsToRemove.length; i++)
					_canvasModel.getCanvas().ddm.removeActivity(itemsToRemove[i]);
				
				_canvasModel.setDirty();
			}
			
			this.onEnterFrame = onUpdateOptionalSequenceData;
		}
	}
	
	private function onUpdateOptionalSequenceData() {
		delete this.onEnterFrame;
		
		var newChildren:Array = _canvasModel.getCanvas().ddm.getComplexActivityChildren(_canvasModel.selectedItem.activity.activityUIID);
		CanvasOptionalActivity(_canvasModel.selectedItem).updateChildren(newChildren);
		
		setModified();
	
		_canvasModel.activeView.getController().clearBusy()	
			
	}
	
	private function showParallelActivityProperties(ca:ComplexActivity){
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('pi_parallel_title');
		runOffline_chk.selected = ca.runOffline;
		defineLater_chk.selected = ca.defineLater;
					
		currentGrouping_lbl.text = "GroupingUIID:"+StringUtils.cleanNull(ca.runOffline.groupingUIID);
	}
	
	private function showGateActivityProperties(ga:GateActivity){
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('pi_activity_type_gate');
		
		//loop through combo to find SI of our gate activity type
		for (var i=0; i<gateType_cmb.dataProvider.length;i++){
			if(_canvasModel.selectedItem.activity.activityTypeID == gateType_cmb.dataProvider[i].data){
				
				gateType_cmb.selectedIndex=i;
			}
		}
		
		//TODO: set the stepper values too!
	
	}
	
	private function showBranchingActivityProperties(ba:BranchingActivity){
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('pi_activity_type_branching');
		
		//loop through combo to find SI of our branching activity type
		for (var i=0; i<branchType_cmb.dataProvider.length;i++){
			if(_canvasModel.selectedItem.activity.activityTypeID == branchType_cmb.dataProvider[i].data){
				
				branchType_cmb.selectedIndex=i;
			}
		}
		
		if(_canvasModel.selectedItem.activity.activityTypeID == Activity.GROUP_BRANCHING_ACTIVITY_TYPE) {
			showAppliedGroupingControls(true, !ba.readOnly);
			showGroupBasedBranchingControls(true, !ba.readOnly);
			
			Debugger.log("defineLater: " + ba.defineLater, Debugger.CRITICAL, "showBranchingActivityProperties", "PropertyInspector");
			//_define_monitor_cb.selected = (ba.defineLater != null) ? ba.defineLater : false;
		} else if(_canvasModel.selectedItem.activity.activityTypeID == Activity.TOOL_BRANCHING_ACTIVITY_TYPE) {
			if(toolActs_cmb.selectedIndex == 0) {
				_canvasModel.selectedItem.activity.toolActivityUIID = null;
				branchToolInputChange(_canvasModel.selectedItem, toolActs_cmb.dataProvider[0].data);
			}
		}
	
	}
	
	private function showTransitionProperties(t:Transition){
		//TODO: show from act and to act
		
	}
	
	private function showBranchControls(v:Boolean):Void {
		_pi_defaultBranch_cb.visible = v;
	}
	
	private function showBranchProperties(b:Branch){
		toolDisplayName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b> - "+Dictionary.getValue('pi_activity_type_sequence');
		title_txt.text = b.sequenceActivity.title;
		
		_pi_defaultBranch_cb.selected = (b.sequenceActivity.activityUIID == _canvasModel.activeView.activity.defaultBranch.sequenceActivity.activityUIID) ? true : false;
		_pi_defaultBranch_cb.enabled = !_pi_defaultBranch_cb.selected;
	}
	
	/**
	 * Shows which grouping activity is applied to this activity, 
	 * if you click the edit button, the the activity is selected, 
	 * and therefore the Grouping obeject properties 
	 * are shown in the PropertyInspector.
	 * @usage   
	 * @param   a The selected Activity
	 * @return  
	 */
	private function showAppliedGroupingProperties(a:Activity){
		//update the grouping drop down values
		appliedGroupingActivity_cmb.dataProvider = getGroupingActivitiesDP();
		_global.breakpoint();

		var appliedGroupingAct:GroupingActivity = _canvasModel.getCanvas().ddm.getGroupingActivityByGroupingUIID(a.groupingUIID);
		Debugger.log('a.groupingUIID='+a.groupingUIID+', appliedGroupingAct.activityUIID :'+appliedGroupingAct.activityUIID ,Debugger.GEN,'showAppliedGroupingProperties','PropertyInspector');
		
		var dp = appliedGroupingActivity_cmb.dataProvider;
		Debugger.log('dp:'+dp.toString(),Debugger.GEN,'showAppliedGroupingProperties','PropertyInspector');
		appliedGroupingActivity_cmb.selectedIndex = 0;
		for(var i=0; i<dp.length;i++){
			
			Debugger.log('dp[i].data.createGroupingID:'+dp[i].data.createGroupingID,Debugger.GEN,'showAppliedGroupingProperties','PropertyInspector');
			if(appliedGroupingAct.activityUIID == dp[i].data.activityUIID){
				appliedGroupingActivity_cmb.selectedIndex = i;
				applied_grouping_lbl.text = dp[i].label
			}
		}	
	}

}


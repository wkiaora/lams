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

import mx.controls.*
import mx.utils.*
import mx.managers.*
import mx.events.*
import org.lamsfoundation.lams.monitoring.*;
import org.lamsfoundation.lams.common.ws.*
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.common.ui.*
import it.sephiroth.TreeDnd;
/**
* @author      DI & DC
*/
class CreateLessonDialog extends MovieClip{
 
    //private static var OK_OFFSET:Number = 50;
    //private static var CANCEL_OFFSET:Number = 50;

    //References to components + clips 
    private var _container:MovieClip;       //The container window that holds the dialog
    private var ok_btn:Button;              //OK+Cancel buttons
    private var cancel_btn:Button;
    private var panel:MovieClip;            //The underlaying panel base
    
	//location tab elements
	private var treeview:Tree;              //Treeview for navigation through workspace folder structure
	private var location_dnd:TreeDnd;
	private var input_txt:TextInput;
	private var currentPath_lbl:Label;
	private var name_lbl:Label;
	private var resourceTitle_txi:TextInput;

		
	
	//properties
	private var description_lbl:Label;
	
    private var resourceDesc_txa:TextArea;
    
    private var fm:FocusManager;            //Reference to focus manager
    private var themeManager:ThemeManager;  //Theme manager
	
	private var _workspaceView:WorkspaceView;
	private var _workspaceModel:WorkspaceModel;
	private var _workspaceController:WorkspaceController;

    
    //Dimensions for resizing
    private var xOkOffset:Number;
    private var yOkOffset:Number;
    private var xCancelOffset:Number;
    private var yCancelOffset:Number;
    
	private var _resultDTO:Object;			//This is an object to contain whatever the user has selected / set - will be passed back to the calling function
	

    private var _selectedDesignId:Number;
    
    //These are defined so that the compiler can 'see' the events that are added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
    
    
    /**
    * constructor
    */
    function CreateLessonDialog(){
        //trace('WorkSpaceDialog.constructor');
        //Set up this class to use the Flash event delegation model
        EventDispatcher.initialize(this);
        _resultDTO = new Object();
		
        //Create a clip that will wait a frame before dispatching init to give components time to setup
        this.onEnterFrame = init;
    }

    /**
    * Called a frame after movie attached to allow components to initialise
    */
	private function init(){
        //Delete the enterframe dispatcher
        delete this.onEnterFrame;
		//TODO: DC apply the themes here
        
        //set the reference to the StyleManager
        themeManager = ThemeManager.getInstance();
        
        //Set the container reference
        Debugger.log('container=' + _container,Debugger.GEN,'init','org.lamsfoundation.lams.WorkspaceDialog');

	//Set the text on the labels
        
        //Set the text for buttons
		currentPath_lbl.text = "<b>"+Dictionary.getValue('ws_dlg_location_button')+"</b>:"
        ok_btn.label = "Create"		//Dictionary.getValue('ws_dlg_ok_button');
        cancel_btn.label = Dictionary.getValue('ws_dlg_cancel_button');
		
		//TODO: Dictionary calls for all the rest of the buttons
		
		//TODO: Make setStyles more efficient
		setStyles();

        //get focus manager + set focus to OK button, focus manager is available to all components through getFocusManager
        fm = _container.getFocusManager();
        fm.enabled = true;
        ok_btn.setFocus();
        //fm.defaultPushButton = ok_btn;
        
        Debugger.log('ok_btn.tabIndex: '+ok_btn.tabIndex,Debugger.GEN,'init','org.lamsfoundation.lams.WorkspaceDialog');
        
       
        //Tie parent click event (generated on clicking close button) to this instance
        _container.addEventListener('click',this);
        //Register for LFWindow size events
        _container.addEventListener('size',this);
		
		//panel.setStyle('backgroundColor',0xFFFFFF);
        
        //Debugger.log('setting offsets',Debugger.GEN,'init','org.lamsfoundation.lams.common.ws.WorkspaceDialog');

        //work out offsets from bottom RHS of panel
        xOkOffset = panel._width - ok_btn._x;
        yOkOffset = panel._height - ok_btn._y;
        xCancelOffset = panel._width - cancel_btn._x;
        yCancelOffset = panel._height - cancel_btn._y;
        
        //Register as listener with StyleManager and set Styles
        themeManager.addEventListener('themeChanged',this);
		location_dnd.dragRules = TreeDnd.DENYALL;
        treeview = location_dnd.getTree();
		//Fire contentLoaded event, this is required by all dialogs so that creator of LFWindow can know content loaded
        
		_container.contentLoaded();
    }
	
	/**
	 * Called by the worspaceView after the content has loaded
	 * @usage   
	 * @return  
	 */
	public function setUpContent():Void{
		
		//register to recive updates form the model
		WorkspaceModel(_workspaceView.getModel()).addEventListener('viewUpdate',this);
		
		Debugger.log('_workspaceView:'+_workspaceView,Debugger.GEN,'setUpContent','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		//get a ref to the controller and kkep it here to listen for events:
		_workspaceController = _workspaceView.getController();
		Debugger.log('_workspaceController:'+_workspaceController,Debugger.GEN,'setUpContent','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		
		 //Add event listeners for ok, cancel and close buttons
        ok_btn.addEventListener('click',Delegate.create(this, ok));
        cancel_btn.addEventListener('click',Delegate.create(this, cancel));
		//think this is failing....
		//Set up the treeview
        setUpTreeview();
		
		itemSelected(treeview.selectedNode);
	}
	
	/**
	 * Recieved update events from the WorkspaceModel. Dispatches to relevent handler depending on update.Type
	 * @usage   
	 * @param   event
	 */
	public function viewUpdate(event:Object):Void{
		Debugger.log('Recived an Event dispather UPDATE!, updateType:'+event.updateType+', target'+event.target,4,'viewUpdate','org.lamsfoundation.lams.ws.WorkspaceDialog');
		 //Update view from info object
        //Debugger.log('Recived an UPDATE!, updateType:'+infoObj.updateType,4,'update','CanvasView');
       var wm:WorkspaceModel = event.target;
	   //set a permenent ref to the model for ease (sorry mvc guru)
	   _workspaceModel = wm;
	  
	   switch (event.updateType){
			case 'POPULATE_LICENSE_DETAILS' :
				//populateAvailableLicenses(event.data, wm);
			case 'REFRESH_TREE' :
                refreshTree(wm);
                break;
			case 'UPDATE_CHILD_FOLDER' :
				updateChildFolderBranches(event.data,wm);
			case 'ITEM_SELECTED' :
				itemSelected(event.data,wm);
				break;
			case 'OPEN_FOLDER' :
				openFolder(event.data, wm);
				break;
			case 'CLOSE_FOLDER' :
				closeFolder(event.data, wm);
				break;
			case 'REFRESH_FOLDER' :
				refreshFolder(event.data, wm);
				break;
			case 'SHOW_DATA' :
				showData(wm);
				break;
			case 'SET_UP_BRANCHES_INIT' :
				setUpBranchesInit();
				break;
				
            default :
                Debugger.log('unknown update type :' + event.updateType,Debugger.GEN,'viewUpdate','org.lamsfoundation.lams.ws.WorkspaceDialog');
		}

	}
	
	
	
	/**
	 * called witht he result when a child folder is opened..
	 * updates the tree branch satus, then refreshes.
	 * @usage   
	 * @param   changedNode 
	 * @param   wm          
	 * @return  
	 */
	private function updateChildFolderBranches(changedNode:XMLNode,wm:WorkspaceModel){
		 Debugger.log('updateChildFolder....:' ,Debugger.GEN,'updateChildFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		 //we have to set the new nodes to be branches, if they are branches
		if(changedNode.attributes.isBranch){
			treeview.setIsBranch(changedNode,true);
			//do its kids
			for(var i=0; i<changedNode.childNodes.length; i++){
				var cNode:XMLNode = changedNode.getTreeNodeAt(i);
				if(cNode.attributes.isBranch){
					treeview.setIsBranch(cNode,true);
				}
			}
		}
		
		 openFolder(changedNode);
	}
	
	private function refreshTree(){
		 Debugger.log('Refreshing tree....:' ,Debugger.GEN,'refreshTree','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		
		 treeview.refresh();// this is USELESS

		//var oBackupDP = treeview.dataProvider;
		//treeview.dataProvider = null; // clear
		//treeview.dataProvider = oBackupDP;
		
		//treeview.setIsOpen(treeview.getNodeDisplayedAt(0),false);
		//treeview.setIsOpen(treeview.getNodeDisplayedAt(0),true);

	}
	
	/**
	 * Just opens the fodler node - DOES NOT FIRE EVENT - so is used after updatting the child folder
	 * @usage   
	 * @param   nodeToOpen 
	 * @param   wm         
	 * @return  
	 */
	private function openFolder(nodeToOpen:XMLNode, wm:WorkspaceModel){
		Debugger.log('openFolder:'+nodeToOpen ,Debugger.GEN,'openFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//open the node
		treeview.setIsOpen(nodeToOpen,true);
		refreshTree();
	
	}
	
	/**
	 * Closes the folder node
	 * 
	 * @usage   
	 * @param   nodeToClose 
	 * @param   wm          
	 * @return  
	 */
	
	private function closeFolder(nodeToClose:XMLNode, wm:WorkspaceModel){
		Debugger.log('closeFolder:'+nodeToClose ,Debugger.GEN,'closeFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		// close the node
		nodeToClose.attributes.isOpen = false;
		treeview.setIsOpen(nodeToClose, false);
		
		refreshTree();
	}
	
	/**
	 * Closes folder, then sends openEvent to controller
	 * @usage   
	 * @param   nodeToOpen 
	 * @param   wm         
	 * @return  
	 */
	private function refreshFolder(nodeToOpen:XMLNode, wm:WorkspaceModel){
		Debugger.log('refreshFolder:'+nodeToOpen ,Debugger.GEN,'refreshFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//close the node
		treeview.setIsOpen(nodeToOpen,false);		
		//we are gonna need to fire the event manually for some stupid reason the tree is not firing it.
		//dispatchEvent({type:'nodeOpen',target:treeview,node:nodeToOpen});
		_workspaceController = _workspaceView.getController();
		_workspaceController.onTreeNodeOpen({type:'nodeOpen',target:treeview,node:nodeToOpen});
	}
	
	
	private function itemSelected(newSelectedNode:XMLNode,wm:WorkspaceModel){
		//update the UI with the new info:
		//_global.breakpoint();
		//Only update the details if the node if its a resource:a
		var nodeData = newSelectedNode.attributes.data;
		if(nodeData.resourceType == _workspaceModel.RT_FOLDER){
			resourceTitle_txi.text = "";
			resourceDesc_txa.text = "";
				
		}else{
			resourceTitle_txi.text = nodeData.name;
			resourceDesc_txa.text = nodeData.description;
			Debugger.log('nodeData.licenseID:'+nodeData.licenseID,Debugger.GEN,'itemSelected','org.lamsfoundation.lams.ws.WorkspaceDialog');
					
			//TODO These Items must also be in the FolderContentsDTO
			/*
			license_txa.text = ;
			licenseID_cmb.value = ;
			*/
		
		}
		
	}
	
	private function setLocationContentVisible(v:Boolean){
		Debugger.log('v:'+v,Debugger.GEN,'setLocationContentVisible','org.lamsfoundation.lams.ws.WorkspaceDialog');
		treeview.visible = v;
		input_txt.visible = v;
		currentPath_lbl.visible = v;
		name_lbl.visible = v;
		resourceTitle_txi.visible = v;
		description_lbl.visible = v;
		resourceDesc_txa.visible = v;
	}
	
	/**
	 * updates the view to show the right controls for the tab
	 * @usage   
	 * @param   tabToSelect 
	 * @param   wm          
	 * @return  
	 */
	private function showData(wm:WorkspaceModel){
		//Debugger.log('tabToSelect:'+tabToSelect,Debugger.GEN,'showTab','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//if(tabToSelect == "LOCATION"){
			setLocationContentVisible(true);
			
		//set the right label on the 'doit' button
			if(wm.currentMode=="OPEN"){
				ok_btn.label = Dictionary.getValue('ws_dlg_open_btn');
			}else if(wm.currentMode=="SAVE" || wm.currentMode=="SAVEAS"){
				ok_btn.label = Dictionary.getValue('ws_dlg_save_btn');
			}else if(wm.currentMode=="READONLY"){
				ok_btn.label = "Create"		//Dictionary.getValue('ws_dlg_create_btn');
			}else{
				Debugger.log('Dont know what mode the Workspace is in!',Debugger.CRITICAL,'showTab','org.lamsfoundation.lams.ws.WorkspaceDialog');
				ok_btn.label = Dictionary.getValue('ws_dlg_ok_btn');
			}
		//}
	}
	
	
    
    /**
    * Event fired by StyleManager class to notify listeners that Theme has changed
    * it is up to listeners to then query Style Manager for relevant style info
    */
    public function themeChanged(event:Object){
        if(event.type=='themeChanged') {
            //Theme has changed so update objects to reflect new styles
            setStyles();
        }else {
            Debugger.log('themeChanged event broadcast with an object.type not equal to "themeChanged"',Debugger.CRITICAL,'themeChanged','org.lamsfoundation.lams.WorkspaceDialog');
        }
    }
    
    /**
    * Called on initialisation and themeChanged event handler
    */
    private function setStyles(){
        //LFWindow, goes first to prevent being overwritten with inherited styles.
        //var styleObj = themeManager.getStyleObject('LFWindow');
        //_container.setStyle('styleName',styleObj);

        //Get the button style from the style manager
       // styleObj = themeManager.getStyleObject('button');
        
        //apply to both buttons
       // Debugger.log('styleObject : ' + styleObj,Debugger.GEN,'setStyles','org.lamsfoundation.lams.WorkspaceDialog');
       // ok_btn.setStyle('styleName',styleObj);
        //cancel_btn.setStyle('styleName',styleObj);
        
        //Get label style and apply to label
       var styleObj = themeManager.getStyleObject('label');
        name_lbl.setStyle('styleName',styleObj);

        //Apply treeview style 
       // styleObj = themeManager.getStyleObject('treeview');
        //treeview.setStyle('styleName',styleObj);

        //Apply datagrid style 
      //  styleObj = themeManager.getStyleObject('datagrid');
        //datagrid.setStyle('styleName',styleObj);

/*
        //Apply combo style 
        styleObj = themeManager.getStyleObject('combo');
        combo.setStyle('styleName',styleObj);
  */
  }

    /**
    * Called by the cancel button 
    */
    private function cancel(){
        trace('Cancel');
        //close parent window
        _container.deletePopUp();
    }
    
    /**
    * Called by the OK button
	* Dispatches the okClicked event and passes a result DTO containing:
	* <code>
	*	_resultDTO.selectedResourceID 	//The ID of the resource that was selected when the dialogue closed
	*	_resultDTO.resourceName 		//The contents of the Name text field
	*	_resultDTO.resourceDescription 	//The contents of the description field on the propertirs tab
	*	_resultDTO.resourceLicenseText 	//The contents of the license text field
	*	_resultDTO.resourceLicenseID 	//The ID of the selected license from the drop down.
    *</code>
	*/
    private function ok(){
        trace('OK');
		_global.breakpoint();
		
		//TODO: Rmeove this code as its been here only for deflopment
		//set the selectedDesignId
		/**/
		if(StringUtils.isNull(input_txt.text)){
			//get the selected value off the tree
			var snode = treeview.selectedNode;
			input_txt.text = snode.attributes.data.resourceID;
			
		}
		_selectedDesignId = Number(input_txt.text);
		
		
		//TODO: Validate you are allowed to use the name etc... Are you overwriting - NOTE Same names are nto allowed in this version
		
		var snode = treeview.selectedNode;
		 Debugger.log('_workspaceModel.currentMode: ' + _workspaceModel.currentMode,Debugger.GEN,'ok','org.lamsfoundation.lams.WorkspaceDialog');
		if(_workspaceModel.currentMode=="SAVE" || _workspaceModel.currentMode=="SAVEAS"){
			//var rid:Number = Number(snode.attributes.data.resourceID);
			if(snode.attributes.data.resourceType==_workspaceModel.RT_LD){
				//run a confirm dialogue as user is about to overwrite a design!
				LFMessage.showMessageConfirm(Dictionary.getValue('ws_chk_overwrite_resource'), Proxy.create(this,doWorkspaceDispatch,true), Proxy.create(this,closeThisDialogue));
	
			}else if (snode.attributes.data.resourceType==_workspaceModel.RT_FOLDER){
				doWorkspaceDispatch(false);
			}else{
				LFMessage.showMessageAlert(Dictionary.getValue('ws_click_folder_file'),null);
			}
		}else{
			doWorkspaceDispatch(true);
		}
		
    }
	
	
	
	/**
	 * Dispatches an event - picked up by the canvas in authoring
	 * sends paramter containing:
	 * _resultDTO.selectedResourceID 
	 * _resultDTO.targetWorkspaceFolderID
	 * 	_resultDTO.resourceName 
		_resultDTO.resourceDescription 
		_resultDTO.resourceLicenseText 
		_resultDTO.resourceLicenseID 
	 * @usage   
	 * @param   useResourceID //if its true then we will send the resorceID of teh item selected in the tree - usually this means we are overwriting something
	 * @return  
	 */
	public function doWorkspaceDispatch(useResourceID:Boolean){
		//ObjectUtils.printObject();
		var snode = treeview.selectedNode;
		
		if(useResourceID){
			//its an LD
			_resultDTO.selectedResourceID = Number(snode.attributes.data.resourceID);
			_resultDTO.targetWorkspaceFolderID = Number(snode.attributes.data.workspaceFolderID);
		}else{
			//its a folder
			_resultDTO.selectedResourceID  = null;
			_resultDTO.targetWorkspaceFolderID = Number(snode.attributes.data.resourceID);
			
		}
		
		_resultDTO.resourceName = resourceTitle_txi.text;
		_resultDTO.resourceDescription = resourceDesc_txa.text;
		//_resultDTO.resourceLicenseText = license_txa.text;
		//_resultDTO.resourceLicenseID = licenseID_cmb.value.licenseID;
		

        dispatchEvent({type:'okClicked',target:this});
	   
        closeThisDialogue();
		
	}
	
	public function closeThisDialogue(){
		 _container.deletePopUp();
	}

	
	/**
    * Event dispatched by parent container when close button clicked
    */
    private function click(e:Object){
        trace('WorkspaceDialog.click');
        e.target.deletePopUp();
    }
	

	/**
	 * Recursive function to set any folder with children to be a branch
	 * TODO: Might / will have to change this behaviour once designs are being returned into the mix
	 * @usage   
	 * @param   node 
	 * @return  
	 */
    private function setBranches(node:XMLNode){
		if(node.hasChildNodes() || node.attributes.isBranch){
			treeview.setIsBranch(node, true);
			for (var i = 0; i<node.childNodes.length; i++) {
				var cNode = node.getTreeNodeAt(i);
				setBranches(cNode);
				/*
				if(cNode.hasChildNodes()){
					treeview.setIsBranch(cNode, true);
					setBranches(cNode);
				}
				*/
				
			}
		}
	}
	

	/**
	 * Sets up the inital branch detials
	 * @usage   
	 * @return  
	 */
	private function setUpBranchesInit(){
		Debugger.log('Running...',Debugger.GEN,'setUpBranchesInit','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		//get the 1st child
		treeview.dataProvider = WorkspaceModel(_workspaceView.getModel()).treeDP;
		var fNode = treeview.dataProvider.firstChild;
		setBranches(fNode);
		treeview.refresh();
	}
	
	
	/**
	 * Sets up the treeview with whatever datya is in the treeDP
	 * TODO - extend this to make it recurse all the way down the tree
	 * @usage   
	 * @return  
	 */
	private function setUpTreeview(){
			
		//Debugger.log('_workspaceView:'+_workspaceView,Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		setUpBranchesInit();
		Debugger.log('WorkspaceModel(_workspaceView.getModel()).treeDP:'+WorkspaceModel(_workspaceView.getModel()).treeDP.toString(),Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		//Debugger.log('_workspaceController.onTreeNodeOpen:'+_workspaceController.onTreeNodeOpen,Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		
		
		treeview.addEventListener("nodeOpen", Delegate.create(_workspaceController, _workspaceController.onTreeNodeOpen));
		treeview.addEventListener("nodeClose", Delegate.create(_workspaceController, _workspaceController.onTreeNodeClose));
		treeview.addEventListener("change", Delegate.create(_workspaceController, _workspaceController.onTreeNodeChange));

		//location_dnd.addEventListener('double_click', dndList);
		//location_dnd.addEventListener('drag_start', dndList);
		//location_dnd.addEventListener('drag_fail', dndList);
		
		//location_dnd.addEventListener('drag_target', dndList);
		location_dnd.addEventListener("drag_complete", Delegate.create(_workspaceController, _workspaceController.onDragComplete));
		//location_dnd.addEventListener('drag_complete', dndList);
		//use the above event, on comlete the drop, send the request to do the move to the server (evt.targetNode);
		//then immediatly invlaidate the cache.  then server may return error if therrte is a problem, else new details willbe shown
		
		
		
		
    }
    
    /**
    * XML onLoad handler for treeview data
 */
    private function tvXMLLoaded (ok:Boolean,rootXML:XML){
        if(ok){
            /*
			//Set the XML as the data provider for the tree
            treeview.dataProvider = rootXML.firstChild;
            treeview.addEventListener("change", Delegate.create(this, onTvChange));
            
            //Add this function to prevent displaying [type function],[type function] when label attribute missing from XML
            treeview.labelFunction = function(node) {
                    return node.nodeType == 1 ? node.nodeName : node.nodeValue;
            };
            */
        }
    }
    
     
    /**
    * Main resize method, called by scrollpane container/parent
    */
    public function setSize(w:Number,h:Number){
        //Debugger.log('setSize',Debugger.GEN,'setSize','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
        //Size the panel
        panel.setSize(w,h);

        //Buttons
        ok_btn.move(w-xOkOffset,h-yOkOffset);
        cancel_btn.move(w-xCancelOffset,h-yCancelOffset);
    }
    
    //Gets+Sets
    /**
    * set the container refernce to the window holding the dialog
    */
    function set container(value:MovieClip){
        _container = value;
    }
	
	/**
	 * 
	 * @usage   
	 * @param   newworkspaceView 
	 * @return  
	 */
	public function set workspaceView (newworkspaceView:WorkspaceView):Void {
		_workspaceView = newworkspaceView;
	}
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get workspaceView ():WorkspaceView {
		return _workspaceView;
	}
	
    
    function get selectedDesignId():Number { 
        return _selectedDesignId;
    }
	
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get resultDTO():Object {
		return _resultDTO;
	}
}
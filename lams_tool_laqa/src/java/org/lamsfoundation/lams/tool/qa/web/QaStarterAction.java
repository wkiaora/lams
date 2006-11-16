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

/**
 * @author Ozgur Demirtas
 * 
 * Created on 8/03/2005
 * 
 * initializes the tool's authoring mode  
 */

/**
 * Tool path The URL path for the tool should be <lamsroot>/tool/$TOOL_SIG.
 * 
 * CONTENT_LOCKED ->CONTENT_IN_USE 
 * 
 * QaStarterAction loads the default content and initializes the presentation Map
 * Requests can come either from authoring envuironment or from the monitoring environment for Edit Activity screen
 * 
 * Check QaUtils.createAuthoringUser again User Management Service is ready
 * 
 * */

/**
 *
 * Tool Content:
 *
 * While tool's manage their own content, the LAMS core and the tools work together to create and use the content. 
 * The tool content id (toolContentID) is the key by which the tool and the LAMS core discuss data - 
 * it is generated by the LAMS core and supplied to the tool whenever content needs to be stored. 
 * The LAMS core will refer to the tool content id whenever the content needs to be used. 
 * Tool content will be covered in more detail in following sections.
 *
 * Each tool will have one piece of content that is the default content. 
 * The tool content id for this content is created as part of the installation process. 
 * Whenever a tool is asked for some tool content that does not exist, it should supply the default tool content. 
 * This will allow the system to render the normal screen, albeit with useless information, rather than crashing. 
*/

/**
*
* Authoring URL: 
*
* The tool must supply an authoring module, which will be called to create new content or edit existing content. It will be called by an authoring URL using the following format: ?????
* The initial data displayed on the authoring screen for a new tool content id may be the default tool content.
*
* Authoring UI data consists of general Activity data fields and the Tool specific data fields.
* The authoring interface will have three tabs. The mandatory (and suggested) fields are given. Each tool will have its own fields which it will add on any of the three tabs, as appropriate to the tabs' function.
*
* Basic: Displays the basic set of fields that are needed for the tool, and it could be expected that a new LAMS user would use. Mandatory fields: Title, Instructions.
* Advanced: Displays the extra fields that would be used by experienced LAMS users. Optional fields: Lock On Finish, Make Responses Anonymous
* Instructions: Displays the "instructions" fields for teachers. Mandatory fields: Online instructions, Offline instructions, Document upload.
* The "Define Later" and "Run Offline" options are set on the Flash authoring part, and not on the tool's authoring screens.
*
* Preview The tool must be able to show the specified content as if it was running in a lesson. It will be the learner url with tool access mode set to ToolAccessMode.AUTHOR.
* Export The tool must be able to export its tool content for part of the overall learning design export.
*
* The format of the serialization for export is XML. Tool will define extra namespace inside the <Content> element to add a new data element (type). Inside the data element, it can further define more structures and types as it seems fit.
* The data elements must be "version" aware. The data elements must be "type" aware if they are to be shared between Tools.
* 
* 
  <!-- ========== Action Mapping Definitions =================================== -->
  <action-mappings>
   <!--Authoring Starter  -->
    <action
		path="/authoringStarter"
		type="org.lamsfoundation.lams.tool.qa.web.QaStarterAction"
		name="QaAuthoringForm"
		scope="request"
		unknown="false"
		validate="false"
    >

	    <forward
			name="load"
			path="/AuthoringMaincontent.jsp"
			redirect="false"
	    />
	
	    <forward
			name="loadViewOnly"
	      	path="/authoring/AuthoringTabsHolder.jsp"
	      	redirect="false"
	    />

	  	<forward
			name="loadMonitoring"
			path="/monitoring/MonitoringMaincontent.jsp"
			redirect="false"
	  	/>
	
	  	<forward
			name="refreshMonitoring"
			path="/monitoring/MonitoringMaincontent.jsp"
			redirect="false"
	  	/>

	</action>  

* 
*/


/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa.web;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaApplicationException;
import org.lamsfoundation.lams.tool.qa.QaComparator;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaGeneralAuthoringDTO;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.QaQuestionContentDTO;
import org.lamsfoundation.lams.tool.qa.QaUtils;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.tool.qa.service.QaServiceProxy;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * 
 * @author Ozgur Demirtas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * A Map data structure is used to present the UI.
 */
public class QaStarterAction extends Action implements QaAppConstants {
	static Logger logger = Logger.getLogger(QaStarterAction.class.getName());
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, QaApplicationException {
	
		QaUtils.cleanUpSessionAbsolute(request);
	    logger.debug("init authoring mode.");
		QaAuthoringForm qaAuthoringForm = (QaAuthoringForm) form;
		logger.debug("qaAuthoringForm: " + qaAuthoringForm);
		
		String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
		logger.debug("contentFolderID: " + contentFolderID);
		qaAuthoringForm.setContentFolderID(contentFolderID);
	
		QaGeneralAuthoringDTO qaGeneralAuthoringDTO= new QaGeneralAuthoringDTO();
		qaGeneralAuthoringDTO.setContentFolderID(contentFolderID);
		
		Map mapQuestionContent= new TreeMap(new QaComparator());
		logger.debug("mapQuestionContent: " + mapQuestionContent);
		
		qaAuthoringForm.resetRadioBoxes();
		
		IQaService qaService =null;
		if ((getServlet() == null) || (getServlet().getServletContext() == null))
		{
		    logger.debug("obtaining qaService from the form");
		    qaService=qaAuthoringForm.getQaService();
		}
		else
		{
		    logger.debug("obtaining qaService via proxy");
		    qaService =QaServiceProxy.getQaService(getServlet().getServletContext());
		}
		logger.debug("qaService: " + qaService);
		
		qaGeneralAuthoringDTO.setCurrentTab("1");
		logger.debug("setting currrent tab to 1:");
		
		
		qaGeneralAuthoringDTO.setMonitoringOriginatedDefineLater(new Boolean(false).toString());
		String servletPath=request.getServletPath();
		logger.debug("getServletPath: "+ servletPath);
		String requestedModule=null;
		if (servletPath.indexOf("authoringStarter") > 0)
		{
			logger.debug("request is for authoring module");
			qaGeneralAuthoringDTO.setActiveModule(AUTHORING);
			qaGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());
			qaGeneralAuthoringDTO.setShowAuthoringTabs(new Boolean(true).toString());
			qaAuthoringForm.setActiveModule(AUTHORING);
			requestedModule=AUTHORING;
		}
		else
		{
			logger.debug("request is for define later module either direcly from define later url or monitoring url");
			qaGeneralAuthoringDTO.setActiveModule(DEFINE_LATER);
			qaGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(false).toString());
			qaGeneralAuthoringDTO.setShowAuthoringTabs(new Boolean(false).toString());
			qaAuthoringForm.setActiveModule(DEFINE_LATER);			
			requestedModule=DEFINE_LATER;
			
			if (servletPath.indexOf("monitoring") > 0)
			{
				logger.debug("request is from monitoring  url.");
				qaGeneralAuthoringDTO.setMonitoringOriginatedDefineLater(new Boolean(true).toString());
			}
		}
		logger.debug("requestedModule: " + requestedModule);
		qaGeneralAuthoringDTO.setRequestedModule(requestedModule);

	
		/* in development this needs to be called only once.  */ 
		/* QaUtils.configureContentRepository(request); */
		
		String sourceMcStarter = (String) request.getAttribute(SOURCE_MC_STARTER);
		logger.debug("sourceMcStarter: " + sourceMcStarter);
		
	    boolean validateSignature=readSignature(request,mapping, qaService, qaGeneralAuthoringDTO, qaAuthoringForm);
		logger.debug("validateSignature:  " + validateSignature);
		if (validateSignature == false)
		{
			logger.debug("error during validation");
		}
		
		/* mark the http session as an authoring activity */
		qaGeneralAuthoringDTO.setTargetMode(TARGET_MODE_AUTHORING);
	    
	    /*
	     * find out whether the request is coming from monitoring module for EditActivity tab or from authoring environment url
	     */
	    logger.debug("no problems getting the default content, will render authoring screen");
	    String strToolContentID="";
	    /*the authoring url must be passed a tool content id*/
	    strToolContentID=request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);
	    logger.debug("strToolContentID: " + strToolContentID);
	    qaGeneralAuthoringDTO.setToolContentID(strToolContentID);
	    
	    SessionMap sessionMap = new SessionMap();
	    List sequentialCheckedCa= new LinkedList();
	    sessionMap.put(ATTACHMENT_LIST_KEY, new ArrayList());
	    sessionMap.put(DELETED_ATTACHMENT_LIST_KEY, new ArrayList());
	    sessionMap.put(ACTIVITY_TITLE_KEY, "");
	    sessionMap.put(ACTIVITY_INSTRUCTIONS_KEY, "");
	    qaAuthoringForm.setHttpSessionID(sessionMap.getSessionID());
	    qaGeneralAuthoringDTO.setHttpSessionID(sessionMap.getSessionID());
	    
	    String defaultContentId=null;
	    //pay attention here: remove request.getSession().getAttribute(TOOL_CONTENT_ID);
	    if (strToolContentID == null)
	    {
	    	/*it is possible that the original request for authoring module is coming from monitoring url which keeps the
	    	 TOOL_CONTENT_ID in the session*/
		    logger.debug("strToolContentID is null, handle this");
	        
	    	Long toolContentID =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
		    logger.debug("toolContentID: " + toolContentID);
		    if (toolContentID != null)
		    {
		        strToolContentID= toolContentID.toString();
			    logger.debug("cached strToolContentID from the session: " + strToolContentID);	
		    }
		    else
		    {
		    	logger.debug("we should IDEALLY not arrive here. The TOOL_CONTENT_ID is NOT available from the url or the session.");
		    	/*use default content instead of giving a warning*/
		    	defaultContentId=qaAuthoringForm.getDefaultContentIdStr();
		    	logger.debug("using MCQ defaultContentId: " + defaultContentId);
		    	strToolContentID=defaultContentId;
		    }
	    }
    	logger.debug("final strToolContentID: " + strToolContentID);
	    
	    if ((strToolContentID == null) || (strToolContentID.equals(""))) 
	    {
	    	QaUtils.cleanUpSessionAbsolute(request);
	    	logger.debug("forwarding to: " + ERROR_LIST);
			return (mapping.findForward(ERROR_LIST));
	    }

	    qaAuthoringForm.setToolContentID(strToolContentID);

		/*
		 * find out if the passed tool content id exists in the db 
		 * present user either a first timer screen with default content data or fetch the existing content.
		 * 
		 * if the toolcontentid does not exist in the db, create the default Map,
		 * there is no need to check if the content is locked in this case.
		 * It is always unlocked since it is the default content.
		*/
        
	    String defaultContentIdStr=null;
	    QaContent qaContent=null;
		if (!existsContent(new Long(strToolContentID).longValue(), qaService)) 
		{
			logger.debug("getting default content");
			/*fetch default content*/
			defaultContentIdStr=qaAuthoringForm.getDefaultContentIdStr();
			logger.debug("defaultContentIdStr:" + defaultContentIdStr);
            qaContent=retrieveContent(request, mapping, qaAuthoringForm, mapQuestionContent, 
                    new Long(defaultContentIdStr).longValue(), true, qaService, qaGeneralAuthoringDTO, sessionMap);
            
            logger.debug("post retrive content :" + sessionMap);
            qaAuthoringForm.setLockWhenFinished("1");
		}
        else
        {
        	logger.debug("getting existing content");
        	/* it is possible that the content is in use by learners.*/
        	qaContent=qaService.loadQa(new Long(strToolContentID).longValue());
        	logger.debug("qaContent: " + qaContent);
        	if (qaService.studentActivityOccurredGlobal(qaContent))
    		{
        		QaUtils.cleanUpSessionAbsolute(request);
    			logger.debug("student activity occurred on this content:" + qaContent);
	    		persistError(request, "error.content.inUse");
	    		logger.debug("add error.content.inUse to ActionMessages.");
				return (mapping.findForward(ERROR_LIST));
    		}
        	qaContent=retrieveContent(request, mapping, qaAuthoringForm, mapQuestionContent, 
                    new Long(strToolContentID).longValue(),false, qaService, qaGeneralAuthoringDTO, sessionMap);
        	
        	logger.debug("post retrive content :" + sessionMap);        	
        }
		
		logger.debug("qaGeneralAuthoringDTO.getOnlineInstructions() :" + qaGeneralAuthoringDTO.getOnlineInstructions());
		logger.debug("qaGeneralAuthoringDTO.getOfflineInstructions():" + qaGeneralAuthoringDTO.getOfflineInstructions());
		
	    if ((qaGeneralAuthoringDTO.getOnlineInstructions() == null) || (qaGeneralAuthoringDTO.getOnlineInstructions().length() == 0))
	    {
	        qaGeneralAuthoringDTO.setOnlineInstructions(DEFAULT_ONLINE_INST);
	        qaAuthoringForm.setOnlineInstructions(DEFAULT_ONLINE_INST);
		    sessionMap.put(ONLINE_INSTRUCTIONS_KEY, DEFAULT_ONLINE_INST);
	    }
	        
	    if ((qaGeneralAuthoringDTO.getOfflineInstructions() == null) || (qaGeneralAuthoringDTO.getOfflineInstructions().length() == 0))
	    {
	        qaGeneralAuthoringDTO.setOfflineInstructions(DEFAULT_OFFLINE_INST);
	        qaAuthoringForm.setOfflineInstructions(DEFAULT_OFFLINE_INST);
	        sessionMap.put(OFFLINE_INSTRUCTIONS_KEY, DEFAULT_OFFLINE_INST);
	    }

		
	    logger.debug("final qaGeneralAuthoringDTO: " + qaGeneralAuthoringDTO);
		logger.debug("will return to jsp with: " + sourceMcStarter);
		String destination=QaUtils.getDestination(sourceMcStarter, requestedModule);
		logger.debug("destination: " + destination);
		
		Map mapQuestionContentLocal=qaGeneralAuthoringDTO.getMapQuestionContent(); 
		logger.debug("mapQuestionContentLocal: " + mapQuestionContentLocal);
		
		logger.debug("mapQuestionContent: " + mapQuestionContent);
		sessionMap.put(MAP_QUESTION_CONTENT_KEY, mapQuestionContent);
		
		logger.debug("persisting sessionMap into session: " + sessionMap);
		request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
		
		logger.debug("before fwding to jsp, qaAuthoringForm : " + qaAuthoringForm);
		logger.debug("final qaGeneralAuthoringDTO: " + qaGeneralAuthoringDTO);
		request.setAttribute(QA_GENERAL_AUTHORING_DTO, qaGeneralAuthoringDTO);
		
		return (mapping.findForward(destination));
	} 
	
	
	
	/**
	 * retrives the existing content information from the db and prepares the data for presentation purposes.
	 * ActionForward retrieveExistingContent(HttpServletRequest request, ActionMapping mapping, QaAuthoringForm qaAuthoringForm, Map mapQuestionContent, long toolContentID)
	 *  
	 * @param request
	 * @param mapping
	 * @param qaAuthoringForm
	 * @param mapQuestionContent
	 * @param toolContentID
	 * @return ActionForward
	 */
	protected QaContent retrieveContent(HttpServletRequest request, ActionMapping mapping, QaAuthoringForm qaAuthoringForm, 
	        Map mapQuestionContent, long toolContentID, boolean isDefaultContent, IQaService qaService,
	        QaGeneralAuthoringDTO qaGeneralAuthoringDTO, SessionMap sessionMap)
	{
	    logger.debug("starting retrieveContent: " + qaService);
	    logger.debug("toolContentID: " + toolContentID);
		logger.debug("isDefaultContent: " + isDefaultContent);

	    logger.debug("getting content with id:" + toolContentID);
	    QaContent qaContent = qaService.retrieveQa(toolContentID);
		logger.debug("QaContent: " + qaContent);
		
		QaUtils.populateAuthoringDTO(request, qaContent, qaGeneralAuthoringDTO);
		
	    qaAuthoringForm.setUsernameVisible(qaContent.isUsernameVisible()?"1":"0");
	    qaAuthoringForm.setShowOtherAnswers(qaContent.isShowOtherAnswers()?"1":"0");
	    qaAuthoringForm.setSynchInMonitor(qaContent.isSynchInMonitor()?"1":"0");	    
	    qaAuthoringForm.setQuestionsSequenced(qaContent.isQuestionsSequenced()?"1":"0");
	    qaAuthoringForm.setLockWhenFinished(qaContent.isLockWhenFinished()?"1":"0");
	    
	    qaGeneralAuthoringDTO.setReflect(qaContent.isReflect()?"1":"0");
	    
	    logger.debug("QaContent isReflect: " + qaContent.isReflect());
	    qaAuthoringForm.setReflect(qaContent.isReflect()?"1":"0");
	    
	    qaAuthoringForm.setReflectionSubject(qaContent.getReflectionSubject());
	    qaGeneralAuthoringDTO.setReflectionSubject(qaContent.getReflectionSubject());
	    
        List attachmentList = qaService.retrieveQaUploadedFiles(qaContent); 
        qaGeneralAuthoringDTO.setAttachmentList(attachmentList);
        qaGeneralAuthoringDTO.setDeletedAttachmentList(new ArrayList());

	    sessionMap.put(ATTACHMENT_LIST_KEY, attachmentList);
	    sessionMap.put(DELETED_ATTACHMENT_LIST_KEY, new ArrayList());


	    qaGeneralAuthoringDTO.setIsDefineLater(new Boolean(qaContent.isDefineLater()).toString());
	    
		qaGeneralAuthoringDTO.setActivityTitle(qaContent.getTitle());
		qaAuthoringForm.setTitle(qaContent.getTitle());

		qaGeneralAuthoringDTO.setActivityInstructions( qaContent.getInstructions());
		qaAuthoringForm.setInstructions(qaContent.getInstructions());
		
		sessionMap.put(ACTIVITY_TITLE_KEY, qaGeneralAuthoringDTO.getActivityTitle());
	    sessionMap.put(ACTIVITY_INSTRUCTIONS_KEY, qaGeneralAuthoringDTO.getActivityInstructions());

	    
	    List listQuestionContentDTO= new  LinkedList();
		
	    /*
		 * get the existing question content
		 */
		logger.debug("setting content data from the db");
		mapQuestionContent.clear();
		Iterator queIterator=qaContent.getQaQueContents().iterator();
		Long mapIndex=new Long(1);
		logger.debug("mapQuestionContent: " + mapQuestionContent);
		while (queIterator.hasNext())
		{
		    QaQuestionContentDTO qaQuestionContentDTO=new QaQuestionContentDTO();
		    
			QaQueContent qaQueContent=(QaQueContent) queIterator.next();
			if (qaQueContent != null)
			{
				logger.debug("question: " + qaQueContent.getQuestion());
				logger.debug("displayorder: " + new Integer(qaQueContent.getDisplayOrder()).toString());
				logger.debug("feedback: " + qaQueContent.getFeedback());
	    		
				mapQuestionContent.put(mapIndex.toString(),qaQueContent.getQuestion());
				
	    		qaQuestionContentDTO.setQuestion(qaQueContent.getQuestion());
	    		qaQuestionContentDTO.setDisplayOrder(new Integer(qaQueContent.getDisplayOrder()).toString());
	    		qaQuestionContentDTO.setFeedback(qaQueContent.getFeedback());
	    		listQuestionContentDTO.add(qaQuestionContentDTO);
	    		/**
	    		 * make the first entry the default(first) one for jsp
	    		 */
	    		if (mapIndex.longValue() == 1)
	    		    qaGeneralAuthoringDTO.setDefaultQuestionContent(qaQueContent.getQuestion());
	    		mapIndex=new Long(mapIndex.longValue()+1);
			}
		}
		logger.debug("Map initialized with existing contentid to: " + mapQuestionContent);
		
		request.setAttribute(TOTAL_QUESTION_COUNT, new Integer(mapQuestionContent.size()));
		
		
		logger.debug("listQuestionContentDTO: " + listQuestionContentDTO);
		request.setAttribute(LIST_QUESTION_CONTENT_DTO,listQuestionContentDTO);
		sessionMap.put(LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);
		
		
		if (isDefaultContent)
		{
		    logger.debug("overwriting default question.");
		    qaGeneralAuthoringDTO.setDefaultQuestionContent("Sample Question 1?");
		    
		}
		
		logger.debug("mapQuestionContent is:" + mapQuestionContent);
		qaGeneralAuthoringDTO.setMapQuestionContent(mapQuestionContent);

		
		logger.debug("qaContent.getOnlineInstructions():" + qaContent.getOnlineInstructions());
		logger.debug("qaContent.getOfflineInstructions():" + qaContent.getOfflineInstructions());
		qaGeneralAuthoringDTO.setOnlineInstructions(qaContent.getOnlineInstructions());
		qaGeneralAuthoringDTO.setOfflineInstructions(qaContent.getOfflineInstructions());
		
		qaAuthoringForm.setOnlineInstructions(qaContent.getOnlineInstructions());
		qaAuthoringForm.setOfflineInstructions(qaContent.getOfflineInstructions());
		sessionMap.put(ONLINE_INSTRUCTIONS_KEY, qaContent.getOnlineInstructions());
		sessionMap.put(OFFLINE_INSTRUCTIONS_KEY, qaContent.getOfflineInstructions());
	    
		logger.debug("ACTIVITY_TITLE_KEY set to:" +  sessionMap.get(ACTIVITY_TITLE_KEY ));
		
		qaAuthoringForm.resetUserAction();
		logger.debug("returning qaContent:" + qaContent);
		return qaContent;
	}

	
	/**
	 * each tool has a signature. QA tool's signature is stored in MY_SIGNATURE. The default tool content id and 
	 * other depending content ids are obtained in this method.
	 * if all the default content has been setup properly the method persists DEFAULT_CONTENT_ID in the session.
	 * 
	 * readSignature(HttpServletRequest request, ActionMapping mapping)
	 * @param request
	 * @param mapping
	 * @return ActionForward
	 */
	public boolean readSignature(HttpServletRequest request, ActionMapping mapping, IQaService qaService, 
	        QaGeneralAuthoringDTO qaGeneralAuthoringDTO, QaAuthoringForm qaAuthoringForm)
	{
		logger.debug("qaService: " + qaService);
		/*
		 * retrieve the default content id based on tool signature
		 */
		long defaultContentID=0;
		try
		{
			logger.debug("attempt retrieving tool with signatute : " + MY_SIGNATURE);
            defaultContentID=qaService.getToolDefaultContentIdBySignature(MY_SIGNATURE);
			logger.debug("retrieved tool default contentId: " + defaultContentID);
			if (defaultContentID == 0)
			{
				logger.debug("default content id has not been setup");
				return false;	
			}
		}
		catch(Exception e)
		{
			logger.debug("error getting the default content id: " + e.getMessage());
			persistError(request,"error.defaultContent.notSetup");
			return false;
		}

		
		/* retrieve uid of the content based on default content id determined above */
		long contentUID=0;
		try
		{
			logger.debug("retrieve uid of the content based on default content id determined above: " + defaultContentID);
			QaContent qaContent=qaService.loadQa(defaultContentID);
			if (qaContent == null)
			{
				logger.debug("Exception occured: No default content");
	    		persistError(request,"error.defaultContent.notSetup");
	    		return false;
			}
			logger.debug("using qaContent: " + qaContent);
			logger.debug("using qaContent uid: " + qaContent.getUid());
			contentUID=qaContent.getUid().longValue();
			logger.debug("contentUID: " + contentUID);
		}
		catch(Exception e)
		{
			logger.debug("Exception occured: No default question content");
			persistError(request,"error.defaultContent.notSetup");
			return false;
		}

		
		logger.debug("QA tool has the default content id: " + defaultContentID);
		qaGeneralAuthoringDTO.setDefaultContentIdStr(new Long(defaultContentID).toString());
		qaAuthoringForm.setDefaultContentIdStr(new Long(defaultContentID).toString());
		
		return true;
	}
	
	
	
	/**
	 * existsContent(long toolContentID)
	 * @param long toolContentID
	 * @return boolean
	 * determine whether a specific toolContentID exists in the db
	 */
	protected boolean existsContent(long toolContentID, IQaService qaService)
	{
		QaContent qaContent=qaService.loadQa(toolContentID);
	    if (qaContent == null) 
	    	return false;
	    
		return true;	
	}
	
	
	/**
	 * bridges define later url request to authoring functionality
	 * 
	 * executeDefineLater(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response, IQaService qaService) 
		throws IOException, ServletException, QaApplicationException
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param qaService
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws QaApplicationException
	 */
	public ActionForward executeDefineLater(ActionMapping mapping, QaAuthoringForm qaAuthoringForm, 
			HttpServletRequest request, HttpServletResponse response, IQaService qaService) 
		throws IOException, ServletException, QaApplicationException {
		logger.debug("calling execute..., qaService will be needed next.");
		return execute(mapping, qaAuthoringForm, request, response);
	}

	
	/**
     * persists error messages to request scope
     * @param request
     * @param message
     */
	public void persistError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
}  


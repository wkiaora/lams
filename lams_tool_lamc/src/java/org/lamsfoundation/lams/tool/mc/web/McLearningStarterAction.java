
package org.lamsfoundation.lams.tool.mc.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McApplicationException;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McContent;
import org.lamsfoundation.lams.tool.mc.McOptsContent;
import org.lamsfoundation.lams.tool.mc.McQueContent;
import org.lamsfoundation.lams.tool.mc.McQueUsr;
import org.lamsfoundation.lams.tool.mc.McSession;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.service.McServiceProxy;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * 
 * @author Ozgur Demirtas
 *
 * <lams base path>/<tool's learner url>&userId=<learners user id>&toolSessionId=123&mode=teacher

 * Tool Session:
 *
 * A tool session is the concept by which which the tool and the LAMS core manage a set of learners interacting with the tool. 
 * The tool session id (toolSessionId) is generated by the LAMS core and given to the tool.
 * A tool session represents the use of a tool for a particulate activity for a group of learners. 
 * So if an activity is ungrouped, then one tool session exist for for a tool activity in a learning design.
 *
 * More details on the tool session id are covered under monitoring.
 * When thinking about the tool content id and the tool session id, it might be helpful to think about the tool content id 
 * relating to the definition of an activity, whereas the tool session id relates to the runtime participation in the activity.

 *  * 
 * Learner URL:
 * The learner url display the screen(s) that the learner uses to participate in the activity. 
 * When the learner accessed this user, it will have a tool access mode ToolAccessMode.LEARNER.
 *
 * It is the responsibility of the tool to record the progress of the user. 
 * If the tool is a multistage tool, for example asking a series of questions, the tool must keep track of what the learner has already done. 
 * If the user logs out and comes back to the tool later, then the tool should resume from where the learner stopped.
 * When the user is completed with tool, then the tool notifies the progress engine by calling 
 * org.lamsfoundation.lams.learning.service.completeToolSession(Long toolSessionId, User learner).
 *
 * If the tool's content DefineLater flag is set to true, then the learner should see a "Please wait for the teacher to define this part...." 
 * style message.
 * If the tool's content RunOffline flag is set to true, then the learner should see a "This activity is not being done on the computer. 
 * Please see your instructor for details."
 *
 * ?? Would it be better to define a run offline message in the tool? We have instructions for the teacher but not the learner. ??
 * If the tool has a LockOnFinish flag, then the tool should lock learner's entries once they have completed the activity. 
 * If they return to the activity (e.g. via the progress bar) then the entries should be read only.
 *
 */

public class McLearningStarterAction extends Action implements McAppConstants {
	static Logger logger = Logger.getLogger(McLearningStarterAction.class.getName());
	 /* Since the toolSessionId is passed, we will derive toolContentId from the toolSessionId
	 *
	 * This class is used to load the default content and initialize the presentation Map for Learner mode 
	 * 
	 * createToolSession will not be called once the tool is deployed.
	 * 
	 * It is important that ALL the session attributes created in this action gets removed by: QaUtils.cleanupSession(request)
	 */ 

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, McApplicationException {
		Map mapQuestionsContent= new TreeMap(new McComparator());
		Map mapAnswers= new TreeMap(new McComparator());

		IMcService mcService = McUtils.getToolService(request);
		logger.debug("retrieving mcService from session: " + mcService);
		if (mcService == null)
		{
			mcService = McServiceProxy.getMcService(getServlet().getServletContext());
		    logger.debug("retrieving mcService from proxy: " + mcService);
		    request.getSession().setAttribute(TOOL_SERVICE, mcService);		
		}

		setupPaths(request);
		McLearningForm mcLearningForm = (McLearningForm) form;

		/*
		 * initialize available question display modes in the session
		 */
		request.getSession().setAttribute(QUESTION_LISTING_MODE_SEQUENTIAL,QUESTION_LISTING_MODE_SEQUENTIAL);
	    request.getSession().setAttribute(QUESTION_LISTING_MODE_COMBINED, QUESTION_LISTING_MODE_COMBINED);
	    
	    /*
	     * mark the http session as a learning activity 
	     */
	    request.getSession().setAttribute(TARGET_MODE,TARGET_MODE_LEARNING);
	    
	    /*
	     * persist time zone information to session scope. 
	     */
	    McUtils.persistTimeZone(request);
	    ActionForward validateParameters=validateParameters(request, mapping);
	    logger.debug("validateParamaters: " + validateParameters);
	    if (validateParameters != null)
	    {
	    	return validateParameters;
	    }
  
	    Long toolSessionID=(Long) request.getSession().getAttribute(TOOL_SESSION_ID);
	    logger.debug("retrieved toolSessionID: " + toolSessionID);
	    /*
	     * By now, the passed tool session id MUST exist in the db through the calling of:
	     * public void createToolSession(Long toolSessionId, Long toolContentId) by the container.
	     *  
	     * make sure this session exists in tool's session table by now.
	     */
		
	    if (!McUtils.existsSession(toolSessionID, request)) 
		{
				logger.debug("tool session does not exist" + toolSessionID);
				/*
				 *for testing only, remove this line in development 
				 */
				Long currentToolContentId= new Long(1234);
				logger.debug("simulating container behaviour: calling createToolSession with toolSessionId : " + 
						toolSessionID + " and toolContentId: " + currentToolContentId);
				try
				{
					mcService.createToolSession(toolSessionID, currentToolContentId);
					logger.debug("simulated container behaviour.");
				}
				catch(ToolException e)
				{
					logger.debug("we should never come here.");
				}
				 
		}
		
		/*
		 * by now, we made sure that the passed tool session id exists in the db as a new record
		 * Make sure we can retrieve it and the relavent content
		 */
		
		McSession mcSession=mcService.retrieveMcSession(toolSessionID);
	    logger.debug("retrieving mcSession: " + mcSession);
	    
	    /*
	     * find out what content this tool session is referring to
	     * get the content for this tool session (many to one mapping)
	     */
	    
	    /*
	     * Each passed tool session id points to a particular content. Many to one mapping.
	     */
		McContent mcContent=mcSession.getMcContent();
	    logger.debug("using mcContent: " + mcContent);
	    request.getSession().setAttribute(TOOL_CONTENT_ID, mcContent.getMcContentId());
	    logger.debug("using TOOL_CONTENT_ID: " + mcContent.getMcContentId());
	    
	    /*
	     * The content we retrieved above must have been created before in Authoring time. 
	     * And the passed tool session id already refers to it.
	     */
	    setupAttributes(request, mcContent);
	    
	    
	    /*
    	 * fetch question content from content
    	 */
    	Iterator contentIterator=mcContent.getMcQueContents().iterator();
    	while (contentIterator.hasNext())
    	{
    		McQueContent mcQueContent=(McQueContent)contentIterator.next();
    		if (mcQueContent != null)
    		{
    			int displayOrder=mcQueContent.getDisplayOrder().intValue();
        		if (displayOrder != 0)
        		{
        			/* add the question to the questions Map in the displayOrder*/
        			mapQuestionsContent.put(new Integer(displayOrder).toString(),mcQueContent.getQuestion());
        		}
        		
        		/* prepare the first question's candidate answers for presentation*/ 
        		if (displayOrder == 1)
        		{
        			logger.debug("first question... ");
        			Long uid=mcQueContent.getUid();
        			logger.debug("uid : " + uid);
        			List listMcOptions=mcService.findMcOptionsContentByQueId(uid);
        			logger.debug("listMcOptions : " + listMcOptions);
        			Map mapOptionsContent=McUtils.generateOptionsMap(listMcOptions);
        			request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
        			logger.debug("updated Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
        		}
    		}
    	}
    	
    	request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
    	logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getSession().getAttribute(MAP_QUESTION_CONTENT_LEARNER));
    	logger.debug("mcContent has : " + mapQuestionsContent.size() + " entries.");
    	request.getSession().setAttribute(TOTAL_QUESTION_COUNT, new Long(mapQuestionsContent.size()).toString());
    	
    	
    	/*
	     * Verify that userId does not already exist in the db.
	     * If it does exist, that means, that user already responded to the content and 
	     * his answers must be displayed  read-only
	     * 
	     */
    	String userID=(String) request.getSession().getAttribute(USER_ID);
    	logger.debug("userID:" + userID);
	    McQueUsr mcQueUsr=mcService.retrieveMcQueUsr(new Long(userID));
	    logger.debug("mcQueUsr:" + mcQueUsr);
	    
	    if (mcQueUsr != null)
	    {
	    	logger.debug("the learner has already responsed to this content, just generate a read-only report.");
	    	//LearningUtil learningUtil= new LearningUtil();
	    	//learningUtil.buidLearnerReport(request,1);    	
	    	//logger.debug("buidLearnerReport called successfully, forwarding to: " + LEARNER_REPORT);
	    	return (mapping.findForward(LEARNER_REPORT));
	    }
	    
	    
	    request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
		logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
		logger.debug("final Options Map for the first question: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
		
		
		return (mapping.findForward(LOAD_LEARNER));	
		
	}
	
	
	/**
	 * sets up session scope attributes based on content linked to the passed tool session id
	 * setupAttributes(HttpServletRequest request, McContent mcContent)
	 * 
	 * @param request
	 * @param mcContent
	 */
	protected void setupAttributes(HttpServletRequest request, McContent mcContent)
	{
		/* returns Integer:  can be 0 or greater than 0,  0 is no passmark, otherwise there is a passmark. */
	    logger.debug("PASSMARK: " + mcContent.getPassMark());
	    if (mcContent.getPassMark() != null)
	    {
	    	int passMark=mcContent.getPassMark().intValue();
	    	request.getSession().setAttribute(PASSMARK, mcContent.getPassMark());
	    }
	    else
	    {
	    	request.getSession().setAttribute(PASSMARK, new Integer(0));
	    }

	    request.getSession().setAttribute(IS_SHOW_LEARNERS_REPORT, new Boolean(mcContent.isShowReport()).toString());
	    logger.debug("IS_SHOW_LEARNERS_REPORT: " + new Boolean(mcContent.isShowReport()).toString());

	    /* same as 1 page per question */
	    logger.debug("IS_QUESTIONS_SEQUENCED: " + mcContent.isQuestionsSequenced());
	    if (mcContent.isQuestionsSequenced())
		{
			request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_SEQUENTIAL);
		}
	    else
	    {
	    	request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_COMBINED);
	    }
	    logger.debug("QUESTION_LISTING_MODE: " + request.getSession().getAttribute(QUESTION_LISTING_MODE));
	    
	    logger.debug("IS_RETRIES: " + new Boolean(mcContent.isRetries()).toString());
	    request.getSession().setAttribute(IS_RETRIES, new Boolean(mcContent.isRetries()).toString());
	    
	    logger.debug("REPORT_TITLE_LEARNER: " + mcContent.getReportTitle());
	    request.getSession().setAttribute(REPORT_TITLE_LEARNER,mcContent.getReportTitle());
	    
	    request.getSession().setAttribute(END_LEARNING_MESSAGE,mcContent.getEndLearningMessage());
	    logger.debug("END_LEARNING_MESSAGE: " + mcContent.getEndLearningMessage());
	    
	    logger.debug("IS_CONTENT_IN_USE: " + mcContent.isContentInUse());
	    request.getSession().setAttribute(IS_CONTENT_IN_USE, new Boolean(mcContent.isContentInUse()).toString());
	    
	    /*
	     * Is the tool activity been checked as Define Later in the property inspector?
	     */
	    logger.debug("IS_DEFINE_LATER: " + mcContent.isDefineLater());
	    request.getSession().setAttribute(IS_DEFINE_LATER, new Boolean(mcContent.isDefineLater()).toString());
	    
	    /*
	     * Is the tool activity been checked as Run Offline in the property inspector?
	     */
	    logger.debug("IS_TOOL_ACTIVITY_OFFLINE: " + mcContent.isRunOffline());
	    request.getSession().setAttribute(IS_TOOL_ACTIVITY_OFFLINE, new Boolean(mcContent.isRunOffline()).toString());
	    
	    
	    /* the following attributes are unused for the moment.
	     * from here...
	     */
	    logger.debug("ACTIVITY_TITLE: " + mcContent.getTitle());
	    request.getSession().setAttribute(ACTIVITY_TITLE,mcContent.getTitle());
	    
	    logger.debug("ACTIVITY_INSTRUCTIONS: " + mcContent.getInstructions());
	    request.getSession().setAttribute(ACTIVITY_INSTRUCTIONS,mcContent.getInstructions());
	    
		logger.debug("IS_USERNAME_VISIBLE: " + mcContent.isUsernameVisible());
	    request.getSession().setAttribute(IS_USERNAME_VISIBLE, new Boolean(mcContent.isUsernameVisible()).toString());
	    
		logger.debug("IS_SHOW_FEEDBACK: " + new Boolean(mcContent.isShowFeedback()).toString());
	    request.getSession().setAttribute(IS_SHOW_FEEDBACK, new Boolean(mcContent.isShowFeedback()).toString());
	    /* .. till here */
	}
	
	
	protected ActionForward validateParameters(HttpServletRequest request, ActionMapping mapping)
	{
		/*
	     * obtain and setup the current user's data 
	     */
		
	    String userID = "";
	    /* get session from shared session.*/
	    HttpSession ss = SessionManager.getSession();
	    /* get back login user DTO*/
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if ((user == null) || (user.getUserID() == null))
	    {
	    	logger.debug("error: The tool expects userId");
	    	persistError(request,"error.learningUser.notAvailable");
	    	request.setAttribute(USER_EXCEPTION_USERID_NOTAVAILABLE, new Boolean(true));
			return (mapping.findForward(ERROR_LIST));
	    }else
	    	userID = user.getUserID().toString();
	    
	    logger.debug("retrieved userId: " + userID);
    	request.getSession().setAttribute(USER_ID, userID);
		
	    
	    /*
	     * process incoming tool session id and later derive toolContentId from it. 
	     */
	    String strToolSessionId=request.getParameter(TOOL_SESSION_ID);
	    long toolSessionId=0;
	    if ((strToolSessionId == null) || (strToolSessionId.length() == 0)) 
	    {
	    	persistError(request, "error.toolSessionId.required");
	    	request.setAttribute(USER_EXCEPTION_TOOLSESSIONID_REQUIRED, new Boolean(true));
			return (mapping.findForward(ERROR_LIST));
	    }
	    else
	    {
	    	try
			{
	    		toolSessionId=new Long(strToolSessionId).longValue();
		    	logger.debug("passed TOOL_SESSION_ID : " + new Long(toolSessionId));
		    	request.getSession().setAttribute(TOOL_SESSION_ID,new Long(toolSessionId));	
			}
	    	catch(NumberFormatException e)
			{
	    		persistError(request, "error.sessionId.numberFormatException");
	    		logger.debug("add error.sessionId.numberFormatException to ActionMessages.");
				request.setAttribute(USER_EXCEPTION_NUMBERFORMAT, new Boolean(true));
				return (mapping.findForward(ERROR_LIST));
			}
	    }
	    return null;
	}

	
	/**
	 * sets up ROOT_PATH and PATH_TO_LAMS attributes for presentation purposes
	 * setupPaths(HttpServletRequest request)
	 * @param request
	 */
	protected void setupPaths(HttpServletRequest request)
	{
		String protocol = request.getProtocol();
		if(protocol.startsWith("HTTPS")){
			protocol = "https://";
		}else{
			protocol = "http://";
		}
		String root = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
		String pathToLams = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/../..";
		request.getSession().setAttribute(ROOT, root);
		request.getSession().setAttribute(ROOT_PATH, root);
		request.getSession().setAttribute(PATH_TO_LAMS, pathToLams);
		
		logger.debug("setting root to: " + request.getSession().getAttribute(ROOT));
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

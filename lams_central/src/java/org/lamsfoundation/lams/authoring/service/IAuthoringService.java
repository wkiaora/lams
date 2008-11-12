/***************************************************************************
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
/* $$Id$$ */
package org.lamsfoundation.lams.authoring.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Vector;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.learningdesign.dto.AuthoringActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.ValidationErrorDTO;
import org.lamsfoundation.lams.learningdesign.exception.LearningDesignException;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.WorkspaceFolder;
import org.lamsfoundation.lams.usermanagement.exception.UserAccessDeniedException;
import org.lamsfoundation.lams.usermanagement.exception.UserException;
import org.lamsfoundation.lams.usermanagement.exception.WorkspaceFolderException;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.FileUtilException;

/**
 * @author Manpreet Minhas 
 */
public interface IAuthoringService {
	
	/** Message key returned by the storeLearningDesignDetails() method */
	public static final String STORE_LD_MESSAGE_KEY = "storeLearningDesignDetails";
	public static final String INSERT_LD_MESSAGE_KEY = "insertLearningDesign";
	public static final String START_EDIT_ON_FLY_MESSAGE_KEY = "startEditOnFly";
	public static final String COPY_TOOL_CONTENT_MESSAGE_KEY = "copyMultipleToolContent";

	/**
	 * Returns a populated LearningDesign object corresponding to the given learningDesignID
	 * 
	 * @param learningDesignID The learning_design_id of the design which has to be fetched
	 * @return LearningDesign The populated LearningDesign object corresponding to the given learningDesignID
	 */
	public LearningDesign getLearningDesign(Long learningDesignID);
	
	
	/**
	 * Create a copy of learning design as per the requested learning design 
	 * and saves it in the given workspacefolder. Does not set the original 
	 * 
	 * @param originalLearningDesign The source learning design id.
	 * @param copyType purpose of copying the design. Can have one of the follwing values
	 * <ul>
	 * <li>LearningDesign.COPY_TYPE_NONE (for authoring enviornment)</li>
	 * <li>LearningDesign.COPY_TYPE_LESSON (for monitoring enviornment while creating a Lesson)</li>
	 * <li>LearningDesign.COPY_TYPE_PREVIEW (for previewing purposes)</li>
	 * </ul>
	 * @param user The user who has sent this request(author/teacher)
	 * @param setOriginalDesign If true, then sets the originalLearningDesign field in the new design
	 * @param custom comma separated values used for tool adapters
	 * @return LearningDesign The new copy of learning design.
	 */
	public LearningDesign copyLearningDesign(LearningDesign originalLearningDesign,Integer copyType,User user, 
			WorkspaceFolder workspaceFolder, boolean setOriginalDesign, String newDesignName, String customCSV) ;
	
	/**
	 * Create a copy of learning design as per the requested learning design
	 * and saves it in the given workspacefolder. Designed to be called when user tries
	 * to copy a learning design using the Flash interface. Does not set the original 
	 * learning design field, so it should not be used for creating lesson learning designs.
	 * 
	 * @param originalLearningDesingID the source learning design id.
	 * @param copyType purpose of copying the design. Can have one of the follwing values
	 * <ul>
	 * <li>LearningDesign.COPY_TYPE_NONE (for authoring enviornment)</li>
	 * <li>LearningDesign.COPY_TYPE_LESSON (for monitoring enviornment while creating a Lesson)</li>
	 * <li>LearningDesign.COPY_TYPE_PREVIEW (for previewing purposes)</li>
	 * </ul>
	 * @param userID The user_id of the user who has sent this request(author/teacher)
	 * @param workspaceFolderID The workspacefolder where this copy of the design would be saved
	 * @param setOriginalDesign If true, then sets the originalLearningDesign field in the new design
	 * @return new LearningDesign   
	 */	
	public LearningDesign copyLearningDesign(Long originalLearningDesignID,Integer copyType,
											 Integer userID, Integer workspaceFolder, boolean setOriginalDesign)
			throws UserException, LearningDesignException, WorkspaceFolderException, IOException;
	
    /** 
     * Insert a learning design into another learning design. This is a copy and paste type of copy - it just dumps the contents (with modified 
     * activity ui ids) in the main learning design. It doesn't wrap up the contents in a sequence activity. Always sets the type to COPY_TYPE_NONE.
     * @param originalDesignID The design to be "modified". Required.
     * @param designToImportID The design to be imported into originalLearningDesign. Required.
     * @param userId Current User. Required.
     * @param customCSV The custom CSV required to insert tool adapter tools, so their content can be copied in the external server
     * @param createNewLearningDesign If true, then a copy of the originalLearningDesign is made and the copy modified. If it is false, then 
     * the originalLearningDesign is modified. Required.
     * @param newDesignName New name for the design if a new design is being create. Optional. 
     * @param workspaceFolderID The folder in which to put the new learning design if createNewLearningDesign = true. May be null if createNewLearningDesign = false
     * @return New / updated learning design
     */ 
     public LearningDesign insertLearningDesign(Long originalDesignID, Long designToImportID, Integer userID, 
    		 boolean createNewLearningDesign, String newDesignName, Integer workspaceFolderID, String customCSV) throws UserException, LearningDesignException,
				 WorkspaceFolderException, IOException;

	/**
	 * @return List Returns the list of all the available LearningDesign's   
	 * */
	public List getAllLearningDesigns();
	 
	/**
	 * @return List Returns a list of all available Learning Libraries
	 */
	public List getAllLearningLibraries();
	
	/**
	 * Returns a string representing the requested LearningDesign in WDDX format
	 * 
	 * @param learningDesignID The learning_design_id of the design whose WDDX packet is requested 
	 * @return String The requested LearningDesign in WDDX format
	 * @throws Exception
	 */
	public String getLearningDesignDetails(Long learningDesignID, String languageCode)throws IOException;
	
	/**
	 * This method saves a new Learning Design to the database.
	 * It received a WDDX packet from flash, deserializes it
	 * and then finally persists it to the database.
	 * 
	 * Note: it does not validate the design - that must be done
	 * separately.
	 *
	 * @param wddxPacket The WDDX packet to be stored in the database
	 * @return Long learning design id 
	 * @throws Exception
	 */
	public Long storeLearningDesignDetails(String wddxPacket) throws Exception;
	
	/** 
	 * Validate the learning design, updating the valid flag appropriately.
	 * 
	 * This needs to be run in a separate transaction to storeLearningDesignDetails to 
	 * ensure the database is fully updated before the validation occurs (due to some
	 * quirks we are finding using Hibernate)
	 * 
	 * @param learningDesignId
	 * @throws Exception
	 */
	public Vector<ValidationErrorDTO> validateLearningDesign(Long learningDesignId);
	
	/**
	 * 
	 * @param learningDesignId
	 * @return
	 */
	public Vector<AuthoringActivityDTO> getToolActivities(Long learningDesignId, String languageCode);
	
	/**
	 * This method returns a output definitions of the Tool
	 * in WDDX format.
	 * 
	 * @return String The required definitions in WDDX format
	 * @throws IOException
	 */
	public String getToolOutputDefinitions(Long toolContentID)throws IOException;
	
	/**
	 * This method returns a list of all available Learning Designs
	 * in WDDX format.
	 * 
	 * @return String The required list in WDDX format
	 * @throws IOException
	 */
	public String getAllLearningDesignDetails()throws IOException;
	
	/**
	 * Saves the LearningDesign to the database. Will update if already saved.
	 * Used when a design is run.
	 * @param learningDesign The LearningDesign to be saved 
	 * */
	public void saveLearningDesign(LearningDesign learningDesign);

	/**
	 * Returns a list of LearningDesign's 
	 * in WDDX format, belonging to the given user
	 *  
	 * @param user The user_id of the User for whom the designs are to be fetched 
	 * @return The requested list of LearningDesign's in WDDX format
	 * @throws IOException
	 */
	public String getLearningDesignsForUser(Long userID) throws IOException;
	
	/**
	 * This method returns a list of all available system libraries in
	 * WDDX format.
	 * 
	 * @return String The required information in WDDX format
	 * @throws IOException
	 */
	public String getAllLearningLibraryDetails(String languageCode)throws IOException;
	
	/**
	 * Returns a string representing the new tool content id in 
	 * WDDX format.
	 * 
	 * Typically, when a user clicks on an activity to edit the tool contnet,
	 * it must have a tool content id passed to it. This method uses the 
	 * ToolContentIDGenerator to generate the new tool content id and passes
	 * this value back to flash in WDDX format. 
	 * 
	 * @param toolID The toolID in which to generate the new tool content id for
	 * @return String The new tool content id in WDDX Format
	 */
	public String getToolContentID(Long toolID) throws IOException;

	/**
	 * Calls an appropriate tool to copy the content indicated by toolContentId.
	 * Returns a string representing the new tool content id in WDDX format.
	 * 
	 * The is called when the user copies and pastes a tool activity icon in authoring.
	 * It should only be called on a ToolActivity - never a Gate or Grouping or
	 * Complex activity.
	 * 
	 * @param toolContentID The toolContentID indicating the content to copy
	 * @param customCSV The customCSV if this is a tool adapter tool.
	 * @return String The new tool content id in WDDX Format
	 */
	public String copyToolContent(Long toolContentID, String customCSV) throws IOException;
	
	/**
	 * Calls an appropriate tools to copy the content indicated by toolContentIds. Batch 
	 * version of String copyToolContent(Long toolContentID). Returns a map containing
	 * the old and new ids, and this is converted to a WDDX format in the calling servlet.
	 * 
	 * The is called when the user copies and pastes a complex activity icon in authoring.
	 * Authoring calls this method to copy all the contained tool activities' content
	 * in one go.
	 * 
	 * It should only be called on a ToolActivity - never a Gate or Grouping or
	 * Complex activity.
	 * 
	 * @param userId Id of the user requesting the copy
	 * @param customCSV the customCSV required to copy tool adapter tools
	 * @param toolContentIDs The toolContentIDs indicating the content to copy
	 * @return New Id map in format oldId1=newId1,oldId2=newId2,oldId3=newId3
	 */
	public String copyMultipleToolContent(Integer userId,List<Long> toolContentIds, String customCSV);
	
	/** Get the available licenses. This will include our supported Creative Common
	 * licenses and an "OTHER" license which may be used for user entered license details.
	 * The picture url supplied should be a full URL i.e. if it was a relative URL in the 
	 * database, it should have been converted to a complete server URL (starting http://)
	 * before sending to the client.
	 * 
	 * @return Vector of LicenseDTO objects. It is a Vector to ensure compatibility with WDDX 
	 */
	public Vector getAvailableLicenses();
	
	/** Delete a learning design from the database. Does not remove any content stored in tools - 
	 * that is done by the LamsCoreToolService */
	public void deleteLearningDesign(LearningDesign design);

	/**
	 * Generates a unique content folder for the newly created design to store 
	 * 
	 * @return String The unique content folder id in WDDX Format
	 */
	public String generateUniqueContentFolder()  throws FileUtilException, IOException;
		
	/**
	 * Prepares a LearningDesign to be ready for Edit-On-The-Fly (Editing).
	 * Return a string representing the updated learning design in WDDX format.
	 * 
	 * @param design The learning design whose WDDX packet is requested 
	 * @param userID  user_id of the User who will be editing the design.
	 * @throws UserException
	 * @throws LearningDesignException
	 * @throws IOException
	 */
	public String setupEditOnFlyGate(Long learningDesignID, Integer userID) throws UserException, LearningDesignException, IOException;
	public boolean setupEditOnFlyLock(Long learningDesignID, Integer userID) throws LearningDesignException, UserException, IOException;
	
	/**
	 * 
	 * 
	 * @param learningDesignID The learning_design_id of the design for which editing has finished.
	 * @param userID user_id of the User who has finished editing the design.
	 * @param cancelled flag specifying whether user cancelled or saved the edit
	 * @return wddx packet.
	 * @throws IOException
	 */
	public String finishEditOnFly(Long learningDesignID, Integer userID, boolean cancelled) throws IOException;
	
	/**
	 * 
	 * @param gate
	 */
	public LearningDesign removeTempSystemGate(GateActivity gate, LearningDesign design);
	
	/**
	 * 
	 * @param design
	 * @return
	 * @throws LearningDesignException
	 */
	public Activity getFirstUnattemptedActivity(LearningDesign design) throws LearningDesignException;
	
	/**
	 * 
	 * @param design
	 * @param userID
	 * @return
	 * @throws LearningDesignException
	 * @throws IOException
	 */
	public boolean isLearningDesignAvailable(LearningDesign design, Integer userID) throws LearningDesignException, IOException;
	
	/**
	 * Returns the generic help url from configuration
	 * 
	 * @return String Generic help url
	 * @throws Exception
	 */
	public String getHelpURL() throws Exception;

	/** Get the message service, which gives access to the I18N text */
	public MessageService getMessageService();

	/**
	 * Get a unique name for a learning design, based on the names of the learning designs in the folder. 
	 * If the learning design has duplicated name in same folder, then the new name will have a timestamp.
	 * The new name format will be oldname_ddMMYYYY_idx. The idx will be auto incremental index number, start from 1.  
	 * Warning - this may be quite intensive as it gets all the learning designs in a folder.
	 * @param originalLearningDesign
	 * @param workspaceFolder
	 * @param copyType
	 * @return
	 */
	public String getUniqueNameForLearningDesign(String originalTitle, Integer workspaceFolderId);

}

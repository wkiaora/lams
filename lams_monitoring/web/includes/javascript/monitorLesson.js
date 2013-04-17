﻿// ********** GLOBAL VARIABLES **********
// copy of lesson SVG so it does no need to be fetched every time
var originalSequenceCanvas = null;
// DIV container for lesson SVG; it gets accessed so many times it's worth to cache it here
var sequenceCanvas = null;
// how learners in pop up lists are currently sorted
var sortOrderAsc = {
	learnerGroup : false,
	classLearner : false,
	classMonitor : false
};

//********** LESSON TAB FUNCTIONS **********

/**
 * Sets up lesson tab.
 */
function initLessonTab(){
	// sets export portfolio availability
	$('#exportAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'learnerExportPortfolioAvailable',
				'learnerExportPortfolio' : checked,
				'lessonID'      : lessonId
			}
		});
	});
	
	// sets presence availability
	$('#presenceAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'presenceAvailable',
				'presenceAvailable' : checked,
				'lessonID'      : lessonId
			},
			success : function() {
				if (checked) {
					$('#imAvailableField').attr('disabled', null);
					alert(LESSON_PRESENCE_ENABLE_ALERT_LABEL);
				} else {
					$('#imAvailableField').attr({
						'checked'  : null,
						'disabled' : 'disabled'
					});
					alert(LESSON_PRESENCE_DISABLE_ALERT_LABEL);
				}
			}
		});
	});
	
	// sets instant messaging availability
	$('#imAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'presenceImAvailable',
				'presenceImAvailable' : checked,
				'lessonID'      : lessonId
			},
			success : function() {
				if (checked) {
					$('#openImButton').css('display', 'inline');
					alert(LESSON_IM_ENABLE_ALERT_LABEL);
				} else {
					$('#openImButton').css('display', 'none');
					alert(LESSON_IM_DISABLE_ALERT_LABEL);
				}
			}
		});
	});
	
	// sets up calendar for schedule date choice
	$('#scheduleDatetimeField').datetimepicker({
		'minDate' : 0
	});
	
	// sets up dialog for editing class
	$('#classDialog').dialog({
		'autoOpen'  : false,
		'height'    : 360,
		'width'     : 700,
		'minWidth'  : 700,
		'modal'     : true,
		'resizable' : true,
		'show'      : 'fold',
		'hide'      : 'fold',
		'open'      : function(){
			// reset sort order
			sortOrderAsc['classLearner'] = false;
			sortOrderAsc['classMonitor'] = false;
			sortDialogList('classLearner');
			sortDialogList('classMonitor');
			colorDialogList('classLearner');
			colorDialogList('classMonitor');
		},
		'buttons' : [
		             {
		            	'text'   : 'Save',
		            	'id'     : 'classDialogSaveButton',
		            	'click'  : function() {
		            		var dialog = $(this);
		            		var learners = getSelectedClassUserList('classLearnerList');
		            		var monitors = getSelectedClassUserList('classMonitorList');
		            		$.ajax({
		            			url : LAMS_URL + 'monitoring/monitoring.do',
		            			cache : false,
		            			data : {
		            				'method'    : 'updateLessonClass',
		            				'lessonID'  : lessonId,
		            				'learners'  : learners,
		            				'monitors'  : monitors
		            			},
		            			success : function() {
		            				dialog.dialog('close');
		            				refreshMonitor();
		            			}
		            		});
						}
		             },
		             {
		            	'text'   : 'Cancel',
		            	'id'     : 'classDialogCancelButton',
		            	'click'  : function() {
							$(this).dialog('close');
						} 
		             }
		]
	});

	$('#classLearnerSortButton').click(function(){
		sortDialogList('classLearner');
	});	
	$('#classMonitorSortButton').click(function(){
		sortDialogList('classMonitor');
	});	
}


/**
 * Shows all learners in the lesson class.
 */
function showLessonLearnersDialog() {
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'LEARNER',
			'classOnly' : true
		},
		success : function(response) {
			 showLearnerGroupDialog(null, LESSON_GROUP_DIALOG_CLASS_LABEL, response);
		}
	});
}


/**
 * Changes lesson state and updates widgets.
 */
function changeLessonState(){
	var method = null;
	var state = +$('#lessonStateField').val();
	switch (state) {
		case 3: //STARTED
			switch (lessonStateId) {
				case 4: //SUSPENDED
					method = "unsuspendLesson";
					break;
				case 6: //ARCHIVED
					method = "unarchiveLesson";
					break;
			}
			break;
		case 4: 
			method = "suspendLesson";
			break;
		case 6: 
			method = "archiveLesson";
			break;
		case 7: //FINISHED
			if (confirm(LESSON_REMOVE_ALERT_LABEL)){
				if (confirm(LESSON_REMOVE_DOUBLECHECK_ALERT_LABEL)) {
					method = "removeLesson";
				}
			}
			break;
	}
	
	if (method) {
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : method,
				'lessonID'  : lessonId
			},
			success : function() {
				if (state == 7) {
					// user chose to finish the lesson, close monitoring and refresh the lesson list
					window.parent.closeMonitorLessonDialog(true);
				} else {
					refreshMonitor();
				}
			}
		});
	}
}


/**
 * Updates widgets in lesson tab according to respose sent to refreshMonitor()
 */
function updateLessonTab(refreshResponse){
	// update lesson state label
	lessonStateId = +refreshResponse.lessonStateID;
	var label = null;
	switch (lessonStateId) {
		case 1:
			label = LESSON_STATE_CREATED_LABEL;
			break;
		case 2:
			label = LESSON_STATE_SCHEDULED_LABEL;
			break;
		case 3:
			label = LESSON_STATE_STARTED_LABEL;
			break;
		case 4:
			label = LESSON_STATE_SUSPENDED_LABEL;
			break;
		case 5:
			label = LESSON_STATE_FINISHED_LABEL;
			break;
		case 6:
			label = LESSON_STATE_ARCHIVED_LABEL;
			break;
		case 7:
			label = LESSON_STATE_REMOVED_LABEL;
			break;
	}
	$('#lessonStateLabel').text(label);
	
	// update available options in change state dropdown menu
	var selectField = $('#lessonStateField');
	// remove all except "Select status" option
	selectField.children('option:not([value="-1"])').remove();
	switch (lessonStateId) {
		case 3:
			$('<option />').attr('value', 4).text(LESSON_STATE_ACTION_DISABLE_LABEL).appendTo(selectField);
			$('<option />').attr('value', 6).text(LESSON_STATE_ACTION_ARCHIVE_LABEL).appendTo(selectField);
			$('<option />').attr('value', 7).text(LESSON_STATE_ACTION_REMOVE_LABEL).appendTo(selectField);
			break;
		case 4:
			$('<option />').attr('value', 3).text(LESSON_STATE_ACTION_ACTIVATE_LABEL).appendTo(selectField);
			$('<option />').attr('value', 6).text(LESSON_STATE_ACTION_ARCHIVE_LABEL).appendTo(selectField);
			$('<option />').attr('value', 7).text(LESSON_STATE_ACTION_REMOVE_LABEL).appendTo(selectField);
			break;
		case 5:
			break;
		case 6:
			$('<option />').attr('value', 3).text(LESSON_STATE_ACTION_ACTIVATE_LABEL).appendTo(selectField);
			$('<option />').attr('value', 7).text(LESSON_STATE_ACTION_REMOVE_LABEL).appendTo(selectField);
			break;
	}
	
	// show/remove widgets for lesson scheduling
	var scheduleControls = $('#scheduleDatetimeField, #scheduleLessonButton, #startLessonButton');
	var startDateField = $('#lessonStartDateSpan');
	switch (lessonStateId) {
		 case 1:
			 scheduleControls.css('display','inline');
			 startDateField.css('display','none');
			 break;
		 case 2:
			 scheduleControls.css('display','none');
			 startDateField.text(refreshResponse.startDate).add('#startLessonButton').css('display','inline');
			 break;
		default: 			
			scheduleControls.css('display','none');
		 	startDateField.text(refreshResponse.startDate).css('display','inline');
		 	break;
	}
}


function scheduleLesson(){
	var date = $('#scheduleDatetimeField').val();
	if (date) {
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'          : 'startOnScheduleLessonJSON',
				'lessonID'        : lessonId,
				'lessonStartDate' : date
			},
			success : refreshMonitor
		});
	} else {
		alert(LESSON_ERROR_SCHEDULE_DATE_LABEL);
	}
}


function startLesson(){
	$.ajax({
		dataType : 'xml',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'          : 'startLesson',
			'lessonID'        : lessonId
		},
		success : refreshMonitor
	});
}


/**
 * Stringifies user IDs who were selected in Edit Class dialog. 
 */
function getSelectedClassUserList(containerId) {
	var list = '';
	$('#' + containerId).children('div.dialogListItem').each(function(){
		if ($('input:checked', this).length > 0){
			list += $(this).attr('userId') + ',';
		}
	});
	return list;
}


function openChatWindow(){
	// variables are set in JSP page
	window.open(LAMS_URL + 'learning/lessonChat.jsp?lessonID=' + lessonId 
			+ '&presenceEnabledPatch=true&presenceImEnabled=true&presenceShown=true&createDateTime='
			+ createDateTimeStr
			,'Chat'
			,'width=650,height=350,resizable=no,scrollbars=no,status=no,menubar=no,toolbar=no');
}



//********** SEQUENCE TAB FUNCTIONS **********

/**
 * Sets up the sequence tab.
 */
function initSequenceTab(){
    // initialise lesson dialog
	$('#learnerGroupDialog').dialog({
			'autoOpen'  : false,
			'height'    : 360,
			'width'     : 330,
			'minWidth'  : 330,
			'modal'     : true,
			'resizable' : true,
			'show'      : 'fold',
			'hide'      : 'fold',
			'open'      : function(){
				// reset sort order
				sortOrderAsc['learnerGroup'] = false;
				sortDialogList('learnerGroup');
				colorDialogList('learnerGroup');
				// until operator selects an user, buttons remain disabled
				$('button.learnerGroupDialogSelectableButton').blur().removeClass('ui-state-hover')
					.attr('disabled', 'disabled');
			},
			'buttons' : [
			             {
			            	'text'   : FORCE_COMPLETE_BUTTON_LABEL,
			            	'id'     : 'learnerGroupDialogForceCompleteButton',
			            	'class'  : 'learnerGroupDialogSelectableButton',
			            	'click'  : function() {
			            		var selectedLearner = $('#learnerGroupList div.dialogListItemSelected');
			            		// make sure there is only one selected learner
			            		if (selectedLearner.length == 1) {
			            			// go to "force complete" mode, similar to draggin user to an activity
			            			var activityId = $(this).dialog('option', 'activityId');
			            			var dropArea = sequenceCanvas.add('#completedLearnersContainer');
			            			dropArea.css('cursor', 'url('
			            					+ LAMS_URL + 'images/icons/user.png),pointer')
			            				.one('click', function(event) {
			            					dropArea.off('click').css('cursor', 'default');
			            					forceComplete(activityId, selectedLearner.attr('userId'), 
			            							selectedLearner.text(), event.pageX, event.pageY);
			            				});
				            		$(this).dialog('close');
				            		alert(FORCE_COMPLETE_CLICK_LABEL.replace('[0]', selectedLearner.text()));
			            		}
							}
			             },
			             {
			            	'text'   : VIEW_LEARNER_BUTTON_LABEL,
			            	'id'     : 'learnerGroupDialogViewButton',
			            	'class'  : 'learnerGroupDialogSelectableButton',
			            	'click'  : function() {
			            		var selectedLearner = $('#learnerGroupList div.dialogListItemSelected');
			            		if (selectedLearner.length == 1) {
			            			// open pop up with user progress in the given activity
			            			openWindow(LAMS_URL 
			            					+ selectedLearner.attr('viewUrl'), "LearnActivity", 800, 600);
			            		}
							}
			             },
			             {
			            	'text'   : CLOSE_BUTTON_LABEL,
			            	'id'     : 'learnerGroupDialogCloseButton',
			            	'click'  : function() {
								$(this).dialog('close');
							} 
			             }
			]
		});
	
	$('#learnerGroupSortButton').click(function(){
		sortDialogList('learnerGroup');
	});	
}


/**
 * Updates learner progress in sequence tab according to respose sent to refreshMonitor()
 */
function updateSequenceTab(response) {
	var learnerCount = 0;
	$.each(response.activities, function(){
		if (this.learners) {
			// are there any learners in this or any activity?
			learnerCount += this.learners.length;
			// put learner icons on each activity shape
			addLearnerIcons(this);
		}
	});
	
	if (learnerCount > 0) {
		// IMPORTANT! Reload SVG, otherwise added icons will not get displayed
		sequenceCanvas.html(sequenceCanvas.html());
	}
	
	var completedLearners = response.completedLearners;
	var learnerTotalCount = learnerCount + (completedLearners ? completedLearners.length : 0 );
	$('#learnersStartedPossibleCell').text(learnerTotalCount + ' / ' + response.numberPossibleLearners);
	addCompletedLearnerIcons(completedLearners, learnerTotalCount);
	
	$.each(response.activities, function(activityIndex, activity){
		addLearnerIconsHandlers(activity);
		
		if (activity.url) {
			var activityGroup = $('rect[id="act' + activity.id + '"]', sequenceCanvas).parent();
			activityGroup.css('cursor', 'pointer').dblclick(function(){
				// double click on activity shape to open Monitoring for this activity
				openWindow(LAMS_URL + activity.url, "MonitorActivity", 900, 720);
			});
		}
	});
}


/**
 * Forces given learner to move to activity indicated on SVG by coordinated (drag-drop)
 */
function forceComplete(currentActivityId, learnerId, learnerName, x, y) {
	// check all activities and "users who finished lesson" bar
	$('rect[id^="act"]', sequenceCanvas).add('#completedLearnersContainer').each(function(){
		// find which activity learner was dropped on
		var act = $(this);
		var actX = act.offset().left;
		var actY = act.offset().top;
		var actEndX = actX + (act.width() ? act.width() : +act.attr('width'));
		var actEndY = actY + (act.height() ? act.height() : +act.attr('height'));
		if (x >= actX && x<= actEndX && y>= actY && y<=actEndY) {
			var previousActivityId = null;
			var executeForceComplete = false;
			
			if (act.attr('id') == 'completedLearnersContainer') {
				executeForceComplete = confirm(FORCE_COMPLETE_END_LESSON_CONFIRM_LABEL
						.replace('[0]',learnerName));
			} else {
				var transitionLine = $('line[id$="to_' 
						+ act.parent().attr('id') + '"]:not([id^="arrow"])'
						, sequenceCanvas);
				// if move to start of sequence, the value is -1
				previousActivityId = transitionLine.length == 1 ?
						transitionLine.attr('id').split('_')[0] : -1;
						
				var targetActivityName = act.siblings('text[id^="TextElement"]').text();
				executeForceComplete = confirm(FORCE_COMPLETE_ACTIVITY_CONFIRM_LABEL
							.replace('[0]', learnerName).replace('[1]', targetActivityName));
			}
			
			if (executeForceComplete) {
				// tell server to force complete the learner
				$.ajax({
					dataType : 'xml',
					url : LAMS_URL + 'monitoring/monitoring.do',
					cache : false,
					data : {
						'method'     : 'forceComplete',
						'lessonID'   : lessonId,
						'learnerID'  : learnerId,
						'activityID' : previousActivityId
					},
					success : function(response) {
						// inform user of result
						var messageElem = $(response).find('var[name="messageValue"]');
						if (messageElem.length > 0){
							alert(messageElem.text());
						}
						
						// progress changed, show it to monitor
						refreshMonitor();
					}
				});
			}
			// we found our target, stop iteration
			return false;
		}
	});
}


/**
 * Draw user icons on top of activities.
 */
function addLearnerIcons(activity) {
	var activityRect = $('rect[id="act' + activity.id + '"]', sequenceCanvas);
	var activityGroup = activityRect.parent();
	var actX = +activityRect.attr('x') + 1;
	var actY = +activityRect.attr('y') + 1;
	var actTooltip = LEARNER_GROUP_LIST_TITLE_LABEL;
	
	$.each(activity.learners, function(learnerIndex, learner){
		if (activity.learners.length > 8 && learnerIndex == 7) {
			// maximum 8 icons fit in an activity 
			var actRightBorder = actX + +activityRect.attr('width');
			var groupTitle = activity.learners.length + ' ' + LEARNER_GROUP_COUNT_LABEL
				+ ' ' + LEARNER_GROUP_SHOW_LABEL;
			// if icons do not fit in shape anymore, show a group icon
			var element = appendXMLElement('image', {
				'id'         : 'act' + activity.id + 'learnerGroup',
				'x'          : actRightBorder - 19,
				'y'          : actY + 1,
				'height'     : 16,
				'width'      : 16,
				'xlink:href' : LAMS_URL + 'images/icons/group.png'
			}, null, activityGroup[0]);
			appendXMLElement('title', null, groupTitle, element);
			// add a small number telling how many learners are in the group
			element = appendXMLElement('text', {
				'id'         : 'act' + activity.id + 'learnerGroupText',
				'x'          : actRightBorder - 10,
				'y'          : actY + 24,
				'text-anchor': 'middle',
				'font-family': 'Verdana',
				'font-size'  : 8
			}, activity.learners.length, activityGroup[0]);
			appendXMLElement('title', null, groupTitle, element);
			// stop processing learners
			return false;
		} else {
			/* make an icon for each learner */
			var element = appendXMLElement('image', {
				'id'         : 'act' + activity.id + 'learner' + learner.id,
				'x'          :  actX + learnerIndex*15,
				'y'          :  actY,
				'height'     : 16,
				'width'      : 16,
				'xlink:href' : LAMS_URL + 'images/icons/user.png'
			}, null, activityGroup[0]);
			var learnerDisplayName = getLearnerDisplayName(learner);
			appendXMLElement('title', null, learnerDisplayName, element);
			actTooltip += '\n' + learnerDisplayName;
		}
	});
	
	appendXMLElement('title', null, actTooltip, activityGroup[0]);
}


/**
 * After SVG refresh, add click/dblclick/drag handlers to user icons.
 */
function addLearnerIconsHandlers(activity) {
	if (activity.learners) {
		var activityGroup = $('rect[id="act' + activity.id + '"]', sequenceCanvas).parent();
		
		$.each(activity.learners, function(learnerIndex, learner){	
			$('image[id="act' + activity.id + 'learner' + learner.id + '"]', activityGroup)
			 .dblclick(function(event){
				 // double click on learner icon to see activity from his perspective
				event.stopPropagation();
				openWindow(LAMS_URL + learner.url, "LearnActivity", 800, 600);
			})
			// drag learners to force complete activities
			.draggable({
				'appendTo'    : '#tabSequence',
				'containment' : '#tabSequence',
			    'distance'    : 20,
			    'scroll'      : false,
			    'cursorAt'	  : {'left' : 10, 'top' : 15},
				'helper'      : function(event){
					// copy of the icon for dragging
					return $('<img />').attr('src', LAMS_URL + 'images/icons/user.png');
				},
				'stop' : function(event, ui) {
					// jQuery droppable does not work for SVG, so this is a workaround
					forceComplete(activity.id, learner.id, getLearnerDisplayName(learner),
							      ui.offset.left, ui.offset.top);
				}
			});
		});

		var learnerGroupIcon = $('*[id^="act' + activity.id + 'learnerGroup"]', activityGroup);
		// 0 is for no group icon, 2 is for icon + digits
		if (learnerGroupIcon.length == 2) {
			var activityName = $('text[id^="TextElement"]', activityGroup).text();
			learnerGroupIcon.dblclick(function(event){
				 // double click on learner icon to see activity from his perspective
				event.stopPropagation();
				showLearnerGroupDialog(activity.id, activityName, activity.learners);
			})
		}
	}
}


/**
 * Add learner icons in "finished lesson" bar.
 */
function addCompletedLearnerIcons(learners, learnerTotalCount) {
	var iconsContainer = $('#completedLearnersContainer');
	// clear all icons except the door
	iconsContainer.children(':not(img#completedLearnersDoorIcon)').remove();
	var completedLearnerCount = (learners ? learners.length : 0 );
	// show (current/total) label
	$('<span />').attr({
		'title' : LEARNER_FINISHED_COUNT_LABEL
			.replace('[0]', completedLearnerCount).replace('[1]', learnerTotalCount)
	}).text('(' + completedLearnerCount + '/' + learnerTotalCount + ')')
	  .appendTo(iconsContainer);
	
	if (learners) {
		// create learner icons, along with handlers
		$.each(learners, function(learnerIndex, learner){
			// maximum 41 icons in the bar
			if (learners.length > 43 && learnerIndex == 42) {
				// if icons do not fit in cell anymore, show a group icon
				$('<img />').attr({
					'src' : LAMS_URL + 'images/icons/group.png',
					'title'      : LEARNER_GROUP_SHOW_LABEL
				}).css('cursor', 'pointer')
				  .dblclick(function(){
					showLearnerGroupDialog(null, LEARNER_FINISHED_DIALOG_TITLE_LABEL, learners);
				}).appendTo(iconsContainer);
				// stop processing learners
				return false;
			} else {
				// make an icon for each learner
				$('<img />').attr({
					'src' : LAMS_URL + 'images/icons/user.png',
					'title'      : getLearnerDisplayName(learner)
				}).appendTo(iconsContainer);
			}
		});
	}
}


/**
 * Shows Edit Class dialog for class manipulation.
 */
function showClassDialog(){
	var learners = [];
	var monitors = [];
	
	// fetch available and alredy participation learners and monitors
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		async : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'LEARNER',
			'classOnly' : false
		},
		success : function(response) {
			learners = response;
		}
	});
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		async : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'MONITOR',
			'classOnly' : false
		},
		success : function(response) {
			monitors = response;
		}
	});
	
	// fill lists
	fillClassDialogList('classLearner', learners, false);
	fillClassDialogList('classMonitor', monitors, true);

	$('#classDialog')
		.dialog('option',
			{
			 'title' : LESSON_EDIT_CLASS_LABEL
			})
		.dialog('open');
}


/**
 * Fills class member list with user information.
 */
function fillClassDialogList(listId, users, disableCreator) {
	var list = $('#' + listId + 'List').empty();
	$.each(users, function(userIndex, user) {
		var checkbox = $('<input />').attr({
	    	 'type' : 'checkbox'
	      });
		if (user.classMember) {
			checkbox.attr('checked', 'checked');
			if (disableCreator && user.lessonCreator) {
				// user creator must not be deselected
				checkbox.attr('disabled', 'disabled');
			}
		}
		
		var userDiv = $('<div />').attr({
			'userId'  : user.id,
			})
          .addClass('dialogListItem')
	      .text(getLearnerDisplayName(user))
	      .prepend(checkbox)
	      .appendTo(list);
		
		if (disableCreator && user.lessonCreator) {
			userDiv.addClass('dialogListItemDisabled');
		} else {
			userDiv.click(function(event){
				if (event.target == this) {
		    		checkbox.attr('checked', checkbox.is(':checked') ? null : 'checked');
		    	}
		    })
		}
	});	
}



//********** LEARNERS TAB FUNCTIONS **********

function initLearnersTab(){
	
}



//********** COMMON FUNCTIONS **********

/**
 * Updates all changeable elements of monitoring screen.
 */
function refreshMonitor(){
	if (originalSequenceCanvas) {
		// put bottom layer, LD SVG
		sequenceCanvas.html(originalSequenceCanvas);
	} else {
		// fetch SVG just once, since it is immutable
		$.ajax({
			dataType : 'text',
			url : LAMS_URL + 'home.do',
			async : false,
			cache : false,
			data : {
				'method'    : 'createLearningDesignThumbnail',
				'svgFormat' : 1,
				'ldId'      : ldId
			},
			success : function(response) {
				originalSequenceCanvas = response;
				sequenceCanvas = $('#sequenceCanvas').html(originalSequenceCanvas);
				
				var canvasHeight = sequenceCanvas.height();
				var canvasWidth = sequenceCanvas.width();
				var svg = $('svg', sequenceCanvas);
				var canvasPaddingTop = canvasHeight/2 - svg.attr('height')/2;
				var canvasPaddingLeft = canvasWidth/2 - svg.attr('width')/2;

				if (canvasPaddingTop > 0) {
					sequenceCanvas.css({
						'padding-top' : canvasPaddingTop,
						'height'      : canvasHeight - canvasPaddingTop		
					});
				}
				if (canvasPaddingLeft > 0) {
					sequenceCanvas.css({
						'padding-left' : canvasPaddingLeft,
						'width'        : canvasWidth - canvasPaddingLeft		
					});
				}
			}
		});
	}
	
	// get update data
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'    : 'getLessonProgressJSON',
			'lessonID'  : lessonId
		},
		success : function(response) {
			// update lesson tab widgets (state, number of learners etc.)
			updateLessonTab(response);
			
			// update learner progress in sequence tab
			updateSequenceTab(response);
		}
	});
}


/**
 * Show a dialog with user list and optional Force Complete and View Learner buttons.
 */
function showLearnerGroupDialog(activityId, dialogTitle, learners) {
	var learnerGroupList = $('#learnerGroupList').empty();
	var learnerGroupDialog = $('#learnerGroupDialog');
	$.each(learners, function(learnerIndex, learner) {
		var learnerDiv = $('<div />').attr({
								'userId'  : learner.id,
								'viewUrl'    : learner.url
								})
		                      .addClass('dialogListItem')
						      .text(getLearnerDisplayName(learner))
						      .appendTo(learnerGroupList);
		
		if (activityId) {
			learnerDiv.click(function(){
		    	  // select a learner
		    	  $(this).addClass('dialogListItemSelected')
		    	  	.siblings('div.dialogListItem')
		    	  	.removeClass('dialogListItemSelected');
		    	  // enable buttons
		    	  $('button.learnerGroupDialogSelectableButton')
		    	  	.attr('disabled', null);
		      })
		      .dblclick(function(){
				// same as clicking View Learner button
				openWindow(LAMS_URL + learner.url, "LearnActivity", 800, 600);
		    });
		}
	});
	
	// no activity ID, i.e. showing finshed learners, so no buttons
	$('button.learnerGroupDialogSelectableButton').css('display', activityId ? 'inline' : 'none');
	
	learnerGroupDialog
		.dialog('option', 
			{
			 'title' : dialogTitle,
			 'activityId' : activityId,
			  
			})
		.dialog('open');	
}


/**
 * Formats learner name.
 */
function getLearnerDisplayName(learner) {
	return learner.firstName + ' ' + learner.lastName + ' (' + learner.login + ')';
}


/**
 * Change order of learner sorting in group dialog.
 */
function sortDialogList(listId) {
	var list = $('#' + listId + 'List');
	var items = list.children('div.dialogListItem');
	var orderAsc = sortOrderAsc[listId];
	if (items.length > 1) {
		items.each(function(){
			$(this).detach();
		}).sort(function(a, b){
			var keyA = $(a).text().toLowerCase();
			var keyB = $(b).text().toLowerCase();
			var result = keyA > keyB ? 1 : keyA < keyB ? -1 : 0;
			return orderAsc ? -result : result;
		}).each(function(){
			$(this).appendTo(list);
		});
		
		var button = $('#' + listId + 'SortButton');
		if (orderAsc) {
			button.html('▼');
			sortOrderAsc[listId] = false;
		} else {
			button.html('▲');
			sortOrderAsc[listId] = true;
		}
	}
}


function colorDialogList(listId) {
	$('#' + listId + 'List div.dialogListItem').each(function(userIndex, userDiv){
		// every odd learner has different background
		$(userDiv).css('background-color', userIndex % 2 ? '#dfeffc' : 'inherit');
	});
}


function openWindow(url, title, width, height) {
	window.open(url, title, "width=" + width + ",height=" + height
			+ ",resizable=yes,scrollbars=yes,status=yes,menubar=no,toolbar=no");
}


/**
 * Makes a XML element with given attributes.
 * jQuery does not work well with SVG in Chrome, so all this manipulation need to be done manually. 
 */
function appendXMLElement(tagName, attributesObject, content, target) {
	var elementText = '<' + tagName + (content ? '>' + content + '</' + tagName + '>'
											   : ' />');
	var element = $.parseXML(elementText).firstChild;
	if (attributesObject) {
		for (attrKey in attributesObject) {
			element.setAttribute(attrKey, attributesObject[attrKey]);
		}
	}

	target.appendChild(element);
	return element;
}
<%@ include file="/common/taglibs.jsp"%>

<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="summaryList" value="${sessionMap.summaryList}"/>
<c:set var="scratchie" value="${sessionMap.scratchie}"/>

<script type="text/javascript">
	<!--	
	$(document).ready(function(){
		<c:forEach var="summary" items="${summaryList}" varStatus="status">
		
			jQuery("#list${summary.sessionId}").jqGrid({
				datatype: "local",
				height: 'auto',
				width: 900,
				shrinkToFit: true,
			   	ondblClickRow: function(rowid) {
			   		var userId = jQuery("#list${summary.sessionId}").getCell(rowid, 'userId');
			   		var toolSessionId = jQuery("#list${summary.sessionId}").getCell(rowid, 'sessionId');

			   		var userSummaryUrl = "<c:url value='/learning/start.do'/>?userID=" + userId + "&toolSessionID=" + toolSessionId + "&mode=teacher&reqId=" + (new Date()).getTime();
					launchPopup(userSummaryUrl, "MonitoringReview");		
			  	},
				
			   	colNames:['#',
						'userId',
						'sessionId',
						"<fmt:message key="label.monitoring.summary.user.name" />",
						"<fmt:message key="label.monitoring.summary.attempts" />",
					    "<fmt:message key="label.monitoring.summary.mark" />"],
					    
			   	colModel:[
			   		{name:'id',index:'id', width:0, sorttype:"int"},
			   		{name:'userId',index:'userId', width:0},
			   		{name:'sessionId',index:'sessionId', width:0},
			   		{name:'userName',index:'userName', width:400},
			   		{name:'totalAttempts',index:'totalAttempts', width:100,align:"right",sorttype:"int"},
			   		{name:'mark',index:'mark', width:100,align:"right",sorttype:"int"}		
			   	],
			   	caption: "${summary.sessionName}"
			}).hideCol("id").hideCol("userId").hideCol("sessionId");
			
   	        <c:forEach var="user" items="${summary.users}" varStatus="i">
   	     		jQuery("#list${summary.sessionId}").addRowData(${i.index + 1}, {
   	   	     		id:"${i.index + 1}",
   	   	     		userId:"${user.userId}",
   	   	     		sessionId:"${user.session.sessionId}",
   	   	     		userName:"${user.lastName}, ${user.firstName}",
   	   				totalAttempts:"${user.totalAttempts}",
   	   				mark:"<c:choose> <c:when test='${user.mark == -1}'>-</c:when> <c:otherwise>${user.mark}</c:otherwise> </c:choose>"
   	   	   	    });
	        </c:forEach>
			
		</c:forEach>

		$("#itemUid").change(function() {
			var itemUid = $(this).val();
			if (itemUid != -1) {
				var itemSummaryUrl = '<c:url value="/monitoring/itemSummary.do?sessionMapID=${sessionMapID}"/>';
				var itemSummaryHref = itemSummaryUrl + "&itemUid=" + itemUid + "&KeepThis=true&TB_iframe=true&height=400&width=650";
				$("#itemSummaryHref").attr("href", itemSummaryHref);	

				//return;
				$("#itemSummaryHref").click(); 		 
			}
	    });
		
		$("#userIdDropdown").change(function() {
			var userId = $(this).val();
			
			if (userId != -1) {
				var toolSessionId = $(this).find('option:selected').attr("alt");
				var userSummaryUrl = "<c:url value='/learning/start.do'/>?userID=" + userId + "&toolSessionID=" + toolSessionId + "&mode=teacher";
	
				launchPopup(userSummaryUrl, "MonitoringReview");
			}
	    });
	});

	function resizeIframe() {
		if (document.getElementById('TB_iframeContent') != null) {
		    var height = top.window.innerHeight;
		    if ( height == undefined || height == 0 ) {
		    	// IE doesn't use window.innerHeight.
		    	height = document.documentElement.clientHeight;
		    }
		    height -= document.getElementById('TB_iframeContent').offsetTop + 60;
		    document.getElementById('TB_iframeContent').style.height = height +"px";
	
			TB_HEIGHT = height + 28;
			tb_position();
		}
	};
	window.onresize = resizeIframe;
	-->		
</script>

<%@ include file="parts/advanceOptions.jsp"%>

<c:choose>
	<c:when test="${empty summaryList}">
		<div align="center">
			<b> <fmt:message key="message.monitoring.summary.no.session" /> </b>
		</div>	
	</c:when>
	<c:otherwise>
	
		<div style="padding-left: 20px; margin-bottom: 10px; margin-top: 15px;">
			<H1><fmt:message key="label.monitoring.summary.summary" /></H1>
		</div>	
	
		<div style="padding-left: 30px; font-size: small; margin-bottom: 20px; font-style: italic;">
			<fmt:message key="label.monitoring.summary.select.student" />
		</div>
	
		<c:forEach var="summary" items="${summaryList}" varStatus="status">
			<div style="padding-left: 30px; <c:if test='${! status.last}'>padding-bottom: 30px;</c:if><c:if test='${ status.last}'>padding-bottom: 15px;</c:if> ">
				<c:if test="${sessionMap.isGroupedActivity}">
					<div style="padding-bottom: 5px; font-size: small;">
						<B><fmt:message key="monitoring.label.group" /></B> ${summary.sessionName}
					</div>
				</c:if>
				
				<table id="list${summary.sessionId}" class="scroll" cellpadding="0" cellspacing="0"></table>
			</div>
		</c:forEach>
		
		<!-- Dropdown menu for choosing scratchie item -->
		
		<div style="padding-left: 20px; margin-bottom: 15px; margin-top: 30px;">
			<H1><fmt:message key="label.monitoring.summary.report.by.scratchie" /></H1>
		</div>
		
		<div style="padding-left: 30px; margin-top: -5px; margin-bottom: 25px;">

			<select id="itemUid" style="float: left">
				<option selected="selected" value="-1"><fmt:message key="label.monitoring.summary.choose" /></option>
    			<c:forEach var="item" items="${scratchie.scratchieItems}">
					<option value="${item.uid}">${item.title}</option>
			   	</c:forEach>
			</select>
			
			<a href="#nogo" class="thickbox" id="itemSummaryHref" style="display: none;"></a>
		</div>
		
		<!-- Dropdown menu for choosing user -->
		
		<div style="padding-left: 20px; margin-bottom: 15px; margin-top: 60px;">
			<H1><fmt:message key="label.monitoring.summary.report.by.user" /></H1>
		</div>
		
		<div style="padding-left: 30px; margin-top: -5px; margin-bottom: 25px;">

			<select id="userIdDropdown" style="float: left">
				<option selected="selected" value="-1"><fmt:message key="label.monitoring.summary.choose" /></option>
    			<c:forEach var="learner" items="${sessionMap.learners}">
					<option value="${learner.userId}" alt="${learner.session.sessionId}">${learner.firstName} ${learner.lastName}</option>
			   	</c:forEach>
			</select>
			
			<a href="#nogo" id="userSummaryHref" style="display: none;"></a>
		</div>
		
		<div style="margin: 50px 20px 0px; padding-bottom: 20px;">
			<a href="<c:url value='/monitoring/manageLeaders.do'/>?sessionMapID=${sessionMapID}&KeepThis=true&TB_iframe=true&height=400&width=650" class="button thickbox" style="float: right;" <fmt:message key="label.manage.leaders" />>
				<fmt:message key="label.manage.leaders" />
			</a>
		</div>
	
	</c:otherwise>
</c:choose>

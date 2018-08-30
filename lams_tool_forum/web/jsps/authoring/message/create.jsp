<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration" %>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys" %>
<%@ page import="org.lamsfoundation.lams.util.FileValidatorSpringUtil" %>
<c:set var="UPLOAD_FILE_MAX_SIZE_AS_USER_STRING"><%=FileValidatorSpringUtil.formatSize(Configuration.getAsInt(ConfigurationKeys.UPLOAD_FILE_LARGE_MAX_SIZE))%></c:set>

<lams:html>
	<lams:head>
		<%@ include file="/common/header.jsp"%>
		<lams:css />
	</lams:head>
	
	<body>
		<div class="panel panel-default">
		<div class="panel-body">
	
		<!-- Basic Info Form-->
		<%@ include file="/common/messages.jsp"%>

		<form:form action="createTopic.do" modelAttribute="topicFormId" id="topicFormId" focus="message.subject" enctype="multipart/form-data">
			<form:hidden path="sessionMapID" />
			<c:set var="sessionMap" value="${sessionScope[topicFormId.sessionMapID]}" />

			<div class="form-group">
		    <label for="message.subject"><fmt:message key="message.label.subject" /> *</label>
		    <form:input type="text" size="30" tabindex="1" path="message.subject" value="${message.subject}" maxlength="60" cssClass="form-control"/>
		    
		    <c:set var="errorKey" value="message.subject" />
	        <c:if test="${not empty errorMap and not empty errorMap[errorKey]}">
	            <lams:Alert id="error" type="danger" close="false">
	                <c:forEach var="error" items="${errorMap[errorKey]}">
	                    <c:out value="${error}" />
	                </c:forEach>
	            </lams:Alert>
	        </c:if>
			</div>
			
			<div class="form-group">
		    <label for="forum.instructions"><fmt:message key="message.label.body" /> *</label>
			<c:set var="body" value=""/>
			<c:if test="${not empty topicFormId.message}">
				<c:set var="body" value="${topicFormId.message.body}"/>
			</c:if>
			<lams:CKEditor id="message.body" value="${body}" contentFolderID="${sessionMap.contentFolderID}"/>
			<c:set var="errorKey" value="message.body" />
	        <c:if test="${not empty errorMap and not empty errorMap[errorKey]}">
	            <lams:Alert id="error" type="danger" close="false">
	                <c:forEach var="error" items="${errorMap[errorKey]}">
	                    <c:out value="${error}" />
	                </c:forEach>
	            </lams:Alert>
	        </c:if>
			</div>

			<c:set var="itemAttachment" value="${topicFormId}" />
			<div class="form-group">
				<label for="attachmentFile"><fmt:message key="message.label.attachment" /></label>
				<lams:FileUpload fileFieldname="attachmentFile" maxFileSize="${UPLOAD_FILE_MAX_SIZE_AS_USER_STRING}" tabindex="3" />
				<form:errors path="message.attachments" />
				<c:set var="errorKey" value="message.attachments" />
		        <c:if test="${not empty errorMap and not empty errorMap[errorKey]}">
		            <lams:Alert id="error" type="danger" close="false">
		                <c:forEach var="error" items="${errorMap[errorKey]}">
		                    <c:out value="${error}" />
		                </c:forEach>
		            </lams:Alert>
		        </c:if>
				<lams:WaitingSpinner id="itemAttachmentArea_Busy"/>
			</div>

			<div class="voffset5 pull-right">
				<a href="#" onclick="hideMessage()" class="btn btn-default btn-xs"> <fmt:message key="button.cancel" /></a>
				<a href="#" onclick="submitMessage()" class="btn btn-default btn-xs loffset5"> <fmt:message key="button.add" /> </a>
			</div>

		</form:form>
	</body>
</lams:html>

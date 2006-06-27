<%@include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<head>
	<title>Submission Export</title>
	<lams:css localLink="true" />
</head>
<body>
	<h2>
		<fmt:message key="activity.title" />
	</h2>
	<br>
	<div id="datatablecontainer">
		<c:choose>
			<c:when test="${empty report}">
				<h3>
					<fmt:message key="label.learner.noUpload" />
					:
				</h3>
			</c:when>
			<c:otherwise>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<c:forEach items="${report}" var="mapElement">
						<c:set var="user" value="${mapElement.key}" />
						<c:set var="submissionList" value="${mapElement.value}" />
						<c:set var="first" value="true" />
						<c:forEach items="${submissionList}" var="submission">
							<c:if test="${first}">
								<c:set var="first" value="false" />
								<tr>
									<td colspan="2">
										<c:out value="${user.firstName}" />
										<c:out value="${user.lastName}" />
										(
										<c:out value="${user.login}" />
										) , 
										<fmt:message key="label.submit.file.suffix"/>:
									</td>
								</tr>
							</c:if>
							<tr>
								<td>
									<fmt:message key="label.learner.filePath" />
									:
								</td>
								<td>
									<a href='<c:out value="${submission.exportedURL}"/>'> <c:out value="${submission.filePath}" /> </a>
								</td>

							</tr>
							<tr>
								<td>
									<fmt:message key="label.learner.fileDescription" />
									:
								</td>
								<td>
									<c:out value="${submission.fileDescription}" escapeXml="false" />
								</td>
							</tr>
							<tr>
								<td>
									<fmt:message key="label.learner.dateOfSubmission" />
									:
								</td>
								<td>
									<c:out value="${submission.dateOfSubmission}" />
								</td>
							</tr>
							<tr>
								<td>
									<fmt:message key="label.learner.marks" />
									:
								</td>
								<td>
									<c:choose>
										<c:when test="${empty submission.marks}">
											<fmt:message key="label.learner.notAvailable" />
										</c:when>
										<c:otherwise>
											<c:out value="${submission.marks}" escapeXml="false" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td>
									<fmt:message key="label.learner.comments" />
									:
								</td>
								<td>
									<c:choose>
										<c:when test="${empty submission.comments}">
											<fmt:message key="label.learner.notAvailable" />
										</c:when>
										<c:otherwise>
											<c:out value="${submission.comments}" escapeXml="false" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>

						</c:forEach>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>

	</div>
</body>
</html:html>


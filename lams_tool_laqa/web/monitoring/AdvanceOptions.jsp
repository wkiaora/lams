<%@ include file="/common/taglibs.jsp"%>
<br>
<h1 style="padding: 30px 0 10px;">
	<img src="<lams:LAMSURL/>/images/tree_closed.gif" id="treeIcon" onclick="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'), '<lams:LAMSURL/>');" />

	<a href="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'),'<lams:LAMSURL/>');" >
		<fmt:message key="monitor.summary.th.advancedSettings" />
	</a>
</h1>
<br />

<div class="monitoring-advanced" id="advancedDiv" style="display:none">
<table class="alternative-color">
	
	<tr>
		<td>
			<fmt:message key="label.learner.answer" />
		</td>
		<td>
			<c:choose>
				<c:when test="${content.showOtherAnswers}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>

	<tr>
		<td>
			<fmt:message key="label.show.names" />
		</td>
		<td>	
			<c:choose>
				<c:when test="${content.usernameVisible}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.notify.teachers.on.response.submit" />
		</td>
		<td>
			<c:choose>
				<c:when test="${content.notifyTeachersOnResponseSubmit}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.authoring.allow.rate.answers" />
		</td>
		<td>
			<c:choose>
				<c:when test="${content.allowRateAnswers}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="monitor.summary.td.addNotebook" />
		</td>	
		<td>
			<c:choose>
				<c:when test="${content.reflect}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	 		
	<c:choose>
		<c:when test="${content.reflect}">
			<tr>
				<td>
					<fmt:message key="monitor.summary.td.notebookInstructions" />
				</td>	
				<td>
					<lams:out value="${content.reflectionSubject}" escapeHtml="true"/>
				</td>
			</tr>
		</c:when>
	</c:choose>
	
	<tr>
		<td>
			<fmt:message key="radiobox.questionsSequenced" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${content.questionsSequenced}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.lockWhenFinished" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${content.lockWhenFinished}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.allowRichEditor" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${content.allowRichEditor}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>

	<tr>
		<td>
			<fmt:message key="label.use.select.leader.tool.output" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${content.useSelectLeaderToolOuput}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>

</table>
</div>

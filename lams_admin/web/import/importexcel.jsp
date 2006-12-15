<%@ include file="/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.admin.service.ISpreadsheetService" %>

<h2>
	<a href="sysadminstart.do"><fmt:message key="sysadmin.maintain" /></a>
	: <fmt:message key="admin.user.import" />
</h2>

<lams:help page="<%= ISpreadsheetService.IMPORT_HELP_PAGE %>"/>

<p>&nbsp;</p>

<p><fmt:message key="msg.import.intro"/></p>
<p>
<ul>
	<li>
		<fmt:message key="msg.import.1"/>
	</li>
	<li>
		<fmt:message key="msg.import.2"/>
		<ul><li><a href="file/lams_users_template.xls">lams_users_template.xls</a></li></ul>
	</li>
	<li>
		<fmt:message key="msg.import.3"/>
		<ul><li><a href="file/lams_roles_template.xls">lams_roles_template.xls</a></li></ul>
	</li>
</ul>
</p>
<p><fmt:message key="msg.import.conclusion"/></p>

<html:form action="/importexcelsave.do" method="post" enctype="multipart/form-data">
<html:hidden property="orgId" />

<table>
	<tr>
		<td align="right"><fmt:message key="label.excel.spreadsheet" />:&nbsp;</td>
		<td><html:file property="file" /></td>
	</tr>
</table>
<p align="center">
<html:submit styleClass="button"><fmt:message key="label.import"/></html:submit> &nbsp; 	
<html:cancel styleClass="button"><fmt:message key="admin.cancel"/></html:cancel>
</p>

</html:form>
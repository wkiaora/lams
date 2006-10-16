<%@ page contentType="text/html; charset=utf-8" language="java" %>

<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-core" prefix="c" %>

<H1 class=no-tabs-below><fmt:message key="cache.title"/></H1>

<p><fmt:message key="cache.explanation1"/></p>
<p><fmt:message key="cache.explanation2"/></p>
<p><fmt:message key="cache.explanation3"/></p>

<!-- cache is a Map, where each key is a node and the value is a set containing the child nodes of this key. Each child node -->
<!-- may or may not have its own value in the map. -->

<p><fmt:message key="cache.entries.title"/></p>

<p><ul>
<c:forEach var="itemEntry" items="${cache}">

<LI><c:out value="${itemEntry.key}"/> <A HREF="cache.do?method=remove&node=<c:out value="${itemEntry.key}"/>"><fmt:message key="cache.button.remove"/></a><BR>
Children:
	<c:forEach var="child" items="${itemEntry.value}">
		<c:out value="${child}"/><BR>
	</c:forEach>

</c:forEach>
</ul></p>

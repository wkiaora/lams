<%--
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

  http://www.gnu.org/licenses/gpl.txt
--%>
<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

				<table class="forms">
						<tr>
					 		<td NOWRAP colspan=2 > <b>  <bean:message key="group.label"/>: </b>
					 		<c:out value="${currentMonitoredToolSessionName}"/>  </td>
						</tr>


			  	 		<tr>
							 <td NOWRAP valign=top align=left> <b> <bean:message key="label.user"/>  </b> </td>  
				  	 		<c:set var="queIndex" scope="request" value="0"/>
							<c:forEach var="currentDto" items="${sessionScope.listMonitoredAnswersContainerDto}">
							<c:set var="queIndex" scope="request" value="${queIndex +1}"/>
								<td NOWRAP valign=top align=left> <b>  <bean:message key="label.question.only"/> <c:out value="${queIndex}"/></b>
									 &nbsp (<bean:message key="label.weight"/> 
									<c:out value="${currentDto.weight}"/>  <bean:message key="label.percent"/>)
								</td>
							</c:forEach>		  	
							 
							 <td NOWRAP valign=top align=left> <b> <bean:message key="label.total"/>  </b> </td>  
			  	 		</tr>						 
			
		  	 		
					<c:forEach var="sessionMarksDto" items="${sessionScope.listMonitoredMarksContainerDto}">
			  	 		<c:set var="currentSessionId" scope="request" value="${sessionMarksDto.sessionId}"/>
			  	 		<c:set var="mapUserMarksDto" scope="request" value="${sessionMarksDto.userMarks}"/>

									<c:forEach var="markData" items="${mapUserMarksDto}">						
						  	 		<c:set var="data" scope="request" value="${markData.value}"/>
						  	 		<c:set var="currentUserSessionId" scope="request" value="${data.sessionId}"/>

									<c:if test="${sessionScope.currentMonitoredToolSession == 'All'}"> 			
										<c:if test="${currentUserSessionId == currentSessionId}"> 	
											<tr>									  	 		
							  	 				<td NOWRAP valign=top align=left> 
														<c:out value="${data.userName}"/> 
												</td>	
			
												<c:forEach var="mark" items="${data.marks}">
													<td NOWRAP valign=top align=left> 
															<c:out value="${mark}"/> 								
													</td>
												</c:forEach>		  										
			
												<td NOWRAP valign=top align=left> 
															<c:out value="${data.totalMark}"/> 																
												</td>							
											</tr>													
										</c:if>																
									</c:if>																		
									</c:forEach>		  	
			

									<c:forEach var="markData" items="${mapUserMarksDto}">						
						  	 		<c:set var="data" scope="request" value="${markData.value}"/>
						  	 		<c:set var="currentUserSessionId" scope="request" value="${data.sessionId}"/>							  	 		
									<c:if test="${sessionScope.currentMonitoredToolSession !='All'}"> 			
										<c:if test="${sessionScope.currentMonitoredToolSession == currentUserSessionId}"> 										
											<c:if test="${currentUserSessionId == currentSessionId}"> 									
											<tr>			
												<td NOWRAP valign=top align=left> 
														<c:out value="${data.userName}"/> 
												</td>	
			
												<c:forEach var="mark" items="${data.marks}">
													<td NOWRAP valign=top align=left> 
															<c:out value="${mark}"/> 								
													</td>
												</c:forEach>		  										
			
												<td NOWRAP valign=top align=left> 
															<c:out value="${data.totalMark}"/> 																
												</td>							
											</tr>														
											</c:if>																
										</c:if>																			
									</c:if>																								
									</c:forEach>		
					</c:forEach>		  	
			</table>		  	 		
			
	
	
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

	<table cellpadding="0">
			         
					<tr>
						<td>							
							<html:checkbox property="questionsSequenced" value="1" styleClass="noBorder">
								<bean:message key="radiobox.onepq" />
							</html:checkbox>
		      			</td>		      			
			         </tr>
			         
	   				<tr>
						<td>							
							<html:checkbox property="retries" value="1"  onclick="javascript:updatePass(this);" styleClass="noBorder">
								<bean:message key="radiobox.retries" />
							</html:checkbox>
		      			</td>		      			
			        </tr>
			        
			        
	   				<tr>
						<td>							
									<select name="passmark">
										<c:forEach var="passmarkEntry" items="${mcGeneralAuthoringDTO.passMarksMap}">
											<c:set var="SELECTED_PASS" scope="request" value=""/>
											<c:if test="${passmarkEntry.value == mcGeneralAuthoringDTO.passMarkValue}"> 			
														<c:set var="SELECTED_PASS" scope="request" value="SELECTED"/>
											</c:if>						

												<option value="<c:out value="${passmarkEntry.value}"/>"  <c:out value="${SELECTED_PASS}"/>> <c:out value="${passmarkEntry.value}"/>  </option>																				
										</c:forEach>		  	
									</select>
									<bean:message key="radiobox.passmark" />
									
									 <a href="javascript:submitMethod('updateMarksList')" class="button">
								    	<bean:message key="label.update.list"/>
								     </a>
									
									
									
		      			</td>		      			
			        </tr>


			        

   				<tr>
						<td>							
							<html:checkbox property="reflect" value="1" styleClass="noBorder">
								<bean:message key="label.reflect" />
							</html:checkbox>
		      			</td>		      			
		        </tr>

   				<tr>
					<td>							
 						<html:textarea cols="30" rows="3" property="reflectionSubject"></html:textarea> 						
	      			</td>
		        </tr>
		</table>
		
		

		
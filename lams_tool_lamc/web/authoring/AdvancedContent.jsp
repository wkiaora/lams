<%@ include file="/common/taglibs.jsp"%>

<div class="checkbox">
	<label for="useSelectLeaderToolOuput">
	<html:checkbox property="useSelectLeaderToolOuput" value="1" styleId="useSelectLeaderToolOuput" onclick="clickSelectLeaderToolOuputHandler();"/>
	<fmt:message key="label.use.select.leader.tool.output" />
	</label>
</div>


<div class="checkbox">
	<label for="prefixAnswersWithLetters">
	<html:checkbox property="prefixAnswersWithLetters" value="1" styleId="prefixAnswersWithLetters"/>
	<fmt:message key="label.prefix.sequential.letters.for.each.answer" />
	</label>
</div>


<div class="checkbox">
	<label for="questionsSequenced">
	<html:checkbox property="questionsSequenced" value="1" styleId="questionsSequenced"/>
	<fmt:message key="radiobox.onepq" />
	</label>
</div>


<div class="checkbox">
	<label for="showMarks">
	<html:checkbox property="showMarks" value="1" styleId="showMarks"/>
	<fmt:message key="label.showMarks" />
	</label>
</div>


<div class="checkbox">
	<label for="randomize">
	<html:checkbox property="randomize" value="1" styleId="randomize"/>
	<fmt:message key="label.randomize" />
	</label>
</div>

<div class="checkbox">
	<label for="displayAnswers">
	<html:checkbox property="displayAnswers" value="1" styleId="displayAnswers"/>
	<fmt:message key="label.displayAnswers" />
	</label>
</div>


<div class="checkbox">
	<label for="retries">
	<html:checkbox property="retries" value="1" styleId="retries"
		onclick="javascript:updatePass(this); submitMethod('updateMarksList');"/>
	<fmt:message key="radiobox.retries" />		
	</label>
</div>

<div class="form-inline loffset20">
	<div class="form-group">
	<select name="passmark"  class="form-control input-sm">
		<c:forEach var="passmarkEntry"
			items="${mcGeneralAuthoringDTO.passMarksMap}">
			<c:set var="SELECTED_PASS" scope="request" value="" />
			<c:if
				test="${passmarkEntry.value == mcGeneralAuthoringDTO.passMarkValue}">
				<c:set var="SELECTED_PASS" scope="request" value="SELECTED" />
			</c:if>
	
			<option value="<c:out value="${passmarkEntry.value}"/>"
				<c:out value="${SELECTED_PASS}"/>>
				<c:out value="${passmarkEntry.value}" />
			</option>
		</c:forEach>
	</select>
	<fmt:message key="radiobox.passmark" />
	</div>
</div>

<div class="checkbox">
	<label for="reflect">
	<html:checkbox property="reflect" value="1" styleClass="noBorder" styleId="reflect"/>
	<fmt:message key="label.reflect" />
	</label>
</div>
<div class="form-group">
<html:textarea property="reflectionSubject" styleId="reflectInstructions"  styleClass="form-control" rows="3"/>
</div>

<script type="text/javascript">
<!--
//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflect");
	function turnOnRefect(){
		if(isEmpty(ra.value)){
		//turn off	
			rao.checked = false;
		}else{
		//turn on
			rao.checked = true;		
		}
	}

	ra.onkeyup=turnOnRefect;
//-->
</script>


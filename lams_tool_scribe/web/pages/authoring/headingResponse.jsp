<%@ include file="/common/taglibs.jsp"%>

<div style="display: none;">

	<div id="itemList">
		<%@ include file="parts/headingList.jsp"%>
	</div>

	<script type="text/javascript">
		var win = null;
		try {
			if (window.parent && window.parent.hideMessage) {
				win = window.parent;
			} else if (window.top && window.top.hideMessage) {
				win = window.top;
			}
		} catch(err) {
			// mute cross-domain iframe access errors
		}
		if (win) {
			win.hideMessage();
			var obj = win.document.getElementById('itemListArea');
			obj.innerHTML= document.getElementById("itemList").innerHTML;
		}
	</script>

</div>



<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams" %>

<!-- 
Include this jsp in your jqGrid page head to get some jqGrid functionaility
 -->

<link rel="stylesheet" type="text/css" media="screen" href="<lams:LAMSURL />includes/javascript/jqgrid/themes/basic/grid.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<lams:LAMSURL />includes/javascript/jqgrid/themes/jqModal.css" />
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL />/includes/javascript/jqgrid/js/jquery-1.2.6.pack.js"></script>
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL />/includes/javascript/jqgrid/js/jquery.jqGrid.js" ></script>
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL />/includes/javascript/jqgrid/js/jqModal.js" ></script>
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL />/includes/javascript/jqgrid/js/jqDnR.js" ></script>

<style>
	.tooltip{
	    position:absolute;
	    z-index:999;
	    left:-9999px;
	    background-color:#dedede;
	    padding:5px;
	    border:1px solid #fff;
	    width:250px;
	    font-size: 1.1em;
	}
</style>

<script type="text/javascript">
		
		// JQGRID LANGUAGE ENTRIES ---------------------------------------------
		
		// editing entries
		$.jgrid.edit = {
		    addCaption: "Add Record",
		    editCaption: "Edit Record",
		    bSubmit: "Submit",
		    bCancel: "Cancel",
			bClose: "Close",
		    processData: "Processing...",
		    msg: {
		        required:"Field is required",
		        number:"Please, enter valid number",
		        minValue:"value must be greater than or equal to ",
		        maxValue:"value must be less than or equal to",
		        email: "is not a valid e-mail",
		        integer: "Please, enter valid integer value",
				date: "Please, enter valid date value"
		    }
		};
		
		// search entries
		$.jgrid.search = {
			    caption: "Search Names...",
			    Find: "Find",
			    Reset: "Reset",
			    odata : ['equal', 'not equal', 'less', 'less or equal','greater','greater or equal', 'begins with','ends with','contains' ]
		};
		
		// ---------------------------------------------------------------------
		
		
		// Applies tooltips to a jqgrid
		function toolTip(gRowObject) {
			var my_tooltip = $('#tooltip'); // Div created for tooltip
			gRowObject.css({ 
				cursor: 'pointer' 
	        }).mouseover(function(kmouse){ 
                if (checkCell(kmouse)) {
                	showToolTip(my_tooltip, kmouse);
                	//setTimeout(function(){showToolTip(my_tooltip, kmouse);}, 1000);
                }
	        }).mousemove(function(kmouse){ 
                if (checkCell(kmouse)) {
                	moveToolTipBox(my_tooltip, kmouse);
                	//setTimeout(function(){moveToolTipBox(my_tooltip, kmouse);}, 1000);
                }
            }).mouseout(function(){ 
                my_tooltip.stop().fadeOut(400); 
        	}).css({cursor:'pointer'}).click(function(e){
                my_tooltip.stop().fadeOut(400); 
        	});
		}
		
		// Check a cell before opening tooltip to make sure empty or invalid cells do not display
		function checkCell(kmouse) {
			var cell = $(kmouse.target).html();
			if (cell != null && cell !="" && cell !="&nbsp;" && cell != "-" && cell.charAt(0) != '<') {
				return true;
			}
			return false;
		}
		
		// Shows a tootip and applies the cell value
		function showToolTip(my_tooltip, kmouse) {
			
			var cell = $(kmouse.target).html();
			my_tooltip.html(cell);
               my_tooltip.css({ 
               	opacity: 0.85, 
               	display: "none" 
           	}).stop().fadeIn(400);
		}
		
		// Moves the tooltip box so it is not in the way of the mouse
		function moveToolTipBox(my_tooltip, kmouse) {
			var border_top = $(window).scrollTop(); 
	        var border_right = $(window).width(); 
	        var left_pos; 
	        var top_pos; 
	        var offset = 15;  
	        if (border_right - (offset * 2) >= my_tooltip.width() + kmouse.pageX)  
	        { 
	        	left_pos = kmouse.pageX + offset;  
	        }  
	        else  
	        { 
	       		left_pos = border_right - my_tooltip.width() - offset; 
	        }  
	        if (border_top + (offset * 2) >= kmouse.pageY - my_tooltip.height())  
	        { 
	        	top_pos = border_top + offset;  
	        }  
	        else  
	        { 
	        	top_pos = kmouse.pageY - my_tooltip.height() - offset; 
	        } 
	        my_tooltip.css({ 
		        left: left_pos, 
		        top: top_pos 
	        }); 
		}
		
		// launches a popup from the page
		function launchPopup(url,title,width,height) {
			var wd = null;
			if(wd && wd.open && !wd.closed){
				wd.close();
			}
			//wd = window.open(url,title,'resizable,width=796,height=570,scrollbars');
			wd = window.open(url,title,'resizable,width='+width+',height='+height+',scrollbars');
			wd.window.focus();
		}
		
</script>
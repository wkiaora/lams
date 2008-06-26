// Add a new marker to the center of the map
function addMarkerToCenter()
{
	var bounds = map.getBounds();
	var point = bounds.getCenter();
	addMarker(point, "", "", -1, false, true, currUser, currUserId);
}


function test()
{
	serialiseMarkers();
	alert(document.authoringForm.markersXML.value);
}

function removeMarker(x)
{
	var ans = confirm(confirmDelete);
	if (ans)
	{
		try{map.removeOverlay(markers[x]);}
		catch (e){}
		markers[x].state = "remove";
		refreshSideBar();
		//serialiseMarkers();
	}
}

function editMarker(x)
{
	markers[x].editingOn = true;
	updateMarkerInfoWindowHtml(markers[x]);
	openInfoWindow(x);
}

function saveMarkerInfo(x)
{
	if (markers[x] != null)
	{
		var title= trim(document.getElementById("markerTitle").value);
		if (title==null || title == "")
		{
			alert(errorMissingTitle);
		}
		else
		{		
			
			var info=document.getElementById("infoWindow").value;
			markers[x].title = title;
			markers[x].infoMessage = info;
			markers[x].editingOn = false;
			
			// change the state to update if it is a pre-existing marker
			if (markers[x].state == "unchanged") {markers[x].state = "update";}
			else (markers[x].state ="save");
			
			updateMarkerInfoWindowHtml(markers[x]);
			refreshSideBar();
			openInfoWindow(x);
		}
	}
}


// TODO: This method should only be included for the authoring pages, put them in another file
function saveMapState()
{
	document.getElementById("mapZoom").value=map.getZoom();
	document.getElementById("mapCenterLatitude").value=map.getCenter().lat();
	document.getElementById("mapCenterLongitude").value=map.getCenter().lng();
	var mapTypeName = map.getCurrentMapType().getName();
	var mapType = "";	
	if 		(mapTypeName == "Satellite"){ mapType = "G_SATELLITE_MAP"; }
	else if (mapTypeName == "Hybrid")	{ mapType = "G_HYBRID_MAP"; }
	else if (mapTypeName == "Terrain") 	{ mapType = "G_PHYSICAL_MAP"; }
	else 								{ mapType = "G_NORMAL_MAP"; }
	document.getElementById("mapType").value=mapType;
}
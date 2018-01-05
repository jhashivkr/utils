var URL = {
	RECORD_FILTER : {
		name : "Filter",
		displayName : "Filter",
		url : "/ipage/browseRow/recordFilter.action?method=load"
	},
	VIEW_ORDER_INFO : {
		name : "View Order Info",
		displayName : "Order Info",
		url : "/ipage/browseRow/orderInfo.action?method=viewOrderInfo"
	},
	MARC : {
		name : "MARC Info",
		displayName : "MARC Info",
		url : "/ipage/browseRow/marc.action"
	},
	LIBRARY_ACTIVITY : {
		name : "Library Activity",
		displayName : "Library Activity",
		url : "/ipage/browseRow/libraryActivity.action"
	},
	PROFILE_MATCHES : {
		name : "Profile Matches",
		displayName : "Profile Matches",
		url : "/ipage/browseRow/profileMatches.action"
	},
	FORMATS : {
		name : "Formats",
		displayName : "Formats",
		url : "/ipage/browseRow/formats.action"
	},
	COMMUNITY_GROUPS : {
		name : "Community Groups",
		displayName : "Community Groups",
		url : "/ipage/browseRow/communityGroups.action?actionType=browseRowCommunityGroups"
	},
	VIEW_DEMAND : {
		name : "Customer Demand",
		displayName : "Customer Demand",
		url : "/ipage/browseRow/viewDemand.action?actionType=notStandingOrder"
	},
	VIEW_DEMAND_STANDING_ORDER : {
		name : "Customer Demand Standing",
		displayName : "Customer Demand Standing",
		url : "/ipage/browseRow/viewDemand.action?actionType=standingOrder"
	},
	MARK_ALL : {
		name : "Mark All",
		displayName : "Mark All",
		url : "/ipage/browseRow/markAll.action?method=load"
	},
	ACTION_ORDERING : {
		name : "Mark For Ordering",
		displayName : "Mark For Ordering",
		url : "/ipage/browseRow/arrow.action?actionType=Ordering"
	},
	ACTION_SENDTO_LIST : {
		name : "Send to List",
		displayName : "Send to List",
		url : "/ipage/browseRow/arrow.action?actionType=sendToList"
	},
	ACTION_FORWARD : {
		name : "Forwarding",
		displayName : "Forwarding",
		url : "/ipage/browseRow/arrow.action?actionType=forward"
	},
	ACTION_EDIT_ORDER_INFO : {
		name : "Edit Order Info",
		displayName : "Order Info",
		url : "/ipage/browseRow/orderInfo.action?method=load"
	},
	PROCESS_LIST : {
		name : "Process List",
		displayName : "Process List",
		url : "/ipage/browseRow/processList.action?method=load"
	},
	ACTION_DOWNLOAD_EXCEL : {
		name : "Download Excel",
		displayName : "Download Excel",
		url : "/ipage/browseRow/downloadExcel.action?method=downloadExcelPopup"
	},
	ACTION_DOWNLOAD_MARK : {
		name : "Download MARC",
		displayName : "Download MARC",
		url : "/ipage/browseRow/downloadExcel.action?method=dmf"
	},
	CREATE_NEW_LIST : {
		name : "Create New List",
		displayName : "Create New List",
		url : "/ipage/browseRow/academicUserLists.action?actionType=createNewList"
	},
	EDIT_LIST : {
		name : "Edit List",
		displayName : "Edit List",
		url : "/ipage/browseRow/academicUserLists.action?actionType=editList"
	},
	ALIBRIS_SEARCH : {
		name : "Alibris Search",
		displayName : "Alibris Result",
		url : "/ipage/browseRow/academicAlibris.action?actionType=AlibrisSearch"
	},
	ORDER_STATUS : {
		name : "Order Status",
		displayName : "Order Status",
		url : "/ipage/browseRow/orderDetailResult.action?actionType=orderStatus"
	},
	ORDER_INVOICE : {
		name : "Invoices",
		displayName : "Invoices",
		url : "/ipage/browseRow/orderInvoice.action?actionType=orderInvoice"
	},
	SERIES_DETAIL : {
		name : "Series Detail",
		displayName : "Series Detail",
		url : "/ipage/customer/standingorder/seriesDetail.action"
	},
	SERIES_ALL_SEARCH : {
		name : "Search All Series",
		displayName : "Search Series",
		url : "/ipage/customer/standingorder/searchAllSeries.action"
	},
	STANDING_PLACE_ORDER : {
		name : "Standing Place",
		displayName : "Standing Place",
		url : "/ipage/customer/standingorder/searchAllSeries.action"
	},
	STANDING_ORIGINAL_ORDER : {
		name : "Standing Original",
		displayName : "Standing Original",
		url : "/ipage/customer/standingorder/searchAllSeries.action"
	},
	STANDING_ORDER_INFO : {
		name : "Standing Order Info",
		displayName : "Standing Order Info",
		url : "/ipage/customer/standingorder/standingOrderInfo.action?actionType=show"
	},
	CHECK_STATUS_STANDING : {
		name : "Check Status Standing",
		displayName : "Check Status Standing",
		url : "/ipage/customer/standingorder/checkStatusStanding.action"
	}
};
var callFadeVar;
var modalHdrTxt = "";
var postOrderInfoSaveAction;
var postOrderInfoSubmitFlag;
var postOrderInfoRetainOnListFlag;

// SmokeScreenDeluxe(5,70,85,'Library Activity', '${param.browseId}')
function SmokeScreenDeluxe(topMargin, containerWidth, containerHeight,
		scrHeader, itemId) {
	// NEW 2013 SMOKESCREEN (GREY SCREEN, NO SCROLLING, SCREEN CENTERED BOX)
	var currentScrollValue = $(window).scrollTop();
	var browserWindowHeight = $(window).height();
	var documentHeight = $(document).height();
	var finalTopMargin = topMargin + '%';
	var finalContainerWidth = containerWidth + '%';
	var finalSideMargin = (100 - containerWidth) / 2 + '%';
	var finalContainerHeight = (containerHeight / 100);

	containerHght = browserWindowHeight * finalContainerHeight;

	$(window).scrollTo($(window).scrollTop(), 1);
	$('html, body').css('overflow', 'hidden');

	var smokeScreenDeluxe = '<div id="smokeScreenDeluxe"></div>';
	var inSmokeContainer = '<div id="inSmokeContainer"></div>';

	$(smokeScreenDeluxe).prependTo($('body')).css({
		position : 'absolute',
		zIndex : 500,
		top : currentScrollValue + "px",
		height : documentHeight + "px",
		width : '100%',
		backgroundColor : '#000000',
		opacity : 0.5,
		color : '#ffffff'
	});

	$(inSmokeContainer).prependTo($('body')).css({
		position : 'absolute',
		zIndex : 1000,
		height : browserWindowHeight * finalContainerHeight,
		width : finalContainerWidth,
		top : currentScrollValue + "px",
		marginTop : finalTopMargin,
		marginLeft : finalSideMargin,
		marginRight : finalSideMargin,
		backgroundColor : '#ffffff',
		border : '4px solid #cccccc',
		borderRadius : '5px',
		color : '#000000',
		minWidth : '391px',
		textAlign: 'left'
	});

	$(window).resize(function() {
		var browserWindowHeight = $(window).height();
		$('#inSmokeContainer').css({
			height : browserWindowHeight * finalContainerHeight
		});
	});

	var headerStr;
	var showHideWaitPopUp;
	if (isCreateOnlyHeader(scrHeader)) {
		headerStr = createOnlyHeader();
		if($("#listTypeId").val()=='ST'){
		showHideWaitPopUp =true;
		}else{
		showHideWaitPopUp =false;
		}
	} else {
		headerStr = createHeader(scrHeader, itemId);
		showHideWaitPopUp = true
	}
	
	$('#inSmokeContainer').append(headerStr);

	// TODO Actual redirection to browseUrl
	if (showHideWaitPopUp) {
		showWaitPopupPopup();
	}
	 jQuery.ajax({ 
			url: getURL(scrHeader, itemId), dataType: "html" 
			}).done(function( responseHtml ) {
			$("#content-pane").html(responseHtml);
			SetContentWrapperPosition();
				if (showHideWaitPopUp) {
					hideWaitPopupPopup();
				}
				});
	/*$('#content-pane').load(getURL(scrHeader, itemId) , function() {
		 
		SetContentWrapperPosition();
		if (showHideWaitPopUp) {
		     hideWaitPopupPopup();
	     }
		
	});*/
}
function SmokeScreenDeluxeForUrl(topMargin, containerWidth, containerHeight,
		scrHeader, itemId, url) {
	// NEW 2013 SMOKESCREEN (GREY SCREEN, NO SCROLLING, SCREEN CENTERED BOX)
	var currentScrollValue = $(window).scrollTop();
	var browserWindowHeight = $(window).height();
	var documentHeight = $(document).height();
	var finalTopMargin = topMargin + '%';
	var finalContainerWidth = containerWidth + '%';
	var finalSideMargin = (100 - containerWidth) / 2 + '%';
	var finalContainerHeight = (containerHeight / 100);

	containerHght = browserWindowHeight * finalContainerHeight;

	$(window).scrollTo($(window).scrollTop(), 1);
	$('html, body').css('overflow', 'hidden');

	var smokeScreenDeluxe = '<div id="smokeScreenDeluxe"></div>';
	var inSmokeContainer = '<div id="inSmokeContainer"></div>';

	$(smokeScreenDeluxe).prependTo($('body')).css({
		position : 'absolute',
		zIndex : 500,
		top : currentScrollValue + "px",
		height : documentHeight + "px",
		width : '100%',
		backgroundColor : '#000000',
		opacity : 0.5,
		color : '#ffffff'
	});

	$(inSmokeContainer).prependTo($('body')).css({
		position : 'absolute',
		zIndex : 1000,
		height : browserWindowHeight * finalContainerHeight,
		width : finalContainerWidth,
		top : currentScrollValue + "px",
		marginTop : finalTopMargin,
		marginLeft : finalSideMargin,
		marginRight : finalSideMargin,
		backgroundColor : '#ffffff',
		border : '4px solid #cccccc',
		borderRadius : '5px',
		color : '#000000',
		minWidth : '391px',
		textAlign: 'left'
	});

	$(window).resize(function() {
		var browserWindowHeight = $(window).height();
		$('#inSmokeContainer').css({
			height : browserWindowHeight * finalContainerHeight
		});
	});

	var headerStr;
	var showHideWaitPopUp;
	if (isCreateOnlyHeader(scrHeader)) {
		headerStr = createOnlyHeader();
		showHideWaitPopUp =false;
	} else {
		headerStr = createHeader(scrHeader, itemId);
		showHideWaitPopUp = true
	}
	
	$('#inSmokeContainer').append(headerStr);

	// TODO Actual redirection to browseUrl
	if (showHideWaitPopUp) {
		showWaitPopupPopup();
	}
	 jQuery.ajax({ 
			url: url, dataType: "html" 
			}).done(function( responseHtml ) {
			$("#content-pane").html(responseHtml);
			SetContentWrapperPosition();
				if (showHideWaitPopUp) {
					hideWaitPopupPopup();
				}
				});
}

function CloseSmokeScreenDeluxe() {

	$("#inSmokeContainer").remove();
	$("#smokeScreenDeluxe").remove();
	$('html, body').css('overflow', 'visible'); // DISPLAY SCROLL BAR. ALLOW
												// USER FROM SCROLLING WINDOW
}

function createHeader(scrHeader, itemId) {
	var headStr = '';
	var bookTitle = $('#Title_' + itemId).attr('value');
	var bookAuthor = $('#Author_' + itemId).attr('value');
	var bookPublisher = $('#Publisher_' + itemId).attr('value');
	var bookPublishingYear = $('#PublishingYear_' + itemId).attr('value');
	var bookEAN = $('#EAN_' + itemId).attr('value');
	var bookISBN = $('#ISBN_' + itemId).attr('value');
	var bookBinding = $('#Binding_' + itemId).attr('value');
	var quantity = $('#qty_' + itemId).attr('value');

	var displayName = getDisplayName(scrHeader);
	// will load the JSPs
	 
	headStr += '<div id="smokeScreen" class="smokeScreen">';
	headStr += '<iframe width="100%" height="100%" src="/images/spacer.gif" class="smokeScreenIframe"></iframe>';
	headStr += '</div>';
	headStr += '<div id="waitPopup" class="toggleBox textWhiteBG textAlignCenter" style="display: none; z-index: 1000; padding: 10px;">';
	headStr += '<img src="/images/circle_loadingAnim.gif"><br /> ';
	headStr += '<img src="/images/pleaseWait_textAnim.gif"><br /> <br /> <br />';
	headStr += '<div id="waitUpdateSection"></div>';
	headStr += '</div>';
	headStr += '<h1 class="contentBannerHead">';
	headStr += '<div id="screenHeader"  style="float:left;">'+displayName+'</div>';
	headStr += '<div id="subScreenHeader" style="float:right;"></div>';    
	headStr += '<div id="closeSmoke" class="closeSmokeScreenDeluxeButton" onclick="javascript:CloseSmokeScreenDeluxe();" title="Close This Window">Close</div></h1>';     

	headStr += '<div class="doublePaddedContainer">';
	headStr += '<div class="doublePaddedContainer totals">';
	// dummy data
	headStr += '<span class="totalsStrong"> ' + bookTitle + ' </span><br>'
			+ bookAuthor + ' | ' + bookPublisher + ' | ' + bookPublishingYear
			+ ' | ' + '<strong>EAN/ISBN: ' + bookEAN + ' / ' + bookISBN
			+ '</strong>| (' + bookBinding + ')';

	headStr += '</div>';
	headStr += '</div>';
	headStr += '<div id="content-pane" ></div>';
	
	return headStr;
}

function hideActions(id) {
	$("#actionMenuContainer_" + id).hide();
}

function showActions(id) {
	$("#actionMenuContainer_" + id).show();
}

function getDisplayName(title) {
	if (URL.RECORD_FILTER.name == title) {
		return URL.RECORD_FILTER.displayName;
	}
	if (URL.VIEW_ORDER_INFO.name == title) {
		return URL.VIEW_ORDER_INFO.displayName;
	}
	if (URL.MARC.name == title) {
		return URL.MARC.displayName;
	}
	if (URL.VIEW_DEMAND.name == title) {
		return URL.VIEW_DEMAND.displayName;
	}
	if (URL.VIEW_DEMAND_STANDING_ORDER.name == title) {
		return URL.VIEW_DEMAND_STANDING_ORDER.displayName;
	}
	if (URL.LIBRARY_ACTIVITY.name == title) {
		return URL.LIBRARY_ACTIVITY.displayName;
	}
	if (URL.PROFILE_MATCHES.name == title) {
		return URL.PROFILE_MATCHES.displayName;
	}
	if (URL.FORMATS.name == title) {
		return URL.FORMATS.displayName;
	}
	if (URL.COMMUNITY_GROUPS.name == title) {
		return URL.COMMUNITY_GROUPS.displayName;
	}
	if (URL.MARK_ALL.name == title) {
		return URL.MARK_ALL.displayName;
	}
	if (URL.PROCESS_LIST.name == title) {
		return URL.PROCESS_LIST.displayName;
	}
	if (URL.ACTION_ORDERING.name == title) {
		return URL.ACTION_ORDERING.displayName;
	}
	if (URL.ACTION_EDIT_ORDER_INFO.name == title) {
		return URL.ACTION_EDIT_ORDER_INFO.displayName;
	}
	if (URL.ACTION_SENDTO_LIST.name == title) {
		return URL.ACTION_SENDTO_LIST.displayName;
	}
	if (URL.ACTION_FORWARD.name == title) {
		return URL.ACTION_FORWARD.displayName;
	}
	if (URL.ACTION_DOWNLOAD_EXCEL.name == title) {
		return URL.ACTION_DOWNLOAD_EXCEL.displayName;
	}
	if (URL.CREATE_NEW_LIST.name == title) {
		return URL.CREATE_NEW_LIST.displayName;
	}
	if (URL.EDIT_LIST.name == title) {
		return URL.EDIT_LIST.displayName;
	}
	if (URL.ALIBRIS_SEARCH.name == title) {
		return URL.ALIBRIS_SEARCH.displayName;
	}
	if (URL.ORDER_STATUS.name == title) {
		return URL.ORDER_STATUS.displayName;
	}
	if (URL.ORDER_INVOICE.name == title) {
		return URL.ORDER_INVOICE.displayName;
	}
	if (URL.SERIES_DETAIL.name == title) {
		return URL.SERIES_DETAIL.displayName;
	}
	if (URL.SERIES_ALL_SEARCH.name == title) {
		return URL.SERIES_ALL_SEARCH.displayName;
	}
	if (URL.STANDING_PLACE_ORDER.name == title) {
		return URL.STANDING_PLACE_ORDER.displayName;
	}
	if (URL.STANDING_ORIGINAL_ORDER.name == title) {
		return URL.STANDING_ORIGINAL_ORDER.displayName;
	}
	if (URL.STANDING_ORDER_INFO.name == title) {
		return URL.STANDING_ORDER_INFO.displayName;
	}
	if (URL.CHECK_STATUS_STANDING.name == title) {
		return URL.CHECK_STATUS_STANDING.displayName;
	}
}

// common function
function getURL(title, itemId) {
	var hostName = location.protocol + "//" + location.host;

	if (URL.RECORD_FILTER.name == title) {
		var url = URL.RECORD_FILTER.url + "&filters="
				+ encodeURIComponent($('#filters').val());

		var searchTerm = encodeURIComponent($(
				'#resultsForm input[name=dsplSearchTerm]').val());
		url = url + "&listTypeId=" + $('#listTypeId').val();

		return url;
	}

	if (URL.VIEW_ORDER_INFO.name == title) {
		return URL.VIEW_ORDER_INFO.url + "&itemId=" + itemId + "&listTypeId="
				+ $('#listTypeId').val() + "&listId=" + $('#listId').val();
	}
	if (URL.MARC.name == title) {
		return URL.MARC.url+"?listId=" + $('#listId').val()+ "&listTypeId=" + $('#listTypeId').val() + "&browseId=" + itemId 
			   + "&ean=" + $('#EAN_' + itemId).attr('value');
	}
	if (URL.VIEW_DEMAND.name == title) {
		return URL.VIEW_DEMAND.url+"&ean="+$('#EAN_' + itemId).attr('value');
	}
	if (URL.VIEW_DEMAND_STANDING_ORDER.name == title) {
			return URL.VIEW_DEMAND_STANDING_ORDER.url+"&issnNo="+$('#OIDISSN_' + itemId).attr('value');
	}
	if (URL.LIBRARY_ACTIVITY.name == title) {
		var listTypeId = $('#listTypeId').val();
		var chkStatEan = $('#prodEan_' + itemId).html();
		var status = $('#statusCode_' + itemId).val();
		var orderDate = $('#orderDate_' + itemId).html();
		var format = $('#BD_' + chkStatEan).html();
		 
		var quantity = $('#qty_' + itemId).val();
		var licenceDesc = $('#licenceDesc_' + itemId).html();
		// alert("oprId=" + itemId + "listTypeId="+listTypeId+",
		// chkStatEan="+chkStatEan+", status="+status+",
		// orderDate="+orderDate+", format="+format+", quantity="+quantity);
		if (listTypeId == "SO") {
			return URL.LIBRARY_ACTIVITY.url + "?chkStatEan=" + chkStatEan
					+ "&listTypeId=" + listTypeId + "&status=" + status
					+ "&format=" + format + "&quantity=" + quantity
					+ "&orderDate=" + orderDate + "&oprId=0&licenceDesc="
					+ licenceDesc;
		} else {
			 
			return URL.LIBRARY_ACTIVITY.url + "?&oprId=" + itemId
					+ "&chkStatEan=" + $('#prodEan_' + itemId).html()
					+ "&listId=" + $('#listId').attr("value") + "&listTypeId="
					+ listTypeId
				    + "&format=" + format;
		}

	}
	if (URL.PROFILE_MATCHES.name == title) {
		return URL.PROFILE_MATCHES.url + "?&reqIsbn=" + $("#ISBN_"+itemId).attr("value") + "&reqLibGrp=" + $("#custGrp").attr("value");		
	}
	if (URL.FORMATS.name == title) {
		 
		 var altEans = [];
		 
		 
		 $('.alt_eans_'+itemId).each(function() {
			  
			altEans.push($(this).val().trim());
			 
		});
		var pageNum =  $('#No').val();
		 
		if(pageNum=='null' || null== pageNum){
			pageNum = 0;
		}
		var searchType =  $('#searchType').val(); 
		 
	      
		return URL.FORMATS.url + "?&ean=" + $('#EAN_' + itemId).attr('value') + "&listTypeId="
				+ $('#listTypeId').val() + "&listId=" + $('#listId').val()+"&altEans=" +altEans + "&itemId=" + itemId +'&pageNum=' +pageNum 
				+ '&searchType='+searchType;
				 
	}
	if (URL.COMMUNITY_GROUPS.name == title) {
		return URL.COMMUNITY_GROUPS.url + "&ean=" + $('#EAN_' + itemId).attr('value');
	}
	// Mark All Nevigations
	if (URL.MARK_ALL.name == title) {
		var searchTerm = encodeURIComponent($(
				'#resultsForm input[name=dsplSearchTerm]').val());

		var url = URL.MARK_ALL.url + "&listTypeId=" + $('#listTypeId').val()
				+ "&listId=" + $('#listId').val() + "&currentPageRecords="
				+ $('#currentPageRecords').val() + "&totalRecords="
				+ $('#totalRecords').val();
		
		if (typeof $('#resultsForm input[name=No]').val() != 'undefined') {
			url = url + "&No=" + $('#resultsForm input[name=No]').val();
		}
		
		if(srUiDelEans.length > 0){
			url = url + "&srUiDelEans=" + srUiDelEans.join(",");
		}

		return url;
	}
	// Process List Nevigations
	if (URL.PROCESS_LIST.name == title) {
		var searchTerm = encodeURIComponent($(
				'#resultsForm input[name=dsplSearchTerm]').val());

		var url = URL.PROCESS_LIST.url + "&listTypeId="
				+ $('#listTypeId').val() + "&listId=" + $('#listId').val()
				+ "&totalRecords=" + $('#totalRecords').val();
				
		if (typeof $('#resultsForm input[name=No]').val() != 'undefined') {
			url = url + "&No=" + $('#resultsForm input[name=No]').val();
		}
		
		if(srUiDelEans.length > 0){
			url = url + "&srUiDelEans=" + srUiDelEans.join(",");
		}

		return url;
	}
	// Action Arrow Nevigations
	if (URL.ACTION_ORDERING.name == title) {
		return URL.ACTION_ORDERING.url;
	}
	if (URL.ACTION_EDIT_ORDER_INFO.name == title) {
		return URL.ACTION_EDIT_ORDER_INFO.url + "&itemId=" + itemId
				+ "&listTypeId=" + $('#listTypeId').val() + "&listId="
				+ $('#listId').val();
	}
	if (URL.ACTION_SENDTO_LIST.name == title) {
				
		return URL.ACTION_SENDTO_LIST.url + "&browseIds=" + itemId
				+ "&listTypeId=" + $('#listTypeId').val() + "&listId="
				+ $('#listId').val() + "&alibrisInd=" + $("#AlibrisInd_"+itemId).val();
	}
	if (URL.ACTION_FORWARD.name == title) {
		return URL.ACTION_FORWARD.url + "&browseIds=" + itemId + "&listTypeId="
				+ $('#listTypeId').val() + "&listId=" + $('#listId').val();
	}
	if (URL.ACTION_DOWNLOAD_EXCEL.name == title) {
		if($('#listTypeId').val() == 'ST')
		{
		var url=URL.ACTION_DOWNLOAD_EXCEL.url + "&browseIds=" + itemId + "&listTypeId="
				+ $('#listTypeId').val() ;
		}else{
		
		var searchTerm = encodeURIComponent($(
				'#resultsForm input[name=dsplSearchTerm]').val());

		var url = URL.ACTION_DOWNLOAD_EXCEL.url + "&listTypeId="
				+ $('#listTypeId').val() + "&listId=" + $('#listId').val()
				+ "&ean=" + $('#ean').val()
				+ "&productDetails=" + $('#productDetails').val()
                + "&searchOptionsOn="
				+ $('#searchForm input[name=searchOptionsOn]').val()
				+ "&searchTerm=" + searchTerm + "&searchBy="
				+ $('#searchForm input[name=searchBy]').val()
				+ "&productLimit="
				+ $('#searchForm input[name=productLimit]').val()
				+ "&productType="
				+ $('#searchForm input[name=productType]').val()
		
		if (typeof $('#productDetails').val() == 'undefined' &&  $('#totalRecords').val() != 'undefined') {
			url = url + "&totalRecords=" + $('#totalRecords').val();
		}
		if (typeof $('#resultsForm input[name=No]').val() != 'undefined') {
			url = url + "&No=" + $('#resultsForm input[name=No]').val();
		}
		}
		return url;
	}
	if (URL.CREATE_NEW_LIST.name == title) {
		var pageStatus="";
		if($("#pageStatus").val()!=""){
			pageStatus="&pageStatus="+$("#pageStatus").val();
		}
		var sortBy="";
		if($("#sortBy").val()!=""){
			sortBy="&sortBy="+$("#sortBy").val();
		}
		
		return URL.CREATE_NEW_LIST.url + "&listId=0"+pageStatus+sortBy;
	}

	if (URL.EDIT_LIST.name == title) {
		var pageStatus="";
		if($("#pageStatus").val()!=""){
			pageStatus="&pageStatus="+$("#pageStatus").val();
		}
		
		var sortBy="";
		if($("#sortBy").val()!=""){
			sortBy="&sortBy="+$("#sortBy").val();
		}
		return URL.EDIT_LIST.url + "&listId=" + $('#listId').val()+"&listActionType="+$('#listActionType').val()+pageStatus+sortBy;
	}
	if (URL.ALIBRIS_SEARCH.name == title) {
		var pageNum =  $('#No').attr('value');
		 
		if(pageNum=='null' || null== pageNum){
			pageNum = 0;
		}
		var searchType =  $('#searchType').val(); 
		 
		 
		var alibrisUrl = URL.ALIBRIS_SEARCH.url + "&listId="
				+ $('#listId').val() + "&listTypeId=" + $('#listTypeId').val()
				+ "&itemId=" + itemId + "&ean="
				+ $('#EAN_' + itemId).attr('value') + '&quantity='
				+ $('#qty_' + itemId).val()
				+ "&pageNum=" +pageNum
				+ "&searchType="+ searchType;
				    
		 
        
		return alibrisUrl;
	}
	if (URL.ORDER_STATUS.name == title) {
		return URL.ORDER_STATUS.url + "&listTypeId="+$('#listTypeId').val()+"&oidSalesOrderDetail=" + itemId;
	}
	if (URL.ORDER_INVOICE.name == title) {
		return URL.ORDER_INVOICE.url + "&listTypeId="+$('#listTypeId').val() + "&oidSalesOrderDetail=" + itemId;
	}
	if (URL.SERIES_DETAIL.name == title) {
		var seriesUrl=URL.SERIES_DETAIL.url + "?method=" 
		+"&issn="+ $('#ISSN_' + itemId).attr('value')
		+"&oidissn="+ $('#OIDISSN_' + itemId).attr('value')
		+"&seriestitle="+$('#SERIESTITLE_' + itemId).attr('value')
		+"&call="+$('#CALL_'+itemId).attr('value');
		if($('#CALL_'+itemId).attr('value')=='standing'){
			seriesUrl=seriesUrl+"&oidSalesOrderDetail="+$('#OIDSALES_'+itemId).attr('value');
		}
		return seriesUrl;
	}
	if (URL.SERIES_ALL_SEARCH.name == title) {
		return URL.SERIES_ALL_SEARCH.url;
	}
	if (URL.STANDING_PLACE_ORDER.name == title) {
		return URL.STANDING_PLACE_ORDER.url +"?method=showplace"
		+ "&oidissn=" + $('#OIDISSN_' + itemId).attr('value');
	}
	if (URL.STANDING_ORIGINAL_ORDER.name == title) {
		return URL.STANDING_ORIGINAL_ORDER.url+"?method=showorder";
	}
	if (URL.STANDING_ORDER_INFO.name == title) {
		return URL.STANDING_ORDER_INFO.url+"&oidSales="+$('#OIDSALES_'+itemId).attr('value')
		+"&issn="+ $('#ISSN_' + itemId).attr('value')
		+"&seriesTitle="+$('#SERIESTITLE_' + itemId).attr('value')+"&forthcoming="+$('#forthcoming').val(); 
	}
	if (URL.CHECK_STATUS_STANDING.name == title) {
		return URL.CHECK_STATUS_STANDING.url + "?ean=" + $('#EAN_' + itemId).attr('value');
	}

	return "/ipage/error.action";
}

function isCreateOnlyHeader(browseUrl) {
var listTypeId=$('#listTypeId').val();
	if (URL.RECORD_FILTER.name == browseUrl) {
		return true;
	}
	if (URL.MARK_ALL.name == browseUrl) {
		return true;
	}
	if (URL.PROCESS_LIST.name == browseUrl) {
		return true;
	}
	if (URL.ACTION_DOWNLOAD_EXCEL.name == browseUrl) {
		return true;
	}
	if (URL.CREATE_NEW_LIST.name == browseUrl) {
		return true;
	}

	if (URL.EDIT_LIST.name == browseUrl) {
		return true;
	}
	
	if (URL.VIEW_DEMAND_STANDING_ORDER.name == browseUrl) {
		return true;
	}
	if (URL.CHECK_STATUS_STANDING.name == browseUrl) {
		return true;
	}
	if (URL.STANDING_ORIGINAL_ORDER.name == browseUrl) {
		return true;
	}
	if (URL.STANDING_ORDER_INFO.name == browseUrl) {
		return true;
	}
	if (URL.SERIES_DETAIL.name == browseUrl) {
		return true;
	}
	if (URL.STANDING_PLACE_ORDER.name == browseUrl) {
		return true;
	}
	if (URL.SERIES_ALL_SEARCH.name == browseUrl) {
		return true;
	}
	if (URL.MARC.name == browseUrl && listTypeId =='ST') {
		return true;
	}
	if (URL.ORDER_INVOICE.name == browseUrl && listTypeId =='ST') {
		return true;
	}
	if (URL.ORDER_STATUS.name == browseUrl && listTypeId =='ST') {
		return true;
	}
	return false;
}

function createOnlyHeader() {
	var headStr = '';

    headStr += '<div id="waitPopup" class="toggleBox textWhiteBG textAlignCenter" style="display: none; z-index: 1000; padding: 10px;">';
	headStr += '<img src="/images/circle_loadingAnim.gif"><br /> ';
	headStr += '<img src="/images/pleaseWait_textAnim.gif"><br /> <br /> <br />';
	headStr += '<div id="waitUpdateSection"></div>';
	headStr += '</div>';
	headStr += '<div id="content-pane"></div>'; // DIV close for
												// mainContentWrapper
   

	return headStr;
}

function closeFloatingMessage() {
	$('#floatMsg').fadeOut();
	clearTimeout(callFadeVar);
}

function displayFloatingMsg(msgText) {
	var divCount = $('#floatMsg').length;
	if (divCount == 0) {
		var msgDiv = '<div id="floatMsg"  onclick="closeFloatingMessage()" class="floatingConfirmationMessage"></div>';
		$(msgDiv).prependTo($('body')).css({});
	}
	$('#floatMsg').empty();
	$('#floatMsg').append(msgText);
	$('#floatMsg').css({
		position : 'absolute',
		top : $(window).scrollTop() + 40 + "px"
	});
	$('#floatMsg').show();
	callFadeVar = setTimeout('closeFloatingMessage()', 8000);
}

function executeActions(actItemName, browseId) {
	if (actItemName == 'Add to Cart') {

		executeProcessActions('Cart', browseId, false, true);
	} else if (actItemName == 'Add to List') {

		executeProcessActions('List_02', browseId, false, true);
	} else if (actItemName == 'Mark for Ordering') {

		executeMarkActions('Order_01', browseId);
	} else if (actItemName == 'Mark for Selection') {

		executeMarkActions('Select_01', browseId);
	} else if (actItemName == 'Order Now') {

		executeProcessActions('Order_02', browseId, true, false);
	} else if (actItemName == 'Select Now') {

		executeProcessActions('Select_02', browseId, true, false);
	} else if (actItemName == 'Shopping Cart Now') {

		executeProcessActions('Cart', browseId, true, false);
	} else if (actItemName == 'Send to List') {

		SmokeScreenDeluxe(5, 70, 85, actItemName, browseId);
	} else if (actItemName == 'Forward') {

		SmokeScreenDeluxe(5, 70, 85, "Forwarding", browseId);
	} else if (actItemName == 'Edit Order Info') {

		SmokeScreenDeluxe(5, 85, 85, actItemName, browseId);
	} else if (actItemName == 'Delete Now') {

		executeDeleteActions('DeleteNow', browseId);
	} else if (actItemName == 'Reject') {

		executeMarkActions('Reject_01', browseId);
	} else if (actItemName == 'Clear Action Now') {

		executeMarkActions('clear', browseId);
	} else if (actItemName == 'Block on Approval') {
		var listTypeId = $('#listTypeId').val();
		if (listTypeId == 'PP') {
			executePrintPrePubActions('Approval Block', browseId);
		} else {
			executeProcessActions('Approval Block', browseId, false, false);
		}
	} else if (actItemName == 'Download') {

		executeMarkActions('Download', browseId);
	} else if (actItemName == 'Mark to Accept') {

		executeMarkActions('Accept', browseId);
	} else if (actItemName == 'Accept Now') {

		executeProcessActions('Accept', browseId, true, false);
	} else if (actItemName == 'Mark to Reject') {

		executeMarkActions('Approval Reject', browseId);
	} else if (actItemName == 'Reject Now') {

		executeProcessActions('Approval Reject', browseId, true, false);
	} else if (actItemName == 'Claim on Approval') {
		var listTypeId = $('#listTypeId').val();
		// Check if List Type is other than Print Pre Pub
		if (listTypeId != 'PP') {
			postOrderInfoSaveAction = 'Approval Claim_01';
			postOrderInfoSubmitFlag = true;
			postOrderInfoRetainOnListFlag = false;
			// call Order Info
			var orderInfoUrl = getURL(URL.ACTION_EDIT_ORDER_INFO.name, browseId);
			orderInfoUrl = orderInfoUrl + '&validateProfile=true';
			SmokeScreenDeluxeForUrl(5, 85, 85, URL.ACTION_EDIT_ORDER_INFO.name, browseId, orderInfoUrl);
			
		} else {
			executePrintPrePubActions('Approval Claim_01', browseId);
		}		
	} else if (actItemName == 'Send a Slip') {

		executePrintPrePubActions('Sent to Slips', browseId);
	} else if (actItemName == 'Approval Claim') {
		postOrderInfoSaveAction = 'Approval Claim_02';
		postOrderInfoSubmitFlag = true;
		postOrderInfoRetainOnListFlag = false;
		// call Order Info
		var orderInfoUrl = getURL(URL.ACTION_EDIT_ORDER_INFO.name, browseId);
		orderInfoUrl = orderInfoUrl + '&validateProfile=true';
		SmokeScreenDeluxeForUrl(5, 85, 85, URL.ACTION_EDIT_ORDER_INFO.name, browseId, orderInfoUrl);
		
	} else if (actItemName == 'Mark to Accept this Edition') {

		executeMarkActions('Accept', browseId);
	} else if (actItemName == 'Accept this Edition Now') {

		executeProcessActions('Accept', browseId, true, false);
	} else if (actItemName == 'Mark to Reject this Edition') {

		executeMarkActions('Reject_02', browseId);
	} else if (actItemName == 'Reject this Edition Now') {

		executeProcessActions('Reject_02', browseId, true, false);
	}
   
	delayHide = setTimeout(function() {
		$("#actionMenuContainer_" + browseId).hide();
	}, 150);
 
}

function executeMarkActions(action, browseId) {
	var url = "/ipage/browseRow/markAll.action?browseIds=" + browseId
			+ "&listTypeId=" + $('#listTypeId').val() + "&listId="
			+ $('#listId').val();

	if (action == 'clear') {

		url = url + "&method=clear";
	} else {

		url = url
				+ "&method=mark&rdrct=no&markAllData.selectionSize=windowOnly&markAllData.action="
				+ action;
				
		if (action == "Order_01") {
			url = url + "&markAllData.orderQuantity=" + $('#qty_' + browseId).val();
		} else if (action == "Select_01") {
			url = url + "&markAllData.selectQuantity=" + $('#qty_' + browseId).val();
		}
	}

	$.post(url, $("form#resultsForm").serialize(), function() {
	}).done(function() {
		var action1 = action;
		if (action == 'clear') {

			action = "";
		} else {

			var actions = action.split('_');

			action = actions[0];
		}

		$('#markAction_' + browseId).html(action);
		
		if (action1 == "Order_01" || action1 == "Select_01") {
			var qtyVal = $('#qty_'+browseId).val();
			if (qtyVal.length == 0 || qtyVal < 1) {
				$('#qty_'+browseId).attr("value", "1");
			}
		}
	}).fail(function() {
	}).always(function() {
	});
}

function executePrintPrePubActions(action, browseId) {
		
		var url = "/ipage/browseRow/printPrePublication.action?markAllData.browseIds="
				+ browseId
				+ "&markAllData.listTypeId="
				+ listTypeId
				+ "&listId=" + $('#listId').val();

		url = url
				+ "&actionType=prePubAction&markAllData.selectionSize=windowOnly&markAllData.action="
				+ action;

		url = url + "&approvalLineNo=" + $('#approvalLineNo_' + browseId).html()
				+ "&ean=" + $('#prodEan_' + browseId).html();
		// append profile
		$.post(url, $("form#resultsForm").serialize(), function() {
		}).done(
				function(responseData) {
					if (null == responseData || responseData == 'undefined') {
						return false;
					}

					// splite responseData
					var data = responseData.split('\u001e');

					if (data[0] == 'false') {

						var actions = action.split('_');

						action = actions[0];

						$('#markAction_' + browseId).html(action);

						if (data.length > 1) {

							$('#fund_' + browseId).html(data[2]);
							$('#profile_' + browseId).html(data[3]);
							$('#approvalLineNo_' + browseId).html(data[4]);
							$('#userAction_' + browseId).html(data[5]);
							$('#userActionDate_' + browseId).html(data[6]);
							$('#userInitials_' + browseId).html(data[7]);
							$('#prePubMatch_' + browseId).html(
									data[8].substring(0, (data[8].length - 5)));
							$('#trFund_' + browseId).show();
							$('#trProfile_' + browseId).show();
							$('#trApprovalLineNo_' + browseId).show();
							$('#trUserAction_' + browseId).show();
							$('#trUserActionDate_' + browseId).show();
							$('#trUserInitials_' + browseId).show();
							$('#trPrePubMatch_' + browseId).show();

							if (data[5] == 'EXCLUDE') {
								displayFloatingMsg('Selection set to Excluded by '
										+ data[7] + ' on ' + data[6]);
							}
						}
					}
				}).fail(function() {
		}).always(function() {
		});
	
}

function executeProcessActions(action, browseId, submitAction, isRetainOnList) {
	var listTypeId = $('#listTypeId').val();
	var url = "/ipage/browseRow/processList.action?method=process&processData.actionArrow=true&browseIds="
			+ browseId
			+ "&listId="
			+ $('#listId').val()
			+ "&markAllData.selectionSize=windowOnly&markAllData.action="
			+ action + "&processData.actions=" + action;
	url = url + "&listTypeId=" + listTypeId;
	if (action == "Order_02") {
		url = url + "&markAllData.orderQuantity=" + $('#qty_' + browseId).val();
	} else if (action == "Select_02") {
		url = url + "&markAllData.selectQuantity="
				+ $('#qty_' + browseId).val();
	} else if (action == "Cart") {
		//url = url + "&markAllData.cartQuantity=" + $('#qty_' + browseId).val();
		if($('#qty_' + browseId).val() < 1){
			url = url + "&markAllData.cartQuantity=1";
		}else{
			url = url + "&markAllData.cartQuantity=" + $('#qty_' + browseId).val();
		}
	}

	var retainOnList = isRetainOnList;

	if (isRetainOnList == false) {

		// check retain on list in session
		// Shopping Cart Now, Accept Now, Reject Now, Select Now, Approval Claim
		retainOnList = $("#retainOnList").val();
		if (retainOnList == 'true') {
			retainOnList = true;
		} else {
			retainOnList = false;
		}
		var listTypeId = $('#listTypeId').val();
		// for OH, always set false if accept now this edition
		if (listTypeId == "OH" && action == 'Accept') {
			retainOnList = false;
		}
	}

	if (retainOnList == true) {
		url = url + "&processData.retainOnList=" + action;
	}

	url = url + "&rdrct=no";

	var no = 0;
	if (typeof $('#resultsForm input[name=No]').val() != 'undefined') {
		no = $('#resultsForm input[name=No]').val();
	}
		
	$.post(url, $("form#resultsForm").serialize(), function() {
	}).done(function(response) {
		var status = response.substring(0, response.indexOf(":"));
		var message = response.substring(response.indexOf(":")+1, response.length);
		if (status == "Success") {
			if (submitAction) {
						
				window.location.href = "/ipage/browseRow/processList.action?method=forwardToAction&listId=" + $('#listId').val() + "&listTypeId=" 
				+ $('#listTypeId').val()+"&No="+no;
			} else {
				//if approval claim or block on approval,
				if (action == 'Approval Claim_02' || action == 'Approval Claim_01' || action == 'Approval Block') {
					var actions = action.split('_');

					action = actions[0];

					$('#markAction_' + browseId).html(action);
				} else {
					$('#markAction_' + browseId).html("");
				}
			}
		} else {
			alert("Processing failed - please contact Customer Care");
		}		
	}).fail(function() {
	}).always(function() {
	});
}

function printModalPopup(modalTitle) {
	var cssData = '';
	var hostName = location.protocol + "//" + location.host;
	var w = window.open();
	$(w.document).attr('title', modalTitle);
	$(w.document.head).append(
			'<link rel="stylesheet" href="' + hostName
					+ '/styles/stylesheet.css" media="screen" />');
	$(w.document.head).append(
			'<link rel="stylesheet" href="' + hostName
					+ '/styles/stylesheet_print.css" media="screen" />');
	$(w.document.head).append(
			'<link rel="stylesheet" href="' + hostName
					+ '/styles/stylesheet_print.css" media="print" />');
	var htmlContent = $("#inSmokeContainer").html();
	$(w.document.body).html(htmlContent);
	w.print();
}
//var ean_arr;
var max_try = 50; // need to be decided
var no_poll_attmpts = 0;
var timeoutID;
var page_ean_arr = [];
var red_keys = jQuery
		.parseJSON('{"keys": ["Accepted","Rejected","Selected for Library","Selected by Library","On Hold For","for Review By","Approval Book","Approval eBook","Awaiting Ratification","Denied","Approval Claim","Order","Holdings","Block"]}');
var red_keys_SO = jQuery.parseJSON('{"keys": ["Shipped","Cancelled"]}');

var listNpriorities = jQuery
		.parseJSON('{"SR":["0","Search Results",""],"HC":["0","Alibris Cancelled Orders","SR"],"NL":["0","null","HC"],"AS":["11","Approval Books Selected","NL"],"OD":["20","Order Details","AS"],"DN":["20","Download","OD"],"RQ":["20","Requests","DN"],"PP":["20","Print Pre-publication Match","RQ"],"HL":["41","Alibris List","PP"],"SA":["42","Sent to Acquisitions","HL"],"AO":["43","Authorize Orders","SA"],"SC":["46","Shopping Cart","SC"],"AB":["60","Review Approval Books","SC"],"OH":["70","On Hold","AB"],"PS":["80","Patron Selection Records","OH"],"SN":["81","Slip Notifications","SN"],"IN":["82","Inbox","IN"],"UD":["83","User Defined List","IN"],"AR":["100","Review Approval Rejects","UD"],"RB":["100","Reject Box","AR"],"SO":["500","Order Detail Results","RB"]}');
var mon_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
		"Aug", "Sep", "Oct", "Nov", "Dec");

var actions_on_condition = new Array("Approval Claim","Claim on Approval","Block on Approval","Reject Both");
var actions_on_condition_map = $.parseJSON('{"Approval Claim":"actionAllowClaim", "Claim on Approval":"actionAllowClaim", "Block on Approval":"actionAllowBlock", "Reject Both":"actionRejectBoth"}');

var thisDate = new Date();
thisDate = mon_names[thisDate.getMonth()] + thisDate.getDate() + ", "
		+ thisDate.getFullYear();

$(document).ready(
		function() {

			// set current page records
			var currentPageRecordCount = $('#currentRecords').val()
					- $('#offset').val() + 1;
			$('#currentPageRecords').attr('value', currentPageRecordCount);

			var obj = $('#list_srch_result #chkstatus span');
			var arr = $.makeArray(obj);
			$(arr).each(function(index, obj) {
				var ean = (($(arr[index]).get(0).id).split("_"))[1];

				if ($('#listTypeId').val() == 'SO') {
					var statusCode = (($(arr[index]).get(0).id).split("_"))[2];
					target_elem = "checkStatus_" + ean + "_" + statusCode;
					target_elem = $('[id=' + target_elem + ']');
					if (statusCode != null && statusCode != '') {
						$.each(red_keys_SO.keys, function(key, value) {
							if (statusCode.indexOf(value) != -1) {
								target_elem.addClass("optionsBarAlertStop");
							} else {
								target_elem.addClass("optionsBarAlertCaution");
							}
						});
					}
					target_elem.html(statusCode);
					target_elem.show();
				} else {
					page_ean_arr.push(ean);
				}
			});
			if ($('#listTypeId').val() != 'SO') {
				timeoutID = setTimeout(pollCheckStatus, 2000);
			}
			;

			// function to update QTY
			$("input[name^='quantity']").focusout(this, function() {
				//var oprId = ((this.id).split("_"))[1];
				//make the ajax call to update the qty of this opr_id
				var qty_update_url = '/ipage/academics/checkstatus/updateBrowseRowQty.action?listId=' + $('#listId').attr("value") + '&oprId=' + ((this.id).split("_"))[1] + '&qty=' + this.value;
				$.post(qty_update_url);
			});
		});

function pollCheckStatus() {
	var url = '/ipage/academics/checkstatus/checkStatusJson.action?list_id='
			+ $('#listId').attr("value") + '&eans=' + page_ean_arr;
	var jqxhr = $
			.getJSON(
					url,
					function(data) {

						$
								.each(
										data,
										function(index, json_obj) {

											// target_elem =
											// $("#checkStatus_"+json_obj.ean);
											target_elem = "checkStatus_"
													+ json_obj.ean;
											target_elem = $('[id='
													+ target_elem + ']');
											bd_elem = $("#BD_" + json_obj.ean);
											bt_elem = $("#BT_" + json_obj.ean);

											target_elem.html(json_obj.browseRowMessage);
											
											// check status display
											if (target_elem.length > 0 && json_obj.browseRowMessage.length > 0) {
												$
														.each(
																red_keys.keys,
																function(key,
																		value) {
																	if (json_obj.browseRowMessage
																			.indexOf(value) != -1) {
																		target_elem
																				.addClass("optionsBarAlertStop");
																	} else {
																		target_elem
																				.addClass("optionsBarAlertCaution");
																	}
																});

												if (json_obj.priority == "40"
														&& json_obj.listType == "DO") {
													target_elem
															.html("Order Record Downloaded");
												} else if (json_obj.priority == "41"
														&& json_obj.listType == "HL") {
													if (json_obj.userID == json_obj.listOwner) {
														target_elem
																.html("In Your Alibris List");
													} else {
														target_elem
																.html("In Alibris List");
													}
													;
												} else if (json_obj.priority == "45"
														&& json_obj.listType == "AO") {
													target_elem
															.html(bd_elem
																	.html()
																	+ " Awaiting Ratification");
												} else if (json_obj.priority == "46"
														&& json_obj.listType == "SC") {
													if (json_obj.userID == json_obj.listOwner) {
														if ($('#listTypeId')
																.val() != "SC") {
															target_elem
																	.html("In Your "
																			+ json_obj.browseRowMessage);
														} else {
															target_elem
																	.html(listNpriorities[listNpriorities["SC"][2]][1]);
														}

													} else {
														target_elem
																.html("In Shopping Cart");
													}
													;
												} else if (json_obj.priority == "71"
														&& json_obj.listType == "OH") {
													target_elem
															.html("Patron Selection on Hold Until " + json_obj.browseRowMessage);
												} else if (json_obj.priority == "81"
														&& json_obj.listType == "SN") {
													if (json_obj.userID == json_obj.listOwner) {
														if ($('#listTypeId')
																.val() != "SN") {
															target_elem
																	.html("In Your "
																			+ json_obj.browseRowMessage);
														} else {
															target_elem
																	.html(listNpriorities[listNpriorities["SN"][2]][1]);
														}

													} else {
														target_elem
																.html("In Slip Notifications");
													}
													;

												} else if (json_obj.priority == "82"
														&& json_obj.listType == "IN") {

													if (json_obj.userID == json_obj.listOwner) {
														if ($('#listTypeId')
																.val() != "IN") {
															target_elem
																	.html("In Your "
																			+ json_obj.browseRowMessage);
														} else {
															target_elem
																	.html(listNpriorities[listNpriorities["IN"][2]][1]);
														}

													} else {
														target_elem
																.html("In an "
																		+ json_obj.browseRowMessage);
													}
													;
												} else if (json_obj.priority == "83") {

													if (json_obj.listType == "HC") {
														if (json_obj.userID == json_obj.listOwner) {
															target_elem
																	.html("On Your Alibris");
														} else {
															target_elem
																	.html("On Alibris");
														}
														;
													} else if (json_obj.listType == "UD") {
														if (json_obj.userID == json_obj.listOwner) {
															target_elem
																	.html("On Your List");
														} else {
															target_elem
																	.html("On List");
														}
														;
													}
													;
												} else if (json_obj.priority == "90"
														&& json_obj.listType == "PS") {
													target_elem
															.html("Patron Selection Record Provided for "
																	+ bd_elem
																			.html()
																	+ thisDate);

												} else if (json_obj.priority == "100"
														&& json_obj.listType == "AR") {
													target_elem
															.html("Approval Book Rejected on "
																	+ thisDate);
												} else if (json_obj.priority == "110"
														&& json_obj.listType == "RB") {
													target_elem
															.html("In <value> Recycle Box");
												} else {
													target_elem
															.html(json_obj.browseRowMessage);
												}
												target_elem.show();

												page_ean_arr = $
														.grep(
																page_ean_arr,
																function(
																		elem_val) {
																	return elem_val != json_obj.ean;
																});

											}
											;
											
											//add - remove action claim / block actions - move it to a proper method
											var menuActions = $("div[class='actionMenuItem']");
											var menuActionsArray = $.makeArray(menuActions);
											
											$(menuActionsArray).each(function(index, obj) {		
												if($.inArray($(menuActionsArray[index]).attr('id'), actions_on_condition) >= 0){
													if(json_obj[actions_on_condition_map[$(menuActionsArray[index]).attr('id')]] == true){
														$(menuActionsArray[index]).show();	
													}
												}
											});
											
											if (json_obj.communityGroup == true) {
												
												target_elem = $("#communityGroup_" + json_obj.ean);
												// community group display
												if (target_elem.length > 0) {
													target_elem.show();
												};			
											};
											
											if(json_obj.lastReceived != ''){
												$('.lastReceivedTR').css({visibility: 'visible'})
												$('#lastReceivedTD').html('<strong>Last Received: </strong>'+ json_obj.lastReceived)
											};  

											target_elem = $("#oidSalesOrderDetail_"
													+ json_obj.ean);
											target_elem
													.val(json_obj.oidSalesOrderDetail);
											
											calculateNetPrice(json_obj);

										});// $.each

					});// $.getJSON

	jqxhr.always(function() {

		if ($('#listTypeId').val() == 'SO') {
			no_poll_attmpts = max_try;
			clearTimeout(timeoutID);
			return false;
		}
		no_poll_attmpts++;

		if (page_ean_arr.length > 0 && no_poll_attmpts < max_try) {
			timeoutID = setTimeout(pollCheckStatus, 2000);
		} else {
			clearTimeout(timeoutID);
		}
	});

};// function pollCheckStatus()

function ebookUrlCreator(ebpData) {
	var baseUrl = "/ipage/product/search/acdm/ebookVendor.action?";
	baseUrl += ebpData;	
	var jqxhr = $.getJSON(baseUrl, function(data) {
		window.open(data.destUrl);
	});// $.getJSON

};
   
//Calculate Estimated Sale Price
function calculateNetPrice(json_obj){
	target_element="estSalePrice_" + json_obj.ean;
	target_element = $('[id='+target_element+']');
	target_pubPrice=$('#publisherPrice_'+json_obj.ean).text();
	target_currencyPref=$('#currencyPref_'+json_obj.ean).text();
	target_defaultCurPref=$('#defaultCurrencyPref').text();
	est_price=json_obj.netPrice;
	if(target_element.length > 0){	
		if(target_element.text().length==0){
				var exchange_json = JSON.parse(exchangeRateJson);
				var target_exchangeRate=exchange_json[target_currencyPref];
			
			if(est_price=="0"){
			         var calculatePrice=0;
					if(target_defaultCurPref==target_currencyPref){
							calculatePrice=target_pubPrice;
						}
					else{
						    calculatePrice=target_pubPrice * target_exchangeRate;
						}	
							calculatePrice = $.trim(calculatePrice);
							if(calculatePrice.length > 0 &&  calculatePrice != "0"){
								 if(!isNaN(calculatePrice)){
									calculatePrice=Number(Math.round(calculatePrice+'e2')+'e-2').toFixed(2);
									target_element.html(calculatePrice+"&nbsp;"+target_defaultCurPref);
								 }
							
							}
							else{
								  if(document.getElementById("estimatedSalePriceBody")){
										document.getElementById("estimatedSalePriceBody").style.display="none";
									}
								
							}
						
			}else{
				if(!isNaN(est_price)){
				est_price=Number(Math.round(est_price+'e2')+'e-2').toFixed(2);
				target_element.html(est_price+"&nbsp;"+target_defaultCurPref);
				}
			}
		}
	}
};

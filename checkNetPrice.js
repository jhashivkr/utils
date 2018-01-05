
var ean_arr = [];
	
$(document).ready(function() {
	var obj = $('#list_srch_result #estSalePrice span');
	var arr = $.makeArray(obj);
	if(typeof arr == "undefined" || arr == null ||arr.length == 0){
		var productDetailEan=$('#productDetailEan').text();
		if(productDetailEan!=""){
			ean_arr.push(productDetailEan);
		};
	}
	else{
	$(arr).each(function(index, obj) { 
		var ean = (($(arr[index]).get(0).id).split("_"))[1];
		ean_arr.push(ean);
	});
	};
	if(ean_arr.length>0){
			pollCheckNetPrice();
	}
});
			
function pollCheckNetPrice(){
	var url = '/ipage/academics/netprice/checkNetPrice.action?list_id=' + $('#listId').attr("value") + '&eans=' + ean_arr;				
	var jqxhr = $.getJSON( url, function( data ) {
		
		$.each(data, function(index, json_obj){
			 
			if((json_obj.ean=='undefined'|| null== json_obj.ean )&&( json_obj.lastReceived == 'undefined' || null == json_obj.lastReceived)){
				var productDetailEan=$('#productDetailEan').text();
				if(productDetailEan!=""){
					if(json_obj["Community_"+productDetailEan]!=''){
								target_elem = $("#communityGroup_" + productDetailEan);
											// community group display
											if (target_elem.length > 0) {
												if (json_obj["Community_"+productDetailEan] === "true") {
													target_elem.show();
												};
											};
					
					}
				}
			}
			else if( json_obj.lastReceived == 'undefined' || null == json_obj.lastReceived){
				loadEstimatedSalePrice(json_obj);
			}else if(json_obj.lastReceived != ''){
				$('.lastReceivedTR').css({visibility: 'visible'})
				$('#lastReceivedTD').html('<strong>Last Received: </strong>'+ json_obj.lastReceived)
				
			}  

			
			
		
				});//$.each
			 
	  });//$.getJSON
	
};//function pollCheckNetPrice()
   
//Calculate Estimated Sale Price
function loadEstimatedSalePrice(json_obj){
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

function ebookUrlCreator(ebpData) {
	var baseUrl = "/ipage/product/search/acdm/ebookVendor.action?";
	baseUrl += ebpData;	
	var jqxhr = $.getJSON(baseUrl, function(data) {
		window.open(data.destUrl);
	});// $.getJSON

};
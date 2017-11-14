package com.ibg.data.uploaders;

import com.ibg.models.selrecords.AcdmExternalPartReservation;
import com.ibg.parsers.json.ExternalPartReservationDetail;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmExternalPartRervationUploader {

	public AcdmExternalPartRervationUploader() {
	}

	public static void startDataUpload(SelectionRecord selectionRecord, String listId) {

		ExternalPartReservationDetail selRecExtPartRes;
		AcdmExternalPartReservation extRes;

		try {

			selRecExtPartRes = selectionRecord.getExternalPartReservation();

			extRes = new AcdmExternalPartReservation();
			extRes.set("OWNER_ID", selectionRecord.getOidContact());
			extRes.set("LIST_ID", listId);
			extRes.set("SUPPLIER_ID", selectionRecord.getOidSupplier());

			extRes.set("SKU", selRecExtPartRes.getSKU());			
			extRes.set("ACTIVE", "true".equalsIgnoreCase(selRecExtPartRes.getActive()) ? "1" : "0" );
			
			if(selRecExtPartRes.getDescription().length() >= 256)
				extRes.set("DESCRIPTION", selRecExtPartRes.getDescription().substring(0, 255));
			else
				extRes.set("DESCRIPTION", selRecExtPartRes.getDescription());			
			
			extRes.set("PRICE", selRecExtPartRes.getPrice());
			extRes.set("PRICE_NOTES", selRecExtPartRes.getPriceNotes());
			extRes.set("DETAILS", selRecExtPartRes.getDetails());
			extRes.set("ALIBRIS_SELL_PRICE", selRecExtPartRes.getAlibrisSellPrice());
			extRes.set("CONDITION", selRecExtPartRes.getCondition());
			extRes.set("PUBLISHER", selRecExtPartRes.getPublisher());

			extRes.set("SELLER_ID", selRecExtPartRes.getSellerID());
			extRes.set("SELLER_NAME", selRecExtPartRes.getSellerName());
			extRes.set("SELLER_RELIABILITY", selRecExtPartRes.getSellerReliability());
			extRes.set("SHIPS_FROM", selRecExtPartRes.getShipsFrom());
			extRes.set("SELLING_PRICE", selRecExtPartRes.getSellingPrice());
			extRes.set("SELLING_CURRENCY", selRecExtPartRes.getSellingCurrency());
			
			if (!extRes.saveIt())
				System.out.println("external part reservation not saved");

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

}

package Stock.data;

public class DBContract {

	public static final String DATABASE_NAME = "mine.db";

	public static final class ManufacturerEntry {

		public static final String TABLE_NAME = "manufacturers";

		public static final String NAME = "name";

	}

	public static final class ItemEntry {

		public static final String TABLE_NAME = "Product";
		
		public static final String ITEM_ID = "ID";
		public static final String ITEM_NAME = "name";
		public static final String ITEM_MinInStock = "MinInStock";
		public static final String MANUFACTURER_NAME = "Maker";
		public static final String ITEM_Category = "Category";
		public static final String ITEM_MaxInStock = "MaxInStock";
		public static final String ITEM_EXPECTED = "Expected";

	}
	
	public static final class OrderEntry {

		public static final String TABLE_NAME = "Orders";
		
		public static final String ORDER_SUPPLIER_ID = "SupID";
		public static final String ORDER_SUPPLIER_NAME = "SupName";
		public static final String ORDER_SUPPLIER_ADDRESS = "SupAddress";
		public static final String ORDER_DATE = "OrderDate";
		public static final String ORDER_ID= "OrdID";
		public static final String ORDER_CONTACT = "Contact";
	}
	
	public static final class ItemsInOrderEntry
	{
		public static final String TABLE_NAME = "ItemsInOrder";
		
		public static final String IIO_ID = "ID";
		public static final String ORDER_ID = "OID";
		public static final String IIO_NAME = "Name";
		public static final String IIO_AMOUNT = "Amount";
		public static final String IIO_CATALOG_PRICE = "CatalogPrice";
		public static final String IIO_DISCOUNT = "Discount";
		public static final String IIO_FINAL_PRICE = "FinalPrice";
		
	}
	
	public static final class FaultyProduct {
		public static final String TABLE_NAME = "FaultyProduct";
		
		public static final String Faulty_ID = "ID";
		public static final String Faulty_Expiration = "ExpirationDate";
		public static final String Faulty_AmountInStock = "AmountInStock";
	}
	
	public static final class CategoryEntry {

		public static final String TABLE_NAME = "categories";
		
		public static final String CATEGORY_ID = "ID";
		public static final String CATEGORY_NAME = "Name";
		public static final String Parent = "ParentCategory";

	}
	
	public static final class UsersEntry {

		public static final String TABLE_NAME = "Users";
		
		public static final String USER_ID = "ID";
		public static final String USER_NAME = "Username";
		public static final String USER_PASSWORD = "Password";
		public static final String USER_CLEARENCE = "Clearence";

	}
	
	public static final class ProductInStockEntry {

		public static final String TABLE_NAME = "ProductInStock";
		
		public static final String PIS_ID = "ID";
		public static final String PIS_Expiration = "ExpirationDate";
		public static final String PIS_AmountInStock = "AmountInStock";
	}

	public static final class AgreementEntry {

		public static final String TABLE_NAME = "agreements";

		public static final String ID = "id";
		public static final String SUPPLIER_SERIAL_NUM = "supplier_serial_number";
		public static final String IS_TRANSPORT_BY_SUPPLIER= "is_transport_by_supplier";

	}
	
	public static final class AgreementDaysEntry {

		public static final String TABLE_NAME = "agreement_days";

		public static final String AGREEMENT_ID = "agreement_id";
		public static final String DAY = "day";

	}

	public static final class AgreementItemsEntry {

		public static final String TABLE_NAME = "agreement_items";

		public static final String AGREEMENT_ID = "agreement_id";
		public static final String CATALOUGE_NUM = "catalouge_num";
		public static final String PRICE = "price";
		public static final String MIN_QDS_QUANTITY = "min_qds_quantity";
		public static final String QDS_DISCOUNT = "qds_discount";
		
	}
	
	public static final class AgreementItemDiscountsEntry {

		public static final String TABLE_NAME = "agreement_item_discount";

		public static final String AGREEMENT_ID = "agreement_id";
		public static final String CATALOUGE_NUM = "catalouge_num";
		public static final String MIN_QUANTITY = "min_quantity";
		public static final String DISCOUNT = "discount";
		
	}

	public static final class SupplierCardEntry {

		public static final String TABLE_NAME = "supplier_cards";

		public static final String SERIAL_NUMBER = "serial_number";
		public static final String NAME = "name";
		public static final String ADDRESS = "address";
		public static final String BANK_ACCOUNT = "bank_account";
		public static final String PAYMENT_TERMS = "payment_terms";

	}

	public static final class ContactEntry {

		public static final String TABLE_NAME = "contacts";

		public static final String EMAIL = "email";
		public static final String NAME = "name";
		public static final String SUPPLIER_ID = "supplier_id";

	}
	
	public static final class ContactPhonesEntry {

		public static final String TABLE_NAME = "contact_phones";

		public static final String EMAIL = "email";
		public static final String PHONE = "phone";

	}

	public static final class SupplierCatalougeItemEntry {

		public static final String TABLE_NAME = "supplier_catalouge_item";

		public static final String SERIAL_NUMBER = "serial_number";
		public static final String ITEM_ID = "ID";
		public static final String ITEM_NAME = "name";
		public static final String MANUFACTURER_NAME = "manufacturer_name";
		public static final String CATALOUGE_ID = "catalouge_id";

	}

}

package Stock.data;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


import Program.OrderToTransport;
import Stock.common.Category;
import Stock.common.Product;
import Stock.common.Tree;
import Stock.market.Item;
import Stock.market.Manufacturer;
import Stock.modul.NeededItem;
import Stock.modul.StockReport;
import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.DiscountTerm;
import Stock.supplierManagement.QDS;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierCard;
import Stock.supplierManagement.SupplierContact;
import Stock.supplyOrder.ItemPricing;
import Program.Main;
import Transport.PL.NewTransport;

public class DBHandler implements Dbms {
	
	private static final boolean DEBUG = false;
	private Connection conn;
	private Tree CategoryTree;
	
	/**
	 * A constructor that creates/connects to the database
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public DBHandler() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + DBContract.DATABASE_NAME);
			conn.setAutoCommit(true);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		if(DEBUG)
			createTables(true);


        CategoryTree = new Tree();
		List Categories = ExecuteList("Select * FROM Categories;", 1);
		Category[] CatArr = new Category[Categories.size()];
		Categories.toArray(CatArr);
		for (Category c : CatArr) {
			if (c.getParentID() == 0) {
				CategoryTree.addNode(c.getID(), c);
				Categories.remove(c);
			}
		}
		while (!Categories.isEmpty()) {
			Category c = (Category) Categories.remove(0);
			if (CategoryTree.getNodes().containsKey(c.getParentID()))
				CategoryTree.addNode(c.getID(), c.getParentID(), c);
			else
				Categories.add(c);
		}
	}
	
	public void createTables(boolean reset) {
		try {
			
			if (reset)
				deleteTables();
			
			final String tableManufacturers = "CREATE TABLE IF NOT EXISTS " + DBContract.ManufacturerEntry.TABLE_NAME + " ( " +
					DBContract.ManufacturerEntry.NAME + " TEXT PRIMARY KEY)";
			execUpdateSQL(tableManufacturers);
			
			final String tableCategories = "CREATE TABLE IF NOT EXISTS " + DBContract.CategoryEntry.TABLE_NAME + " ( " +
					DBContract.CategoryEntry.CATEGORY_ID + " integer NOT NULL PRIMARY KEY, " +
					DBContract.CategoryEntry.CATEGORY_NAME + " TEXT NOT NULL, " +
					DBContract.CategoryEntry.Parent + " integer DEFAULT NULL, " +
					"FOREIGN KEY (" + DBContract.CategoryEntry.Parent + ") REFERENCES " +
					DBContract.CategoryEntry.TABLE_NAME + "(" + DBContract.CategoryEntry.CATEGORY_ID + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableCategories);
			
			final String tableItems = "CREATE TABLE IF NOT EXISTS " + DBContract.ItemEntry.TABLE_NAME + " ( " +
					DBContract.ItemEntry.ITEM_ID + " integer NOT NULL PRIMARY KEY, " +
					DBContract.ItemEntry.ITEM_NAME + " TEXT NOT NULL, " +
					DBContract.ItemEntry.ITEM_MinInStock + " integer NOT NULL, " +
					DBContract.ItemEntry.MANUFACTURER_NAME + " TEXT NOT NULL, " +
					DBContract.ItemEntry.ITEM_Category + " integer DEFAULT NULL, " +
					DBContract.ItemEntry.ITEM_MaxInStock + " integer DEFAULT NULL CHECK(MaxInStock > 2*MinInStock), " +
					DBContract.ItemEntry.ITEM_EXPECTED + " integer DEFAULT 0, " +
					"FOREIGN KEY (" + DBContract.ItemEntry.MANUFACTURER_NAME + ") REFERENCES " +
					DBContract.ManufacturerEntry.TABLE_NAME + "(" + DBContract.ManufacturerEntry.NAME + ") ON UPDATE CASCADE" + "," +
					"FOREIGN KEY (" + DBContract.ItemEntry.ITEM_Category + ") REFERENCES " +
					DBContract.CategoryEntry.TABLE_NAME + "(" + DBContract.CategoryEntry.CATEGORY_ID + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableItems);
			
			final String tablePIS = "CREATE TABLE IF NOT EXISTS " + DBContract.ProductInStockEntry.TABLE_NAME + " ( " +
					DBContract.ProductInStockEntry.PIS_ID + " integer NOT NULL, " +
					DBContract.ProductInStockEntry.PIS_Expiration + " TEXT NOT NULL, " +
					DBContract.ProductInStockEntry.PIS_AmountInStock + " integer NOT NULL, " +
					"PRIMARY KEY (" + DBContract.ProductInStockEntry.PIS_ID + ", " + DBContract.ProductInStockEntry.PIS_Expiration + "), " +
					"FOREIGN KEY (" + DBContract.ProductInStockEntry.PIS_ID + ") REFERENCES " +
					DBContract.ItemEntry.TABLE_NAME + "(" + DBContract.ItemEntry.ITEM_ID + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tablePIS);
			
			final String tableFaulty = "CREATE TABLE IF NOT EXISTS " + DBContract.FaultyProduct.TABLE_NAME + " ( " +
					DBContract.FaultyProduct.Faulty_ID + " integer NOT NULL, " +
					DBContract.FaultyProduct.Faulty_Expiration + " DATE NOT NULL, " +
					DBContract.FaultyProduct.Faulty_AmountInStock + " integer NOT NULL, " +
					"PRIMARY KEY (" + DBContract.FaultyProduct.Faulty_ID + ", " + DBContract.FaultyProduct.Faulty_Expiration + ") )";
			execUpdateSQL(tableFaulty);
			
			final String tableUsers = "CREATE TABLE IF NOT EXISTS " + DBContract.UsersEntry.TABLE_NAME + " ( " +
					DBContract.UsersEntry.USER_ID + " integer NOT NULL PRIMARY KEY, " +
					DBContract.UsersEntry.USER_NAME + " TEXT NOT NULL UNIQUE, " +
					DBContract.UsersEntry.USER_PASSWORD + " TEXT NOT NULL, " +
					DBContract.UsersEntry.USER_CLEARENCE + " integer DEFAULT 0) ";
			execUpdateSQL(tableUsers);
			
			final String tableOrder = "CREATE TABLE IF NOT EXISTS " + DBContract.OrderEntry.TABLE_NAME + " ( " +
					DBContract.OrderEntry.ORDER_ID + " integer PRIMARY KEY, " +
					DBContract.OrderEntry.ORDER_SUPPLIER_ID + " integer NOT NULL, " +
					DBContract.OrderEntry.ORDER_SUPPLIER_NAME + " TEXT NOT NULL, " +
					DBContract.OrderEntry.ORDER_SUPPLIER_ADDRESS + " TEXT NOT NULL, " +
					DBContract.OrderEntry.ORDER_DATE + " TEXT NOT NULL, " +
					DBContract.OrderEntry.ORDER_CONTACT + " TEXT) ";
			execUpdateSQL(tableOrder);
			
			final String tableItemsInOrder = "CREATE TABLE IF NOT EXISTS " + DBContract.ItemsInOrderEntry.TABLE_NAME + " ( " +
					DBContract.ItemsInOrderEntry.IIO_ID + " integer NOT NULL, " +
					DBContract.ItemsInOrderEntry.ORDER_ID + " integer NOT NULL, " +
					DBContract.ItemsInOrderEntry.IIO_NAME + " TEXT NOT NULL, " +
					DBContract.ItemsInOrderEntry.IIO_AMOUNT + " integer NOT NULL, " +
					DBContract.ItemsInOrderEntry.IIO_CATALOG_PRICE + " integer NOT NULL, " +
					DBContract.ItemsInOrderEntry.IIO_DISCOUNT + " REAL NOT NULL, " +
					DBContract.ItemsInOrderEntry.IIO_FINAL_PRICE + " integer NOT NULL," +
					"PRIMARY KEY(" + DBContract.ItemsInOrderEntry.IIO_ID + "," + DBContract.ItemsInOrderEntry.ORDER_ID + ")) ";
			execUpdateSQL(tableItemsInOrder);
			
			final String tableAgreements = "CREATE TABLE IF NOT EXISTS " + DBContract.AgreementEntry.TABLE_NAME + " ( " +
					DBContract.AgreementEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					DBContract.AgreementEntry.SUPPLIER_SERIAL_NUM + " INT NOT NULL, " +
					DBContract.AgreementEntry.IS_TRANSPORT_BY_SUPPLIER + " INT NOT NULL," +
					"FOREIGN KEY (" + DBContract.AgreementEntry.SUPPLIER_SERIAL_NUM + ") REFERENCES " +
					DBContract.SupplierCardEntry.TABLE_NAME + "(" + DBContract.SupplierCardEntry.SERIAL_NUMBER + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableAgreements);
			
			final String tableAgreementDays = "CREATE TABLE IF NOT EXISTS " + DBContract.AgreementDaysEntry.TABLE_NAME + " ( " +
					DBContract.AgreementDaysEntry.AGREEMENT_ID + " INT NOT NULL, " +
					DBContract.AgreementDaysEntry.DAY + " INT NOT NULL, " +
					"PRIMARY KEY (" + DBContract.AgreementDaysEntry.AGREEMENT_ID + ", " + DBContract.AgreementDaysEntry.DAY + "), " +
					"FOREIGN KEY (" + DBContract.AgreementDaysEntry.AGREEMENT_ID + ") REFERENCES " +
					DBContract.AgreementEntry.TABLE_NAME + "(" + DBContract.AgreementEntry.ID + ") ON UPDATE CASCADE, " +
					"UNIQUE (" + DBContract.AgreementDaysEntry.AGREEMENT_ID + ", " + DBContract.AgreementDaysEntry.DAY + ") ON CONFLICT IGNORE" + ")";
			execUpdateSQL(tableAgreementDays);
			
			final String tableItemsAgreements = "CREATE TABLE IF NOT EXISTS " + DBContract.AgreementItemsEntry.TABLE_NAME + " ( " +
					DBContract.AgreementItemsEntry.AGREEMENT_ID + " INT NOT NULL, " +
					DBContract.AgreementItemsEntry.CATALOUGE_NUM + " INT NOT NULL, " +
					DBContract.AgreementItemsEntry.PRICE + " REAL NOT NULL, " +
					DBContract.AgreementItemsEntry.MIN_QDS_QUANTITY + " INT, " +
					DBContract.AgreementItemsEntry.QDS_DISCOUNT + " REAL, " +
					"PRIMARY KEY (" + DBContract.AgreementItemsEntry.AGREEMENT_ID + ", " +
					DBContract.AgreementItemsEntry.CATALOUGE_NUM + "), " +
					"FOREIGN KEY (" + DBContract.AgreementItemsEntry.AGREEMENT_ID + ") REFERENCES " +
					DBContract.AgreementEntry.TABLE_NAME + "(" + DBContract.AgreementEntry.ID + ") ON UPDATE CASCADE, " +
					"FOREIGN KEY (" + DBContract.AgreementItemsEntry.CATALOUGE_NUM + ") REFERENCES " +
					DBContract.SupplierCatalougeItemEntry.TABLE_NAME + "(" + DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableItemsAgreements);
			
			final String tableItemDiscountSAgreements = "CREATE TABLE IF NOT EXISTS " + DBContract.AgreementItemDiscountsEntry.TABLE_NAME + " ( " +
					DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID + " INT NOT NULL, " +
					DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM + " INT NOT NULL, " +
					DBContract.AgreementItemDiscountsEntry.MIN_QUANTITY + " INT NOT NULL, " +
					DBContract.AgreementItemDiscountsEntry.DISCOUNT + " REAL NOT NULL, " +
					"PRIMARY KEY (" + DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID + ", " +
					DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM + ", " +
					DBContract.AgreementItemDiscountsEntry.MIN_QUANTITY + "), " +
					"FOREIGN KEY (" + DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID + ") REFERENCES " +
					DBContract.AgreementItemsEntry.TABLE_NAME + "(" + DBContract.AgreementItemsEntry.AGREEMENT_ID + ") ON UPDATE CASCADE, " +
					"FOREIGN KEY (" + DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM + ") REFERENCES " +
					DBContract.AgreementItemsEntry.TABLE_NAME + "(" + DBContract.AgreementItemsEntry.CATALOUGE_NUM + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableItemDiscountSAgreements);
			
			final String tableSupplierCards = "CREATE TABLE IF NOT EXISTS " + DBContract.SupplierCardEntry.TABLE_NAME + " ( " +
					DBContract.SupplierCardEntry.SERIAL_NUMBER + " INT PRIMARY KEY, " +
					DBContract.SupplierCardEntry.NAME + " TEXT NOT NULL, " +
					DBContract.SupplierCardEntry.ADDRESS + " TEXT NOT NULL, " +
					DBContract.SupplierCardEntry.BANK_ACCOUNT + " INT NOT NULL, " +
					DBContract.SupplierCardEntry.PAYMENT_TERMS + " TEXT NOT NULL)";
			execUpdateSQL(tableSupplierCards);
			
			final String tableContracts = "CREATE TABLE IF NOT EXISTS " + DBContract.ContactEntry.TABLE_NAME + " ( " +
					DBContract.ContactEntry.EMAIL + " TEXT PRIMARY KEY, " +
					DBContract.ContactEntry.NAME + " TEXT NOT NULL," +
					DBContract.ContactEntry.SUPPLIER_ID + " INT NOT NULL," +
					"FOREIGN KEY (" + DBContract.ContactEntry.SUPPLIER_ID + ") REFERENCES " +
					DBContract.SupplierCardEntry.TABLE_NAME + "(" + DBContract.SupplierCardEntry.SERIAL_NUMBER + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableContracts);
			
			final String tableContractPhones = "CREATE TABLE IF NOT EXISTS " + DBContract.ContactPhonesEntry.TABLE_NAME + " ( " +
					DBContract.ContactPhonesEntry.EMAIL + " TEXT NOT NULL, " +
					DBContract.ContactPhonesEntry.PHONE + " TEXT NOT NULL, " +
					"PRIMARY KEY (" + DBContract.ContactPhonesEntry.EMAIL + ", " + DBContract.ContactPhonesEntry.PHONE + "), " +
					"FOREIGN KEY (" + DBContract.ContactPhonesEntry.EMAIL + ") REFERENCES " +
					DBContract.ContactEntry.TABLE_NAME + "(" + DBContract.ContactEntry.EMAIL + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableContractPhones);
			
			final String tableSupplierCardsItems = "CREATE TABLE IF NOT EXISTS " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + " ( " +
					DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER + " TEXT NOT NULL, " +
					DBContract.SupplierCatalougeItemEntry.ITEM_ID + " integer NOT NULL, " +
					DBContract.SupplierCatalougeItemEntry.ITEM_NAME + " TEXT NOT NULL, " +
					DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME + " TEXT NOT NULL, " +
					DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID + " INT NOT NULL, " +
					"PRIMARY KEY (" + DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER + ", " +
					DBContract.SupplierCatalougeItemEntry.ITEM_ID + ")" +
					"FOREIGN KEY (" + DBContract.SupplierCatalougeItemEntry.ITEM_ID + ") REFERENCES " +
					DBContract.ItemEntry.TABLE_NAME + "(" + DBContract.ItemEntry.ITEM_ID + ") ON UPDATE CASCADE, " +
					"FOREIGN KEY (" + DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER + ") REFERENCES " +
					DBContract.SupplierCardEntry.TABLE_NAME + "(" + DBContract.SupplierCardEntry.SERIAL_NUMBER + ") ON UPDATE CASCADE" + ")";
			execUpdateSQL(tableSupplierCardsItems);
			
			
			
			if(reset)
				fillDatabase();
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to create tables!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void fillDatabase() throws SQLException
	{
		execUpdateSQL("INSERT OR IGNORE INTO `agreement_days` (agreement_id,day) VALUES (5,0), (5,1), (5,2), (5,3), (5,4), (5,5)");
		execUpdateSQL("insert OR IGNORE INTO `categories` (ID,Name,ParentCategory) VALUES (1,'Dairy',NULL), (2,'IceCream',1), (3,'Meat',NULL), (4,'Steaks',3)");
		execUpdateSQL("INSERT OR IGNORE INTO `Product` (ID,name,MinInStock,Maker,Category,MaxInStock,Expected) VALUES (1,'Milk',20,'Tnuva',1,100,0),"+
				"(2,'Sinta',10,'REDRED',4,300,0),"+
				"(3,'ChocolateIceCream',50,'Nestle',2,120,0)");
		execUpdateSQL("insert OR IGNORE INTO `Users` (ID,Username,Password,Clearence) VALUES (1,'Admin','Admin',1), (2,'User','User',0)");
		execUpdateSQL("insert OR IGNORE INTO `ProductInStock` (ID,ExpirationDate,AmountInStock) VALUES (1,'2018-10-24',9), (2,'2022-12-15',100), (3,'2016-10-10',40)");
		for (int i = 1; i <= 5; i++)
		{
			execUpdateSQL("INSERT OR IGNORE INTO supplier_cards (serial_number,Name, Bank_account, Payment_terms,address) VALUES ("+i+",'sup"+i+"',"+i+",'Yearly','somewhere')");
			for (int j = 1; j <= 3; j++)
			{
				System.out.println(i+", "+j);
				execUpdateSQL("INSERT OR IGNORE INTO agreements (is_transport_by_supplier,supplier_serial_number,id) VALUES (" + 1+","+i+","+(j+i*4)+")");
				execUpdateSQL("INSERT OR IGNORE INTO supplier_catalouge_item (serial_number,name,manufacturer_name,catalouge_id) VALUES ("+i+",'Milk"+i+"','Tnuva"+i+"',1)");
				execUpdateSQL("INSERT OR IGNORE INTO agreement_items (agreement_id,catalouge_num,price) VALUES ("+(j+i*4)+","+1+",10),("+(j+i*4)+","+2+",20),("+(j+i*4)+","+3+",30)");
				
				for (int j2 = 1; j2 <= 2; j2++)
				{
					execUpdateSQL(
							"INSERT OR IGNORE INTO agreement_item_discount (agreement_id, catalouge_num,min_quantity,discount) VALUES ("
									+ (j+i*4) + "," + j2 + ",10," + ((10.0 - j2) / 10.0) + ")");
				}
				
				
			}
			
			
		}
	}
	
	private void deleteTables() throws SQLException {
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ManufacturerEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.AgreementEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.AgreementDaysEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.AgreementItemsEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.AgreementItemDiscountsEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.SupplierCardEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ContactEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ContactPhonesEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ProductInStockEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ItemEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.CategoryEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.UsersEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.FaultyProduct.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.OrderEntry.TABLE_NAME);
		execUpdateSQL("DROP TABLE IF EXISTS " + DBContract.ItemsInOrderEntry.TABLE_NAME);
		
		
	}
	
	private String getNormalDate()
	{
		String date="'";
		Calendar tmp = Main.today;
		date+=tmp.get(Calendar.YEAR)+"-";
		date+=((tmp.get(Calendar.MONTH)+1)<10)? "0"+(tmp.get(Calendar.MONTH)+1) : (tmp.get(Calendar.MONTH)+1);
		date+="-";
		date+=(tmp.get(Calendar.DAY_OF_MONTH)<10)? "0"+tmp.get(Calendar.DAY_OF_MONTH) : tmp.get(Calendar.DAY_OF_MONTH);
		return date+"'";
	}
	
	public void close() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void execUpdateSQL(String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	public void startTransaction() {
		try {
			execUpdateSQL("BEGIN TRANSACTION");
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to begin transaction!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void commit() {
		try {
			execUpdateSQL("END TRANSACTION");
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to end transaction!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void rollback() {
		try {
			execUpdateSQL("ROLLBACK");
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to rollback");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void addManufacturer(Manufacturer m) {
		try {
			final String sql1 = "INSERT INTO " + DBContract.ManufacturerEntry.TABLE_NAME + " ( " +
					DBContract.ManufacturerEntry.NAME + ") "
					+ "VALUES ( " +
					"'" + m.getManufacturerName() + "') ";
			execUpdateSQL(sql1);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add manufacturer");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addDiscountToQDS(Agreement agr, CataloguedItem ct, int minQuantity, double discount) {
		try {
			final String sql = "INSERT INTO " + DBContract.AgreementItemDiscountsEntry.TABLE_NAME + " (" +
					DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID + ", " +
					DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM + ", " +
					DBContract.AgreementItemDiscountsEntry.MIN_QUANTITY + ", " +
					DBContract.AgreementItemDiscountsEntry.DISCOUNT + ")"
					+ " VALUES " +
					"(" + agr.getAgreementId() + ", " +
					ct.getCatalogueNumber() + ", " +
					minQuantity + ", " +
					discount + ")";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add qds!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addItem(Item i) {
		try {
			final String sql1 = "INSERT INTO " + DBContract.ItemEntry.TABLE_NAME + " ( " +
					DBContract.ItemEntry.ITEM_NAME + ", " +
					DBContract.ItemEntry.MANUFACTURER_NAME + ") "
					+ "VALUES ( " +
					"'" + i.getItemName() + "', " +
					"'" + i.getManufacturer().getManufacturerName() + "') ";
			execUpdateSQL(sql1);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add item");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addSupplier(Supplier s) {
		try {
			final String sql1 = "INSERT INTO " + DBContract.SupplierCardEntry.TABLE_NAME + " ( " +
					DBContract.SupplierCardEntry.SERIAL_NUMBER + ", " +
					DBContract.SupplierCardEntry.NAME + ", " +
					DBContract.SupplierCardEntry.ADDRESS + ", " +
					DBContract.SupplierCardEntry.PAYMENT_TERMS + ", " +
					DBContract.SupplierCardEntry.BANK_ACCOUNT + ") "
					+ "VALUES ( " +
					s.getCard().getCardNumber() + ", " +
					"'" + s.getSupplierName() + "', " +
					"'" + s.getCard().getAddress() + "', " +
					"'" + s.getCard().getPaymentTerms().toString() + "', " +
					"'" + s.getCard().getBankAccount() + "') ";
			execUpdateSQL(sql1);
			
			if (s.getCard().getContacts() != null)
				for (SupplierContact contact : s.getCard().getContacts())
					if (contact != null)
						addContact(s, contact);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add supplier card");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addCatalougeItem(CataloguedItem ci) {
		try {
			final String sql = "INSERT INTO " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + " ( " +
					DBContract.SupplierCatalougeItemEntry.ITEM_NAME + ", " +
					DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME + ", " +
					DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER + ", " +
					DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID + ") "
					+ "VALUES ( " +
					"'" + ci.getItem().getItemName() + "', " +
					"'" + ci.getItem().getManufacturer().getManufacturerName() + "', " +
					ci.getSupplier().getCard().getCardNumber() + ", " +
					ci.getCatalogueNumber() + ") ";
			execUpdateSQL(sql);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add catalouge item");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addContact(Supplier s, SupplierContact c) {
		try {
			final String sql = "INSERT INTO " + DBContract.ContactEntry.TABLE_NAME + " ( " +
					DBContract.ContactEntry.EMAIL + ", " +
					DBContract.ContactEntry.NAME + ", " +
					DBContract.ContactEntry.SUPPLIER_ID + ") "
					+ "VALUES (" +
					"'" + c.getEmail() + "', " +
					"'" + c.getName() + "', " +
					"'" + s.getCard().getCardNumber() + "')";
			execUpdateSQL(sql);
			
			if (c.getPhoneNumbers() != null)
				for (String phoneNumber : c.getPhoneNumbers())
					addContactPhoneNumber(c, phoneNumber);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add contact");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			System.out.println("ERROR! Failed to add contact");
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	public void addItemToAgreement(Agreement agr, CataloguedItem item, double price) {
		try {
			final String sql = "INSERT INTO " + DBContract.AgreementItemsEntry.TABLE_NAME + " ( " +
					DBContract.AgreementItemsEntry.AGREEMENT_ID + ", " +
					DBContract.AgreementItemsEntry.CATALOUGE_NUM + ", " +
					DBContract.AgreementItemsEntry.PRICE + ") "
					+ "VALUES (" +
					agr.getAgreementId() + ", " +
					item.getCatalogueNumber() + ", " +
					price + ") ";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add item to agreement!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addContactPhoneNumber(SupplierContact c, String phoneNumber) {
		try {
			final String sql = "INSERT INTO " + DBContract.ContactPhonesEntry.TABLE_NAME + " ( " +
					DBContract.ContactPhonesEntry.EMAIL + ", " +
					DBContract.ContactPhonesEntry.PHONE + ")"
					+ " VALUES ( " +
					"'" + c.getEmail() + "', " +
					"'" + phoneNumber + "') ";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add contact phone number!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * adds only the agreement entry into the database.
	 * requires the supplier serial number & isTransportationBySupplier
	 */
	public void addAgreement(Supplier signedSupplier, Agreement agr) {
		try {
			final String sql = "INSERT INTO " + DBContract.AgreementEntry.TABLE_NAME + " ( " +
					DBContract.AgreementEntry.SUPPLIER_SERIAL_NUM + ", " +
					DBContract.AgreementEntry.IS_TRANSPORT_BY_SUPPLIER + ") "
					+ "VALUES (" +
					"'" + signedSupplier.getCard().getCardNumber() + "', " +
					(agr.isTransportationBySupplier() ? 1 : 0) + ") ";
			
			execUpdateSQL(sql);
			agr.setAgreementId(getLatestAgreementId());
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add agreement! values: " + signedSupplier.getCard().getCardNumber() +
					", " + agr.isTransportationBySupplier());
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void addAgreementSupplyDay(Agreement agr, int day) {
		try {
			final String sql = "INSERT INTO " + DBContract.AgreementDaysEntry.TABLE_NAME + " ( " +
					DBContract.AgreementDaysEntry.AGREEMENT_ID + ", " +
					DBContract.AgreementDaysEntry.DAY + ") "
					+ "VALUES ( " +
					agr.getAgreementId() + ", " +
					day + ") ";
			execUpdateSQL(sql);
			
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to add agreement day");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void changeContactMail(SupplierContact c, String newMail) {
		try {
			final String sql = "UPDATE " + DBContract.ContactEntry.TABLE_NAME +
					" SET " + DBContract.ContactEntry.EMAIL +
					" = '" + newMail + "'" +
					" WHERE " + DBContract.ContactEntry.EMAIL +
					" = '" + c.getEmail() + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to update contact mail");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void changeItemName(Item item, String name) {
		try {
			final String sql = "UPDATE " + DBContract.ItemEntry.TABLE_NAME +
					" SET " + DBContract.ItemEntry.ITEM_NAME +
					" = '" + name + "'" +
					" WHERE " + DBContract.ItemEntry.ITEM_NAME +
					" = '" + item.getItemName() + "' AND " +
					DBContract.ItemEntry.MANUFACTURER_NAME +
					" = '" + item.getManufacturer().getManufacturerName() + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to update item name!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void removeContact(Supplier s, SupplierContact c) {
		try {
			final String sql = "DELETE FROM " + DBContract.ContactEntry.TABLE_NAME +
					" WHERE " + DBContract.ContactEntry.EMAIL +
					" = '" + c.getEmail() + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete supplier");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeContactPhoneNumber(SupplierContact c, String phoneNumber) {
		try {
			final String sql = "DELETE FROM " + DBContract.ContactPhonesEntry.TABLE_NAME +
					" WHERE " + DBContract.ContactPhonesEntry.EMAIL +
					" = '" + c.getEmail() + "' AND " +
					DBContract.ContactPhonesEntry.PHONE +
					" = '" + phoneNumber + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete a contact phone!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeSupplierAgreement(Supplier s, Agreement agr) {
		try {
			final String sql = "DELETE FROM " + DBContract.AgreementEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementEntry.ID +
					" = " + agr.getAgreementId();
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete agreement!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeItem(Item item) {
		try {
			final String sql = "DELETE FROM " + DBContract.ItemEntry.TABLE_NAME +
					" WHERE " + DBContract.ItemEntry.ITEM_NAME +
					" = '" + item.getItemName() + "' AND " +
					DBContract.ItemEntry.MANUFACTURER_NAME +
					" = '" + item.getManufacturer().getManufacturerName() + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete item!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeCatalougeItem(CataloguedItem item) {
		try {
			final String sql = "DELETE FROM " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME +
					" WHERE " + DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID +
					" = " + item.getCatalogueNumber() + " AND " +
					DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER +
					" = " + item.getSupplier().getCard().getCardNumber();
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete catalouge item!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeItemFromAgreement(Agreement agr, int catalogueNumber) {
		try {
			final String sql = "DELETE FROM " + DBContract.AgreementItemsEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementItemsEntry.CATALOUGE_NUM +
					" = " + catalogueNumber;
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete item from agreement!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeDiscountToQDS(Agreement agr, CataloguedItem ct, int minQuantity, double discount) {
		try {
			final String sql = "DELETE FROM " + DBContract.AgreementItemDiscountsEntry.TABLE_NAME + " WHERE " +
					DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID +
					" = " + agr.getAgreementId() + " AND " +
					DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM +
					" = " + ct.getCatalogueNumber() + " AND " +
					DBContract.AgreementItemDiscountsEntry.MIN_QUANTITY +
					" = " + minQuantity + " AND " +
					DBContract.AgreementItemDiscountsEntry.DISCOUNT +
					" = " + discount;
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete qds!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeDayFromAgreement(Agreement agr, int day) {
		try {
			final String sql = "DELETE FROM " + DBContract.AgreementDaysEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementDaysEntry.DAY +
					" = " + day;
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete day from agreement!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeSupplier(Supplier s) {
		try {
			final String sql = "DELETE FROM " + DBContract.SupplierCardEntry.TABLE_NAME +
					" WHERE " + DBContract.SupplierCardEntry.SERIAL_NUMBER +
					" = " + s.getCard().getCardNumber();
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete supplier");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeManufacturer(Manufacturer manufacturer) {
		try {
			final String sql = "DELETE FROM " + DBContract.ManufacturerEntry.TABLE_NAME +
					" WHERE " + DBContract.ManufacturerEntry.NAME +
					" = '" + manufacturer.getManufacturerName() + "'";
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete manufacturer");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void removeAgreement(int agreementId) {
		try {
			final String sql = "DELETE FROM " + DBContract.AgreementEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementEntry.ID +
					" = " + agreementId;
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to delete agreement");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public Manufacturer[] getManufacturers() {
		try {
			String sql = "SELECT * FROM " + DBContract.ManufacturerEntry.TABLE_NAME;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Manufacturer> m = new LinkedList<Manufacturer>();
			while (rs.next()) {
				m.add(new Manufacturer(rs.getString(DBContract.ManufacturerEntry.NAME)));
			}
			rs.close();
			stmt.close();
			Manufacturer[] arr = new Manufacturer[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving manufacturers!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Item[] getItems() {
		try {
			String sql = "SELECT * FROM " + DBContract.ItemEntry.TABLE_NAME;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Item> m = new LinkedList<Item>();
			while (rs.next()) {
				m.add(new Item(rs.getString(DBContract.ItemEntry.ITEM_NAME),
						new Manufacturer(rs.getString(DBContract.ItemEntry.MANUFACTURER_NAME))));
			}
			rs.close();
			stmt.close();
			Item[] arr = new Item[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving items!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Item[] getItems(String manName) {
		try {
			String sql = "SELECT * FROM " + DBContract.ItemEntry.TABLE_NAME +
					" WHERE " + DBContract.ItemEntry.MANUFACTURER_NAME + " = '" + manName + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Item> m = new LinkedList<Item>();
			while (rs.next()) {
				m.add(new Item(rs.getString(DBContract.ItemEntry.ITEM_NAME),
						new Manufacturer(rs.getString(DBContract.ItemEntry.MANUFACTURER_NAME))));
			}
			rs.close();
			stmt.close();
			Item[] arr = new Item[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving items!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Supplier[] getSuppliers() {
		try {
			String sql = "SELECT * FROM " + DBContract.SupplierCardEntry.TABLE_NAME;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Supplier> m = new LinkedList<Supplier>();
			while (rs.next()) {
				SupplierCard sc = new SupplierCard(rs.getString(DBContract.SupplierCardEntry.NAME),
						rs.getInt(DBContract.SupplierCardEntry.SERIAL_NUMBER),
						rs.getString(DBContract.SupplierCardEntry.ADDRESS),
						rs.getInt(DBContract.SupplierCardEntry.BANK_ACCOUNT),
						SupplierCard.PaymentTerm.valueOf(rs.getString(DBContract.SupplierCardEntry.PAYMENT_TERMS)),
						null);
				m.add(new Supplier(sc));
			}
			rs.close();
			stmt.close();
			Supplier[] arr = new Supplier[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving suppliers!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public SupplierContact[] getContacts(Supplier s) {
		try {
			String sql = "SELECT * FROM " + DBContract.ContactEntry.TABLE_NAME +
					" WHERE " + DBContract.ContactEntry.SUPPLIER_ID + " = " + s.getCard().getCardNumber();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<SupplierContact> m = new LinkedList<SupplierContact>();
			while (rs.next()) {
				m.add(new SupplierContact(rs.getString(DBContract.ContactEntry.NAME),
						rs.getString(DBContract.ContactEntry.EMAIL)));
			}
			rs.close();
			stmt.close();
			SupplierContact[] arr = new SupplierContact[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving contacts!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public String[] getContactsPhones(String email) {
		try {
			String sql = "SELECT * FROM " + DBContract.ContactPhonesEntry.TABLE_NAME +
					" WHERE " + DBContract.ContactPhonesEntry.EMAIL + " = '" + email + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<String> m = new LinkedList<String>();
			while (rs.next()) {
				m.add(rs.getString(DBContract.ContactPhonesEntry.PHONE));
			}
			rs.close();
			stmt.close();
			String[] arr = new String[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving contact phones!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Agreement[] getAgreements(int supplierNum) {
		try {
			String sql = "SELECT * FROM " + DBContract.AgreementEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementEntry.SUPPLIER_SERIAL_NUM + " = " + supplierNum;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Agreement> m = new LinkedList<Agreement>();
			while (rs.next()) {
				QDS qds = new QDS();
				ItemPricing[] itemPricings = getAgreementsCatalougeItems(rs.getInt(DBContract.AgreementEntry.ID));
				for (ItemPricing ip : itemPricings) {
					DiscountTerm[] discountTerms = getAgreementsItemsDiscount(rs.getInt(DBContract.AgreementEntry.ID), ip.getCatalougeItem().getCatalogueNumber());
					for (DiscountTerm dt : discountTerms)
						qds.addDiscount(ip.getCatalougeItem(), dt.getMinQuantity(), dt.getDiscount());
				}
				
				m.add(new Agreement(rs.getInt(DBContract.AgreementEntry.ID),
						null,
						qds,
						false,
						rs.getInt(DBContract.AgreementEntry.IS_TRANSPORT_BY_SUPPLIER) == 1));
			}
			rs.close();
			stmt.close();
			Agreement[] arr = new Agreement[m.size()];
			m.toArray(arr);
			return arr;
		} catch (Exception e) {
			System.out.println("ERROR! Failed retreiving contacts!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Integer[] getAgreementSupplyDays(int agreementId) {
		try {
			String sql = "SELECT * FROM " + DBContract.AgreementDaysEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementDaysEntry.AGREEMENT_ID + " = " + agreementId;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<Integer> m = new LinkedList<Integer>();
			while (rs.next()) {
				m.add(rs.getInt(DBContract.AgreementDaysEntry.DAY));
			}
			rs.close();
			stmt.close();
			Integer[] arr = new Integer[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving agreement days!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public CataloguedItem[] getSuppliersCatalougeItems(int supplierNum) {
		try {
			String sql = "SELECT * FROM " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME +
					" WHERE " + DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER + " = " + supplierNum;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<CataloguedItem> m = new LinkedList<CataloguedItem>();
			while (rs.next()) {
				Item item = new Item(rs.getString(DBContract.SupplierCatalougeItemEntry.ITEM_NAME),
						new Manufacturer(rs.getString(DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME)));
				m.add(new CataloguedItem(item,
						rs.getInt(DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID),
						null));
			}
			rs.close();
			stmt.close();
			CataloguedItem[] arr = new CataloguedItem[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving supplier items!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public CataloguedItem[] getItemCatalougeItems(String name, Manufacturer man) {
		try {
			String sql = "SELECT * FROM " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME +
					" JOIN " + DBContract.SupplierCardEntry.TABLE_NAME +
					" ON " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + "." + DBContract.SupplierCatalougeItemEntry.SERIAL_NUMBER +
					" = " + DBContract.SupplierCardEntry.TABLE_NAME + "." + DBContract.SupplierCardEntry.SERIAL_NUMBER +
					" WHERE " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + "." + DBContract.SupplierCatalougeItemEntry.ITEM_NAME + " = '" + name + "'" +
					" AND " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + "." + DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME + " = '" + man.getManufacturerName() + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<CataloguedItem> m = new LinkedList<CataloguedItem>();
			while (rs.next()) {
				Item item = new Item(rs.getString(DBContract.SupplierCatalougeItemEntry.ITEM_NAME),
						new Manufacturer(rs.getString(DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME)));
				m.add(new CataloguedItem(item,
						rs.getInt(DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID),
						new Supplier(new SupplierCard(rs.getString(DBContract.SupplierCardEntry.NAME),
								rs.getInt(DBContract.SupplierCardEntry.SERIAL_NUMBER),
								rs.getString(DBContract.SupplierCardEntry.ADDRESS),
								rs.getInt(DBContract.SupplierCardEntry.BANK_ACCOUNT),
								SupplierCard.PaymentTerm.valueOf(rs.getString(DBContract.SupplierCardEntry.PAYMENT_TERMS)),
								null))));
			}
			rs.close();
			stmt.close();
			CataloguedItem[] arr = new CataloguedItem[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving supplier items!");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public ItemPricing[] getAgreementsCatalougeItems(int agreementId) {
		try {
			String sql = "SELECT * FROM " + DBContract.AgreementItemsEntry.TABLE_NAME +
					" JOIN " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME +
					" ON " + DBContract.AgreementItemsEntry.TABLE_NAME + "." + DBContract.AgreementItemsEntry.CATALOUGE_NUM +
					" = " + DBContract.SupplierCatalougeItemEntry.TABLE_NAME + "." + DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID +
					" WHERE " + DBContract.AgreementItemsEntry.AGREEMENT_ID + " = " + agreementId;
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<ItemPricing> m = new LinkedList<ItemPricing>();
			while (rs.next()) {
				Item item = new Item(rs.getString(DBContract.SupplierCatalougeItemEntry.ITEM_NAME),
						new Manufacturer(rs.getString(DBContract.SupplierCatalougeItemEntry.MANUFACTURER_NAME)));
				CataloguedItem ci = new CataloguedItem(item,
						rs.getInt(DBContract.SupplierCatalougeItemEntry.CATALOUGE_ID), null);
				m.add(new ItemPricing(ci, rs.getDouble(DBContract.AgreementItemsEntry.PRICE)));
			}
			rs.close();
			stmt.close();
			ItemPricing[] arr = new ItemPricing[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving agreement items!");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			// doesn't happen
		}
		return null;
	}
	
	public DiscountTerm[] getAgreementsItemsDiscount(int agreementId, int ci) {
		try {
			String sql = "SELECT * FROM " + DBContract.AgreementItemDiscountsEntry.TABLE_NAME +
					" WHERE " + DBContract.AgreementItemDiscountsEntry.AGREEMENT_ID + " = " + agreementId + " AND " +
					DBContract.AgreementItemDiscountsEntry.CATALOUGE_NUM + " = " + ci;
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			LinkedList<DiscountTerm> m = new LinkedList<DiscountTerm>();
			while (rs.next()) {
				m.add(new DiscountTerm(rs.getInt(DBContract.AgreementItemDiscountsEntry.MIN_QUANTITY),
						rs.getDouble(DBContract.AgreementItemDiscountsEntry.DISCOUNT)));
			}
			rs.close();
			stmt.close();
			DiscountTerm[] arr = new DiscountTerm[m.size()];
			m.toArray(arr);
			return arr;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving agreement items discounts!");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			// doesn't happen
		}
		return null;
	}
	
	public int getLatestAgreementId() {
		try {
			String sql = "SELECT * FROM " + DBContract.AgreementEntry.TABLE_NAME +
					" ORDER BY " + DBContract.AgreementEntry.ID + " DESC";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int ret = rs.getInt(DBContract.AgreementEntry.ID);
				rs.close();
				stmt.close();
				return ret;
			} else return -1;
		} catch (SQLException e) {
			System.out.println("ERROR! Failed retreiving last agreement id!");
			e.printStackTrace();
			System.exit(0);
		}
		return -1;
	}
	
	public void changeSupplier(Supplier s) {
		try {
			final String sql = "UPDATE " + DBContract.SupplierCardEntry.TABLE_NAME +
					" SET " + DBContract.SupplierCardEntry.NAME +
					" = '" + s.getCard().getSupplierName() + "', " +
					DBContract.SupplierCardEntry.BANK_ACCOUNT +
					" = " + s.getCard().getBankAccount() + "," +
					DBContract.SupplierCardEntry.PAYMENT_TERMS +
					" = '" + s.getCard().getPaymentTerms() + "'" +
					" WHERE " + DBContract.SupplierCardEntry.SERIAL_NUMBER +
					" = " + s.getCard().getSerial_no();
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to update supplier!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void changeAgreementDeliverySpecs(Agreement agr, boolean isDeliveryBySupplier) {
		try {
			final String sql = "UPDATE " + DBContract.AgreementEntry.TABLE_NAME +
					" SET " + DBContract.AgreementEntry.IS_TRANSPORT_BY_SUPPLIER +
					" = " + (isDeliveryBySupplier ? 1 : 0) +
					" WHERE " + DBContract.AgreementEntry.ID +
					" = " + agr.getAgreementId();
			execUpdateSQL(sql);
		} catch (SQLException e) {
			System.out.println("ERROR! Failed to update agreement delivery specs!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public Item[] getDetailedItems() {
		/*
		 * return all items 
		 * containing all corresponding catalougeItems
		 * containing all supplier
		 * containing all corresponding agreements
		 * containing all catalougeItems
		 * containing all discounts
		 */
		
		Item[] items = getItems();
		for (Item i : items) {
			CataloguedItem[] catalougeItems = getItemCatalougeItems(i.getItemName(), i.getManufacturer());
			for (CataloguedItem ci : catalougeItems) {
				i.addCatalougeItem(ci);
				Agreement[] agreements = getAgreements(ci.getSupplier().getCard().getCardNumber());
				for (Agreement a : agreements) {
					ci.getSupplier().addAgreement(a);
					ItemPricing[] agreementCatalougeItem = getAgreementsCatalougeItems(a.getAgreementId());
					for (ItemPricing ip : agreementCatalougeItem) {
						a.addItem(ip.getCatalougeItem(), ip.getPrice());
					}
				}
			}
		}
		
		return items;
	}
	
	
	public boolean PutItems(List<Product> toAdd) {
		String query = "REPLACE into ProductInStock (ID,ExpirationDate,AmountInStock) VALUES ";
		int i = 1;
		for (Product p : toAdd) {
			Calendar d = p.getExpirationDate();
			String Month = ((d.get(Calendar.MONTH) + 1) + "").length() == 1 ? "0" + (d.get(Calendar.MONTH) + 1) : (d.get(Calendar.MONTH) + 1) + "";
			String Day = ((d.get(Calendar.DATE)) + "").length() == 1 ? "0" + (d.get(Calendar.DATE)) : (d.get(Calendar.DATE)) + "";
			query += "(" + p.getID() + ",date('" + (d.get(Calendar.YEAR)) + "-" + Month + "-" + Day + "'), COALESCE((SELECT AmountInStock FROM ProductInStock WHERE id = " + p.getID() + " AND ExpirationDate=" + "date('" + (d.get(Calendar.YEAR)) + "-" + Month + "-" + Day + "')), 0)+" + p.getAmount() + " " + ((toAdd.size() != i) ? "), " : ") ");
			i++;
		}
		ExecuteNonQuery(query + ";");
		return true;
	}
	
	
	public boolean TakeItems(Product p, int amount) {
		int stockCount = ExecuteScalarQuery("Select SUM(AmountInStock) From ProductInStock Where ID=" + p.getID() + " GROUP BY ID;");
		if (amount <= stockCount && stockCount != -1) {
			while (amount > 0) {
				int currStock = ExecuteScalarQuery("SELECT AmountInStock,ExpirationDate FROM ProductInStock WHERE ID= " + p.getID() + " GROUP BY ID HAVING ExpirationDate=MIN(ExpirationDate);");
				if (currStock > amount)
					ExecuteScalarQuery("Update ProductInStock Set AmountInStock = AmountInStock - " + amount + " Where ID=" + p.getID() + " AND ExpirationDate IN (Select MIN(ExpirationDate) FROM (SELECT * FROM ProductInStock) As PIS Where PIS.ID=" + p.getID() + " GROUP BY PIS.ID);");
				if (currStock <= amount)
					ExecuteNonQuery("Delete From ProductInStock Where ID=" + p.getID() + " AND ExpirationDate IN (Select MIN(ExpirationDate) FROM (SELECT * FROM ProductInStock) As PIS Where PIS.ID=" + p.getID() + " GROUP BY PIS.ID);");
				amount = amount - currStock;
			}
			return true;
		} else
			return false;
	}
	
	public StockReport CheckStock() {
		@SuppressWarnings({"rawtypes"})
		List lst = ExecuteList("SELECT " + DBContract.ItemEntry.ITEM_ID + ",DATE('10-10-1000'),(" + DBContract.ItemEntry.ITEM_MaxInStock + "-stock-" + DBContract.ItemEntry.ITEM_EXPECTED + ")"
				+ " FROM Product LEFT OUTER JOIN (SELECT ID as IDTEMP,SUM(AmountInStock) as stock FROM ProductInStock GROUP BY IDTEMP) ON Product.ID=IDTEMP WHERE stock+Product.Expected < MinInStock OR stock is NULL", 0);
		
		StockReport sr = new StockReport();
		
		for (Object o : lst) {
			Product p = (Product) o;
			sr.addItem(new Item(p.getID()), p.getAmount());
		}
		if(sr.isEmpty())
			return null;
		return sr;
		
	}
	
	public void makeOrders(StockReport sr)
	{
		for(NeededItem item : sr.readItems())
		{
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				statement = conn.createStatement();
				if(statement.execute("SELECT Product.ID,Product.name,agreement_items.price,discount,discount*price*"+item.getQuantity()+" as finalPrice,agreements.supplier_serial_number,agreements.ID FROM (((( Product join supplier_catalouge_item on Product.ID=supplier_catalouge_item.serial_number) "
						+ " join agreements on agreements.supplier_serial_number=supplier_catalouge_item.serial_number) join agreement_items on agreement_items.catalouge_num) "
						+ " join agreement_item_discount on agreement_item_discount.agreement_id=agreements.id AND "
						+ " agreement_items.catalouge_num=agreement_item_discount.catalouge_num AND agreement_item_discount.min_quantity <= "+item.getQuantity()+") "
						+ " WHERE Product.ID="+item.getItem().getID()+""
						+ " GROUP BY Product.ID  HAVING discount*price = MIN(discount*price)"))
				{
					resultSet = statement.getResultSet();
					if(resultSet.isClosed())
						continue;
					int ID = resultSet.getInt("ID");
					String supplier_serial_number = resultSet.getString("supplier_serial_number");
					int Amount = item.getQuantity();
					int CatalogPrice = resultSet.getInt("price");
					double discount = resultSet.getDouble("discount");
					double finalPrice = resultSet.getDouble("finalPrice");
					String Name = resultSet.getString("Name");
					int expected = ExecuteScalarQuery("SELECT Expected FROM Product WHERE ID="+ID);
					if(expected > 0)
						continue;
					int OrdID = ExecuteScalarQuery("SELECT OrdID FROM Orders WHERE SupID="+supplier_serial_number+" AND OrderDate="+getNormalDate()+"");
					if(OrdID==-1)
					{
						//Make new order
						execUpdateSQL("INSERT INTO Orders (SupID,SupName,SupAddress,OrderDate,Contact) "
								+ "SELECT supplier_cards.serial_number,supplier_cards.name,supplier_cards.address,"+getNormalDate()+",email FROM supplier_cards LEFT OUTER JOIN contacts on serial_number=supplier_id"
								+" WHERE serial_number="+supplier_serial_number);

						OrdID = ExecuteScalarQuery("SELECT OrdID FROM Orders WHERE SupID="+supplier_serial_number+" AND OrderDate="+getNormalDate()+"");
					}
					
					resultSet.close();
					execUpdateSQL("INSERT OR IGNORE INTO ItemsInOrder (OID,ID,Name,Amount,CatalogPrice,Discount,FinalPrice) VALUES"
							+ " ("+OrdID+","+ID+",'"+Name+"',"+Amount+","+CatalogPrice+","+discount+","+finalPrice+")");




                    int source = ExecuteScalarQuery("SELECT ID FROM Place,supplier_cards WHERE Place.address=supplier_cards.address AND supplier_cards.serial_number="+supplier_serial_number);
                    int dest = ExecuteScalarQuery("SELECT ID FROM Place WHERE Place.address='SuperLee'");
                    OrderToTransport ott = new OrderToTransport(OrdID, LocalDate.now(), LocalTime.parse("10:00") ,source,dest,new NeededItem(new Item(ID),Amount));
                    if(!NewTransport.addOutcomingTransport(ott))
					{
						ott = new OrderToTransport(OrdID, LocalDate.now(), LocalTime.parse("16:00") ,source,dest,new NeededItem(new Item(ID),Amount));
						if(!NewTransport.addOutcomingTransport(ott))
						{
							storeOrder(OrdID);
						}
						else
						{
							execUpdateSQL("UPDATE Product SET Expected=Expected+" + Amount + " WHERE ID=" + ID);
							execUpdateSQL("UPDATE Orders SET Backed=0 WHERE OrdID="+OrdID);
						}
					}
					else
					{
						execUpdateSQL("UPDATE Product SET Expected=Expected+" + Amount + " WHERE ID=" + ID);
						execUpdateSQL("UPDATE Orders SET Backed=0 WHERE OrdID="+OrdID);
					}
				}
				else
                {
                    int i = 5;
                }

				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException sqlEx) {
					}
				}// ignore
				
				statement = null;
			}
		}
	}
	public void retryOrders()
	{
		List<Product> lst = ExecuteList("SELECT ID,Amount FROM ItemsInOrder WHERE OID IN (SELECT OrdID FROM Orders WHERE Backed=1)",3);
		StockReport sr = new StockReport();
		for(Product p : lst)
		{
			sr.addItem(new Item(p.getID()),p.getAmount());
		}
		ExecuteNonQuery("DELETE FROM ItemsInOrder WHERE OID IN (SELECT OrdID FROM Orders WHERE Backed=1)");
		ExecuteNonQuery("DELETE FROM Orders WHERE Backed=1");
		makeOrders(sr);

	}

	private void storeOrder(int ordID)
	{
		try
		{
			execUpdateSQL("UPDATE Orders SET Backed=1 WHERE OrdID="+ordID);

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


	private void ExecuteNonQuery(String queryString) {
		Statement statement = null;
		try {
			statement = conn.createStatement();
			
			statement.execute(queryString);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
					System.out.println("Problem");
				}
			}// ignore
			
			statement = null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	
	private List ExecuteList(String queryString, int type) {
		Statement statement = null;
		ResultSet resultSet = null;
        List answer;
        switch (type){
		case(3):
        case(0):
            answer = new LinkedList<Product>();
            break;
        case(1):
            answer = new LinkedList<Category>();
            break;
        case(2):
            answer = new LinkedList<Integer>();
            break;
        default:
            return null;
        }
		try {
			statement = conn.createStatement();
			
			if (statement.execute(queryString))
				resultSet = statement.getResultSet();
			
			generateListFromResultSet(resultSet, type, answer);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
		
		return answer;
	}
	
	private String ExecuteQuery(String queryString) {
		Statement statement = null;
		ResultSet resultSet = null;
		String answer = "";
		try {
			statement = conn.createStatement();
			
			if (statement.execute(queryString))
				resultSet = statement.getResultSet();
			
			answer = generateStringFromResultSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
		return answer;
	}
	
	public void UpdateFaulty() {
		ExecuteNonQuery("INSERT INTO faultyproduct SELECT * FROM ProductInStock WHERE ProductInStock.ExpirationDate<="+getNormalDate()+"; ");
		ExecuteNonQuery("DELETE FROM ProductInStock WHERE ProductInStock.ExpirationDate<="+getNormalDate()+";");
	}
	
	private int ExecuteScalarQuery(String queryString) {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = conn.createStatement();
			if (statement.execute(queryString))
				resultSet = statement.getResultSet();
			else
				return -1;
			int columns = resultSet.getMetaData().getColumnCount();
			StringBuilder message = new StringBuilder();
			if (columns == 0)
				return -1;
			if (resultSet.isClosed())
				return -1;
			resultSet.next();
			
			message.append(resultSet.getString(1));
			
			
			return Integer.parseInt(message.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			
			return -1;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
	}
	private double ExecuteDoubleQuery(String queryString) {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = conn.createStatement();
			if (statement.execute(queryString))
				resultSet = statement.getResultSet();
			else
				return -1;
			int columns = resultSet.getMetaData().getColumnCount();
			StringBuilder message = new StringBuilder();
			if (columns == 0)
				return -1;
			if (resultSet.isClosed())
				return -1;
			resultSet.next();
			
			message.append(resultSet.getString(1));
			
			
			return Double.parseDouble(message.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
	}
	public String printEntireStock() throws Exception {
		String message = ExecuteQuery("Select Product.ID,Product.Name,Product.MinInStock,Product.Maker,Categories.Name,ExpirationDate,amountinstock "
				+ "from product LEFT OUTER JOIN productinstock pis ON Product.ID = pis.ID,Categories "
				+ "WHERE Product.Category = Categories.ID;");
		System.out.println("ItemID" + "\t|\t" + "Name" + "\t|\t" + "MinInStock" + "\t|\tMaker\t|\t" + "Cat" + "\t|\t" + "Expires\t" + "\t|\t" + "Stock\t|");
		System.out.println(message);
		return message;
	}
	
	private String generateStringFromResultSet(ResultSet rs) throws SQLException {
		int columns = rs.getMetaData().getColumnCount();
		StringBuilder message = new StringBuilder();
		if (columns == 0)
			return "";
		while (rs.next()) {
			for (int i = 1; i <= columns; i++) {
				String tmp = rs.getString(i);
				if (rs.wasNull())
					message.append("0\t|\t");
				else
					message.append(tmp + "\t|\t");
			}
			message.append("\n");
		}
		return message.toString();
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void generateListFromResultSet(ResultSet rs, int type, List lst) throws SQLException {
		int columns = rs.getMetaData().getColumnCount();
		if (columns == 0)
			return;
		while (rs.next()) {
			if (type == 0) {

				Date temp = rs.getDate(2);
				Calendar c = Calendar.getInstance();
				if (!rs.wasNull())
					c.setTime(temp);
				lst.add(new Product(rs.getInt(1), c, rs.getInt(3)));
			}
			if (type == 1) {
				lst.add(new Category(rs.getInt(1), rs.getInt(3)));
			}
            if(type == 2)
            {
                lst.add(new Integer(rs.getInt("OrdID")));
            }
			if (type == 3) {
				Calendar c = Calendar.getInstance();
				lst.add(new Product(rs.getInt(1), c, rs.getInt(2)));
			}
		}
	}
	
	public String printWithSubCategories(int catID) {
		String ans = catID + " ";
		String answer;
		ans += printWithSubHelper(catID);
		System.out.println("ItemID" + "\t|\t" + "Name" + "\t|\t" + "MinInStock" + "\t|\t" + "Maker" + "\t|\t" + "Cat" + "\t|\t" + "Stock\t|");
		System.out.println((answer = ExecuteQuery("Select Product.ID,Product.Name,Product.MinInStock,Product.Maker,Categories.Name,amountinstock "
				+ "from product LEFT OUTER JOIN productinstock pis ON Product.ID = pis.ID,Categories "
				+ "WHERE Product.Category = Categories.ID AND (Category=" + ans + ");")));
		
		return answer;
	}
	
	private String printWithSubHelper(int catID) {
		String ans = "";
		if (!CategoryTree.getNodes().get(catID).getChildren().isEmpty()) {
			for (int i : CategoryTree.getNodes().get(catID).getChildren()) {
				ans += " OR Category=" + i + printWithSubHelper(i);
			}
			return ans;
		} else
			return "";
	}
	
	public void printCategory(int catID) {
		System.out.println("ItemID" + "\t|\t" + "Name" + "\t|\t" + "MinInStock" + "\t|\t" + "Maker	" + "|\t" + "Cat" + "\t|\t" + "Stock\t|");
		System.out.println(ExecuteQuery("Select Product.ID,Product.Name,Product.MinInStock,Product.Maker,Categories.Name,amountinstock "
				+ "from product LEFT OUTER JOIN productinstock pis ON Product.ID = pis.ID,Categories "
				+ "WHERE Product.Category = Categories.ID AND (Category=" + catID + ");"));
	}
	
	public String listCategories() {
		String ans;
		System.out.println(ans = ExecuteQuery("Select ID,Name from categories;"));
		return ans;
	}
	
	public int countCategories() {
		int i = 0;
		String ans = "";
		String IDstring = ExecuteQuery("Select Count(ID) from Categories;");
		if (IDstring.length() == 0)
			return -1;
		while (Character.isDigit(IDstring.charAt(i))) {
			ans += IDstring.charAt(i);
			i++;
		}
		
		return Integer.parseInt(ans);
	}
	
	public int getIDfromName(String name) {
		int i = 0;
		String ans = "";
		String IDstring = ExecuteQuery("Select ID from Product WHERE Name=\"" + name + "\";");
		if (IDstring.length() == 0)
			return -1;
		while (Character.isDigit(IDstring.charAt(i))) {
			ans += IDstring.charAt(i);
			i++;
		}
		
		return Integer.parseInt(ans);
	}
	
	
	public int logIn(String Username, String Password) {
		Statement statement = null;
		ResultSet resultSet = null;
		String password = "";
		int clearence = -1;
		try {
			statement = conn.createStatement();
			
			if (statement.execute("SELECT Password,Clearence FROM Users WHERE Username='" + Username + "';")) {
				resultSet = statement.getResultSet();
				while (resultSet.next()) {
					password = resultSet.getString(1);
					clearence = resultSet.getInt(2);
				}
				if (Password.equals(password))
					return clearence;
				else
					return -1;
			} else
				return -1;
		} catch (SQLException e) {
			System.out.print("");
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
		return -1;
		
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void recreateTree() {
		CategoryTree = new Tree();
		List Categories = ExecuteList("Select * FROM Categories;", 1);
		Category[] CatArr = new Category[Categories.size()];
		Categories.toArray(CatArr);
		for (Category c : CatArr) {
			if (c.getParentID() == 0) {
				CategoryTree.addNode(c.getID(), c);
				Categories.remove(c);
			}
		}
		while (!Categories.isEmpty()) {
			Category c = (Category) Categories.remove(0);
			if (CategoryTree.getNodes().containsKey(c.getParentID()))
				CategoryTree.addNode(c.getID(), c.getParentID(), c);
			else
				Categories.add(c);
		}
	}
	
	public void checkForTimedOrder()
	{
		int day = Main.today.get(Calendar.DAY_OF_WEEK);
		day = (day+1)%7;
		Statement statement = null;
		ResultSet resultSet = null;
		System.out.println(getNormalDate());
		try {
			statement = conn.createStatement();
			int count = ExecuteScalarQuery("SELECT COUNT(agreement_id) FROM agreements join agreement_days ON id=agreement_id"
					+ " WHERE day="+day+" ORDER BY supplier_serial_number");
			if(count==0)
				return;
			if(statement.execute("SELECT agreement_id,supplier_serial_number FROM agreements join agreement_days ON id=agreement_id"
					+ " WHERE day="+day+" ORDER BY supplier_serial_number"))
			{
				resultSet = statement.getResultSet();
				
				int[][] agreementToSupplier = new int[count][2];
				int i=0;
				while(resultSet.next())
				{
					agreementToSupplier[i][0]=resultSet.getInt(1);
					agreementToSupplier[i][1]=resultSet.getInt(2);
					i++;
				}
				resultSet.close();
				resultSet=null;
				
				for (int j = 0; j < agreementToSupplier.length; j++)
				{
					count = ExecuteScalarQuery("SELECT COUNT(Product.ID) FROM Product join supplier_catalouge_item ON Product.ID=supplier_catalouge_item.serial_number"
									+ " join agreement_items ON agreement_items.catalouge_num=supplier_catalouge_item.catalouge_id"
									+ " WHERE MaxInStock>(Expected+MinInStock) AND agreement_items.agreement_id="+agreementToSupplier[j][0]+" AND serial_number="+agreementToSupplier[j][1]);
					if(count==0)
						continue;
					statement = conn.createStatement();
					if (statement.execute(
							"SELECT Product.ID,(MaxInStock-Expected-MinInStock) FROM Product join supplier_catalouge_item ON Product.ID=supplier_catalouge_item.serial_number"
									+ " join agreement_items ON agreement_items.catalouge_num=supplier_catalouge_item.catalouge_id"
									+ " WHERE MaxInStock>(Expected+MinInStock) AND agreement_items.agreement_id="+agreementToSupplier[j][0]+" AND serial_number="+agreementToSupplier[j][1]))
					{
						resultSet = statement.getResultSet();
						
						double[][] itemsToOrder = new double[count][6];
						i=0;
						StockReport sr = new StockReport();
						while(resultSet.next())
						{
							sr.addItem(new Item(resultSet.getInt(1)),resultSet.getInt(2));

							itemsToOrder[i][0]=resultSet.getInt(1);
							itemsToOrder[i][1]=resultSet.getInt(2);
							i++;
						}
						//Make the order

						makeOrders(sr);

//						i=0;
//						String[] productNames = new String[itemsToOrder.length];
//						for(double[] product : itemsToOrder)
//						{
//							if (statement.execute("SELECT Price,discount FROM agreement_items join agreement_item_discount ON agreement_items.agreement_id=agreement_item_discount.agreement_id "
//									+ " WHERE agreement_items.agreement_id="+agreementToSupplier[j][0]))
//							{
//								resultSet = statement.getResultSet();
//								product[2]=resultSet.getInt(1);
//								product[3]=resultSet.getDouble(2);
//								product[4]=product[2]*product[3]*product[1];
//							}
//							if (statement.execute("SELECT Name From Product WHERE ID="+product[0]))
//							{
//								resultSet = statement.getResultSet();
//								productNames[i]=resultSet.getString(1);
//								i++;
//							}
//
//						}
//						int OrdID = ExecuteScalarQuery("SELECT OrdID FROM Orders WHERE SupID="+agreementToSupplier[j][1]+" AND OrderDate="+getNormalDate()+"");
//						if(OrdID==-1)
//						{
//							//Make new order
//							execUpdateSQL("INSERT INTO Orders (SupID,SupName,SupAddress,OrderDate,Contact) "
//									+ "SELECT supplier_cards.serial_number,supplier_cards.name,supplier_cards.address,"+getNormalDate()+",email FROM supplier_cards LEFT OUTER JOIN contacts on serial_number=supplier_id"
//									+" WHERE serial_number="+agreementToSupplier[j][1]);
//
//							OrdID = ExecuteScalarQuery("SELECT OrdID FROM Orders WHERE SupID="+agreementToSupplier[j][1]+" AND OrderDate="+getNormalDate()+"");
//						}
//
//						for (int k = 0; k < itemsToOrder.length; k++)
//						{
//							execUpdateSQL("INSERT OR IGNORE INTO ItemsInOrder (OID,ID,Name,Amount,CatalogPrice,Discount,FinalPrice) VALUES"
//											+ " (" + OrdID + "," + itemsToOrder[k][0] + ",'" + productNames[k] + "'," + itemsToOrder[k][1] + "," + itemsToOrder[k][2]
//											+ "," + itemsToOrder[k][3] + "," + itemsToOrder[k][4] + ")");
//							execUpdateSQL("UPDATE Product SET Expected=Expected+" + itemsToOrder[k][1] + " WHERE ID=" + itemsToOrder[k][0]);
//						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
			}// ignore
			
			statement = null;
		}
	}

	public String printFaulty()
	{
		return ExecuteQuery("Select * From faultyProduct,Product WHERE Product.ID = faultyProduct.ID;");
	}

	public void clearFaulty()
	{
		ExecuteNonQuery("Delete From faultyProduct;");
	}

    public String printPendingOrders()
    {
        StringBuilder builder = new StringBuilder();
        List<Integer> orderIDs;
        Statement statement = null;
        ResultSet resultSet = null;
        orderIDs = ExecuteList("SELECT OrdID FROM Orders",2);
        if(orderIDs == null)
            return "There are no pending orders!";
        for(Integer i : orderIDs)
        {
            builder.append("Order Number: "+i+"\n");

            try {
                statement = conn.createStatement();

                if (statement.execute("SELECT ID,Name,Amount,CatalogPrice,Discount,FinalPrice FROM ItemsInOrder WHERE OID="+i))
                    resultSet = statement.getResultSet();


                int columns = resultSet.getMetaData().getColumnCount();
                if (columns == 0)
                    continue;
                builder.append("ID\t|\tName\t|\tAmount\t|\tCatalogPrice\t|\tDiscount\t|\tFinalPrice\t\n");
                while (resultSet.next())
                {
                    builder.append(resultSet.getInt("ID")+"\t|\t"+resultSet.getString("Name")+"\t|\t"+resultSet.getInt("Amount")+"\t\t|\t\t"+resultSet.getInt("CatalogPrice")+"\t\t\t|\t\t"+resultSet.getDouble("Discount")+"\t\t|\t\t"+resultSet.getInt("FinalPrice")+"\t\t|\n");
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException sqlEx) {
                    }
                }// ignore

                statement = null;
            }


        }
        return builder.toString();
    }

	public void insertTransportToWarehouse(int transportID)
	{
		List<Integer> orderIDs;
		Statement statement = null;
		ResultSet resultSet = null;
		orderIDs = ExecuteList("SELECT OrderID FROM TransOrder",2);
		if(orderIDs == null)
			return;
		for(Integer i : orderIDs)
		{
			try {
				statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);

				if (statement.execute("SELECT ItemID,NumOfItems FROM TransOrder WHERE TransportID="+transportID+" AND OrderID="+i))
					resultSet = statement.getResultSet();
				if(resultSet==null)
					continue;
				int columns = resultSet.getMetaData().getColumnCount();
				if (columns == 0)
				{
					resultSet.close();
					continue;
				}
				while (resultSet.next())
				{
					int NumOfItems = resultSet.getInt("NumOfItems");
					int ItemID =resultSet.getInt("ItemID");
					execUpdateSQL("UPDATE ItemsInOrder SET Amount=Amount-"+NumOfItems+" WHERE OID="+i+" AND ID="+ItemID);
					execUpdateSQL("UPDATE Product SET Expected=Expected-"+NumOfItems);
					execUpdateSQL("INSERT INTO ProductInStock (ID,ExpirationDate,AmountInStock) VALUES ("+ItemID+", date('now','+4 month'),"+NumOfItems+")");
				}
				execUpdateSQL("DELETE FROM ItemsInOrder WHERE OID="+i+" AND Amount<=0");
				execUpdateSQL("DELETE FROM Orders WHERE Orders.OrdID NOT IN (SELECT DISTINCT OID FROM ItemsInOrder)");
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException sqlEx) {
					}
				}// ignore
				statement = null;
			}
		}
		/*
		@TODO Here we need to send a message to Transport to return their truck (transportID)!
		 */
	}
}
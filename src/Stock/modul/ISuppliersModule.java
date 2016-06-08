package Stock.modul;
import Stock.market.Item;
import Stock.market.Manufacturer;
import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.QDS;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierContact;
import Stock.supplierManagement.SupplierCard.PaymentTerm;

public interface ISuppliersModule {
	public Manufacturer[] getRepresentedManufacturers(Supplier s);
	public Supplier[] getRepresentingSuppliers(Manufacturer man);
	public int addSupplierCard(String supplierName, int ssl, String address, int bankAccount, PaymentTerm paymentTerms, SupplierContact contact);
	public void removeSupplier(int supplierCardNumber);
	public QDS getAgreementQDS(int agreementId);
	public void addContactNumber(String contactMail, String phoneNumber);
	public void removeContactNumber(String contactMail, String phoneNumber);
	public void changeContactMail(String oldMail, String newMail);
	public void addContact(int cardNumber, String contactMail, String contactName);
	public void removeContact(int cardNumber, String contactMail);
	public void addAgreement(Supplier signedSupplier, boolean isTransportationFixed, boolean isTransportationBySupplier);
	public void removeAgreement(int agreementId);
	public void addItemToAgreement(Agreement agr, CataloguedItem item, double price);
	public void removeItemFromAgreement(Agreement agr, int catalogueNumber);
	public boolean addDiscount(Agreement agr, CataloguedItem ct, int minQuantity, double discount);
	public CataloguedItem[] getSoldItems(Supplier s);
	public int getCatalogueNumber(Item item, Supplier s);
	public void checkForTimedOrder();
	public void makeOrders(StockReport sr);
	
}

package Stock.modul;
import java.util.*;

import Stock.data.DBHandler;
import Stock.data.Dbms;
import Stock.market.Item;
import Stock.market.Manufacturer;
import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.DiscountTerm;
import Stock.supplierManagement.ItemDiscountTerm;
import Stock.supplierManagement.QDS;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierCard;
import Stock.supplierManagement.SupplierContact;
import Stock.supplierManagement.SupplierCard.PaymentTerm;
import Stock.supplyOrder.ItemPricing;
import Stock.supplyOrder.SupplyOrder;

public class SuppliersModule implements ISuppliersModule {
	private List<Supplier> suppliers;
	private DBHandler db = new DBHandler();
	
	public SuppliersModule() {
		suppliers = new LinkedList<Supplier>();
		db.createTables(false);
	}
	
	

	public int addSupplierCard(String supplierName, int ssl, String address, int bankAccount, PaymentTerm paymentTerms, SupplierContact contact) {
		SupplierCard sc = new SupplierCard(supplierName, ssl, address, bankAccount, paymentTerms, contact);
		suppliers.add(new Supplier(sc));
		db.addSupplier(new Supplier(sc));
		return sc.getCardNumber();
	}

	public void removeSupplier(int supplierCardNumber) {
		for (Supplier s : suppliers) {
			if (s.getCard().getCardNumber() == supplierCardNumber) {
				suppliers.remove(s);
				db.removeSupplier(s);
				return;
			}
		}
	}

	public QDS getAgreementQDS(int agreementId) {
		for (Supplier s: suppliers)
		for (Agreement agr: s.getAgreements()) {
			if (agr.getAgreementId() == agreementId)
				return agr.getQDS();
		}
		return null;
	}

	public Manufacturer[] getRepresentedManufacturers(Supplier s) {
		return s.getRepresentedManufacturers();
	}

	public Supplier[] getRepresentingSuppliers(Manufacturer man) {
		return man.getRepresentingSuppliers();
	}

	public void addContactNumber(String contactMail, String phoneNumber) {
		for (Supplier s : suppliers)
		for (SupplierContact c : s.getCard().getContacts()) {
			if (c.getEmail().equals(contactMail)) {
				c.addPhoneNumber(phoneNumber);
				db.addContactPhoneNumber(c, phoneNumber);
			}
		}
	}

	public void removeContactNumber(String contactMail, String phoneNumber) {
		for (Supplier s : suppliers)
			for (SupplierContact c : s.getCard().getContacts()) {
				if (c.getEmail().equals(contactMail)) {
					c.removePhoneNumber(phoneNumber);
					db.removeContactPhoneNumber(c, phoneNumber);
				}
			}
	}
	
	public void changeContactMail(String oldMail, String newMail) {
		for (Supplier s : suppliers)
			for (SupplierContact c : s.getCard().getContacts()) {
				if (c.getEmail().equals(oldMail))
					c.changeEmail(newMail);
				db.changeContactMail(c, newMail);
			}
	}
	
	public void addContact(int cardNumber, String contactMail, String contactName) {
		SupplierContact c;
		for (Supplier s : suppliers) {
			if (s.getCard().getCardNumber() == cardNumber) {
				s.getCard().addContact(c = new SupplierContact(contactName, contactMail));
				db.addContact(s, c);
			}
		}
	}
	
	public void removeContact(int cardNumber, String contactMail) {
		for (Supplier s : suppliers) {
			if (s.getCard().getCardNumber() == cardNumber) {
				s.getCard().removeContact(contactMail);
				db.removeContact(s, new SupplierContact("", contactMail));
			}
		}
	}
	
	public void addAgreement(Supplier signedSupplier, boolean isTransportationFixed,
			boolean isTransportationBySupplier) {
		Agreement agr = new Agreement(0, signedSupplier, null, isTransportationFixed, isTransportationBySupplier);
		signedSupplier.addAgreement(agr);
		db.addAgreement(signedSupplier, agr);
	}
	
	public void removeAgreement(int agreementId) {
		for (Supplier s : suppliers) {
			for (Agreement agr : s.getAgreements()) {
				if (agr.getAgreementId() == agreementId) {
					try {
						s.removeAgreement(agr);
						db.removeSupplierAgreement(s, agr);
					} catch (Exception e) {
						// shouldn't happen
						e.printStackTrace();
					}
					return;
				}
			}
		}
	}
	
	public void addItemToAgreement(Agreement agr, CataloguedItem item, double price) {
		agr.addItem(item, price);
		db.addItemToAgreement(agr, item, price);
	}
	
	public void removeItemFromAgreement(Agreement agr, int catalogueNumber) {
		agr.removeItem(catalogueNumber);
		db.removeItemFromAgreement(agr, catalogueNumber);
	}
	
	public boolean addDiscount(Agreement agr, CataloguedItem ct, int minQuantity, double discount) {
		try {
			agr.getQDS().addDiscount(ct, minQuantity, discount);
			db.addDiscountToQDS(agr, ct, minQuantity, discount);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public CataloguedItem[] getSoldItems(Supplier s) {
		return s.getSoldItems();
	}
	
	public int getCatalogueNumber(Item item, Supplier s) {
		for (CataloguedItem ct : item.getCataloguedItems())
			if (ct.getSupplier().getSupplierName().equals(s.getSupplierName()))
				return ct.getCatalogueNumber();
		return 0;
	}
	public CataloguedItem[] getSuppliersCatalougeItems(int cardNumber) {
		return db.getSuppliersCatalougeItems(cardNumber);
	}
	public void addManufacturer(Manufacturer manufacturer) {
		db.addManufacturer(manufacturer);
		
	}
	public void removeManufacturer(Manufacturer manufacturer) {
		db.removeManufacturer(manufacturer);
	}
	public Manufacturer[] getManufacturers() {
		return db.getManufacturers();
	}
	public Item[] getItems(String name) {
		return db.getItems(name);
	}
	public void addCatalougeItem(CataloguedItem ci) {
		db.addCatalougeItem(ci);
	}
	public void addItem(Item item) {
		db.addItem(item);
	}
	public void changeItemName(Item item, String string) {
		db.changeItemName(item, string);
	}
	public void removeItem(Item item) {
		db.removeItem(item);
	}
	public Supplier[] getSuppliers() {
		return db.getSuppliers();
	}
	public void addSupplier(Supplier supplier) {
		db.addSupplier(supplier);
	}
	public void removeSupplier(Supplier supplier) {
		db.removeSupplier(supplier);
	}
	public SupplierContact[] getContacts(Supplier supplier) {
		return db.getContacts(supplier);
	}
	public void addContact(Supplier supplier, SupplierContact sc) {
		db.addContact(supplier, sc);
	}
	public void removeContact(Supplier supplier, SupplierContact supplierContact) {
		db.removeContact(supplier, supplierContact);
	}
	public String[] getContactsPhones(String email) {
		return db.getContactsPhones(email);
	}
	public void addContactPhoneNumber(SupplierContact supplierContact, String string) {
		db.addContactPhoneNumber(supplierContact, string);
	}
	public void removeContactPhoneNumber(SupplierContact supplierContact, String string) {
		db.removeContactPhoneNumber(supplierContact, string);
	}
	public void removeCatalougeItem(CataloguedItem cataloguedItem) {
		db.removeCatalougeItem(cataloguedItem);
	}
	public Agreement[] getAgreements(int cardNumber) {
		return db.getAgreements(cardNumber);
	}
	public void addAgreement(Supplier supplier, Agreement agreement) {
		db.addAgreement(supplier, agreement);
	}
	public ItemPricing[] getAgreementsCatalougeItems(int agreementId) {
		return db.getAgreementsCatalougeItems(agreementId);
	}
	public void addDiscountToQDS(Agreement agreement, CataloguedItem catalougeItem, Integer valueOf, Double valueOf2) {
		db.addDiscountToQDS(agreement, catalougeItem, valueOf, valueOf2);
	}
	public Integer[] getAgreementSupplyDays(int agreementId) {
		return db.getAgreementSupplyDays(agreementId);
	}
	public void addAgreementSupplyDay(Agreement agreement, Integer valueOf) {
		db.addAgreementSupplyDay(agreement, valueOf);
	}
	public void removeDayFromAgreement(Agreement agreement, Integer valueOf) {
		db.removeDayFromAgreement(agreement, valueOf);
	}
	public void removeDiscountToQDS(Agreement agreement, CataloguedItem catalougeItem, int minQuantity,
			double discount) {
		db.removeDiscountToQDS(agreement, catalougeItem, minQuantity, discount);
	}
	public DiscountTerm[] getAgreementsItemsDiscount(int agreementId, int catalogueNumber) {
		return db.getAgreementsItemsDiscount(agreementId, catalogueNumber);
	}
	public void changeSupplier(Supplier s) {
		db.changeSupplier(s);
	}
	public void changeAgreementDeliverySpecs(Agreement agreement, boolean b) {
		db.changeAgreementDeliverySpecs(agreement, b);
	}
	public Item[] getItems() {
		return db.getItems();
	}
	public Item[] getDetailedItems() {
		return db.getDetailedItems();
	}





	@Override
	public void checkForTimedOrder()
	{
		db.checkForTimedOrder();
	}



	@Override
	public void makeOrders(StockReport sr)
	{
		db.makeOrders(sr);
	}	
}

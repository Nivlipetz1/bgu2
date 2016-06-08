package Stock.data;

import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierCard;
import Stock.supplierManagement.SupplierContact;

public interface Dbms {

	void addSupplier(Supplier s);

	void removeSupplier(Supplier s);

	void changeContactMail(SupplierContact c, String newMail);

	void addContact(Supplier s, SupplierContact c);

	void removeContact(Supplier s, SupplierContact c);

	void addContactPhoneNumber(SupplierContact c, String phoneNumber);

	void removeContactPhoneNumber(SupplierContact c, String phoneNumber);

	void addAgreement(Supplier signedSupplier, Agreement agr);

	void removeSupplierAgreement(Supplier s, Agreement agr);

	void addItemToAgreement(Agreement agr, CataloguedItem item, double price);

	void removeItemFromAgreement(Agreement agr, int catalogueNumber);

	void addDiscountToQDS(Agreement agr, CataloguedItem ct, int minQuantity, double discount);

}

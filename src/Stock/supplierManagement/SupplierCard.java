package Stock.supplierManagement;

import java.util.LinkedList;
import java.util.List;

public class SupplierCard {
	public static enum PaymentTerm {Monthly, Yearly, ByOrder}

	private String supplierName;
	private int bankAccount, serial_no;
	private PaymentTerm paymentTerms;
	private List<SupplierContact> contacts;
	private String address;

	public SupplierCard(String supplierName, int ssl, String address, int bankAccount, PaymentTerm paymentTerms, SupplierContact contact) {
		this.supplierName = supplierName;
		this.address = address;
		this.bankAccount = bankAccount;
		this.paymentTerms = paymentTerms;
		serial_no = ssl;
		contacts = new LinkedList<SupplierContact>();
		contacts.add(contact);
	}

	public int getCardNumber() {
		return serial_no;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public int getBankAccount() {
		return bankAccount;
	}
	public PaymentTerm getPaymentTerms() {
		return paymentTerms;
	}
	public SupplierContact[] getContacts() {
		SupplierContact[] array = new SupplierContact[contacts.size()];
		return contacts.toArray(array);
	}
	public void addContact(SupplierContact sc) {
		contacts.add(sc);
	}
	public void removeContact(SupplierContact sc) throws Exception {
		if (contacts.size() == 1)
			throw new Exception("A supplier card must contain at least one contact! ");
		contacts.remove(sc);
	}
	public void removeContact(String contactMail) {
		for (SupplierContact c : contacts)
			if (c.getEmail().equals(contactMail)) {
				try {
					removeContact(c);
				} catch (Exception e) {
					// shouldn't happen
					e.printStackTrace();
				}
			}
	}
	public void setName(String string) {
		this.supplierName = string;
	}
	public int getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(int serial_no) {
		this.serial_no = serial_no;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public void setBankAccount(int bankAccount) {
		this.bankAccount = bankAccount;
	}
	public void setPaymentTerms(PaymentTerm paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	public void setContacts(List<SupplierContact> contacts) {
		this.contacts = contacts;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "serial_no: " + serial_no + "\n" +
				"supplierName: " + supplierName + "\n" +
				"bankAccount: " + bankAccount + "\n" +
				"address: " + address + "\n" +
				"paymentTerms: " + paymentTerms;
	}
}

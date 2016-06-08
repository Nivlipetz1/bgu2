package Stock.supplierManagement;

import java.util.LinkedList;
import java.util.List;

public class SupplierContact {
	private String name, email;
	private List<String> phoneNumbers;
	
	public SupplierContact(String name, String email) {
		this.name = name;
		this.email = email;
		phoneNumbers = new LinkedList<String>();
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public void changeEmail(String email) {
		this.email = email;
	}

	public String[] getPhoneNumbers() {
		String[] array = new String[phoneNumbers.size()];
		return phoneNumbers.toArray(array);
	}
	public void addPhoneNumber(String number) {
		phoneNumbers.add(number);
	}
	public void removePhoneNumber(String number) {
		for (String i : phoneNumbers) {
			if (i.equals(number))
				phoneNumbers.remove(i);
		}
	}
}

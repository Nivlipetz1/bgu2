package Stock.market;

import java.util.List;

import Stock.supplierManagement.Supplier;

public class Manufacturer {
	private String manufacturerName;
	private List<Supplier> delegates;
	
	public Manufacturer(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}
	public String getManufacturerName() {
		return manufacturerName;
	}
	public void addDelegate(Supplier del) {
		if (!delegates.contains(del))
			delegates.add(del);
	}
	public Supplier[] getRepresentingSuppliers() {
		Supplier[] arr = new Supplier[delegates.size()];
		return delegates.toArray(arr);
	}
	public void removeDelegate(Supplier del) {
		delegates.remove(del);
	}
}

package Stock.modul;

import java.util.List;

import Stock.common.Product;

public interface IWarehouseModule
{
	public void recreateTree();
	public int getIDfromName(String name);
	public int countCategories();
	public String listCategories();
	public void printCategory(int catID);
	public String printWithSubCategories(int catID);
	public String printEntireStock() throws Exception;
	public void UpdateFaulty();
	public StockReport CheckStock();
	public boolean TakeItems(Product p, int amount);
	public boolean PutItems(List<Product> toAdd);
	public String printPendingOrders();
}

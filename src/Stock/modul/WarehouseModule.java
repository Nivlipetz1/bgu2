package Stock.modul;

import java.util.List;

import Stock.common.Product;
import Stock.data.DBHandler;

public class WarehouseModule implements IWarehouseModule
{
	DBHandler db = new DBHandler();
	@Override
	public void recreateTree()
	{
		db.recreateTree();
	}

	@Override
	public int getIDfromName(String name)
	{
		return db.getIDfromName(name);
	}

	@Override
	public int countCategories()
	{
		return db.countCategories();
	}

	@Override
	public String listCategories()
	{
		return db.listCategories();
	}

	@Override
	public void printCategory(int catID)
	{
		db.printCategory(catID);
	}

	@Override
	public String printWithSubCategories(int catID)
	{
		return db.printWithSubCategories(catID);
	}

	@Override
	public String printEntireStock() throws Exception
	{
		return db.printEntireStock();
	}

	@Override
	public void UpdateFaulty()
	{
		db.UpdateFaulty();
	}

	@Override
	public StockReport CheckStock()
	{
		return db.CheckStock();
	}

	@Override
	public boolean TakeItems(Product p, int amount)
	{
		return db.TakeItems(p, amount);
	}

	@Override
	public boolean PutItems(List<Product> toAdd)
	{
		return db.PutItems(toAdd);
	}

	@Override
	public String printPendingOrders()
	{
		return db.printPendingOrders();
	}

	public String printFaulty()
	{
		return db.printFaulty();
	}

	public void clearFaulty()
	{
		db.clearFaulty();
	}

}

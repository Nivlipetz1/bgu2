package Stock.modul;
import java.util.LinkedList;
import java.util.List;

import Stock.market.Item;

public class StockReport {
	private List<NeededItem> itemsToOrder = new LinkedList<NeededItem>();
	public void addItem(Item item, int quantity) {
		itemsToOrder.add(new NeededItem(item, quantity));
	}
	public NeededItem[] readItems() {
		NeededItem[] arr = new NeededItem[itemsToOrder.size()];
		return itemsToOrder.toArray(arr);
	}
	public boolean isEmpty()
	{
		return itemsToOrder.size()==0;
	}
}

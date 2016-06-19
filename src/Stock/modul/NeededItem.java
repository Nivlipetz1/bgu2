package Stock.modul;
import Stock.market.Item;

public class NeededItem {
	private Item item;
	private int quantity;
	public Item getItem() {
		return item;
	}
	public int getQuantity() {
		return quantity;
	}
	public NeededItem(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public void setAmount(int amount)
	{
		this.quantity = amount;
	}
}

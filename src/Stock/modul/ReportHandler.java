package Stock.modul;
import java.util.Collection;

import Stock.supplyOrder.SupplyOrder;

public interface ReportHandler {
	public Collection<SupplyOrder> makeOrders(StockReport sr) throws Exception ;
}

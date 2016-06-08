package Stock.common;

import java.util.*;
/*
 * This class describes a ProductInStock Element! NOT a product.
 * 
 */
public class Product
{
	private int ID;
	private Calendar ExpirationDate;
	private int amount;
	
	public Product(int iD, Calendar expirationDate, int amount)
	{
		ID = iD;
		ExpirationDate = expirationDate;
		this.amount = amount;
	}
	
	public int getID()
	{
		return ID;
	}
	public Calendar getExpirationDate()
	{
		return ExpirationDate;
	}
	public int getAmount()
	{
		return amount;
	}
}

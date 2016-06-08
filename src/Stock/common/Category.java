package Stock.common;

public class Category
{
	private int ID;
	private int parentID;
	public int getParentID()
	{
		return parentID;
	}
	public Category(int ID,int parent)
	{
		this.ID = ID;
		this.parentID = parent;
	}
	public int getID()
	{
		return ID;
	}
}

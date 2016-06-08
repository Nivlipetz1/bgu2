package Stock.common;
import java.util.ArrayList;

public class Node {

    private int identifier;
    private Category data;
    private ArrayList<Integer> children;

    public Node(int identifier, Category data) {
        this.identifier = identifier;
        this.data = data;
        children = new ArrayList<Integer>();
    }
    public Category getData()
	{
		return data;
	}
	public void setData(Category data)
	{
		this.data = data;
	}
    public int getIdentifier() {
        return identifier;
    }
    public ArrayList<Integer> getChildren() {
        return children;
    }
    public void addChild(int identifier) {
        children.add(identifier);
    }
}
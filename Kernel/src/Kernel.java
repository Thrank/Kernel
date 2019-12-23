import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;

public class Kernel
{
	//private List<Item> items;
	private SortedMap<String, Item> items;
	//private HashMap<Integer, Item> hashItems;
	
	public Kernel(List<Item> it)
	{
		for(Item i:it)
			this.addItem(i);
	}
	public Kernel(SortedMap<String, Item> it) {
		this.items = it;
	}
	public Kernel()
	{
		//this.items = new ArrayList<>();
		//this.hashItems = new HashMap<>();
		this.items = new TreeMap<>();
	}
	
	public void addItem(Item it)
	{
		//hashItems.put(this.size(), it);
		this.items.put(it.getName(), it);
		//items.add(it);
	}
	public void addItem(SortedMap<String, Item> it) {
		this.items.putAll(it);
	}
	
	public boolean contains(Item it)
	{
		//return hashItems.containsValue(it);
		return this.items.containsKey(it.getName());
		//return items.stream().anyMatch(it2 -> it2.getName().equals(it.getName()));
	}
	
	public int size()
	{
		//return hashItems.size();
		return this.items.size();
	}
	
	public void removeItem(Item it) {
		//
	}
}
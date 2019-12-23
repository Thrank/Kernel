import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Bucket
{
	//private List<Item> items;
	private SortedMap<String, Item> items;
	//private HashMap<String, Item> hashItems;

	//Constructor of the hashmap
	public Bucket()
	{
		//items = new ArrayList<>();
		this.items = new TreeMap<>();
		//hashItems=new HashMap<>();
	}
	
	public void addItem(Item it)
	{
		//items.add(it);
		items.put(it.getName(), it);
	}
	public void addItem(SortedMap<String, Item> it) {
		this.items.putAll(it);
	}

	public int size()
	{
		//return hashItems.size();
		return items.size();
	}
	
	public  List<Item> getItems() {
		List<Item> result = new ArrayList<>();
		for(String key : items.keySet()) {
			result.add(items.get(key));
		}
		return result;
	}
	public SortedMap<String, Item> getItemMap() {
		return this.items;
	}
	
	/*public HashMap<Integer, Item> getItems()
	{
		return hashItems;
	}*/
	
	public boolean contains(Item it)
	{
		return this.items.containsKey(it.getName());
		//return hashItems.containsValue(it);
		//return items.stream().anyMatch(it2 -> it2.getName().equals(it.getName()));
	}
	
	public void removeItem(Item it)
	{
		if(this.contains(it)) {
			this.items.remove(it.getName());
		}
		/*for(Map.Entry<Integer, Item> entry: hashItems.entrySet()) {
			if(it.getName().contentEquals(entry.getValue().getName())) {
				hashItems.remove(entry.getKey());
				break;
			}
		}*/
		/*for(int i = 0; i< items.size(); i++)
		{
			Item it2 = items.get(i);
			if(it2.getName().equals(it.getName()))
			{
				items.remove(it2);
				break;
			}
		}*/
	}
	
	public void removeItem(SortedMap<String, Item> it) {
		this.items.keySet().removeAll(it.keySet());
	}
}
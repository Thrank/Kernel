import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterByValueAndAbsoluteRC implements ItemSorter {
	//sorter that compares Xr and then the Rc. (this is the default sorter)
	@Override
	public void sort(List<Item> items)
	{
		items.sort(Comparator.comparing(Item::getXr).reversed()
					.thenComparing(Item::getRc));
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;
		
	}

}

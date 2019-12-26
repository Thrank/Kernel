import java.util.Comparator;
import java.util.List;

public class ItemSorterByOccurrences implements ItemSorter {
	//sorter that sort by comparing Xr and then the occurrences of a variables.
	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getXr).reversed().
				thenComparing(Item::getOcc).reversed());
		/*HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}*/
		//return hashItems;
	}

}

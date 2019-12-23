import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterByLowerBound implements ItemSorter {
	//sorter that compares first the Xr, then reverse the list and compares LowerBound (reversed) and the the Rc
	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getXr).reversed().
				thenComparing(Item::getLowerBound).reversed().
				thenComparing(Item::getRc));
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;

	}

}

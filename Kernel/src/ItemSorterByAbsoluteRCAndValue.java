import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterByAbsoluteRCAndValue implements ItemSorter {
	//Sorter that compares first the Rc and then the Xr (relaxed value).
	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getRc).thenComparing(Item::getXr));
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;
	}

}

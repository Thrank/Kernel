import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterByValueAndObj implements ItemSorter {

	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getXr).reversed().
				thenComparing(Item::getObjCoeff).reversed());
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;
	}

}

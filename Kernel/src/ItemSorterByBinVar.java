import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterByBinVar implements ItemSorter {

	//This item sorter look at the type of variables and then at the Rc (reduced cost)
	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getVarType).
				thenComparing(Item::getRc));
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;
	}

}

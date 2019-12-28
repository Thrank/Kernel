import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemSorterPercentageByValueAndAbsoluteRC implements ItemSorter {
	
	int IndexEnd = 0;
	//This sorter is unuseful.
	@Override
	public void sort(List<Item> items) {
		IndexEnd = (int) 5;
		items.subList(0,IndexEnd).sort(Comparator.comparing(Item::getXr).reversed().
				thenComparing(Item::getRc));
		
		HashMap<String, Item> hashItems = new HashMap<>();
		for(Item it : items) {
			hashItems.put(it.getName(), it);
		}
		//return hashItems;
		
	}

}

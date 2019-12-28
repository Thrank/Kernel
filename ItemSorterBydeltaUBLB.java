import java.util.Comparator;
import java.util.List;

public class ItemSorterBydeltaUBLB implements ItemSorter {

	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getDeltaUBLB));
	}

}

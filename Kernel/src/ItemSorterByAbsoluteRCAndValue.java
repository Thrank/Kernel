import java.util.Comparator;
import java.util.List;

public class ItemSorterByAbsoluteRCAndValue implements ItemSorter {

	@Override
	public void sort(List<Item> items) {
		items.sort(Comparator.comparing(Item::getRc).thenComparing(Item::getXr));
	}

}

import java.util.Comparator;
import java.util.List;

public class ItemSorterPercentageByValueAndAbsoluteRC implements ItemSorter {
	
	int IndexEnd = 0;
	//List<Item> it;
	@Override
	public void sort(List<Item> items) {
		IndexEnd = (int) 5;//((int) items.size()*0.15);
		//items.sort(Comparator.comparing(Item::getXr).reversed().
		//		thenComparing(Item::getRc));
		//System.out.println("VECCHIO: "+items);
		//it = items.subList(0,IndexEnd);
		items.subList(0,IndexEnd).sort(Comparator.comparing(Item::getXr).reversed().
				thenComparing(Item::getRc));
		System.out.println("NUOVO:   "+items);
		
	}

}

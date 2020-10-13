import java.util.ArrayList;
import java.util.List;

public class BucketBuilderOverlapping implements BucketBuilder {

	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		
		int size = (int) Math.floor(items.size()*config.getBucketSize());
		System.out.println("");
		System.out.println("NUMERO DI ITEMS: "+items.size());
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("");
		for(int k=0; k<(items.size()); k++) {
			b.addItem(items.get(k));
			//if the bucket is full, create new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
				System.out.println("");
				//The number here set how many position we go back to overlap.
				k=(int) (k-Math.floor(config.getBucketOver()*config.getBucketSize()*items.size()));
			}
			
			
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}
		
		
		
		return buckets;
	}

}

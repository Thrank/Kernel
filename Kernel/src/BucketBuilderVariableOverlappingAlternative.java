import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableOverlappingAlternative implements BucketBuilder {

	private int p = 0;
	private int startingPoint = 0;
	
	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		double count = 0.15;
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension for the incremental overlapping
		int size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("");
		System.out.println("");
		System.out.println("");
		//Cycle for buckets
		for(int k=0; k<(items.size()); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				//Look to increment the buckets
				if(p<=3) { //make new bucket bigger
					size = (int) ((int) (items.size()*config.getBucketSizeIncremental()+Math.sqrt(count)*
							(config.getBucketSizeIncremental()*items.size())));
					count+=0.15;
					p++;
					k=startingPoint;
				} else { //go ahead to make new bucket in new positions
					startingPoint=k;
					//restart with the original size of buckets
					size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
					count=0.15;
					p=0;
				}
				//The maximum value of count will be 0.6 for now.
				System.out.println("VALORE SATURAZIONE: "+(items.size()*config.getBucketSizeIncremental()+
						Math.sqrt(0.6)*(items.size()*config.getBucketSizeIncremental())));
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size+"\n");

			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}

		return buckets;
	}

}

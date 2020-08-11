import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableOverlappingAlternative2 implements BucketBuilder {

	private int p = 1;
	private int startingPoint = 0;
	private double satMaxValue = 0;
	
	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		List<Bucket> buckets = new ArrayList<>();
		satMaxValue = config.getMaxIncrement();
		Bucket b = new Bucket();
		//take the base size dimension for the incremental overlapping
		int size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
		System.out.println("\nNUOVO BUCKET CON DIMENSIONE: "+size+"\n");
		//Cycle for buckets
		for(int k=0; k<(items.size()); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				//Look to increment the buckets
				if(p<=config.getBucketMax()) { //make new bucket bigger
					size = (int) ((int) items.size()*config.getBucketSizeIncremental()*
							(1+satMaxValue*Math.sqrt(p/config.getBucketMax())));
					p++;
					k=startingPoint;
					if(p == config.getBucketMax()) {
						startingPoint = k; //I'm at the last bucket, I'll keep trace of the last element
					}
				} else { //go ahead to make new bucket in new positions
					//startingPoint=(int) (k-(satMaxValue*Math.sqrt(p/config.getBucketMax())* //the inside the sqrt is always 1 xD
					//		(config.getBucketSizeIncremental()*items.size())));
					//restart with the original size of buckets
					size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
					p=1;
				}
				//The maximum value of count will be 0.6 for now. (OLD comment)
				System.out.println("VALORE SATURAZIONE: "+(items.size()*config.getBucketSizeIncremental()+
						satMaxValue*Math.sqrt(p/config.getBucketMax())*(items.size()*config.getBucketSizeIncremental())));
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size+"\n");

			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}

		return buckets;
	}

}

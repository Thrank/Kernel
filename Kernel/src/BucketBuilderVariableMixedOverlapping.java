import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableMixedOverlapping implements BucketBuilder {

	private int p = 1;
	private int startingPoint = 0;
	private double satMaxValue = 0;

	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		satMaxValue = config.getMaxIncrement();
		//take the base size dimension for the incremental overlapping
		int size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size+"\n");
		//Cycle for buckets
		for(int k=0; k<(items.size()); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				//Look to increment the buckets
				if(p<=config.getBucketMax()) { //make new bucket bigger
					size = (int) (items.size()*config.getBucketSizeIncremental()*
							(1+Math.sqrt(p/config.getBucketMax())));
							//+Math.sqrt(p/config.getBucketMax())*
							//(config.getBucketSizeIncremental()*items.size()));*/
					p++;
					k=startingPoint;
				} else { //go ahead to make new bucket in new positions
					startingPoint=k;
					//restart with the original size of buckets
					size = (int) Math.floor(items.size()*config.getBucketSizeIncremental());
					p=1;
				}
				//The maximum value of count will be 0.6 for now.
				System.out.println("VALORE SATURAZIONE: "+(items.size()*config.getBucketSizeIncremental()+
						1*Math.sqrt(p/config.getBucketMax())*(items.size()*config.getBucketSizeIncremental())));
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size+"\n");

			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}
		//new size dimension for second iteration of buckets
		size = (int) Math.floor(items.size()*config.getBucketSize());
		int count = 2; //with 0 solve probPortfolio
		System.out.println("NUOVO BUCKET PARTE 2 CON DIMENSIONE: "+size+"\n");
		System.out.println("IL VALORE DI items size: "+items.size());
		//b = new Bucket();
		for(int k=0; k<items.size(); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				k=(int)(k-Math.floor(config.getBucketOver()*size));
				System.out.println("IL VALORE DI K: "+k);
				size =(int)(items.size()*config.getBucketSizeStart()+
						(items.size()*config.getBucketSize()-
								items.size()*config.getBucketSizeStart())/count);
				count++;
				System.out.println("VALORE SATURAZIONE1: "+items.size()*config.getBucketSizeStart());
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size+"\n");
			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}

		return buckets;
	}

}

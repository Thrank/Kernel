import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableLimited implements BucketBuilder 
{
	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		double count = 2;
		//take the max number of buckets desired.
		int numBucket = config.getBucketMax();
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension in base of the amount of buckets that I want.
		int size = (items.size()/(numBucket));
		System.out.println("\nNUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("NUMERO TOTALE DI ELEMENTI: "+items.size()+"\n");
		for(Item it : items) {
			b.addItem(it);
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				size =(int)(items.size()/numBucket+
						(items.size()/numBucket-items.size()*config.getBucketSizeStart())/count);
				/*if(config.getBucketSize()>=config.getBucketSizeStart()) {
					size = (int) ((int) (items.size()/numBucket-Math.atan(config.getBucketSizeStart()))/
							(count+10)+items.size()/numBucket);
					System.out.println("VALORE SATURAZIONE: "+items.size()/numBucket);
				} else {
					size = (int) ((int) (items.size()/numBucket-Math.atan(config.getBucketSizeStart()))/
							(count+25)+items.size()/numBucket);
					System.out.println("VALORE SATURAZIONE: "+items.size()/numBucket);
				}*/
				count++;
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}
		
		return buckets;
	}

}

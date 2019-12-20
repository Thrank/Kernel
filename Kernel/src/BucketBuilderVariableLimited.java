import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableLimited implements BucketBuilder 
{
	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		double count = 1;
		//take the max number of buckets desired.
		int numBucket = config.getBucketMax();
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension in base of the amount of buckets that I want.
		int size = (items.size()/(numBucket));
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("NUMERO TOTALE DI ELEMENTI: "+items.size());
		System.out.println("");
		System.out.println("");
		System.out.println("");
		for(Item it : items) {
			b.addItem(it);
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				if(config.getBucketSize()>=config.getBucketSizeStart()) {
					size = (int) ((int) (items.size()/numBucket/*config.getBucketSize()*/-Math.atan(config.getBucketSizeStart()))/
							(count+10)+items.size()/numBucket/*config.getBucketSizeStart()*/);
					System.out.println("VALORE SATURAZIONE: "+items.size()/numBucket/*config.getBucketSizeStart()*/);
				} else {
					size = (int) ((int) (items.size()/numBucket/*config.getBucketSize()*/-Math.atan(config.getBucketSizeStart()))/
							(count+25)+items.size()/numBucket/*config.getBucketSizeStart()*/);
					System.out.println("VALORE SATURAZIONE: "+items.size()/numBucket/*config.getBucketSizeStart()*/);
				}
				//changing size dimension. Size fixed to BucketSizeStart
				//size = (int) Math.max(items.size()*(Math.atan(config.getBucketSizeStart()*countS)),
				//	items.size()*(config.getBucketSize()-((Math.atan(config.getBucketSizeEnd()*countS2)))));
				
				/*size1 = (int) ((int) items.size()*(Math.atan(config.getBucketSizeStart()*countS1)));
				size2 = (int) ((int) items.size()*(config.getBucketSize()-((Math.atan(config.getBucketSizeEnd()*countS2)))));
				if(size==size2) {
					System.out.println("HO PRESO IL SECONDO VALORE!!!");
				}
				if(size==size1) {
					System.out.println("HO PRESO IL PRIMO VALORE!!!");
				}*/
				count+=5;
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
				System.out.println("");
				System.out.println("");
				System.out.println("");
			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}
		
		return buckets;
	}

}

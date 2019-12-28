import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariable implements BucketBuilder 
{
	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		//double countS2 = 1;
		double count = 2;
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension
		int size = (int) Math.floor(items.size()*config.getBucketSize());
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("");
		System.out.println("");
		System.out.println("");
		for(Item it : items) {
			b.addItem(it);
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				//changing size dimension. Size fixed to BucketSizeStart
				size =(int)(items.size()*config.getBucketSizeStart()+
						(items.size()*config.getBucketSize()-items.size()*config.getBucketSizeStart())/count);
				/*if(config.getBucketSize()>=config.getBucketSizeStart()) {
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+10)+items.size()*config.getBucketSizeStart());
				} else {
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+25)+items.size()*config.getBucketSizeStart());
				}*/
				System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				//This is an old version of BucketBuilderVariable with different function
				/*size = (int) Math.max(items.size()*(Math.atan(config.getBucketSizeStart()*countS1)),
					items.size()*(config.getBucketSize()-((Math.atan(config.getBucketSizeEnd()*countS2)))));
				
				size1 = (int) ((int) items.size()*(Math.atan(config.getBucketSizeStart()*countS1)));
				size2 = (int) ((int) items.size()*(config.getBucketSize()-((Math.atan(config.getBucketSizeEnd()*countS2)))));
				if(size==size2) {
					System.out.println("HO PRESO IL SECONDO VALORE!!!");
				}
				if(size==size1) {
					System.out.println("HO PRESO IL PRIMO VALORE!!!");
				}*/
				//count+=5;
				count++;
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

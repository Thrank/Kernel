import java.util.ArrayList;
import java.util.List;

public class BucketBuilderKernelMixed implements BucketBuilder {

	@Override
	public List<Bucket> build(List<Item> items, Configuration config) {
		//double countS2 = 1;
		double count = 1;
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension
		int size = (int) Math.floor(items.size()*config.getBucketSize());
		//int size1 = 0;
		//int size2 = 0;
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
				if(config.getBucketSize()>=config.getBucketSizeStart()) {
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+10)+items.size()*config.getBucketSizeStart());
					System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				} else {
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+25)+items.size()*config.getBucketSizeStart());
					System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				}
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

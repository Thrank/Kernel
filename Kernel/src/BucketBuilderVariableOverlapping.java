import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableOverlapping implements BucketBuilder {

	public List<Bucket> build(List<Item> items, Configuration config) {
		double count = 1;
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
		for(int k=0; k<(items.size()); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				k=(int)(k-0.8*size);
				//changing size dimension. First: Size>=S1
				if(config.getBucketSize()>=config.getBucketSizeStart()) {
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+10)+items.size()*config.getBucketSizeStart());
					System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				} else { //Second: Size<S1
					size = (int) ((int) (items.size()*config.getBucketSize()-Math.atan(config.getBucketSizeStart()))/
							(count+25)+items.size()*config.getBucketSizeStart());
					System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				}
				count+=5;
				//go back in the list. ATTENTION: look at 0.4. It must be bigger that bucketSize and bucketSizeStar
				//k=(int) (k-0.95*size*items.size());
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

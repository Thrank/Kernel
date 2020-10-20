import java.util.ArrayList;
import java.util.List;

public class BucketBuilderVariableOverlapping implements BucketBuilder {

	public List<Bucket> build(List<Item> items, Configuration config) {
		double count = 2; //with 0 or 0.25 solve probPortfolio, 2 standard
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		//take the base size dimension
		int size = (int) Math.floor(items.size()*config.getBucketSize());
		System.out.println("");
		System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
		System.out.println("");
		for(int k=0; k<items.size(); k++) {
			b.addItem(items.get(k));
			//open a new bucket
			if(b.size()==size) {
				buckets.add(b);
				b = new Bucket();
				//go back in the list a percentage of the last bucket's size
				System.out.println("SONO ARRIVATO A: "+k);
				//System.out.println("ELEMENTI TOTALI: "+items.size());
				k=(int)(k-Math.floor(config.getBucketOver()*size));
				size =(int)(items.size()*config.getBucketSizeStart()+
						(items.size()*config.getBucketSize()-items.size()*config.getBucketSizeStart())/count);
				System.out.println("VALORE SATURAZIONE: "+items.size()*config.getBucketSizeStart());
				count++;
				//go back in the list. ATTENTION: look at 0.4. It must be bigger that bucketSize and bucketSizeStar
				//The comment up here is old. It should work now.
				//k=(int) (k-config.getBucketOver()*size*items.size());
				System.out.println("NUOVO BUCKET CON DIMENSIONE: "+size);
				System.out.println("NUOVO PUNTO DI PARTENZA: "+k);
				System.out.println("");
			}
		}
		if(b.size() < size && b.size() > 0) {
			buckets.add(b);
		}
		
		return buckets;
	}
}

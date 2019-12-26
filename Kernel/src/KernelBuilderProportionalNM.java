import java.util.List;

public class KernelBuilderProportionalNM implements KernelBuilder {

	@Override
	public Kernel build(List<Item> items, List<Constraint> constraints, Configuration config) {
		Kernel kernel = new Kernel();
		
		for(Item it : items) {
			if(it.getXr()>0 && kernel.size()<Math.round
			(config.getKernelSize()*items.size()/constraints.size())) {
				kernel.addItem(it);
			}
		}
		return kernel;
	}

}

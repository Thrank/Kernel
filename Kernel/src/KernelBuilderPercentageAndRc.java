import java.util.List;

public class KernelBuilderPercentageAndRc implements KernelBuilder {

	@Override
	public Kernel build(List<Item> items, List<Constraint> constraints, Configuration config) {
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			//This kernel will try to add positive Rc at the beginning to avoid to adding them lately
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size())
					&& it.getRc()>=0)
			{
				kernel.addItem(it);
			}
		}	
		return kernel;
	}

}

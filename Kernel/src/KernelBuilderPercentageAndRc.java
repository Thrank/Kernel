import java.util.List;

public class KernelBuilderPercentageAndRc implements KernelBuilder {

	@Override
	public Kernel build(List<Item> items, Configuration config) {
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size())
					&& it.getRc()>=0)
			{
				kernel.addItem(it);
			}
		}	
		return kernel;
	}

}

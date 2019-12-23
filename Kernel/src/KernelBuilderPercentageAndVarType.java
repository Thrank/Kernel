import java.util.List;

public class KernelBuilderPercentageAndVarType implements KernelBuilder {

	@Override
	public Kernel build(List<Item> items, Configuration config) {
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			//This kernel will add only binary variable to the kernel set
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size())
					&& it.getVarType()=='B')
			{
				kernel.addItem(it);
			}
		}	
		return kernel;
	}

}

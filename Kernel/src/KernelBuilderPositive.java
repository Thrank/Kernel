import java.util.List;

public class KernelBuilderPositive implements KernelBuilder
{
	@Override
	public Kernel build(List<Item> items, List<Constraint> constraints,Configuration config)
	{
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			if(it.getXr()> 0)
			{
				kernel.addItem(it);
			}
		}
		return kernel;
	}
}

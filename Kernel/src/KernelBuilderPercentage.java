import java.util.List;

public class KernelBuilderPercentage implements KernelBuilder
{
	@Override
	public Kernel build(List<Item> items, List<Constraint> constraints, Configuration config)
	{
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size()))
			{
				kernel.addItem(it);
			}
		}
		System.out.println("VARIABILI POSTE NEL KERNEL: "+ kernel.size());
		return kernel;
	}
}
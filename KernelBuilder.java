import java.util.List;

public interface KernelBuilder
{
	public Kernel build(List<Item> items, List<Constraint> constraints, Configuration config);
}
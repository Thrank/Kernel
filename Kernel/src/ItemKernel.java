
public class ItemKernel {
	private String name;
	private double value;
	private double obj;
	
	public ItemKernel(String name, double value, double obj) {
		this.name = name;
		this.value = value;
		this.obj = obj;
	}
	
	public double getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
}

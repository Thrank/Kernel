public class Item
{
	private String name;
	private double rc;
	private double xr;
	private char varType;
	private double lowerBound;
	private double upperBound;
	private double obj;
	private int occ;
	private double RcLB;
	private double deltaUBLB;
	
	public Item(String name, double xr, double rc, char varType, double lowerBound, double upperBound,
			double obj, int occ, double RcLB, double deltaUBLB)
	{
		this.name = name;
		this.xr = xr;
		this.rc = rc;
		this.varType = varType;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.obj = obj;
		this.occ = occ;
		this.RcLB = RcLB;
		this.deltaUBLB = deltaUBLB;
	}
	
	public Item(String name, double value, double obj) {
		this.name = name;
		this.xr = value;
		this.obj = obj;
	}

	public String getName()
	{
		return name;
	}
	
	public double getRc()
	{
		return rc;
	}
	
	public double getXr()
	{
		return xr;
	}
	
	public double getAbsoluteRC()
	{
		return Math.abs(rc);
	}
	
	public char getVarType() {
		return varType;
	}
	public double getLowerBound() {
		return lowerBound;
	}
	public double getUpperBound() {
		return upperBound;
	}
	public double getObjCoeff() {
		return obj;
	}
	public int getOcc() {
		return occ;
	}
	public double getRcLB() {
		return RcLB;
	}
	public double getDeltaUBLB() {
		return deltaUBLB;
	}
}
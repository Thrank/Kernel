
public class Constraint {
	
	private String name;
	private char sense;
	private double rhs;
	
	public Constraint(String name, char sense, double rhs) {
		this.name =name;
		this.sense = sense;
		this.rhs = rhs;
	}

}

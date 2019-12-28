import java.util.List;

public class Constraint {
	
	private String name;
	private char sense;
	private double rhs;
	private List<String> varsName;
	
	public Constraint(String name, char sense, double rhs, List<String> varsName) {
		this.name =name;
		this.sense = sense;
		this.rhs = rhs;
		this.varsName = varsName;
	}
	
	public String getConsName() {
		return name;
	}
	public char getSense() {
		return sense;
	}
	public double getRHS() {
		return rhs;
	}
	public List<String> getVincolo() {
		return varsName;
	}

}

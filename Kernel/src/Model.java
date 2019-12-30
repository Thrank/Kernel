import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Map;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRB.CharAttr;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.IntAttr;
import gurobi.GRB.StringAttr;
import gurobi.GRBCallback;
import gurobi.GRBColumn;
import gurobi.GRBConstr;

public class Model
{
	private String mpsFilePath;
	private String logPath;
	private int timeLimit;
	private Configuration config;
	private boolean lpRelaxation;
	private GRBEnv env;
	private GRBModel model;
	private GRBConstr constr;
	private GRBLinExpr linExpr;
	private boolean hasSolution;
	private double positiveThreshold = 1e-5;
	
	public Model(String mpsFilePath, String logPath, int timeLimit, Configuration config, boolean lpRelaxation)
	{
		this.mpsFilePath = mpsFilePath;
		this.logPath = logPath;
		this.timeLimit = timeLimit;	
		this.config = config;
		this.lpRelaxation = lpRelaxation;
		this.hasSolution = false;
	}
	
	public void buildModel()
	{
		try
		{
			env = new GRBEnv();
			setParameters();
			model = new GRBModel(env, mpsFilePath);
			
			if(lpRelaxation) {
				System.out.println("ESEGUO IL RILASSATO\n");
				model = model.relax();
			}
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setParameters() throws GRBException
	{
		env.set(GRB.StringParam.LogFile, logPath+"log.txt");
		env.set(GRB.IntParam.Threads, config.getNumThreads());
		env.set(GRB.IntParam.Presolve, config.getPresolve());
		env.set(GRB.DoubleParam.MIPGap, config.getMipGap());
		if (timeLimit > 0)
			env.set(GRB.DoubleParam.TimeLimit, timeLimit);
		//env.set(GRB.IntParam.Method, 0);
		//env.set(GRB.IntParam.Presolve, 0);
	}
	
	public void solve()
	{
		try
		{
			System.out.println("SONO A MODEL OPTIMIZE\n");
			model.optimize(); //Gurobi al termine di questa operazione risponde con dati sull'ottimizzazione del problema
			if(model.get(IntAttr.SolCount) > 0)
				hasSolution = true;
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	
	public List<String> getVarNames()
	{
		List<String> varNames = new ArrayList<>();
		//Look at all variables inside the model
		for(GRBVar v : model.getVars())
		{
			try
			{
				varNames.add(v.get(StringAttr.VarName));
			} catch (GRBException e)
			{
				e.printStackTrace();
			}
		}
		return varNames;
	}

	public double getVarValue(String v)
	{
		try
		{
			if(model.get(IntAttr.SolCount) > 0)
			{
				return model.getVarByName(v).get(DoubleAttr.X);
			}
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public double getVarRC(String v)
	{
		try
		{
			if(model.get(IntAttr.SolCount) > 0)
			{
				return model.getVarByName(v).get(DoubleAttr.RC);
			}
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	//Take the varType: C = continuous, B = binary, I = integer, S = semi-
	//-continuous, N = semi-integer.
	public char getVarType(String v) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				return model.getVarByName(v).get(CharAttr.VType);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return 'e';
	}

	public double getLowerBound(String v) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				return model.getVarByName(v).get(DoubleAttr.LB);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public double getUpperBound(String v) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				return model.getVarByName(v).get(DoubleAttr.UB);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public double getObjCoeff(String v) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				return model.getVarByName(v).get(DoubleAttr.Obj);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public double getRcLB(String v) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				double Rc = model.getVarByName(v).get(DoubleAttr.RC);
				double LowerBound = model.getVarByName(v).get(DoubleAttr.LB);
				return Rc*LowerBound;
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	//Method to search the occurrences of a variables inside constraints.
	public int getVarOcc(String v) {
		try {
			int occ=0;
			if(model.get(IntAttr.SolCount) > 0) {
				GRBVar variable = model.getVarByName(v);
				GRBColumn column = model.getCol(variable);
				occ = column.size();
				/*GRBVar[] variables = model.getVars();
				System.out.println("NUMERO VARIABILI: "+variables.length);
				for(int i=0; i<variables.length; i++) {
					if(variables[i].get(StringAttr.VarName).equalsIgnoreCase(v)) {
						GRBColumn column = model.getCol(variables[i]);
						occ = column.size();
					}
				}*/
				System.out.println("OCCORRENZE: "+occ);
				return occ;
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void disableItems(/*HashMap<Integer, Item> toDisable*/
			List<Item> items)
	{
		try
		{
			/*toDisable.forEach((k, v) -> {
				try {
					model.addConstr(model.getVarByName(v.getName()),
							GRB.EQUAL, 0, "FIX_VAR_"+v.getName());
				} catch (GRBException e) {
					e.printStackTrace();
				}
			});*/
			for(Item it : items)
			{
				//it fix variable "to disable" in a new constraint = 0.
				model.addConstr(model.getVarByName(it.getName()),
				GRB.EQUAL, 0, "FIX_VAR_"+it.getName());
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void exportSolution()
	{
		try
		{
			model.write("bestSolution.sol");
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	
	public void readSolution(String path)
	{
		try
		{
			model.read(path);
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	
	public void readSolution(Solution solution)
	{
		try
		{
			for(GRBVar var : model.getVars())
			{
				var.set(DoubleAttr.Start, solution.getVarValue(var.get(StringAttr.VarName)));
			}
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}

	public boolean hasSolution()
	{
		return hasSolution;
	}
	
	public Solution getSolution()
	{
		Solution sol = new Solution();
		
		try
		{
			sol.setObj(model.get(DoubleAttr.ObjVal));
			Map<String, Double> vars = new HashMap<>();
			for(GRBVar var : model.getVars())
			{
				vars.put(var.get(StringAttr.VarName), var.get(DoubleAttr.X));
			}
			sol.setVars(vars);
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
		return sol;
	}
	
	/*public void removeVarSolution(List<Item> items) {
		Solution sol = new Solution();
		try {
			sol.setObj(model.get(DoubleAttr.ObjVal));
			Map<String, Double> vars = new HashMap<>();
			for(int i=0; i<items.size();i++) {
				vars.put(items.get(i).getName(), items.get(i).getXr());
			}
			sol.unsetVars(vars);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	*/
	
	public void addBucketConstraint(List<Item> items)
	{
		GRBLinExpr expr = new GRBLinExpr();
			
		try
		{
			for(Item it : items)
			{
				expr.addTerm(1, model.getVarByName(it.getName()));
			}
			model.addConstr(expr, GRB.GREATER_EQUAL, 1, "bucketConstraint");
		} catch (GRBException e)
		{
			e.printStackTrace();
		}	
	}

	public void addObjConstraint(double obj)
	{
		try
		{
			model.getEnv().set(GRB.DoubleParam.Cutoff, obj);
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	
	public /*HashMap<Integer, Item>*/List<Item> getSelectedItems(List<Item> items/*HashMap<Integer, Item> hashItems*/)
	{
		//HashMap<Integer, Item> hashSelected = new HashMap<>();
		List<Item> selected = new ArrayList<>();
		/*for(Map.Entry<Integer, Item> entry : hashItems.entrySet()) {
			Integer key = entry.getKey();
			Item it = entry.getValue();
			try {
				if(model.getVarByName(it.getName()).get(DoubleAttr.X)> positiveThreshold)
					hashSelected.put(key, it);
			} catch (GRBException e) {
				e.printStackTrace();
			}
		}*/
		for(Item it : items)
		{
			try
			{
				if(model.getVarByName(it.getName()).get(DoubleAttr.X)> positiveThreshold)
					selected.add(it);
			} catch (GRBException e)
			{
				e.printStackTrace();
			}
		}
		return selected;
		//return hashSelected;
	}
	
	public SortedMap<String, Item> getSelectedItems(SortedMap<String,
			Item> items) {
		SortedMap<String, Item> selected = new TreeMap<>();
		for(String it: items.keySet()) {
			try {
				if(model.getVarByName(it).get(DoubleAttr.X)> positiveThreshold)
					selected.put(it, items.get(it));
			} catch (GRBException e) {
				e.printStackTrace();
			}
		}
		return selected;
	}
	
	public void setCallback(GRBCallback callback)
	{
		model.setCallback(callback);
	}

	public List<String> getConsNames() {
		List<String> consNames = new ArrayList<>();
		//Look at all variables inside the model
		for(GRBConstr c : model.getConstrs())
		{
			try
			{
				consNames.add(c.get(StringAttr.ConstrName));
			} catch (GRBException e)
			{
				e.printStackTrace();
			}
		}
		return consNames;
	}

	public char getSense(String c) {
		try {
			if(model.get(IntAttr.SolCount) > 0) {
				return model.getConstrByName(c).get(CharAttr.Sense);
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return 'e';
	}

	public double getRightHandSide(String c) {
		try {
			if(model.get(IntAttr.SolCount) > 0)
				return model.getConstrByName(c).get(DoubleAttr.RHS);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public List<String> getVincolo(String c) {
		List<String> varsName = new ArrayList<>();
		int size = 0;
		GRBVar var;
		String name;
		try {
			constr = model.getConstrByName(c);
			linExpr = model.getRow(constr);
			size = linExpr.size();
			if(linExpr.size()==1) {
				var = linExpr.getVar(0);
				name = var.get(StringAttr.VarName);
				varsName.add(name);
				return varsName;
			} else {
				for(int i=0; i<size; i++) {
					var = linExpr.getVar(i);
					name = var.get(StringAttr.VarName);
					varsName.add(name);
				}
				return varsName;
			}
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return null;
	}

	//If the variable is fixed, add constraint to the model.
	public void addVarFixedConstraints(String v, double value) {
		GRBLinExpr constraint = new GRBLinExpr();
		try {
			model.addConstr(constraint, GRB.EQUAL, value, v);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		
		
	}
}

	/*public void disableItems(List<Item> items) {
		{
			try
			{
				for(Item it : items)
				{
					model.addConstr(model.getVarByName(it.getName()),
					GRB.EQUAL, 0, "FIX_VAR_"+it.getName());
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}*/
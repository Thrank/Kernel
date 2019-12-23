import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import gurobi.GRBCallback;

public class KernelSearch
{
	//private boolean k=true;
	//private boolean tempoLimite = false;
	
	private String instPath;
	private String logPath;
	private Configuration config;
	private HashMap<String, Item> hashItems;
	private List<Item> items;
	private List<Constraint> constraints;
	private ItemSorter sorter;
	private BucketBuilder bucketBuilder;
	private KernelBuilder kernelBuilder;
	private int tlim;
	private Solution bestSolution;
	private List<Bucket> buckets;
	//private HashMap<Integer, Item> hashBuckets;
	private Kernel kernel;
	private int tlimKernel;
	private int tlimBucket;
	private int numIterations;
	private GRBCallback callback;
	private int timeThreshold = 15; //it was initialized at 5 in the original code.
	private boolean notTheFirstIteraction = false;
	
	
	private Instant startTime;

	private int iter;
	private int tempoAttuale;
	
	public KernelSearch(String instPath, String logPath, Configuration config)
	{
		this.instPath = instPath;
		this.logPath = logPath;
		this.config = config;
		bestSolution = new Solution();
		configure(config);
	}
	
	private void configure(Configuration configuration)
	{
		sorter = config.getItemSorter();
		tlim = config.getTimeLimit();
		bucketBuilder = config.getBucketBuilder();
		kernelBuilder = config.getKernelBuilder();
		tlimKernel = config.getTimeLimitKernel();
		numIterations = config.getNumIterations();
		tlimBucket = config.getTimeLimitBucket();
	}
	
	public Solution start()
	{
		startTime = Instant.now();
		System.out.println("LAVORO INIZIATO A: "+startTime);
		callback = new CustomCallback(logPath, startTime);
		//the last true is to let the system execute the lp relaxation
		Model model = new Model(instPath, logPath, tlim, config, true);
		model.buildModel();
		model.solve();
		
		items = buildItems(model);
		constraints = buildConstraints(model);
		//If the problem is too big, give to the analysis more time
		if(items.size()>= 10000) {
			System.out.println("TEMPI ANALISI KERNEL E BUCKET AUMENTATI!\n");
			tlimBucket=200;
			tlimKernel=100;
		}
		
		sorter.sort(items);
		
		kernel = kernelBuilder.build(items, config);
		buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
		solveKernel();
		iterateBuckets();
		
		return bestSolution;
	}
	
	private List<Constraint> buildConstraints(Model model) {
		List<Constraint> constraints = new ArrayList<>();
		List<String> consNames = model.getConsNames();
		for(String c : consNames) {
			char sense = model.getSense(c);
			double rhs = model.getRightHandSide(c);
			
			Constraint cons = new Constraint(c, sense, rhs);
			constraints.add(cons);
		}
		return constraints;
	}

	private List<Item> buildItems(Model model)
	{
		//Model model = new Model(instPath, logPath, tlim/*config.getTimeLimit()*/, config, true);
		// time limit equal to the global time limit
		//model.buildModel();
		//model.solve(); //the LP Relaxation
		//HashMap<String, Item> hashItems = new HashMap<>();
		List<Item> items = new ArrayList<>();
		List<String> varNames = model.getVarNames();
		//This cycle is reasonable fast
		for(String v : varNames)
		{
			double value = model.getVarValue(v);
			double rc = model.getVarRC(v); // can be called only after solving the LP relaxation
			char varType = model.getVarType(v);
			double lowerBound = model.getLowerBound(v);
			double upperBound = model.getUpperBound(v);
			double obj = model.getObjCoeff(v); //this should be the coeff in the object function related to the variable
			double RcLB = model.getRcLB(v);
			//this operation is very long (fixed!)
			int occ = model.getVarOcc(v); //take occurrences
			double deltaUBLB = upperBound-lowerBound;
			//System.out.println("Variabile: "+ v +" "+ lowerBound +" "+ upperBound);
			Item it = new Item(v, value, rc, varType, lowerBound, upperBound, obj, occ, RcLB, deltaUBLB);
			//hashItems.put(v, it);
			items.add(it);
		}
		return items;
	}
	
	private void solveKernel()
	{
		//It's not so useful, but we add it to be sure
		if(getRemainingTime()<=timeThreshold)
			return;
		Model model = new Model(instPath, logPath, Math.min(tlimKernel, getRemainingTime()), config, false);	
		model.buildModel();
		if(!bestSolution.isEmpty())
			model.readSolution(bestSolution);
		
		List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList());
		model.disableItems(toDisable);
		model.setCallback(callback);
		model.solve();
		
		System.out.println("\nSONO NEL SOLVEKERNEL!\n");
		
		if(model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj() || bestSolution.isEmpty()))
		{
			bestSolution = model.getSolution();
			//this one write the solution in the txt
			model.exportSolution();
		}
		
		System.out.println("\nSONO NEL SOLVEKERNEL 2!\n");
	}
	
	private void iterateBuckets()
	{
		System.out.println("\nSONO NELL'ITERATEBUCKET!\n");
		for (int i = 0; i < numIterations; i++)
		{
			System.out.println("\n\nSONO NEL FOR DELL'ITERATEBUCKET!\n");
			tempoAttuale = getRemainingTime(); //it's casted to int in the method
			System.out.println("TEMPO RIMANENTE: "+tempoAttuale+" TEMPO MASSIMO: "+timeThreshold);
			if(getRemainingTime() <= timeThreshold) //{
				return;
			System.out.println("ITERAZIONE: "+ i);
			solveBuckets();
			System.out.println("\nFINITA LA RISOLUZIONE BUCKET\n");
			if(i > 0) {
				notTheFirstIteraction = true;
			}
		}
	}

	private void solveBuckets()
	{
		iter=0;
		//Iterate every single buckets and try to solve one at one
		for(Bucket b : buckets)
		{
			System.out.println("BUCKET: "+iter++);
			System.out.println("\nSONO NEL SOLVEBUCKET!\n");
			List<Item> toDisable = new ArrayList<Item>();
			int counter=0;
			System.out.println("NUMERO ITEM: "+items.size());
			//This operation is slow as fuck (fixed!)
			for(Item it : items) {
				if(counter%100==0)
					System.out.println("NEL CICLO: "+counter);
				if(!kernel.contains(it) && !b.contains(it)) {
					//toDisable.put(i, it);
					//i++;
					toDisable.add(it);
				}
				//If we want to stop this cycle when it exceeds the time limit.
				/*if(getRemainingTime()<=0) {
					return;
				}*/
				counter++;
			}
			//List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it) && !b.contains(it)).collect(Collectors.toList());
			System.out.println("\nSONO DOPO IL CICLO!\n");
			if(getRemainingTime() <= timeThreshold) //{
				return;
			Model model = new Model(instPath, logPath, Math.min(tlimBucket, getRemainingTime()), config, false);
			System.out.println("TEMPO RIMANENTE: "+getRemainingTime());
			System.out.println("TEMPO MINIMO TRA I DUE PER BUCKET: "+Math.min(tlimBucket, getRemainingTime()));
			model.buildModel();
			System.out.println("\nMODELLO FATTO!\n");
			model.disableItems(toDisable);
			//model.disableItems(toDisable);
			//model.addBucketConstraint(b.getItems()); // can we use this constraint regardless of the type of variables chosen as items?
			System.out.println("\nMODELLO FATTO 2!\n");
			if(!bestSolution.isEmpty())
			{
				model.addObjConstraint(bestSolution.getObj());		
				model.readSolution(bestSolution);
			}
			System.out.println("\nMODELLO FATTO 3\n");
			model.setCallback(callback);
			System.out.println("\nMODELLO FATTO 4\n");
			model.solve();
			System.out.println("\nMODELLO FATTO 5\n");
			if(getRemainingTime() <= timeThreshold)
				return;
			if(model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj() || bestSolution.isEmpty()))
			{
				bestSolution = model.getSolution();
				SortedMap<String, Item> selected = model.getSelectedItems(b.getItemMap());
				kernel.addItem(selected);
				b.removeItem(selected);
				model.exportSolution();
			}
			System.out.println("\nMODELLO FATTO 6\n");
			tempoAttuale = getRemainingTime(); //it's casted to int in the method
			System.out.println("TEMPO RIMANENTE: "+tempoAttuale+" TEMPO MASSIMO: "+timeThreshold);
			if(getRemainingTime() <= timeThreshold) {
				return;
			}
			System.out.println("\nFINE SOLVEBUCKETS\n");
		}
		//After the first iteraction of all buckets, look to throw away some variable inside kernel
		//if(notTheFirstIteraction)
			//kernelControl();
	}
	//this method should throw away some elements of the kernel
	private void kernelControl() {
		
		//List<Item> toThrowAway = new ArrayList<>();
		
		Model model = new Model(instPath, logPath, Math.min(tlimKernel, getRemainingTime()), config, false);
		model.buildModel();
		//We can set also the callback here if we need.
		//model.solve();
		List<String> varNames = model.getVarNames();
		List<Item> itemsKernel = new ArrayList<>();
		for(Item it : items) {
			if(kernel.contains(it)) {
				for(String v : varNames) {
					double value = model.getVarValue(v);
					double obj = model.getObjCoeff(v);
					Item itKernel = new Item(v, value, obj);
					itemsKernel.add(itKernel);
				}
			}
		}
		itemsKernel.sort(Comparator.comparing(Item::getXr));
		int sizeK = (int) 0.005*itemsKernel.size();
		for(int i=0; i<sizeK; i++) {
			
		}
	}

	private int getRemainingTime()
	{
		//return (int) (tlim - Duration.between(startTime, Instant.now()).getSeconds());
		return (int) (tlim - Duration.between(startTime, Instant.now()).toMillis()/(double)1e3);
	}
}
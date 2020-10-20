import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import gurobi.GRBCallback;

public class KernelSearch
{
	//private boolean k=true;
	//private boolean tempoLimite = false;
	
	private String instPath;
	private String logPath;
	private Configuration config;
	//private HashMap<String, Item> hashItems;
	private List<Item> items;
	private List<Constraint> constraints;
	private ItemSorter sorter;
	private BucketBuilder bucketBuilder;
	private KernelBuilder kernelBuilder;
	private int tlim;
	private Solution bestSolution;
	private double oldSolution = 0;
	private List<Bucket> buckets;
	//private HashMap<Integer, Item> hashBuckets;
	private Kernel kernel;
	private int tlimKernel;
	private int tlimBucket;
	private int numIterations;
	private GRBCallback callback;
	private int timeThreshold = 15; //it was initialized at 5 in the original code.
	//private boolean notTheFirstIteraction = false;
	private int configTest = 0;
	
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
			//set new time to analyze buckets and kernel, because the problem is too big and we need more time.
			//we sacrifice worst variables in our analysis.
			tlimBucket=200; //standard=200 seconds
			tlimKernel=100; //standard=100 seconds
		}
		
		sorter.sort(items);
		
		kernel = kernelBuilder.build(items, constraints, config);
		buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
		solveKernel();
		iterateBuckets();
		
		return bestSolution;
	}
	
	private List<Constraint> buildConstraints(Model model) {
		List<Constraint> constraints = new ArrayList<>();
		List<String> consNames = model.getConsNames();
		List<String> varsName = new ArrayList<>();
		for(String c : consNames) {
			char sense = model.getSense(c);
			double rhs = model.getRightHandSide(c);
			varsName = model.getVincolo(c);
			
			Constraint cons = new Constraint(c, sense, rhs, varsName);
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
			//if a variable is fixed, add a new constraint to fix it to one of his bounds.
			//if(lowerBound == upperBound)
			//	model.addVarFixedConstraints(v, lowerBound); //no tested!
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
		if(!bestSolution.isEmpty()) {
			model.readSolution(bestSolution);
		}
		
		List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList());
		model.disableItems(toDisable);
		model.setCallback(callback);
		model.solve();
		
		System.out.println("\nSONO NEL SOLVEKERNEL!\n");
		//this is ok only for the first iteration
		if((model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj() || bestSolution.isEmpty()))
				/*&& !notTheFirstIteraction*/)
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
		oldSolution = bestSolution.getObj();
		for (int i = 0; i < numIterations; i++)
		{
			oldSolution = bestSolution.getObj();
			System.out.println("\n\nSONO NEL FOR DELL'ITERATEBUCKET!\n");
			tempoAttuale = getRemainingTime(); //it's casted to int in the method
			System.out.println("TEMPO RIMANENTE: "+tempoAttuale+" TEMPO MASSIMO: "+timeThreshold);
			if(getRemainingTime() <= timeThreshold) //{
				return;
			System.out.println("ITERAZIONE: "+ i);
			solveBuckets();
			System.out.println("\nFINITA LA RISOLUZIONE BUCKET\n");
			/*if(i > 0) {
				notTheFirstIteraction = true;
			}*/
		}
		
		//Attention: this debugging function must be removed in the final version of the project
		/*System.out.println("TERMINATA PRIMA ITERAZIONE DI BUCKET!\nPREMI ENTER PER CONTINUARE.");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
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
			//This operation is very slow (fixed!)
			for(Item it : items) {
				if(counter%100==0)
					System.out.println("NEL CICLO: "+counter);
				if(!kernel.contains(it) && !b.contains(it)) {
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
		if(!bestSolution.isEmpty() /*|| bestSolution.getObj()==oldSolution*/) {
			//We should put here another condition because like this is pretty unuseful
			if(bestSolution.getObj() == oldSolution) {
				if(config.kernelControlActivated) {
					System.out.println("SOLUZIONE VECCHIA UGUALE ALLA NUOVA -> KERNEL CONTROL");
					kernelControl(buckets.get(buckets.size()-1)); //we have to give to kernelControl the last buckets (size of array -1).
				}
			}
		} else { //no best solution found -> do everything from the start with other parameter.
			//change sorter (i.e.) if no solutions has been found in all bucket
			if(config.resetAll) {
				System.out.println("NESSUNA SOLUZIONE TROVATA -> RIORDINO E RICOSTRUISCO");
				configTest++;
				if(configTest == 1) {
					sorter = new ItemSorterByRcPerLowerBound();
					//kernelBuilder remains the same
					config.setBucketSize(0.4);
					config.setBucketSizeVariable(0.2, 0.3);
					config.setBucketOver(0.2);
					bucketBuilder = new BucketBuilderVariableOverlapping();
					
					//re-build everything and start again
					sorter.sort(items);
					kernel = kernelBuilder.build(items, constraints, config);
					buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
					//if you are here, one iteration is gone
					numIterations -= 1;
					System.out.println("CONFIGURAZIONE 2\nSORTER: 7\nKERNELBUILDER: 0\nBUCKETBUILDER: 4\nBUCKETSIZE: "+config.getBucketSize()+" "+config.getBucketSizeStart()+" "+config.getBucketSizeIncremental()+"\n");
					//restart working on it
					solveKernel();
					iterateBuckets();
					//out of this method
					return;
				}
				if(configTest == 2) {
					sorter = new ItemSorterByValueAndAbsoluteRC();
					kernelBuilder = new KernelBuilderPercentage();
					config.setBucketSize(0.2);
					config.setBucketSizeVariable(0.4, 0.5);
					bucketBuilder = new BucketBuilderVariableOverlappingAlternative();
					sorter.sort(items);
					kernel = kernelBuilder.build(items,  constraints, config);
					buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
					System.out.println("TEMPI DI LAVORO VINCOLATI A KERNEL:20, BUCKET:30");
					tlimKernel = 20;
					tlimBucket = 30;
					numIterations -= 1;
					System.out.println("CONFIGURAZIONE 3\nSORTER: 0\nKERNELBUILDER: 1\nBUCKETBUILDER: 6\nBUCKETSIZE: "+config.getBucketSize()+" "+config.getBucketSizeStart()+" "+config.getBucketSizeIncremental()+"\n");
					solveKernel();
					iterateBuckets();
					return;
				}
				if(configTest == 3) {
					sorter = new ItemSorterByRcPerLowerBound();
					kernelBuilder = new KernelBuilderPositive();
					config.setBucketSize(0.2);
					config.setBucketSizeVariable(0.4, 0.5);
					bucketBuilder = new BucketBuilderVariableOverlappingAlternative();
					sorter.sort(items);
					kernel = kernelBuilder.build(items,  constraints, config);
					buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
					System.out.println("TEMPI DI LAVORO VINCOLATI A KERNEL:20, BUCKET:60");
					tlimKernel = 20;
					tlimBucket = 120;
					numIterations -= 1;
					System.out.println("CONFIGURAZIONE 4\nSORTER: 7\nKERNELBUILDER: 0\nBUCKETBUILDER: 7\nBUCKETSIZE: "+config.getBucketSize()+" "+config.getBucketSizeStart()+" "+config.getBucketSizeIncremental()+"\n");
					solveKernel();
					iterateBuckets();
					return;
				}
			}
		}
	}
	//this method should throw away some elements of the kernel
	private void kernelControl(Bucket b) {
		System.out.println("SONO DENTRO KERNELCONTROL!");
		//SortedMap<String, Item> toThrowAway = new TreeMap<>();
		//I have to build the model again? Gurobi really change names?
		Model model = new Model(instPath, logPath, Math.min(tlimKernel, getRemainingTime()), config, false);
		model.buildModel();
		//We can set also the callback here if we need.
		//not sure if we have to solve it again. We've already solved it with the last buckets, so data should be updated.
		//model.solve();
		List<Item> itemsKernel = new ArrayList<>();
		System.out.println("NUMERO ITEM: "+ items.size());
		for(Item it : items) {
			if(kernel.contains(it)) {
				String v = it.getName();
				double value = it.getXr();
				double obj = it.getObjCoeff();
				//double value = model.getVarValue(v);
				//double obj = model.getObjCoeff(v);
				Item itKernel = new Item(v, value, obj);
				itemsKernel.add(itKernel);
				System.out.println("\nNUOVA VARIABILE TROVATA NEL KERNEL!");
			}
		}
		System.out.println("\nVARIABILI NEL KERNEL: "+ itemsKernel.size()+"\n");
		//with this sorting we have all the item that we want to remove at the beginning of the list.
		itemsKernel.sort(Comparator.comparing(Item::getXr).thenComparing(Item::getObjCoeff));
		
		//Decide how much variables to throw away.
		int sizeK = (int) Math.round(0.05*itemsKernel.size()); //in the test there was a 10% of variables, but it's too much
		
		//throw away from kernel and put inside the last bucket
		for(Item it : itemsKernel.subList(0, sizeK)){
			//Item it = itemsKernel.get(i);
			kernel.removeItem(it);
			b.addItem(it);
			System.out.println("RIMOSSA VARIABILE DAL KERNEL");
		}
		System.out.println("VARIABILI BUTTATE: "+sizeK);
		//we have to put out from the solution the variables we removed from the kernel set
		//model.removeVarSolution(itemsKernel.subList(0, sizeK));
		solveKernel();
	}

	private int getRemainingTime()
	{
		//return (int) (tlim - Duration.between(startTime, Instant.now()).getSeconds());
		return (int) (tlim - Duration.between(startTime, Instant.now()).toMillis()/(double)1e3);
	}
}
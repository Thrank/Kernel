import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gurobi.GRBCallback;

public class KernelSearch
{
	//private boolean k=true;
	//private boolean tempoLimite = false;
	
	private String instPath;
	private String logPath;
	private Configuration config;
	private List<Item> items;
	private ItemSorter sorter;
	private BucketBuilder bucketBuilder;
	private KernelBuilder kernelBuilder;
	private int tlim;
	private Solution bestSolution;
	private List<Bucket> buckets;
	private Kernel kernel;
	private int tlimKernel;
	private int tlimBucket;
	private int numIterations;
	private GRBCallback callback;
	private int timeThreshold = 15; //it was initialized at 5 in the original code.
	
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
		callback = new CustomCallback(logPath, startTime);
		items = buildItems();
		sorter.sort(items);
		/*if(k==true) {
			for(Item item2 : items) {
				System.out.println(item2.getName()+" "+item2.getXr()+" "+item2.getRc());
				k=false;
			}
		} else {
			k=true;
		}*/
		kernel = kernelBuilder.build(items, config);
		buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), config);
		solveKernel();
		iterateBuckets();
		
		return bestSolution;
	}

	private List<Item> buildItems()
	{
		Model model = new Model(instPath, logPath, config.getTimeLimit(), config, true); // time limit equal to the global time limit
		model.buildModel();
		model.solve();
		List<Item> items = new ArrayList<>();
		List<String> varNames = model.getVarNames();
		for(String v : varNames)
		{
			double value = model.getVarValue(v);
			double rc = model.getVarRC(v); // can be called only after solving the LP relaxation
			Item it = new Item(v, value, rc);
			items.add(it);
		}
		return items;
	}
	
	private void solveKernel()
	{
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
				System.out.println("TEMPO RIMANENTE: "+tempoAttuale+"TEMPO MASSIMO: "+timeThreshold);
				if(getRemainingTime() <= timeThreshold) //{
					return;
				/*} else { //Se tempo per bucket troppo piccolo, lo raddoppio
					if(tempoLimite == true) {
						timeThreshold = timeThreshold*2;
						tempoLimite = false;
					}
				}*/
				System.out.println("ITERAZIONE: "+ i);
				solveBuckets();
				System.out.println("\nFINITA LA RISOLUZIONE BUCKET\n");
		}		
	}

	private void solveBuckets()
	{
		iter=0;
		for(Bucket b : buckets)
		{
			System.out.println("BUCKET: "+iter++);
			System.out.println("\nSONO NEL SOLVEBUCKET!\n");
			//I put this here because there is a problem in time limiting. Don't erase it (or if you hate this
			//erase instead the one at the end of this method.
			
			/*if(getRemainingTime() <= timeThreshold) {
				return;
			}*/
				
			
			List<Item> toDisable = new ArrayList<Item>();
			int counter=0;
			System.out.println("NUMERO ITEM: "+items.size());
			for(Item it : items) {
				if(counter%100==0)
					System.out.println("NEL CICLO: "+counter);
				if(!kernel.contains(it) && !b.contains(it)) {
					toDisable.add(it);
				}
				counter++;
			}
			//List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it) && !b.contains(it)).collect(Collectors.toList());
			System.out.println("\nSONO DOPO IL CICLO!\n");
			Model model = new Model(instPath, logPath, Math.min(tlimBucket, getRemainingTime()), config, false);	
			model.buildModel();
			System.out.println("\nMODELLO FATTO!\n");
			model.disableItems(toDisable);
		//	model.addBucketConstraint(b.getItems()); // can we use this constraint regardless of the type of variables chosen as items?
			System.out.println("\nMODELLO FATTO 2!\n");
			if(!bestSolution.isEmpty())
			{
				model.addObjConstraint(bestSolution.getObj());		
				model.readSolution(bestSolution);
			}
			System.out.println("\nMODELLO FATTO 3\n");
			model.setCallback(callback);
			model.solve();
			System.out.println("\nMODELLO FATTO 4\n");
			if(model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj()  || bestSolution.isEmpty()))
			{
				bestSolution = model.getSolution();
				List<Item> selected = model.getSelectedItems(b.getItems());
				selected.forEach(it -> kernel.addItem(it));
				selected.forEach(it -> b.removeItem(it));
				model.exportSolution();
			}
			System.out.println("\nMODELLO FATTO 5\n");
			tempoAttuale = getRemainingTime(); //it's casted to int in the method
			System.out.println("TEMPO RIMANENTE: "+tempoAttuale+"TEMPO MASSIMO: "+timeThreshold);
			if(getRemainingTime() <= timeThreshold) {
				return;
			}
			System.out.println("\nFINE SOLVEBUCKETS\n");
		}	
	}

	private int getRemainingTime()
	{
		//return (int) (tlim - Duration.between(startTime, Instant.now()).getSeconds());
		return (int) (tlim - Duration.between(startTime, Instant.now()).toMillis()/(double)1e3);
	}
}
public class Configuration
{
	private int numThreads;
	private double mipGap;
	private int presolve;
	private int timeLimit;
	private ItemSorter sorter;
	private double kernelSize;
	private double bucketSize;
	private double bucketSizeStart;
	private double bucketSizeIncremental;
	private double maxIncrement;
	private double bucketOver;
	private int bucketMax;
	private BucketBuilder bucketBuilder;
	private KernelBuilder kernelBuilder;
	private int timeLimitKernel;
	private int numIterations;
	private int timeLimitBucket;
	boolean kernelControlActivated;
	boolean resetAll;
	

	public BucketBuilder getBucketBuilder()
	{
		return bucketBuilder;
	}

	public double getBucketSize()
	{
		return bucketSize;
	}
	public double getBucketSizeStart() {
		return bucketSizeStart;
	}
	public double getBucketSizeIncremental() {
		return bucketSizeIncremental;
	}

	public ItemSorter getItemSorter()
	{
		return sorter;
	}

	public KernelBuilder getKernelBuilder()
	{
		return kernelBuilder;
	}

	public double getKernelSize()
	{
		return kernelSize;
	}

	public double getMipGap()
	{
		return mipGap;
	}

	public int getNumIterations()
	{
		return numIterations;
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	public int getPresolve()
	{
		return presolve;
	}

	public int getTimeLimit()
	{
		return timeLimit;
	}

	public int getTimeLimitBucket()
	{
		return timeLimitBucket;
	}

	public int getTimeLimitKernel()
	{
		return timeLimitKernel;
	}
	
	public int getBucketMax() {
		return bucketMax;
	}
	public double getMaxIncrement() {
		return maxIncrement;
	}
	public double getBucketOver() {
		return bucketOver;
	}

	public void setBucketBuilder(BucketBuilder bucketBuilder)
	{
		this.bucketBuilder = bucketBuilder;
	}

	public void setBucketSize(double bucketSize)
	{
		this.bucketSize = bucketSize;
	}
	// For when we need variable buckets
	public void setBucketSizeVariable(double bucketSizeStart, double bucketSizeEnd)
	{
		this.bucketSizeStart = bucketSizeStart;
		this.bucketSizeIncremental = bucketSizeEnd;
	}

	public void setItemSorter(ItemSorter sorter)
	{
		this.sorter = sorter;
	}

	public void setKernelBuilder(KernelBuilder kernelBuilder)
	{
		this.kernelBuilder = kernelBuilder;
	}

	public void setKernelSize(double kernelSize)
	{
		this.kernelSize = kernelSize;
	}

	public void setMipGap(double mipGap)
	{
		this.mipGap = mipGap;
	}

	public void setNumIterations(int numIterations)
	{
		this.numIterations = numIterations;
	}

	public void setNumThreads(int numThreads)
	{
		this.numThreads = numThreads;
	}

	public void setPresolve(int presolve)
	{
		this.presolve = presolve;
	}

	public void setTimeLimit(int timeLimit)
	{
		this.timeLimit = timeLimit;
	}

	public void setTimeLimitBucket(int timeLimitBucket)
	{
		this.timeLimitBucket = timeLimitBucket;
	}

	public void setTimeLimitKernel(int timeLimitKernel)
	{
		this.timeLimitKernel = timeLimitKernel;
	}

	public void setBucketMax(int bucketMax) {
		this.bucketMax = bucketMax;
		
	}

	public void setMaxIncrement(double maxIncrement) {
		this.maxIncrement = maxIncrement;
		
	}

	public void setBucketOver(double bucketOver) {
		this.bucketOver = bucketOver;
	}

	/*public void setBucketSizeStart(double bucketSizeStart) {
		this.bucketSizeStart = bucketSizeStart;
	}

	public void setBucketSizeEnd(double bucketSizeEnd) {
		this.bucketSizeEnd = bucketSizeEnd;
	}*/
	/*public void controlBucket(double bucketSizeEnd, double bucketSizeStart) {
		if(bucketSizeStart<=bucketSizeEnd) {
			System.out.println("ATTENZIONE: BUCKETSIZESTART e BUCKETSIZEEND impostati in maniera sbagliata!");
		}
	}*/
}
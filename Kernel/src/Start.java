public class Start
{
	public static void main(String[] args)
	{
		String pathmps = "./instances/hanoi5.mps";//args[0];
		String pathlog = "./hanoi5/"; //args[1];
		String pathConfig = "config.txt"; //args[2];
		Configuration config = ConfigurationReader.read(pathConfig);		
		KernelSearch ks = new KernelSearch(pathmps, pathlog, config);
		ks.start();
	}
}
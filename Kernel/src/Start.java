public class Start
{
	public static void main(String[] args)
	{
		String pathmps = "./instances/triptim2.mps";//args[0];
		String pathlog = "./triptim2/"; //args[1];
		String pathConfig = "config.txt"; //args[2];
		Configuration config = ConfigurationReader.read(pathConfig);		
		KernelSearch ks = new KernelSearch(pathmps, pathlog, config);
		ks.start();
	}
}
public class Start
{
	public static void main(String[] args)
	{
		String pathmps = "./instances/sct32.mps";//args[0];
		String pathlog = "./sct32/"; //args[1];
		String pathConfig = "config.txt"; //args[2];
		Configuration config = ConfigurationReader.read(pathConfig);		
		KernelSearch ks = new KernelSearch(pathmps, pathlog, config);
		ks.start();
	}
}
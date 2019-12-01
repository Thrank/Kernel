public class Start
{
	public static void main(String[] args)
	{
		String pathmps = "./instances/amaze22012-03-15i.mps";//args[0];
		String pathlog = "./amaze22012-03-15i/"; //args[1];
		String pathConfig = "config.txt"; //args[2];
		Configuration config = ConfigurationReader.read(pathConfig);		
		KernelSearch ks = new KernelSearch(pathmps, pathlog, config);
		ks.start();
	}
}
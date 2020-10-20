public class Start
{
	public static void main(String[] args)
	{
		String pathmps = "./instances/neos-4335793-snake.mps";//args[0];
		String pathlog = "./neos-4335793-snake/"; //args[1];
		String pathConfig = "config.txt"; //args[2];
		Configuration config = ConfigurationReader.read(pathConfig);		
		KernelSearch ks = new KernelSearch(pathmps, pathlog, config);
		ks.start();
	}
}
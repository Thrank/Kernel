import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationReader
{
	//static boolean iterateBucketAgain = false;

	public static Configuration read(String path)
	{
		Configuration config = new Configuration();
		
		List<String> lines = new ArrayList<>();
		
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path)))
        {
        	lines = br.lines().collect(Collectors.toList());
        } catch (IOException e)
		{
			e.printStackTrace();
		}
        
        for(String line : lines)
        {
        	String[] splitLine = line.split("\\s+");
        	
        	switch(splitLine[0])
			{
        		case "THREADS":
        			config.setNumThreads(Integer.parseInt(splitLine[1]));
        			break;
        		case "MIPGAP": 
        			config.setMipGap(Double.parseDouble(splitLine[1]));
        			break;
        		case "PRESOLVE":
        			config.setPresolve(Integer.parseInt(splitLine[1]));
        			break;
        		case "TIMELIMIT":
        			config.setTimeLimit(Integer.parseInt(splitLine[1]));
        			break;
        		case "SORTER":
        			switch(Integer.parseInt(splitLine[1]))
        			{
        				case 0:
        					config.setItemSorter(new ItemSorterByValueAndAbsoluteRC());
        					break;
        				case 1:
        					config.setItemSorter(new ItemSorterByAbsoluteRCAndValue());
        					break;
        				case 2:
        					config.setItemSorter(new ItemSorterPercentageByValueAndAbsoluteRC());
        					break;
        				case 3:
        					config.setItemSorter(new ItemSorterByOccurrences());
        					break;
        				case 4:
        					config.setItemSorter(new ItemSorterByBinVar());
        					break;
        				case 5:
        					config.setItemSorter(new ItemSorterByLowerBound());
        					break;
        				case 6:
        					config.setItemSorter(new ItemSorterByValueAndObj());
        					break;
        				case 7:
        					config.setItemSorter(new ItemSorterByRcPerLowerBound());
        					break;
        				case 8:
        					config.setItemSorter(new ItemSorterBydeltaUBLB());
        				default:
        					System.out.println("Unrecognized item sorter.");
        			}
        			break;
        		case "KERNELSIZE":
        			config.setKernelSize(Double.parseDouble(splitLine[1]));
        			break;
        		case "BUCKETSIZE":
        			config.setBucketSize(Double.parseDouble(splitLine[1]));
        			if(splitLine.length == 4)
        				config.setBucketSizeVariable(Double.parseDouble(splitLine[2]), Double.parseDouble(splitLine[3]));
        			break;
        		case "BUCKETMAX":
        			config.setBucketMax(Integer.parseInt(splitLine[1]));
        			break;
        		case "BUCKETBUILDER":
        			switch(Integer.parseInt(splitLine[1]))
        			{
        				case 0:
        					config.setBucketBuilder(new DefaultBucketBuilder());
        					break;
        				case 1:
        					config.setBucketBuilder(new BucketBuilderVariable());
        					break;
        				case 2:
        					config.setBucketBuilder(new BucketBuilderOverlapping());
        					break;
        				case 3:
        					config.setBucketBuilder(new BucketBuilderVariableLimited());
        					break;
        				case 4:
        					config.setBucketBuilder(new BucketBuilderVariableOverlapping());
        					break;
        				case 5:
        					config.setBucketBuilder(new BucketBuilderKernelMixed());
        					break;
        				case 6:
        					config.setBucketBuilder(new BucketBuilderVariableOverlappingAlternative());
        					break;
        				case 7:
        					config.setBucketBuilder(new BucketBuilderVariableOverlappingAlternative2());
        					break;
        				case 8:
        					config.setBucketBuilder(new BucketBuilderVariableMixedOverlapping());
        					//iterateBucketAgain = true;
        					break;
        				default:
        					System.out.println("Unrecognized bucket builder.");
        			}
        			break;
        		case "TIMELIMITKERNEL":
        			config.setTimeLimitKernel(Integer.parseInt(splitLine[1]));
        			break;
        		case "NUMITERATIONS":
        			config.setNumIterations(Integer.parseInt(splitLine[1]));
        			break;
        		case "TIMELIMITBUCKET":
        			config.setTimeLimitBucket(Integer.parseInt(splitLine[1]));
        			break;
        		case "KERNELBUILDER":
        			switch(Integer.parseInt(splitLine[1]))
        			{
        				case 0:
        					config.setKernelBuilder(new KernelBuilderPositive());
        					break;
        				case 1:
        					config.setKernelBuilder(new KernelBuilderPercentage());
        					break;
        				case 2:
        					config.setKernelBuilder(new KernelBuilderPercentageAndRc());
        					break;
        				case 3:
        					config.setKernelBuilder(new KernelBuilderPercentageAndVarType());
        					break;
        				default:
        					System.out.println("Unrecognized kernel builder.");
        			}
        			break;
        		default:
        			System.out.println("Unrecognized parameter name.");			
			}
        }
        return config;
	}
}
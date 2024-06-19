import java.util.*;

public class Test
{
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java Test <intensity> <resolution>");
			System.exit(0);
		}
	   
		String java_version = System.getProperty("java.version");
		String os           = System.getProperty("os.name");
		String os_version   = System.getProperty("os.version");
		String machine      = System.getProperty("os.arch");
		//System.out.println("Current java version is " + java_version);
		//System.out.println("Current os is " + os + " " + os_version + " on " + machine);
		//System.out.println("Image file is " + filename);
		int  intensity  = Integer.parseInt(args[0]);
		int  resolution = Integer.parseInt(args[1]);
		Test test       = new Test(intensity, resolution);
	}
	
	public Test(int intensity, int resolution)
	{
		ArrayList v  = new ArrayList();
		for(int i = 0; i < resolution; i++)
		{
		    ArrayList xy = new ArrayList();
		    for(int j = 0; j < resolution; j++)
		    {
		        ArrayList xyz= new ArrayList();
		        for(int k = 0; k < resolution; k++)
		        {
		        	double value = i * 100 + j * 10 + k;
		        	xyz.add(value);
		        }
		        xy.add(xyz);
		    }
		    v.add(xy);
		}
		
		ArrayList w = getAverage(v, resolution, resolution, resolution);
		
		double voxel = getVoxel(w, 0,  0,  0);
		System.out.println("Voxel is " + voxel);
	}
	
	public double getVoxel(ArrayList v, int x, int y, int z)
	{
		ArrayList xy    = (ArrayList) v.get(x);
		ArrayList xyz   = (ArrayList) xy.get(y);
		double    voxel = (double)    xyz.get(z);
		
		return voxel;
	}
	
	public void setVoxel(ArrayList v, int x, int y, int z, double value)
	{
		ArrayList xy    = (ArrayList) v.get(x);
		ArrayList xyz   = (ArrayList) xy.get(y);
		xyz.set(z, value);
	}
	
	public ArrayList getNeighbors(ArrayList v, int x, int y, int z, int xdim, int ydim, int zdim)
	{
		ArrayList w = new ArrayList();
		
		if(x > 0)
		{
			double voxel = getVoxel(v, x - 1, y, z);
			w.add(voxel);
		}
		if(x < xdim - 1)
		{
			double voxel = getVoxel(v, x + 1, y, z);
			w.add(voxel);	
		}
		if(y > 0)
		{
			double voxel = getVoxel(v, x, y - 1, z);
			w.add(voxel);
		}
		if(y < ydim - 1)
		{
			double voxel = getVoxel(v, x, y + 1, z);
			w.add(voxel);	
		}
		if(z > 0)
		{
			double voxel = getVoxel(v, x, y, z - 1);
			w.add(voxel);
		}
		if(z < zdim - 1)
		{
			double voxel = getVoxel(v, x, y, z + 1);
			w.add(voxel);	
		}
		
		return w;
	}
	
	public ArrayList getAverage(ArrayList v, int xdim, int ydim, int zdim)
	{
		ArrayList w = new ArrayList();
		
		for(int i = 0; i < xdim; i++)
		{
		    ArrayList xy = new ArrayList();
		    for(int j = 0; j < ydim; j++)
		    {
		        ArrayList xyz= new ArrayList();
		        for(int k = 0; k < zdim; k++)
		        {
		        	double value = 0;
		        	xyz.add(value);
		        }
		        xy.add(xyz);
		    }
		    w.add(xy);
		}
		
		for(int x = 0; x < xdim; x++)
		{
			for(int y = 0; y < ydim; y++)
			{
				for(int z = 0; z < zdim; z++)
				{
				    double voxel = getVoxel(v, x, y, z);  
				    
				    double total = voxel;
				    
				    ArrayList neighbors = getNeighbors(v, x, y, z, xdim, ydim, zdim);
				    
				    for(int i = 0; i < neighbors.size(); i++)
				    {
				        double neighbor = (double)neighbors.get(i);	
				        total          += neighbor;
				    }
				    
				    double average = total / (neighbors.size() + 1);
				    
				    setVoxel(w, x, y, z, average);
				}	
			}
		}
		
		return w;
	}
	
	
	
}
import java.util.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Test
{
	BufferedImage image;
	ImageCanvas   image_canvas;
	
	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.out.println("Usage: java Test <intensity> <difference> <resolution>");
			System.exit(0);
		}
	   
		String java_version = System.getProperty("java.version");
		String os           = System.getProperty("os.name");
		String os_version   = System.getProperty("os.version");
		String machine      = System.getProperty("os.arch");
		//System.out.println("Current java version is " + java_version);
		//System.out.println("Current os is " + os + " " + os_version + " on " + machine);
		
		int  intensity  = Integer.parseInt(args[0]);
		int  difference = Integer.parseInt(args[1]);
		int  resolution = Integer.parseInt(args[2]);
		Test test       = new Test(intensity, difference, resolution);
	}
	
	public Test(double intensity, int difference, int resolution)
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
		        	double value = 0;
		        	xyz.add(value);
		        }
		        xy.add(xyz);
		    }
		    v.add(xy);
		}
		
		setVoxel(v, resolution / 2 - difference, resolution / 2 - difference, resolution / 2, -intensity);
		setVoxel(v, resolution / 2 + difference, resolution / 2 + difference, resolution / 2,  intensity);
		
		ArrayList w = getAverage(v, resolution, resolution, resolution);
		
		double negative = getVoxel(w, resolution / 2 - difference,  resolution / 2 - difference,  resolution / 2);
		System.out.println("Negative pole is " + String.format("%.2f", negative));
		double positive = getVoxel(w, resolution / 2 + difference,  resolution / 2 + difference,  resolution / 2);
		System.out.println("Positive pole is " + String.format("%.2f", positive));
		System.out.println();
		
		
		int size = w.size();
		double max_delta = (double)w.get(size - 1);
		//System.out.println("Max delta is " + String.format("%.2f", max_delta));
		
		ArrayList previous_volume = w;
		ArrayList current_volume;
		
		int iterations = 0;
		double threshold = 1.;
		threshold /= resolution * 100;
		while(max_delta > threshold)
		{
			current_volume  = getAverage(previous_volume, resolution, resolution, resolution);
			previous_volume = current_volume;
			iterations ++;
			max_delta = (double)previous_volume.get(size - 1);
			//System.out.println("Max delta is " + max_delta);
		}
		max_delta = (double)previous_volume.get(size - 1);
		System.out.println("Max delta after " + iterations + " iterations is " + max_delta);
		System.out.println();
		
		double plane[][] = getPlane(previous_volume, 2, resolution - 1, resolution, resolution);
		
		double max = 0; 
		double min = 0;
		for(int i = 0; i < resolution; i++)
		{
			for(int j = 0; j < resolution; j++)
			{
			    if(plane[i][j] < min)
			    	min = plane[i][j];
			    if(plane[i][j] > max)
				    max = plane[i][j];		
			}
		}
		
		System.out.println("Min is " + min + ", max is " + max);
		//System.out.println("Min is " + String.format("%.2f", min) + ", max is " + String.format("%.2f", max));
		System.out.println();
		
		double range = max - min;
		for(int i = 0; i < resolution; i++)
		{
			for(int j = 0; j < resolution; j++)
			{
			    plane[i][j] -= min;
			    plane[i][j] /= range;
			    plane[i][j] *= 255;
			    
			    if(plane[i][j] < 1)
			    	System.out.println(i + " " + j + " is zero.");
			}
		}
		
		image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_RGB);	
		
		int [] blue =  new int[resolution * resolution];
		int [] green = new int[resolution * resolution];
		int [] red =   new int[resolution * resolution];
		
		for(int i = 0; i < resolution; i++)
		{
			for(int j = 0; j < resolution; j++)
			{
				int k = i * resolution + j;
				
				int pixel = 0;
				
				blue[k]  = (int)plane[i][j];
				green[k] = (int)plane[i][j];
				red[k]   = (int)plane[i][j];
				
				pixel |= blue[k] << 16;
				pixel |= green[k] << 8;
				pixel |= red[k];
			    	
			    image.setRGB(j, i, pixel);
			}
		}
		
		String file_string = new String("C:/Users/Brian Crowley/Desktop/foo.jpg");
        try 
        {  
            ImageIO.write(image, "jpg", new File(file_string)); 
        } 
        catch(IOException e) 
        {  
            e.printStackTrace(); 
        }      
		
		JFrame frame = new JFrame("Relaxer");
		WindowAdapter window_handler = new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
			    System.exit(0);
			}
		};
		frame.addWindowListener(window_handler);
			    
		ImageCanvas image_canvas = new ImageCanvas();
		image_canvas.setSize(resolution, resolution);
		frame.getContentPane().add(image_canvas, BorderLayout.CENTER);		
		frame.pack();
		frame.setLocation(400, 200);
		frame.setVisible(true);
	}
	
	public double getVoxel(ArrayList v, int x, int y, int z)
	{
		ArrayList xy    = (ArrayList) v.get(x);
		ArrayList xyz   = (ArrayList) xy.get(y);
		double    voxel = (double)    xyz.get(z);
		
		return voxel;
	}
	
	public double[][]  getPlane(ArrayList v, int axis, int location, int xdim, int ydim)
	{
		double [][] plane = new double[ydim][xdim];
		
		
		if(axis == 0)
		{
		    int x = location;
		    
		    for(int i = 0; i < ydim; i++)
			{
				for(int j = 0; j < xdim; j++)
				{
				    int y = j;
				    int z = i;
					double voxel = getVoxel(v, x, y, z);	
					plane[i][j] = voxel;
				}
			}
		}
		else if(axis == 1)
		{
		    int y = location;	
		    
		    for(int i = 0; i < ydim; i++)
			{
				for(int j = 0; j < xdim; j++)
				{
				    int x = j;
				    int z = i;
				    double voxel = getVoxel(v, x, y, z);
				    plane[i][j] = voxel;
				}
			}
		}
		else if(axis == 2)
		{
		    int z = location;	
		    for(int i = 0; i < ydim; i++)
			{
				for(int j = 0; j < xdim; j++)
				{
					int x = j;
				    int y = i;	
				    double voxel = getVoxel(v, x, y, z);
				    plane[i][j] = voxel;
				}
			}
		}
		else
		{
			System.out.println("Value for axis must be 0, 1, or 2.");
			return plane;
		}
		
		return plane;
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
		ArrayList w      = new ArrayList();
		double max_delta = 0;
		
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
				    double delta   = voxel - average;
				    
				    if(Math.abs(delta) > max_delta)
				    	max_delta = Math.abs(delta);
				    
				    setVoxel(w, x, y, z, average);
				}	
			}
		}
		w.add(max_delta);
		return w;
	}
	
	class ImageCanvas extends Canvas
    {
        public synchronized void paint(Graphics g)
        {
            g.drawImage(image, 0, 0, this);
        }
    }   
}
package unsw.blackout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalTime;

public class Satellite
{
    // the mother set of all sorts of satellites
    ArrayList<Connections> connections = new ArrayList<Connections>();
    double height;
    String id;
    double position;
    ArrayList<String> possibleConnections = new ArrayList<String>();
    String type;
    double velocity;

    /**
     * Satellites no aruguemented constructor
     * needed for creating sub classes
     */
    public Satellite()
    {


    }

    /**
     * Connections constructor
     * @param height
     * @param id
     * @param position
     * @param type
     */
    public Satellite(double height, String id,double position,String type)
    {
        this.height = height;
        this.id = id;
        this.position = position;
        this.type = type;  
    } 


    /**
     * get Satellite's connections
     * @return type
     */
    public ArrayList<Connections> GetConnections()
    {
        return this.connections;
    }

    /**
     * get Satellite's connections
     * @return type
     */
    public double GetHeight()
    {
        return this.height;
    }

    /**
     * get Satellite's connections
     * @return id
     */
    public String GetId()
    {
        return this.id;
    }

    /**
     * get Satellite's position
     * @return position
     */
    public double GetPosition()
    {
        return this.position;
    }

    /**
     * get Satellite's possibleConnections
     * @return possibleConnections
     */
    public ArrayList<String> GetpossibleConnections()
    {
        return this.possibleConnections;
    }

    /**
     * get Satellite's type
     * @return type
     */
    public String GetType()
    {
        return this.type;
    }

    /**
     * get Satellite's velocity
     * @return velocity
     */
    public double GetVelocity()
    {
        return this.velocity;
    }

    public void move_Satellite()
    {
        this.position = this.position + velocity * 1;
        if(this.position >= 360.0)
            this.position = this.position - 360.0;
    }

//########################################################################################################################
// possible connections
    public void sort_possible_connections()
    {
        // sort the given arraylist<String>
        Collections.sort(this.possibleConnections);
    }

    public void clean_possible_connections()
    {
        this.possibleConnections.clear();
    }

    public void add_possible_connections(String device_id, String deviceType)
    {
        System.out.println("This method should not be called");
    }

    
// Connections
// ##########################################################################################################################
    public void sort_connections()
    {
        Collections.sort(
            this.connections,
            new startTimeComparator().
            thenComparing(new deviceIdComparator()));
    }

    public int add_one_connection(LocalTime current_time, Device new_device)
    {
        System.out.println("This method should not be called");
        return 1;
    }

    public void close_one_connection(String device_id, LocalTime current_time)
    {
        System.out.println("This method should not be called");
    }
   
    
    public static void main(String argv[])
    {
        Satellite s = new Satellite(10000,"Id1",20,"Random");
        s.velocity = 20;
        s.move_Satellite();
        int counter = 0;
        while(counter < 17)
        {
            s.move_Satellite();
            counter += 1;
        }
        //System.out.println(s.position);
        //System.out.println(s.GetVelocity());
        System.out.println(s.toString());
    }
    
    
    
}

class startTimeComparator implements Comparator<Connections> 
{
    
    public int compare(Connections c1, Connections c2) {
        LocalTime t1 = c1.GetStartTime();
        LocalTime t2 = c2.GetStartTime();
        return t1.compareTo(t2);
      }
}

class deviceIdComparator implements Comparator<Connections> 
{
    
    public int compare(Connections c1, Connections c2) {
        String s1 = c1.GetConneectedDevice().GetId();
        String s2 = c2.GetConneectedDevice().GetId();
        return s1.compareTo(s2);
      }
}




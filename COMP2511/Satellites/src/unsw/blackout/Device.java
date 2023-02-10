package unsw.blackout;

import java.util.ArrayList;
import java.util.Comparator;
import org.json.JSONObject;
import java.time.LocalTime;

public class Device
{
    private String id;
    private Boolean is_connected;
    private double position;     // degrees
    private ArrayList<LocalTime> activationPeriods = new ArrayList<LocalTime>();
    private String type;
    private int delay;           // in minutes
    private int XSateIndicator = -1;
   

    /**
     * Device no aruguemented constructor
     * needed for creating sub classes
     */
    public Device()
    {
        
    }

    /**
     * Device constructor
     * @param type
     * @param id
     * @param pos
     */
    public Device(String type,String id, double position, int delay)
    {
        this.type = type;
        this.id = id;
        this.position = position;
        this.delay = delay;
        this.is_connected = false;
    }

    /**
     * Set the Device's type 
     * @param type
     */
    public void SetType(String type)
    {
        this.type = type;
    }

     /**
     * Set the Device's id
     * @param id
     */
    public void SetId(String id)
    {
        this.id = id;
    }

     /**
     * Set the Device's pos
     * @param pos
     */
    public void SetPos(double position)
    {
        this.position = position;
    }

     /**
     * Set the Device's delay
     * @param delay
     */
    public void SetDelay(int delay)
    {
        this.delay = delay;
    }

    /**
     * Set the Device's is_connected
     * @param is_connected
     */
    public void SetConnected(Boolean is_connected)
    {
        this.is_connected = is_connected;
    }

    /**
     * Set the Device's one time period
     * @param is_connected
     */
    public void SetActivationPeriods(LocalTime start, int duration)
    {
        this.activationPeriods.add(start);
        LocalTime end = start.plusMinutes(duration);
        this.activationPeriods.add(end);
    }

    /**
     * Set the Device's type 
     * @param XSate
     */
    public void SetXSate(int XSate)
    {
        this.XSateIndicator = XSate;
    }
   
    //################################################################################################
    // getters
    /**
     * get Device's type
     * @return type
     */
    public String GetType()
    {
        return this.type;
    }

    /**
     * get Device's id
     * @return type
     */
    public String GetId()
    {
        return this.id;
    }

    /**
     * get Device's pos
     * @return type
     */
    public double GetPos()
    {
        return this.position;
    }

    /**
     * get Device's type
     * @return type
     */
    public int GetDelay()
    {
        return this.delay;
    }

    /**
     * get Device's interval
     * @return type
     */
    public ArrayList<LocalTime> GetactivationPeriods()
    {
        return this.activationPeriods;
    }

    /**
     * get Device's interval
     * @return type
     */
    public Boolean GetConnectivity()
    {
        return this.is_connected;
    }

    /**
     * Set the Device's type 
     * @param XSate
     */
    public int GetXSate()
    {
        return this.XSateIndicator;
    }

    //#########################################################################################################
    public Boolean IsActive(LocalTime current)
    {
        int counter = 0;
        while(counter < this.activationPeriods.size())
        {
            LocalTime start = this.activationPeriods.get(counter);
            LocalTime end = this.activationPeriods.get(counter + 1);
            if((current.isBefore(end) && current.isAfter(start)) || current.equals(end) || current.equals(start))
                return true;
            counter += 2;
        }
        return false;
    }

    public ArrayList<JSONObject> activationPeriodsToString()
    {
        ArrayList<LocalTime> list_start = new ArrayList<LocalTime>();
        ArrayList<LocalTime> list_copy = new ArrayList<LocalTime>();
        int counter = 0;
        while(counter < this.activationPeriods.size())
        {
            list_start.add(this.activationPeriods.get(counter));
            counter += 2;
        }

        counter = 0;
        while(counter < this.activationPeriods.size())
        {
            LocalTime temp = this.activationPeriods.get(counter);
            list_copy.add(temp);
            counter += 1;
        }

        list_start.sort(new TimeComparator());
        counter = 0;
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        while(counter < list_start.size())
        {
            int inner_counter = 0;
            LocalTime end = null;
            LocalTime start = list_start.get(counter);
            while(inner_counter < list_copy.size())
            {
                if((list_copy.get(inner_counter) != null) && (list_copy.get(inner_counter)).equals(start))
                {
                    if(list_copy.get(inner_counter + 1) != null)
                        end = list_copy.get(inner_counter + 1);
                    list_copy.set(inner_counter + 1, null);
                }
                inner_counter += 1;
            }
            JSONObject ele = new JSONObject();
            ele.put("endTime", end);
            ele.put("startTime", start);
            result.add(ele);
            counter += 1;
        }

        return result;
    }

    /*
    public static void main(String argv[])
    {
        Device new_device = new Device("Hd","1001",100.1,200);
        new_device.SetDelay(300);
        new_device.SetId("new id");
        new_device.SetType("new type");
        new_device.SetPos(300.303030);
        System.out.println(new_device.type);
        System.out.println(new_device.id);
        System.out.println(new_device.position);
        System.out.println(new_device.delay);
        LocalTime a =LocalTime.of(12,01,0,0);
        LocalTime b =LocalTime.of(12,32,0,0);
        LocalTime c =LocalTime.of(16,32,0,0);
        new_device.SetActivationPeriods(a, 30);
        new_device.SetActivationPeriods(b, 30);
        new_device.SetActivationPeriods(c, 30);
        ArrayList<LocalTime> new_t = new ArrayList<LocalTime>();
        new_t = new_device.GetactivationPeriods();
        ArrayList<String> a_list = new_device.activationPeriodsToString();
        int counter = 0;
        while(counter < a_list.size())
        {
            System.out.print(a_list.get(counter));
            counter += 1;
        }
        System.out.println(new_device.IsActive(LocalTime.of(16,01,0,0)));
    }
    */
}

class TimeComparator implements Comparator<LocalTime> 
{
    public int compare(LocalTime t1, LocalTime t2) {
        return t1.compareTo(t2);
      }
}


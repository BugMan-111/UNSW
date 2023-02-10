package unsw.blackout;

import java.time.LocalTime; // import the LocalDate class
import static java.time.temporal.ChronoUnit.MINUTES;

// single element in the arraylist<connections>

public class Connections
{
    private LocalTime endTime;      
    private int minutesActive;
    private String satelliteId;
    private LocalTime startTime; 
    private Device connected_device;

    /**
     * Connections no aruguemented constructor
     * needed for creating sub classes
     */
    public Connections()
    {
        
    }   

     /**
     * Connections constructor
     * @param deviceId
     * @param satelliteId
     * @param startTime
     */
    public Connections(String satelliteId,LocalTime startTime, Device connected_device)
    {
        this.satelliteId = satelliteId;
        this.startTime = startTime;
        this.connected_device = connected_device;
        this.endTime = null;
    }

    /**
     * Set the Connection's connected_device
     * @param connected_device
     */
    public void SetDeviceId(Device connected_device)
    {
        this.connected_device = connected_device;
    }

    /**
     * Set the Connection's end time
     * @param endTime
     */
    public void SetEndTime(LocalTime endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Set the Connection's minutesActive
     * @param delay
     */
    public void SetMinutesActive(int delay)
    {
        long difference;
        difference = this.startTime.until(this.endTime, MINUTES);
        this.minutesActive = (int) difference - 1 -delay;
    }

    /**
     * Set the Connection's satelliteId
     * @param satelliteId
     */
    public void SetsatelliteId(String satelliteId)
    {
        this.satelliteId = satelliteId;
    }

    /**
     * Set the Connection's startTime
     * @return startTime
     */
    public void SetStartTime(LocalTime startTime)
    {
        this.startTime = startTime;
    }


    //######################################################################################################################################################
    
    /**
     * Get the Connection's end time
     * @return endTime
     */
    public LocalTime GetEndTime()
    {
        return this.endTime;
    }

    /**
     * Get the Connection's minutesActive
     * @return minutesActive
     */
    public int GetMinutesActive()
    {
       return this.minutesActive;
    }

    /**
     * Get the Connection's satelliteId
     * @return satelliteId
     */
    public String GetsatelliteId()
    {
        return this.satelliteId;
    }

    /**
     * Get the Connection's startTime
     * @return startTime
     */
    public LocalTime GetStartTime()
    {
        return this.startTime;
    }

    /**
     * Get the Connection's connectedDevice
     * @return startTime
     */
    public Device GetConneectedDevice()
    {
        return this.connected_device;
    }

    /*

    public String ToString()
    {
        return "{\n" +
            "   deviceId:" + this.connected_device.GetId() + ",\n" + 
            "   endTime:"+ this.GetEndTime() + ",\n" +
            "   minutesActive:" + this.GetMinutesActive() + ",\n" + 
            "   satelliteId:" + this.GetsatelliteId() + ",\n" + 
            "   startTime:" + this.GetStartTime() + "\n" + 
          "}";
    }
    
    public static void main(String argv[])
    {
        LocalTime t = LocalTime.of(0,0,0,0);
        Device d = new Device("a","the_id", 20, 5);
        Connections new_con = new Connections("d1", t,d);
        System.out.println(new_con.ToString());
    }
    */

}


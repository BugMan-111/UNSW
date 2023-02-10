package unsw.blackout;

//import java.util.ArrayList;
import java.time.LocalTime;


public class SpaceXSatellite extends Satellite
{
    /**
     * Connections constructor
     * @param height
     * @param id
     * @param position
     * @param type
     */
    public SpaceXSatellite(double height, String id,double position,String type)
    {
        super(height,id,position,type);
        this.velocity = ((double)3330 / (double)60)/height;  // calculate w/s
    } 
    //#####################################################################################################################
    // Possible connections
    //add the id of devices that could be connected by the sate 
    @Override
    public void add_possible_connections(String device_id, String deviceType)
    {
        int counter = 0;
        int result = 1;
        while(counter < this.connections.size())
        {
            if(this.connections.get(counter).GetConneectedDevice().GetId().equals(device_id)
               && this.connections.get(counter).GetEndTime() == null)
                result = 0;
            counter += 1;
        }
        if((deviceType.equals("HandheldDevice") || deviceType.equals("MobileXPhone"))  && result == 1)
        {
            this.possibleConnections.add(device_id);
            this.sort_connections();
        }
    }

    //#########################################################################################################################################
    // connections
    
    @Override
    public int add_one_connection(LocalTime current_time, Device new_device)
    {
        if(new_device.GetType().equals("HandheldDevice") || new_device.GetType().equals("MobileXPhone"))
        {
            Connections new_con = new Connections(this.id,current_time,new_device);
            this.connections.add(new_con);
        }  
        return 1;
    }
    @Override
    public void close_one_connection(String device_id, LocalTime current_time)
    {
        // find the one to close
        Connections con = null;
        int counter = 0;
        while(counter < this.connections.size())
        {
            con = this.connections.get(counter);
            if(con.GetConneectedDevice().GetId().equals(device_id))
                break;
            counter += 1;
        }

        // change the one
        if (con != null)
        {
            con.SetEndTime(current_time);
            // delay is 0
            con.SetMinutesActive(0);
        }
    
        // and set the edited one to the same location
        this.connections.set(counter, con);
    }

    /*
    public static void main(String argv[])
    {
        SpaceXSatellite s = new SpaceXSatellite(10000, "SX1", 20, "X");
        int counter = 0;
        while(counter < 1440)
        {
            s.move_Satellite();
            counter += 1;
        }
        System.out.println(s.GetPosition());
        counter = 0;
        s.add_possible_connections("ID4", "HandheldDevice");
        s.add_possible_connections("ID2", "HandheldDeviceasd");
        s.add_possible_connections("ID3", "HandheldDeviceasd");
        s.add_possible_connections("ID5", "HandheldDevice");
        s.add_possible_connections("ID1", "HandheldDevice");

        //s.clean_possible_connections();

        //s.sort_possible_connections();

        while(counter < 3)
        {
            //System.out.println(s.GetpossibleConnections().get(counter));
            counter += 1;
        }

        Device d1 = new Device("HandheldDevice", "id3", 20, 5);
        Device d2 = new Device("a", "id1", 21, 5);
        Device d3 = new Device("HandheldDevice", "id2", 22, 5);
        Device d4 = new Device("HandheldDevice", "id4", 23, 5);

        s.add_one_connection(LocalTime.of(12,00,0,0), d1);
        s.add_one_connection(LocalTime.of(12,00,0,0), d2);
        s.add_one_connection(LocalTime.of(12,00,0,0), d3);
        s.add_one_connection(LocalTime.of(12,30,0,0), d4);

        s.sort_connections();

        counter = 0;
        while(counter < 3)
        {
            //System.out.println(s.GetConnections().get(counter).GetConneectedDevice().GetId());
            counter += 1;
        }

        s.close_one_connection("id2", LocalTime.of(13,30,0,0));
        s.close_one_connection("id3", LocalTime.of(13,30,0,0));
        s.close_one_connection("id4", LocalTime.of(13,30,0,0));

        counter = 0;
        while(counter < 3)
        {
            if(s.GetConnections().get(counter).GetEndTime() == null)
                System.out.println(s.GetConnections().get(counter).GetConneectedDevice().GetId());
            counter += 1;
        }

        s.add_one_connection(LocalTime.of(12,40,0,0), d1);
        s.add_one_connection(LocalTime.of(12,40,0,0), d2);
        s.add_one_connection(LocalTime.of(12,40,0,0), d3);
        s.add_one_connection(LocalTime.of(12,40,0,0), d4);

        s.sort_connections();

        counter = 0;
        while(counter < 6)
        {
            System.out.println(s.GetConnections().get(counter).GetConneectedDevice().GetId());
            counter += 1;
        }
    }
    */

}


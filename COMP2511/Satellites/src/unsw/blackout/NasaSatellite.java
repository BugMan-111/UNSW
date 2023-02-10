package unsw.blackout;

import java.time.LocalTime;

public class NasaSatellite extends Satellite
{
    /**
     * Connections constructor
     * @param height
     * @param id
     * @param position
     * @param type
     */
    public NasaSatellite(double height, String id,double position,String type)
    {
        super(height,id,position,type);
        this.velocity = ((double)5100 / (double)60)/height;  // calculate w/s
    } 

    //#####################################################################################################################
    // Possible connections
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
        if(result == 1)
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
        Connections con = null;
        int counter = 0;
        int total = 0;
        while(counter < this.connections.size())
        {
            con = this.connections.get(counter);
            if (con.GetEndTime() == null)
                total += 1;
            counter += 1;
        }
        if(total >= 6 && (new_device.GetPos() < 30 || new_device.GetPos() > 40))
            return -1;
        counter = 0;
        if(total >= 6 && (new_device.GetPos() >= 30 && new_device.GetPos() <= 40))
        {
            this.sort_connections();
            counter = 0;
            while(counter < this.connections.size())
            {
                con = this.connections.get(counter);
                if(con.GetEndTime() == null)
                {
                    double con_pos = con.GetConneectedDevice().GetPos();
                    if(con_pos < 30 || con_pos > 40)
                    {
                        this.close_one_connection(con.GetConneectedDevice().GetId(), current_time);
                        Connections new_con = new Connections(this.id,current_time,new_device);
                        this.connections.add(new_con);
                        this.sort_connections();
                        return 1;
                    }
                }
                counter += 1;
            }
            return -1;
        }
        Connections new_con = new Connections(this.id,current_time,new_device);
        this.connections.add(new_con);
        this.sort_connections();
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
            // delay is 10
            con.SetMinutesActive(10);
        }
    
        // and set the edited one to the same location
        this.connections.set(counter, con);
    }

    /*
    public static void main(String argv[])
    {
        NasaSatellite s = new NasaSatellite(10000, "SX1", 20, "X");
        int counter = 0;
        while(counter < 1440)
        {
            s.move_Satellite();
            counter += 1;
        }
        System.out.println(s.GetPosition());

        
        Device d1 = new Device("DesktopDevice", "id1", 20, 5);
        Device d2 = new Device("DesktopDevice", "id2", 21, 5);
        Device d3 = new Device("LaptopDevice", "id3", 22, 5);
        Device d4 = new Device("LaptopDevice", "id4", 23, 5);
        Device d5 = new Device("h", "id5", 23, 5);
        Device d6 = new Device("h", "id6", 23, 5);
        Device d7 = new Device("h", "id7", 35, 5);
        //Device d7 = new Device("h", "id7", 29, 5);
        //Device d7 = new Device("h", "id7", 40, 5);
        //Device d7 = new Device("h", "id7", 41, 5);
        //Device d7 = new Device("h", "id7", 35, 5);

        s.add_one_connection(LocalTime.of(12,00,0,0), d1);
        s.add_one_connection(LocalTime.of(12,00,0,0), d2);
        s.add_one_connection(LocalTime.of(12,00,0,0), d3);
        s.add_one_connection(LocalTime.of(12,00,0,0), d4);
        s.add_one_connection(LocalTime.of(12,00,0,0), d5);
        s.add_one_connection(LocalTime.of(12,00,0,0), d6);
        s.add_one_connection(LocalTime.of(12,00,0,0), d7);

        s.sort_connections();
        counter = 0;

        while(counter < s.GetConnections().size())
        {
            System.out.println(s.GetConnections().get(counter).GetConneectedDevice().GetId());
            counter += 1;
        }
    }
    */
    
}



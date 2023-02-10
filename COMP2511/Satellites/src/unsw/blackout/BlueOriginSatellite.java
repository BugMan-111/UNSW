package unsw.blackout;

import java.time.LocalTime;

public class BlueOriginSatellite extends Satellite
{
    /**
     * Connections constructor
     * @param height
     * @param id
     * @param position
     * @param type
     */
    public BlueOriginSatellite(double height, String id,double position,String type)
    {
        super(height,id,position,type);
        this.velocity = ((double)8500 / (double)60)/this.height;  // calculate w/s
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
        // max max 5 laptops, 2 desktops, no limit on handheld, overall limit is 10

        // count the numbers for laptops,desktops,and handhelds, as well as the total number
        // find the one to close
        Connections con = null;
        int counter = 0;
        int lap = 0;
        int desk = 0;
        int total = 0;
        while(counter < this.connections.size())
        {
            con = this.connections.get(counter);
            if(con.GetEndTime() == null)
            {
                if(con.GetConneectedDevice().GetType().equals("LaptopDevice"))
                    lap += 1;
                else if(con.GetConneectedDevice().GetType().equals("DesktopDevice"))
                    desk += 1;
                total += 1;
            }
            counter += 1;
        }
        if(total >= 10)
            return -1;
        if(new_device.GetType().equals("LaptopDevice") && lap >= 5)
            return -1;
        if(new_device.GetType().equals("DesktopDevice") && desk >= 2)
            return -1;
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
            // delay is 0
            con.SetMinutesActive(con.GetConneectedDevice().GetDelay());
        }
    
        // and set the edited one to the same location
        this.connections.set(counter, con);
    }

    /*
    public static void main(String argv[])
    {
        BlueOriginSatellite s = new BlueOriginSatellite(10000, "SX1", 20, "X");
        int counter = 0;
        while(counter < 1440)
        {
            s.move_Satellite();
            counter += 1;
        }
        System.out.println(s.GetPosition());

        
        Device d1 = new Device("LaptopDevice", "id3", 20, 5);
        Device d2 = new Device("LaptopDevice", "id1", 21, 5);
        Device d3 = new Device("LaptopDevice", "id2", 22, 5);
        Device d4 = new Device("LaptopDevice", "id4", 23, 5);
        Device d5 = new Device("LaptopDevice", "id6", 23, 5);
        Device d6 = new Device("LaptopDevice", "id5", 23, 5);
        Device d7 = new Device("DesktopDevice", "id7", 23, 5);
        Device d8 = new Device("DesktopDevice", "id8", 23, 5);
        Device d9 = new Device("DesktopDevice", "id9", 23, 5);
        Device d10 = new Device("h", "id10", 23, 5);
        Device d11 = new Device("h", "id11", 23, 5);
        Device d12 = new Device("h", "id12", 23, 5);
        Device d13 = new Device("h", "id13", 23, 5);

        s.add_one_connection(LocalTime.of(12,00,0,0), d1);
        s.add_one_connection(LocalTime.of(12,00,0,0), d2);
        s.add_one_connection(LocalTime.of(12,00,0,0), d3);
        s.add_one_connection(LocalTime.of(12,00,0,0), d4);
        s.add_one_connection(LocalTime.of(12,00,0,0), d5);
        s.add_one_connection(LocalTime.of(12,00,0,0), d6);
        s.add_one_connection(LocalTime.of(12,00,0,0), d7);
        s.add_one_connection(LocalTime.of(12,00,0,0), d8);
        s.add_one_connection(LocalTime.of(12,00,0,0), d9);
        s.add_one_connection(LocalTime.of(12,00,0,0), d10);
        s.add_one_connection(LocalTime.of(12,00,0,0), d11);
        s.add_one_connection(LocalTime.of(12,00,0,0), d12);
        s.add_one_connection(LocalTime.of(12,00,0,0), d13);

        s.sort_connections();
        counter = 0;

        while(counter < s.GetConnections().size())
        {
            //System.out.println(s.GetConnections().get(counter).GetConneectedDevice().GetId());
            counter += 1;
        }

        s.close_one_connection("id8",LocalTime.of(12,00,0,0) );
        s.add_possible_connections("id8", "DesktopDevice");
        System.out.println(s.GetpossibleConnections().get(0));  
    }
    */
    
}


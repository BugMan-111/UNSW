package unsw.blackout;

import java.time.LocalTime;

public class SovietSatellite extends Satellite
{
     /**
     * Connections constructor
     * @param height
     * @param id
     * @param position
     * @param type
     */
    public SovietSatellite(double height, String id,double position,String type)
    {
        super(height,id,position,type);
        this.velocity = ((double)6000 / (double)60)/height;  // calculate w/s
    } 
    @Override
    public void move_Satellite()
    {
        if(this.position > 140 && this.position < 190)
            this.velocity = this.velocity * (double)(1);
        else if(this.position > 345 || this.position <= 140)
        {
            if(this.velocity < 0)
                this.velocity = this.velocity * (double)(-1);
        }
        else if(this.position >= 190 && this.position <= 345)
        {
            if(this.velocity >= 0)
                this.velocity = this.velocity * (double)(-1);
        }
        this.position = this.position + this.velocity;
        if(this.position >= 360.0)
            this.position = this.position - 360.0;
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
        if((deviceType.equals("LaptopDevice") || deviceType.equals("DesktopDevice")) && result == 1)
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
        int total = 0;
        String get_first = null;
        int get_first_index = 0;
        while(counter < this.connections.size())
        {
            con = this.connections.get(counter);
            if (con.GetEndTime() == null)
            {
                get_first = con.GetConneectedDevice().GetId();
                get_first_index = counter;
                break;
            }
            counter += 1;
        }
        counter = 0;
        while(counter < this.connections.size())
        {
            con = this.connections.get(counter);
            if (con.GetEndTime() == null)
                total += 1;
            counter += 1;
        }
        if(total >= 9)
        {
            drop_one_connection_soviet(get_first, current_time);
            Connections new_con = new Connections(this.id,current_time,new_device);
            this.connections.add(new_con);
            return 10 + get_first_index;
        }
        else
        {
            Connections new_con = new Connections(this.id,current_time,new_device);
            this.connections.add(new_con);
        }
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
            // delay is 2 * ori
            con.SetMinutesActive(2*con.GetConneectedDevice().GetDelay());
        }
    
        // and set the edited one to the same location
        this.connections.set(counter, con);
    }

    private void drop_one_connection_soviet(String device_id, LocalTime current_time)
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
            // delay is 2 * ori
            con.SetMinutesActive(2*con.GetConneectedDevice().GetDelay() - 1);
        }
    
        // and set the edited one to the same location
        this.connections.set(counter, con);
    }

    /*
    public static void main(String argv[])
    {
        // values 140,190,345,344,346
        SovietSatellite s = new SovietSatellite(10000, "SX1", 346, "X");
        // first set the pos to 140, expected value = 140 + vel * 1
        int counter = 0;
        
        while(counter < 1)
        {
            s.move_Satellite();
            counter += 1;
        }
        System.out.println(s.GetPosition());

        
        Device d1 = new Device("DesktopDevice", "id1", 20, 5);
        Device d2 = new Device("DesktopDevice", "id2", 21, 5);
        Device d3 = new Device("LaptopDevice", "id3", 22, 5);
        Device d4 = new Device("LaptopDevice", "id4", 23, 5);
        Device d5 = new Device("LaptopDevice", "id5", 23, 5);
        Device d6 = new Device("LaptopDevice", "id6", 23, 5);
        Device d7 = new Device("DesktopDevice", "id7", 35, 5);
        Device d8 = new Device("DesktopDevice", "id8", 29, 5);
        Device d9 = new Device("DesktopDevice", "id9", 40, 5);
        Device d10 = new Device("DesktopDevice", "idA10", 41, 5);
        //Device d10 = new Device("h", "id7", 35, 5);

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


package unsw.blackout;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;


public class Blackout {
    // set the vars
    private LocalTime current_time = LocalTime.of(0,0,0,0);
    private ArrayList<Device> list_of_Devices = new ArrayList<Device>();
    private ArrayList<Satellite> list_of_Satellites = new ArrayList<Satellite>();

    public void createDevice(String id, String type, double position) {
        if(type.equals("HandheldDevice"))
        {
            Device new_device = new Device(type,id,position,1);
            list_of_Devices.add(new_device);
        }
        else if(type.equals("LaptopDevice"))
        {
            Device new_device = new Device(type,id,position,2);
            this.list_of_Devices.add(new_device);
        }
        else if(type.equals("DesktopDevice"))
        {
            Device new_device = new Device(type,id,position,5);
            this.list_of_Devices.add(new_device);
        }
        else if(type.equals("MobileXPhone"))
        {
            Device new_device = new Device(type,id,position,1);
            new_device.SetXSate(1);
            this.list_of_Devices.add(new_device);
        }
        this.list_of_Devices.sort(new DeviceNameComparator());
    }



//#################################################################################################################################################################
    public void createSatellite(String id, String type, double height, double position) {
        if(type.equals("SpaceXSatellite"))
        {
            SpaceXSatellite s = new SpaceXSatellite(height,id,position,type);
            Init_Sate_possible_connections(s);
        }
        else if(type.equals("BlueOriginSatellite"))
        {
            BlueOriginSatellite s = new BlueOriginSatellite(height,id,position,type);
            Init_Sate_possible_connections(s);
        }
        else if(type.equals("NasaSatellite"))
        {
            NasaSatellite s = new NasaSatellite(height,id,position,type);
            Init_Sate_possible_connections(s);
        }
        else if(type.equals("SovietSatellite"))
        {
            SovietSatellite s = new SovietSatellite(height,id,position,type);
            Init_Sate_possible_connections(s);
        }
        this.list_of_Satellites.sort(new SatePosComparator());
        int counter = 0;
        while(counter < this.list_of_Satellites.size())
        {
            Satellite s = this.list_of_Satellites.get(counter);
            Update_Sate_possible_connections(s, counter);
            counter += 1;
        }
    }



    public void scheduleDeviceActivation(String deviceId, LocalTime start, int durationInMinutes) {
        int counter = 0;
        while(counter < this.list_of_Devices.size())
        {
            if(this.list_of_Devices.get(counter).GetId().equals(deviceId))
                break;
            counter += 1;
        }
        Device new_device = this.list_of_Devices.get(counter);
        new_device.SetActivationPeriods(start, durationInMinutes);
        this.list_of_Devices.set(counter, new_device);
    }



    public void removeSatellite(String id) {
        int counter = 0;
        while(counter < this.list_of_Satellites.size())
        {
            if(this.list_of_Satellites.get(counter).GetId().equals(id))
                break;
            counter += 1;
        }
        this.list_of_Satellites.remove(counter);
        this.list_of_Satellites.sort(new SatePosComparator());
    }



    public void removeDevice(String id) {
        int counter = 0;
        while(counter < this.list_of_Devices.size())
        {
            if(this.list_of_Devices.get(counter).GetId().equals(id))
                break;
            counter += 1;
        }
        this.list_of_Devices.remove(counter);
        // fill update the possible connections for each sate
        counter = 0;
        while(counter < this.list_of_Satellites.size())
        {
            Satellite s = this.list_of_Satellites.get(counter);
            Update_Sate_possible_connections(s, counter);
            counter += 1;
        }
        this.list_of_Devices.sort(new DeviceNameComparator());
    }



    public void moveDevice(String id, double newPos) {
        int counter = 0;
        while(counter < this.list_of_Devices.size())
        {
            if(this.list_of_Devices.get(counter).GetId().equals(id))
                break;
            counter += 1;
        }
        Device new_device = this.list_of_Devices.get(counter);
        new_device.SetPos(newPos);
        this.list_of_Devices.set(counter, new_device);

        // fill update the possible connections for each sate
        counter = 0;
        while(counter < this.list_of_Satellites.size())
        {
            Satellite s = this.list_of_Satellites.get(counter);
            Update_Sate_possible_connections(s, counter);
            counter += 1;
        }
        this.list_of_Devices.sort(new DeviceNameComparator());
    }
    


//#########################################################################################################################
    public JSONObject showWorldState() {
        JSONObject result = new JSONObject();

        this.list_of_Devices.sort(new DeviceNameComparator());
        this.list_of_Satellites.sort(new SateComparator());

        ArrayList<JSONObject> AJ = new ArrayList<JSONObject>();
        int counter = 0;
        while(counter < this.list_of_Devices.size())
        {
            Device d = this.list_of_Devices.get(counter);
            JSONArray ja = new JSONArray(d.activationPeriodsToString());
            JSONObject one_device = new JSONObject();
            one_device.put("isConnected", d.GetConnectivity());
            one_device.put("id", d.GetId());
            one_device.put("position", d.GetPos());
            one_device.put("activationPeriods", ja);
            one_device.put("type", d.GetType());
            counter += 1;
            AJ.add(one_device);
            //System.out.println(d.GetId());
        }
        JSONArray devices = new JSONArray(AJ);

        ArrayList<JSONObject> AJs2 = new ArrayList<JSONObject>();
        counter = 0;
        while(counter < this.list_of_Satellites.size())
        {
            Satellite s = this.list_of_Satellites.get(counter);
            JSONObject one_s = new JSONObject();
            ArrayList<JSONObject> AJs = new ArrayList<JSONObject>();
            int inner = 0;
            ArrayList<Connections> con_list = s.GetConnections(); 
            while(inner < con_list.size())
            {
                Connections con;
                con = con_list.get(inner);
                JSONObject Jo = new JSONObject();
                Jo.put("deviceId", con.GetConneectedDevice().GetId());
                Jo.put("endTime", con.GetEndTime());
                Jo.put("minutesActive", con.GetMinutesActive());
                Jo.put("satelliteId", con.GetsatelliteId());
                Jo.put("startTime", con.GetStartTime());
                AJs.add(Jo);
                inner += 1;
            }
            JSONArray ja1 = new JSONArray(AJs);
            one_s.put("connections", ja1);
            one_s.put("height", s.GetHeight());
            one_s.put("id", s.GetId());
            one_s.put("position", s.GetPosition());
            JSONArray ja2 = new JSONArray(s.GetpossibleConnections());
            one_s.put("possibleConnections", ja2);
            one_s.put("type", s.GetType());
            //System.out.println(s.GetVelocity());
            one_s.put("velocity", s.GetVelocity() * s.GetHeight());
            AJs2.add(one_s);
            counter += 1;
        }

        JSONArray satellites = new JSONArray(AJs2);

        result.put("devices", devices);
        result.put("currentTime", this.current_time);
        result.put("satellites", satellites);

        // you'll want to replace this for Task2
        //result.put("currentTime", this.current_time);

        return result;
    }



//#################################################################################################################################################################
    public void simulate(int tickDurationInMinutes) {
        int TimeCounter = 1;

        this.list_of_Satellites.sort(new SatePosComparator());
        this.list_of_Devices.sort(new DeviceNameComparator());

        // for every new simulation set the current t to 00:00
        //this.current_time = LocalTime.of(0,0,0,0);

        while(TimeCounter <= tickDurationInMinutes)
        {
            int counter = 0;
            // move the satellite
            while(counter < this.list_of_Satellites.size())
            {
                Satellite s = this.list_of_Satellites.get(counter);
                s.move_Satellite();
                this.list_of_Satellites.set(counter, s);
                counter += 1;
            }

            // fill update the possible connections for each sate
            counter = 0;
            while(counter < this.list_of_Satellites.size())
            {
                Satellite s = this.list_of_Satellites.get(counter);
                Update_Sate_possible_connections(s, counter);
                counter += 1;
            }

            //for every sate Disconnect inactive devices
            handle_Device_disconnections();

            //for every MobileX check if there is any reachable Xsate
            Xmoblie_to_Xsate();

            counter = 0;
        
            while(counter < this.list_of_Satellites.size())
            {
                Satellite s = this.list_of_Satellites.get(counter);
                handle_connections(s,counter);
                counter += 1;
            }

            this.current_time = this.current_time.plusMinutes(1);
            TimeCounter += 1;
        }
       
    }
    //#######################################################################################################################
    //Helpers
    private void Init_Sate_possible_connections(Satellite s)
    {
        s.clean_possible_connections();
        int counter = 0;
            while(counter < this.list_of_Devices.size())
            {
                Device d = list_of_Devices.get(counter);
                if(MathsHelper.satelliteIsVisibleFromDevice(s.GetPosition(), s.GetHeight(), d.GetPos()) == true)
                {
                    // if the device is visible, input the device to add possible device method
                    s.add_possible_connections(d.GetId(), d.GetType());
                }
                counter += 1;
            }
            list_of_Satellites.add(s);  
            s.sort_possible_connections();
    }

    private void Update_Sate_possible_connections(Satellite s, int pos)
    {
        s.clean_possible_connections();
            int inner_counter = 0;
            while(inner_counter < this.list_of_Devices.size())
            {
                Device d = list_of_Devices.get(inner_counter);
                if(MathsHelper.satelliteIsVisibleFromDevice(s.GetPosition(), s.GetHeight(), d.GetPos()) == true)
                {
                    // if the device is visible, input the device to add possible device method
                    s.add_possible_connections(d.GetId(), d.GetType());
                }
                inner_counter += 1;
            }
            s.sort_possible_connections();
            this.list_of_Satellites.set(pos, s);
    }

    private void handle_connections(Satellite s, int pos)
    {
            int counter_inner = 0;
            ArrayList<String> possible_con = s.GetpossibleConnections();
            // every possible_con array has been sorted
            while(counter_inner < possible_con.size())
            {
                int counter_inner2 = 0;
                while(counter_inner2 < this.list_of_Devices.size())
                {
                    // for every device in the possible list
                    Device d = this.list_of_Devices.get(counter_inner2);
                    if(d.GetId().equals(possible_con.get(counter_inner)))
                    {
                        //append d to connections
                        if(d.IsActive(this.current_time) == true && d.GetConnectivity() == false)
                        {
                            if(d.GetXSate() == -1)
                            {
                                int result;
                                result = s.add_one_connection(this.current_time, d);
                                if (result == 1)
                                {
                                    d.SetConnected(true);
                                    this.list_of_Devices.set(counter_inner2,d);
                                    this.list_of_Satellites.set(pos,s);
                                }
                                else if(result >= 10)
                                {
                                    d.SetConnected(true);
                                    this.list_of_Devices.set(counter_inner2,d);
                                    this.list_of_Satellites.set(pos,s);
                                    result = result - 10;
                                    int i = 0;
                                    while(i < this.list_of_Devices.size())
                                    {
                                        Device a = this.list_of_Devices.get(i);
                                        if(a.GetId().equals(s.GetConnections().get(result).GetConneectedDevice().GetId()))
                                        {
                                            a.SetConnected(false);
                                            list_of_Devices.set(i, a);
                                            break;
                                        }
                                        i += 1;
                                    }
                                }
                            }
                            else if(d.GetXSate() == 1 && s.GetType().equals("SpaceXSatellite"))
                            {
                                s.add_one_connection(this.current_time, d);
                                d.SetConnected(true);
                                this.list_of_Devices.set(counter_inner2,d);
                                this.list_of_Satellites.set(pos,s);
                            }
                        }   
                    }
                    counter_inner2 += 1;
                }
                counter_inner += 1;
            }
    }

    private void Xmoblie_to_Xsate()
    {
        int counter = 0;
        while(counter < this.list_of_Devices.size())
        {
            Device d = this.list_of_Devices.get(counter);
            if(d.GetConnectivity() == false && d.GetType().equals("MobileXPhone"))
            {
                int inner = 0;
                Boolean result = false;
                while(inner < this.list_of_Satellites.size())
                {
                    Satellite s = this.list_of_Satellites.get(inner);
                    if(
                        MathsHelper.satelliteIsVisibleFromDevice(s.GetPosition(), s.GetHeight(), d.GetPos()) == false
                        &&
                        s.GetType().equals("SpaceXSatellite")
                    )
                        result = true;
                    inner +=1 ;
                }
                if(result == true)
                {
                    d.SetXSate(1);
                    this.list_of_Devices.set(counter, d);
                }
            }
            counter += 1;
        }
        return;
    }

    private void handle_Device_disconnections()
    {
            int counter = 0;
            while(counter < this.list_of_Satellites.size())
            {
                Satellite s = this.list_of_Satellites.get(counter);
                int counter_inner = 0;
                while(counter_inner < s.GetConnections().size())
                {
                    Connections con = s.GetConnections().get(counter_inner);
                    if(con.GetEndTime() == null)
                    {
                        // match with the device
                        Device d = null;
                        int counter_inner2 = 0;
                        while(counter_inner2 < this.list_of_Devices.size())
                        {
                            d = this.list_of_Devices.get(counter_inner2);
                            if(d.GetId().equals(con.GetConneectedDevice().GetId()))
                                break;
                            counter_inner2 += 1;
                        }
                        // if the connection has not been closed
                        // check if the device is still reachable for the sate
                        // check id the device is still active
                        if(
                            (MathsHelper.satelliteIsVisibleFromDevice(s.GetPosition(), s.GetHeight(), d.GetPos()) == false)
                            ||
                            (d.IsActive(this.current_time) == false)
                        )
                        {
                            s.close_one_connection(d.GetId(), this.current_time);
                            d.SetConnected(false);
                            this.list_of_Devices.set(counter_inner2,d);
                            this.list_of_Satellites.set(counter,s);
                        }
                    }
                    counter_inner += 1;
                }
                counter += 1;
            }
    }
}


//##############################################################################################################################################
class SateComparator implements Comparator<Satellite> 
{
    public int compare(Satellite s1, Satellite s2) 
    {
        String n1 = s1.GetId();
        String n2 = s2.GetId();
        return n1.compareTo(n2);
    }
}
    

class DeviceNameComparator implements Comparator<Device> 
{
    
    public int compare(Device d1, Device d2) 
    {
        String s1 = d1.GetId();
        String s2 = d2.GetId();
        return s1.compareTo(s2);
      }
}

class SatePosComparator implements Comparator<Satellite> 
{
    
    public int compare(Satellite s1, Satellite s2) 
    {
        double p1 = s1.GetPosition();
        double p2 = s2.GetPosition();
        if(p1 > p2)
            return 1;
        return -1;
    }
}

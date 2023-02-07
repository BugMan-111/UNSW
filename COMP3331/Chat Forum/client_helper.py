from socket import *
import sys
import time
from datetime import datetime

# ultility
def date_to_timestamp(date_str):
     return time.mktime(datetime.strptime(date_str,"%d %b %Y %H:%M:%S").timetuple())

def check_key(d,s):
    for item in d:
        if item[0] == s:
            return True
    return False
     

# check if the format of client's MSG command is correct
def check_MSG(the_command_with_args):
    # if there are multipe words in the the command, then it is valid
    if the_command_with_args[0] != 'M':
        return False
    if len(the_command_with_args.split()) > 1:
        return True
    return False 

# check if the format of delet a MSG is correct
def check_DLT(the_command_with_args):
    # the number of args needs to be exactly 3
    # arg[0] = dlt,arg[1] = number, ar[2] = timestamp
    list_arg = the_command_with_args.split(' ',2)
    if the_command_with_args[0] != 'D':
        return False
    if len(list_arg) != 3:
        return False
    try:
        int(list_arg[1])
    except ValueError:
        return False
        pass
    try:
        date_to_timestamp(list_arg[2])
    except ValueError:
        return False
        pass
    return True

# check if the format of delet a MSG is correct
def check_EDT(the_command_with_args):
    # the number of args needs to be exactly 3
    # arg[0] = dlt,arg[1] = number, arg[2] = timestamp,arg[3] = message
    # arg[0] = dlt,arg[1] = number, arg[2] = timestamp + message
    CM_NUM_TIMES = the_command_with_args.split(' ',2)
    if the_command_with_args[0] != 'E':
        return False
    if len(CM_NUM_TIMES) != 3:
        return False
    try:
        number = CM_NUM_TIMES[1]
    except ValueError:
        return False
    try:
        timestamp = CM_NUM_TIMES[2][:19]
    except ValueError:
        return False
    try:
        message = CM_NUM_TIMES[2][20:]
    except ValueError:
        return False
    try:
        int(number)
    except ValueError:
        return False
    try:
        date_to_timestamp(timestamp)
    except ValueError:
        return False
    return True

# check if the format of client's RDM command is correct
def check_RDM(the_command_with_args):
    # the RDM command should have one aruguement
    if the_command_with_args[0] != 'R':
        return False
    if len(the_command_with_args.split(' ',1)) != 2:
        return False
    try:
        date_to_timestamp((the_command_with_args.split(' ',1))[1])
    except ValueError:
        return False
    return True

# check if the format of client's ATU command is correct
def check_ATU(the_command_with_args):
    # the ATU command should have no aruguement
    if the_command_with_args[0] != 'A':
        return False
    if the_command_with_args == 'ATU':
        return True
    else:
        return False 


# check if the format of client's OUT command is correct
def check_OUT(the_command_with_args):
    # the OUT command should have no aruguement
    if the_command_with_args[0] != 'O':
        return False
    if the_command_with_args == 'OUT':
        return True
    else:
        return False

# check if the format of client's UDP command is correct
def check_UPD(the_command_with_args):
    # the UPD command should have exactily two augurment
    if len(the_command_with_args.split(' ')) != 3:
        return False
    # simply check if the file exists
    filename = the_command_with_args.split(' ')[2]
    try:
        f = open(filename,'rb')
    # Do something with the file
    except FileNotFoundError:
        print("\nSorry it seems like the file that you want to send is not in this directory!")
        return False
    return True

    #UPD 2 example2.MP4
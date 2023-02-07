from server_helper import *
import time
from datetime import datetime
import sys

# note please use python2.7 to run this program

# data structres

# list of forzen usernames, and their starting time of frozen
# example [james,11:11 1/1/2020,attempt_times]
list_of_failed_usernames = []

# list of client_num,recieving_time,user_name,client_addr,connectionSocket,client_UDP_port
list_of_authenticated_users_and_sockets = []

# list of messages and users
# examples [messagenum starting at 1,timestamp,username,message,edited]
list_of_messages_with_clients = []

########################################################################################################################################
# gerneral utility func
# convert ctime to our time formation
def convert_ctime(time_str):
    # get rid of week day
    time_str = time_str[4:]
    new = time_str.split()
    return new[1] + ' ' + new[0] + ' ' + new[3] + ' ' + new[2]

# convert a time string to time(float)
def date_to_timestamp(date_str):
     return time.mktime(datetime.strptime(date_str,"%d %b %Y %H:%M:%S").timetuple())

# Utility functions for auth 

# check the input username and password with crendentials.txt
def Authentication(user_username,user_password):
    # this function checks wether the user is inputing correct name && password
    # if it is return TRUE, else return False
    # note both name and password are case sensitive
    FILE = open('credentials.txt','r')
    status = False
    for one_profile in FILE:
        data = one_profile.split()
        # data[0] = name, data[1] = password
        if data[0] == user_username and data[1] == user_password:
            status = True
    return status

# write the detial of a successfully logged in client
def write_to_logFile(total,time,user_name,client_addr,client_UDP_port):
    # this function is used for writing log information to log files
    # note all the parameters are strings
    f = open("userlog.txt", "a")
    msg = total +"; " + time + "; " + user_name + "; " + client_addr + "; " + client_UDP_port + '\n'
    f.write(msg)
    f.close()

# defreeze a user
def defreeze_a_user(list_of_failed_usernames,user_name,failed_limits):
     new_list = []
     if not (not list_of_failed_usernames):
        for one_list in list_of_failed_usernames:
           if one_list[0] == user_name:
               if one_list[2] >= failed_limits - 1 and time.time() >= (one_list[1] + 10):
                    num_of_attempts = 0
               else:
                    new_list.append(one_list)
     return new_list


# return the number of attempts of that user
def check_client_attempt(list_of_failed_usernames,user_name):
    if not (not list_of_failed_usernames):
           for one_list in list_of_failed_usernames:
                if one_list[0] == user_name:
                    return one_list[2]
    return -100
    
# assign a username to failed attempt list
def assign_username_to_failed_list(list_of_failed_usernames,user_name):
    # if the list is empty simply assign the username into the list
    if not list_of_failed_usernames:
        list_of_failed_usernames.append( [user_name,time.time(),1] )
        return

    status = False

    # if the user already exits in the list,then update the failed attempt times
    if not (not list_of_failed_usernames):
           for one_list in list_of_failed_usernames:
                if one_list[0] == user_name:
                    # updating current attempting time and times of failed attempts
                    one_list[1] = time.time()
                    one_list[2] += 1
                    status = True
                    break
    
    # if the list is not empty and the new failed username is absent
    if status == False:
        list_of_failed_usernames.append( [user_name,time.time(),1] )

# remove a user from the failed_list
def remove_user_from_failed_list(user_name):
    global list_of_failed_usernames
    counter = 0
    while counter < len(list_of_failed_usernames):
        if list_of_failed_usernames[counter][0] == user_name:
            list_of_failed_usernames.remove(list_of_failed_usernames[counter])
        counter += 1


#### Server related functions
def auth_a_client(connectionSocket,failed_limits,client_addr,t_lock):
    global list_of_failed_usernames
    # recieve the user name and password from client
    username_and_password = (connectionSocket.recv(1024).decode('utf-8')).split()
    try:
        user_name = username_and_password[0]
    except IndexError:
        connectionSocket.close()
        return False
    password = username_and_password[1]
    client_UDP_port = int(username_and_password[2])
    recieving_time = time.time()


    # if the username is in the frozen list, but it has epired the frozen time, defreeze it
    # recall one element in the list ---> (username,time of frozen)
    t_lock.acquire()
    list_of_failed_usernames = defreeze_a_user(list_of_failed_usernames,user_name,failed_limits)
    t_lock.release()

    # if the number of attemps equals to the limits, do not check the username and password, forze the account
    # and let user know that the account has been frozen
    if check_client_attempt(list_of_failed_usernames,user_name) == failed_limits - 1: 
        connectionSocket.send("\nInvalid Password. Your account has been blocked. Please try again 10 seconds later".encode('utf-8'))
        connectionSocket.close()
        return False
    elif Authentication(user_name,password) == False:
        # else authenticate this username with its password, if failed close the connection between server and that client
        connectionSocket.send("\nInvalid Password. Please try again".encode('utf-8'))
        # if the client failed to log in,update the list
        t_lock.acquire()
        assign_username_to_failed_list(list_of_failed_usernames,user_name)
        t_lock.release()
        connectionSocket.close()
        return False
    elif Authentication(user_name,password) == True:
        # the user successfully logs in
        connectionSocket.send("\nWelcome to TOOM!".encode('utf-8'))
        # obtain new client number
        client_num = len(list_of_authenticated_users_and_sockets) + 1
        # assign new active client to the list
        # client number + time + username + client address + client_tcp_socket + UDPport
        t_lock.acquire()
        # remove the user from the failed_list
        remove_user_from_failed_list(user_name)
        # append the user to the log file and memory storage
        list_of_authenticated_users_and_sockets.append([client_num,recieving_time,user_name,client_addr,connectionSocket,client_UDP_port] )
        write_to_logFile(str(client_num),convert_ctime(time.ctime(recieving_time)), user_name,client_addr,str(client_UDP_port))
        t_lock.release()
        return True

########################################################################################################################################
# MSG
# Utility functions for MSG

# write the detial of a successfully logged in client
def write_to_messagelog(num,the_time,username,msg,edited):
    # this function is used for writing log information to log files
    # note all the parameters are strings
    f = open("messagelog.txt", "a")
    msg = num +"; " + the_time + "; " + username + "; " + msg + "; " + edited + '\n'
    f.write(msg)
    f.close()

# main MSG function
def handle_msg(username,msg,clientSocket):
    # append the msg to the list
    messages_with_detail = [
            len(list_of_messages_with_clients) + 1, #msg number
            time.time(),    # timestamp
            username,       # username
            msg,        # message
            'no'        # edited
    ]
    list_of_messages_with_clients.append( messages_with_detail )
    # print information on the server terminal
    print("\n" + username + " posted Message " + str(messages_with_detail[0]) + " '" + msg + "'" + " at " + convert_ctime(time.ctime(messages_with_detail[1])) )
    # write the message to the file
    write_to_messagelog(str(messages_with_detail[0]),convert_ctime(time.ctime(messages_with_detail[1])),username,msg,'no')
    # send response to the user
    clientSocket.send(("\n" + "Message " + str(messages_with_detail[0]) + " Posted at " + convert_ctime(time.ctime(messages_with_detail[1]))).encode('utf-8'))


########################################################################################################################################
# rewrite msglog
def rewrite_msglog(l):
    f = open("messagelog.txt", "w")
    for m in l:
        msg = str(m[0]) +"; " + convert_ctime(time.ctime(m[1])) + "; " + m[2] + "; " + m[3] + "; " + m[4] + '\n'
        f.write(msg)
    f.close()
# DLT
# Utility functions for DLT
# function which update the msg log file
def delete_and_update_dataStorage(msgnum):
    global list_of_messages_with_clients
    # msg started at 1, so list[0] is mapped to msg number 1
    deleted_msg = list_of_messages_with_clients.pop(msgnum - 1)[3]
    counter = 0
    while counter < len(list_of_messages_with_clients):
        list_of_messages_with_clients[counter][0] = counter + 1
        counter += 1
    if list_of_messages_with_clients == []:
        # restore the list_of_message
        f = open("messagelog.txt", "w")
        f.close()
    return [time.time(),deleted_msg]

# DLT main function
def handle_dlt(username,msgnum,timestamp,clientSocket):
    # timestamp = float
    msgnum = int(msgnum)
    # check if it is valid to delete
    # a[0] = num --> int,a[1] = time --> float, a[2] = username, a[3] =msg, a[4] = edited
    # if msgnum > size of the list
    if msgnum > len(list_of_messages_with_clients) or msgnum < 1:
        clientSocket.send('\nPlease enter a valid message number'.encode('utf-8'))
        return
    for one_msg in list_of_messages_with_clients:
        if one_msg[0] == msgnum:
            # if the timestamp is not matching with the date storage
            if timestamp != convert_ctime(time.ctime(one_msg[1])):
                clientSocket.send('\nPlease enter a valid timestamp which matches the input message number'.encode('utf-8'))
                return
            if username != one_msg[2]:
                clientSocket.send('\nSorry you can only delete the message sent by yourself'.encode('utf-8'))
                return
            # else it is ok to delet the message 
            deleted_time, deleted_msg = delete_and_update_dataStorage(msgnum)
            if list_of_messages_with_clients != []:
                rewrite_msglog(list_of_messages_with_clients)
            clientSocket.send(('\nMessage ' + str(msgnum) + " '" + deleted_msg + "'" + ' has been successfully deleted at ' + convert_ctime(time.ctime(deleted_time))).encode('utf-8'))
            print("\n" + username + " deleted Message " + str(msgnum) + " '" + deleted_msg + "'" + " at " + convert_ctime(time.ctime(deleted_time)) )
            break
########################################################################################################################################
# EDT
# Utility functions for EDT
# function which dupdate the msg log file
def update_dataStorage(msgnum,msg):
    global list_of_messages_with_clients
    result = 0
    for item in list_of_messages_with_clients:
        if item[0] == msgnum:
            # a[0] = num --> int,a[1] = time --> float, a[2] = username, a[3] =msg, a[4] = edited
            item[1] = time.time()
            item[3] = msg
            item[4] = 'yes'
            result = item[1]
            break
    return result
         

# EDT main function
def handle_edt(username,msgnum,timestamp,newmsg,clientSocket):
    # timestamp = float
    global list_of_messages_with_clients
    msgnum = int(msgnum)
    # check if it is valid to delete
    # a[0] = num --> int,a[1] = time --> float, a[2] = username, a[3] =msg, a[4] = edited
    # if msgnum > size of the list
    if msgnum > len(list_of_messages_with_clients) or msgnum < 1:
        clientSocket.send('\nPlease enter a valid message number'.encode('utf-8'))
        return
    for one_msg in list_of_messages_with_clients:
        if one_msg[0] == msgnum:
            # if the timestamp is not matching with the date storage
            if timestamp != convert_ctime(time.ctime(one_msg[1])):
                clientSocket.send('\nPlease enter a valid timestamp which matches the input message number'.encode('utf-8'))
                return
            if username != one_msg[2]:
                clientSocket.send('\nSorry you can only edit the message sent by yourself'.encode('utf-8'))
                return
            # else it is ok to delet the message
            new_time = update_dataStorage(msgnum,newmsg)
            rewrite_msglog(list_of_messages_with_clients)
            clientSocket.send(('\nMessage ' + str(msgnum) + ' has been successfully edited at ' + convert_ctime(time.ctime(new_time))).encode('utf-8'))
            print("\n" + username + " edited Message to " + str(msgnum) + " '" + newmsg + "'" + " at " + convert_ctime(time.ctime(new_time)) )
            break
########################################################################################################################################
# RDM 
def handle_rdm(username,timestamp_str,clientSocket):
    f = open("messagelog.txt", 'r')
    # 3; 1 Apr 2021 20:05:35; Hans; ASDASDASD; no\n
    print('\n{} is issued RDM command'.format(username))
    print('Return list of messages:')
    status = False
    msg_to_client = ''
    for line in f:
        data = line.split(';')
        if date_to_timestamp(data[1][1:]) > date_to_timestamp(timestamp_str):
            print(line)
            msg_to_client += (data[0] + ';' + data[1] + ';' + data[2] + ';' + data[3] + '\n')
            status = True
    if status == False:
        clientSocket.send("empty".encode('utf-8'))
    else:
        clientSocket.send(msg_to_client.encode('utf-8'))
########################################################################################################################################
# ATU 
def handle_atu(username,clientSocket):
    f = open('userlog.txt', 'r')
    status = False
    msg_to_client = ''
    #1; 1 Apr 2021 20:35:52; 1; 127.0.0.1; 6001
    print('\n{} is issued ATU command'.format(username))
    print('Return active user list:')
    for line in f:
        # arg[2] = username
        data = line.split(';')
        # username in logfile :<space> + <name>
        if username != data[2][1:]:
            print(line)
            msg_to_client += line[3:] + '\n'
            status = True
    if status == False:
        clientSocket.send("empty".encode('utf-8'))
    else:
        clientSocket.send(msg_to_client.encode('utf-8'))

        
#########################################################################################################################################
# OUT 
# Utility functions for OUT
# delete the client from the userlog
def rewrite_userlog(l):
    f = open("userlog.txt", "w")
    for m in l:
        #num; timestamp; username; IP; UDP port
        msg = str(m[0]) +"; " + convert_ctime(time.ctime(m[1])) + "; " + m[2] + "; " + m[3] + "; " + str(m[5]) + '\n'
        f.write(msg)
    f.close()
# function which deletes the client from the list
# list of arg[0] = client_num, arg[1] = recieving_time, arg[2] = username, arg[3] = client_addr, arg[4] = connectionSocket, arg[5] = client_UDP_port
def delete_client_from_dataStorage(uid):
    global list_of_authenticated_users_and_sockets
    list_of_authenticated_users_and_sockets.pop(uid - 1)
    counter = 0
    while counter < len(list_of_authenticated_users_and_sockets):
        list_of_authenticated_users_and_sockets[counter][0] = counter + 1
        counter += 1
    if list_of_authenticated_users_and_sockets == []:
        # if there is no currently active user, clear the logfile
        f = open("userlog.txt", "w")
        f.close()

def handle_out(username,clientSocket):
    # rewrite msglog
    global list_of_authenticated_users_and_sockets
    if list_of_authenticated_users_and_sockets != []:
        for item in list_of_authenticated_users_and_sockets:
            if item[2] == username:
                delete_client_from_dataStorage(int(item[0]))
                if list_of_authenticated_users_and_sockets != []:
                    rewrite_userlog(list_of_authenticated_users_and_sockets)
                break
        clientSocket.send('you have successfully logged out'.encode('utf-8'))



        
        
    



   






# note please use python2.7 to run this program
from socket import *
from server_helper import *
import time
import sys
import threading
from datetime import datetime

def one_connection_process(clientSocket,client_addr,failed_limits,serverSocket,t_lock):
    # if the user passes the authentication
    if auth_a_client(clientSocket,failed_limits,client_addr,t_lock) == True:
        while 1:
            # receive the client's command
            command_and_args = (clientSocket.recv(1024).decode('utf-8'))
            command_and_args = command_and_args.split(';')
            try: 
                command = command_and_args[1]
            except IndexError:
                break
                
            if command == 'MSG':
                # arg[0] = username,arg[1] = command,arg[2] = message
                #examples (messagenum starting at 1,timestamp,username,message,edited)
                t_lock.acquire()
                handle_msg(command_and_args[0],command_and_args[2],clientSocket)
                t_lock.release()
            elif command == 'DLT':
                # arg[0] = username,arg[1] = command,arg[2] = msgnum,arg[3] = timestamp
                t_lock.acquire()
                handle_dlt(command_and_args[0],command_and_args[2],command_and_args[3],clientSocket)
                t_lock.release()
            elif command == 'EDT':
                # arg[0] = username,arg[1] = command,arg[2] = msgnum,arg[3] = timestamp,arg[4] = new msg 
                t_lock.acquire()
                handle_edt(command_and_args[0],command_and_args[2],command_and_args[3],command_and_args[4],clientSocket)
                t_lock.release()
            elif command == 'RDM':
                # arg[1] = command,arg[2] = timestamp in string
                handle_rdm(command_and_args[0],command_and_args[2],clientSocket)
            elif command == 'ATU':
                # arg[0] = username
                handle_atu(command_and_args[0],clientSocket)
            elif command == 'OUT':
                # arg[0] = username
                t_lock.acquire()
                handle_out(command_and_args[0],clientSocket)
                t_lock.release()
                break   

        # if the client send the out command, close the connection
        clientSocket.close()





if __name__ == "__main__":
    # before start the server erease the content of both userlog and messagelog
    f = open("userlog.txt", "w")
    f.close()
    f = open("messagelog.txt", "w")
    f.close()
    # make sure the server port is integer and the failed limits are between 1 and 5
    try:
        TCP_server_port = int(sys.argv[1])
    except ValueError:
        print("Please enter an (integer) for server port \n")
        print ("the server is forced to quit!!!!!!!!")
        exit()
    try:
        failed_limits = int(sys.argv[2])
    except ValueError:
        print("Please enter an (integer) between 1 and 5\n")
        print ("the server is forced to quit!!!!!!!!")
        exit()
    # the number of consecutive fails should be a number(int) between >= 1 and <= 5
    if failed_limits >5 or failed_limits <1:
        print ("Invalid number of allowed failed consecutive attempt: number.")
        print ("The valid value of argument number is an integer between 1 and 5\n")
        print ("the server is forced to quit!!!!!!!!")
        exit()

    # create server socket
    serverSocket = socket(AF_INET, SOCK_STREAM)

    # bind the server socket
    serverSocket.bind(('', TCP_server_port))

    # socket listen
    serverSocket.listen(1)

    #create lock
    t_lock = threading.Lock()

    # create process allocatater
    # this main function will create a 
    while 1:
        # accept the incomming connection request
        clientSocket, client_addr = serverSocket.accept()

        # sever_helper.py, give a client authentication
        # if the client is authenticated then create a thread for that client 
        # to interact with the system
        t = threading.Thread(target = one_connection_process, args = [clientSocket,client_addr[0],failed_limits,serverSocket,t_lock])
        t.daemon = True
        t.start()
        
    
    
        

   
        



    








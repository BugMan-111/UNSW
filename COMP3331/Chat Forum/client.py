# note please use python3 to run this program

# note P2P function is implemented as the following
# 1. three way hadshaking between client and clien to obtain the 'connection'
# 2. sender sends packages to the receiver
# 3. three way hadshaking: sender sends a signal indicating the end of the transmission to the receiver

from socket import *
import sys
from client_helper import * 
import time
from datetime import datetime
import threading

# list of active users
ATU_list = []

# data structure
# UDP_sender_box
UDP_sender_box = {
    'connected' : 'no',
    'current ack' : -1,
    'work done' : 'no',
}

# a dictionary of lists
# init state: empty
# {address : [name = str, list_of_data_chunks = []]} one element
UDP_receiverBox = {}

# index of the sent file
index_of_sent_file = 0

# UPD suggesting counter
UPD_suggesting_counter = False


############################################################################################################################################################################
############################################################################################################################################################################
# get the postfix of file
def send_a_file_to_client(FileString,username, dest_addr,listening_port):
    global UDP_sender_box
    global index_of_sent_file
    # create UDP socket
    clientSocket = socket(AF_INET, SOCK_DGRAM)

    # get the filename and postfix
    fileName = FileString.split('.')[0]
    file_post_fix = FileString.split('.')[1]

    # convert the file to chunks of binaries
    content = open(FileString, 'rb').read()
   
    # 1 hex = 4 bits --> 2 hex = 8 bits = 1 byte
    # len(file) in bytes = len(file) / 2
    container = []

    size_of_file_in_bytes = len(content)

    if (size_of_file_in_bytes / 1024) > int((size_of_file_in_bytes / 1024)):
        # have reminder, therefore total package size + 1
        total_packages = int((size_of_file_in_bytes / 1024)) + 1
    else:
        total_packages = int((size_of_file_in_bytes / 1024))

    counter = 0
    
    # handshaking
    package_header_type = (0).to_bytes(1, byteorder='big')
    package_header_ack = (listening_port).to_bytes(2, byteorder='big')
    payload = (username + '_' + FileString).encode('utf-8')
    # type(1 bytes) + ack(2 bytes) + username.utf(unkown but less likely to be greater than 1024 bytes)
    clientSocket.sendto(package_header_type + package_header_ack + payload,dest_addr)
    while 1:
        # if the handshaken has been done
        if UDP_sender_box['connected'] == 'yes':
            break
        else:
            # re-send the handshaken request 
            clientSocket.sendto(package_header_type + package_header_ack + payload, dest_addr)
            time.sleep(0.25)
    # make the window size 100
    # 0 ~ 99 ---> count = 100
    ack = 1
    status = False
    while 1:
            counter = 0
            while counter < 100:
                # decide the ack_number and identity number
                # format = type_identifier(1) + ack number(2) + payload(1024)
                if ack + counter > total_packages:
                    status = True
                    break
                package_header_type = (1).to_bytes(1, byteorder='big')
                package_header_ack = (ack + counter).to_bytes(2, byteorder='big')
                payload = content[(ack + counter - 1) * 1024 : (ack + counter) * 1024]
                clientSocket.sendto(package_header_type + package_header_ack + payload, dest_addr)
                counter += 1
            # update the ack
            time.sleep(1)
            ack = UDP_sender_box['current ack']
            if status == True and ack == total_packages:
                break
    # inform the receive of the end of the transmission
    package_header_type = (4).to_bytes(1, byteorder='big')
    package_header_ack = (index_of_sent_file).to_bytes(2, byteorder='big')
    # type(1 bytes) + ack(2 bytes) + username.utf(unkown but less likely to be greater than 1024 bytes)
    while 1:
        # if the handshaken has been done
        if UDP_sender_box['work done'] == 'yes':
            break
        else:
            # re-send the handshaken request 
            clientSocket.sendto(package_header_type + package_header_ack, dest_addr)
            time.sleep(0.25)
    # print msg
    print('\n\nthe file has been successfully uploaded to the destination !')
    print('\nEnter one of the following commands (MSG, DLT, EDT, RDM, ATU, OUT, UPD): ',end =" ")
    # clean the sender's box
    UDP_sender_box = {
        'connected' : 'no',
        'current ack' : -1,
        'work done' : 'no', 
    }
    # update index_send file
    if index_of_sent_file == 0:
        index_of_sent_file = 1
    else:
        index_of_sent_file = 0





############################################################################################################################################################################
############################################################################################################################################################################
# main process which allows client to run MSG, DLT, EDT, RDM, ATU, OUT, UPD commands 
def handle_interaction_with_server(finalised_clientSocket,listening_port,user_name):
    global ATU_list
    global UPD_suggesting_counter
    # the client logs in successfully
    while 1:
        # asking the user to choose a command
        the_command_with_args = input('\nEnter one of the following commands (MSG, DLT, EDT, RDM, ATU, OUT, UPD): ')
        try:
            command = the_command_with_args.split()[0]
        except IndexError:
            print('\nPlease make sure you are using the following commands(eg.MSG,DLT,...)')
            continue

        if command == 'MSG':
            if check_MSG(the_command_with_args) == True:
                # send MSG message
                msg = the_command_with_args.split(' ',1)[1]
                finalised_clientSocket.send((user_name + ';' + command + ';' + msg).encode('utf-8'))
                print((finalised_clientSocket.recv(1024).decode('utf-8')))
            else:
                print('\nooops, it appears you are not using the correct format for MSG')
                print('the correct format of MSG is: MSG <text>')
        elif command == 'DLT':
            if check_DLT(the_command_with_args) == True:
                # send DLT NUMBER TIMESTAMP 
                DLT_args = the_command_with_args.split(' ',2)
                finalised_clientSocket.send((user_name + ';' + command + ';' + DLT_args[1] + ';' + DLT_args[2]).encode('utf-8'))
                print((finalised_clientSocket.recv(1024).decode('utf-8')))
            else:
                print('\nooops, it appears you are not using the correct format for DLT')
                print('the correct format of DLT is: DLT <messagenumber> <timestamp>')
        elif command == 'EDT':
            if check_EDT(the_command_with_args) == True:
                # send message
                CM_NUM_TIMES = the_command_with_args.split(' ',2)
                number = CM_NUM_TIMES[1]
                # 19 Feb 2021 21:39:04(19) (20)meg(started from 21)
                timestamp = CM_NUM_TIMES[2][:20]
                message = CM_NUM_TIMES[2][21:]
                finalised_clientSocket.send((user_name + ';' + command + ';' + number + ';' + timestamp + ';' + message).encode('utf-8'))
                print(finalised_clientSocket.recv(1024).decode('utf-8'))
            else:
                print('\nooops, it appears you are not using the correct format for EDT')
                print('the correct format of EDT is: EDT <messagenumber> <timestamp> <message>')
        elif command == 'RDM':
            if check_RDM(the_command_with_args) == True:
                # send message
                RDM_args = the_command_with_args.split(' ',1)
                finalised_clientSocket.send((user_name + ';' + command + ';' + RDM_args[1]).encode('utf-8'))
                data = finalised_clientSocket.recv(1024).decode('utf-8')
                if data == 'empty':
                    print('\nSorry no message was sent after the given time')
                else:   
                    data = data.split('\n')
                    print()
                    for item in data:
                        if (not item) == False:
                            print(item)
            else:
                print('\nooops, it appears you are not using the correct format for RDM')
                print('the correct format of RDM is: RDM <timestamp>')
        elif command == 'ATU':
            if check_ATU(the_command_with_args) == True:
                # send message
                finalised_clientSocket.send((user_name + ';' + command).encode('utf-8'))
                data = finalised_clientSocket.recv(1024).decode('utf-8')
                ATU_list = []
                if data == 'empty':
                    print('\nOops it apears you are the only active user for now')
                else:   
                    data = data.split('\n')
                    print()
                    for item in data:
                        if item != '':
                            new = item.split(';')
                            atu_date = new[0]
                            atu_user_name = new[1][1:]
                            atu_address = new[2][1:]
                            atu_port = int(new[3])
                            ATU_list.append([atu_user_name,(atu_address,atu_port)])
                            if (not item) == False:
                                print(atu_date + ' ' + atu_user_name)
            else:
                print('\nooops, it appears you are not using the correct format for ATU')
                print('the correct format of ATU is: ATU') 
        elif command == 'OUT':
            if check_OUT(the_command_with_args) == True:
                # send message
                finalised_clientSocket.send((user_name + ';' + command).encode('utf-8'))
                print((finalised_clientSocket.recv(1024).decode('utf-8')))
                break
            else:
                print('\nooops, it appears you are not using the correct format for OUT')
                print('the correct format of OUT is: OUT') 
        elif command == 'UPD':
            if UPD_suggesting_counter == False:
                print('\nWe noticed it is you first time to use UPD, Please make sure you run ATU everytime just before(please be hurry) you want to use UPD')
                UPD_suggesting_counter = True
            if check_UPD(the_command_with_args) == True:
                send_user = the_command_with_args.split()[1]
                filename = the_command_with_args.split()[2]
                # get data addr = tuple(ip,port)
                # send file to that user
                # dest_addr = (address, port)
                dest_addr = ()
                if ATU_list != []:
                    for item in ATU_list:
                        if item[0] == send_user:
                            dest_addr = item[1]
                    if dest_addr == ():
                        print('\nSorry it seems like the user is not currently active')
                        print('please try using ATU to obtain the active user agian')
                        continue
                    t_p2p = threading.Thread(target = send_a_file_to_client, args = [filename,user_name,dest_addr,listening_port])
                    t_p2p.start()
                    print("\nHey we've started to transfer your file, you can do something else at the same time")
                else:
                    print('\nAhhhhhh please please please make sure you run ATU before you use command UPD :(')
            else:
                print('\nooops, it appears you are not using the correct format or there are erros in your commands for UPD')
                print('the correct format of UPD is: UPD <username> <filename>') 
        else:
            print('\nPlease make sure you are using the following commands(eg.MSG,DLT,...)')







############################################################################################################################################################################
############################################################################################################################################################################
# UDP server receives package from other clients
def p2p_receiver(listening_port):
    global UDP_receiverBox
    global UDP_sender_box

    received_list = {}
    # create UDP server
    serverSocket = socket(AF_INET, SOCK_DGRAM)
    try:
        serverSocket.bind(('localhost', listening_port))
    except OSError:
        print('\nPlease change the P2P(UDP) listening port, as it seems to be occupied !!!')
        return

    while 1:
        # the size of one package will always be 1 + 2 + 1024 = 3 + 1024 = 1027 bytes
        package, clientAddress = serverSocket.recvfrom(1027)
        #Updating the sender_box or the receiver_box
        package_type = int.from_bytes(package[0:1], byteorder='big')

        if package_type == 0:
            # if 0 in the first byte of the header is received
                # the client should interpret it as the sender is handshaking(send username) to this client(receiver)
                # package[3:] = total - 1 - 2 = payload 
            client_listening_port = int.from_bytes(package[1:3], byteorder='big')
            # (0) = sender listening port, (1) = username (2) = ack_num (3) = chunks of cotent 
            UDP_receiverBox[clientAddress] = [client_listening_port,(package[3:]).decode('utf-8'),0,[]]
            package_header_type = (3).to_bytes(1, byteorder='big')
            package_header_ack = (0).to_bytes(2, byteorder='big')
            serverSocket.sendto(package_header_type + package_header_ack, (clientAddress[0],UDP_receiverBox[clientAddress][0]))
        elif package_type == 1:
            # if 1 in the first byte of the header is received
                # the client should interpret it as the sender is sending data with ack to this client(receiver)
            if clientAddress in UDP_receiverBox[clientAddress] == False:
                raise Exception('error the sender sends the packets without handshaking')

            sender_ack = int.from_bytes(package[1:3], byteorder='big')
            # (0) = sender listening port, (1) = username (2) = ack_num (3) = chunks of cotent 
            if sender_ack != UDP_receiverBox[clientAddress][2] + 1:
                # discard(not accepting) the data
                package_header_type = (2).to_bytes(1, byteorder='big')
                # send the receiver's current ack back to sender
                package_header_ack = (UDP_receiverBox[clientAddress][2]).to_bytes(2, byteorder='big')
                serverSocket.sendto(package_header_type + package_header_ack,  (clientAddress[0],UDP_receiverBox[clientAddress][0]))
            else:
                # send the new ack back
                package_header_type = (2).to_bytes(1, byteorder='big')
                package_header_ack = (sender_ack).to_bytes(2, byteorder='big')
                serverSocket.sendto(package_header_type + package_header_ack,  (clientAddress[0],UDP_receiverBox[clientAddress][0]))
                # accept the package
                UDP_receiverBox[clientAddress][2] = sender_ack
                UDP_receiverBox[clientAddress][3].append(package[3:])
        elif package_type == 2:
            # if 2 in the first bytes of the header is received
                # the client should interpret it as the receiver is sending ack to this client(sender)
            receiver_new_ack = int.from_bytes(package[1:3], byteorder='big')
            UDP_sender_box['current ack'] = receiver_new_ack
        elif package_type == 3:
            # if 3 in the first bytes of the header is received
                # the client should interpret it as the receiver is accepting the handshaking to this client(sender)
            UDP_sender_box['connected'] = 'yes'
            UDP_sender_box['current ack'] = 0
        elif package_type == 4:
            # if 4 in the first bytes of the header is received
                # the client should interpret it as the sender send the end of transmission signal to the client(receiver)
            # (0) = sender listening port, (1) = username (2) = ack_num (3) = chunks of cotent 
            file_index = int.from_bytes(package[1:3], byteorder='big')
            # if the client_addr existed as a key and the index number does not match the current index number
            if clientAddress in received_list and received_list[clientAddress] == file_index:
                pass
            else:
                chunks = UDP_receiverBox[clientAddress][3]
                new = b''
                for item in chunks:
                    new += item
                file_write = open(UDP_receiverBox[clientAddress][1],'wb')
                file_write.write(new)
                file_write.close()
                print()
                print('\nA file {} has been received please go to check it !!'.format(UDP_receiverBox[clientAddress][1]))
                print('\nEnter one of the following commands (MSG, DLT, EDT, RDM, ATU, OUT, UPD): ',end =" ")
                received_list[clientAddress] = file_index
            # send ack of end of the transmission back to the sender
            package_header_type = (5).to_bytes(1, byteorder='big')
            package_header_ack = (0).to_bytes(2, byteorder='big')
            serverSocket.sendto(package_header_type + package_header_ack, (clientAddress[0],UDP_receiverBox[clientAddress][0]))
        elif package_type == 5:
            # if 5 in the first bytes of the header is received
                # the client should interpret it as the receiver ackonldeged the finishing of file transmission
            UDP_sender_box['work done'] = 'yes'







############################################################################################################################################################################
############################################################################################################################################################################
# main func 
if __name__ == "__main__":
    #get the server ip address
    server_ip_address = sys.argv[1]

    # get the port number
    try:
        server_port = int(sys.argv[2])
    except ValueError:
        print("\nPlease enter an (integer) for server port")
        print ("the server is forced to quit!!!!!!!!")
        exit()
    # get the UDP port number
    try:
        UDP_port = int(sys.argv[3])
    except ValueError:
        print("\nPlease enter an (integer) for UDP server port that the program is currently listening to")
        print ("the server is forced to quit!!!!!!!!")
        exit()

    #create client socket(tcp)
    clientSocket = socket(AF_INET, SOCK_STREAM)

    # prompt user to input username
    user_name = input('\nusername: ')

    # the finalised clientsocket
    finalised_clientSocket = None


    while 1:
        clientSocket = socket(AF_INET, SOCK_STREAM)

        try:
            clientSocket.connect((server_ip_address, server_port))
        except ConnectionRefusedError:
            print('\nPlease double check that you entered a valid server address')
            sys.exit()

        # prompt user to input password to the server
        try:
            user_password = input('\npassword: ')
        except EOFError:
            print()
            sys.exit()
        if user_password == "":
            print('\nIt appreas that you entered nothing, Due to technical consideration, the pogram is forced to quit')
            clientSocket.close()
            exit()

        # send user name and password
        clientSocket.send((user_name + ' ' + user_password + ' ' + str(UDP_port)).encode('utf-8'))

        # recieve logging status from the server, if the password is not matching the user name,prompt the user to try agian
        server_replying_msg = clientSocket.recv(1024).decode('utf-8')

        # if the server's replying state is OK then end the loggin process(user manages to log in)
        if server_replying_msg == "\nWelcome to TOOM!":
            print(server_replying_msg)
            finalised_clientSocket = clientSocket
            break
        # if the server's replying msg is not ok then simply display the replying message, and close that socket
        print(server_replying_msg)
        clientSocket.close()
    
    # the user successfully logged in 
    # init two threads, one for interaction with server, the other for p2p
    lock = threading.Lock()
    t1 = threading.Thread(target = handle_interaction_with_server, args = [finalised_clientSocket,UDP_port,user_name])
    t2 = threading.Thread(target = p2p_receiver, args = [UDP_port])

    t1.daemon = True
    t2.daemon = True

    t1.start()
    t2.start()

    while 1:
        # if t1 is ended
        if not t1.is_alive():
            # stop the UDP receiver and exit the program
            clientSocket.close()
            sys.exit()
        if not t2.is_alive():
            clientSocket.close()
            sys.exit()

    
   

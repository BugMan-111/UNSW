#!/usr/bin/python3
# q1

import sqlite3,sys
import operator
from collections import OrderedDict,deque
from sys import maxsize
import itertools


# get number of actors
def num_of_actors(cur):
    cur.execute('''SELECT DISTINCT COUNT(*)
                   FROM actor
      '''.format()
    )
    return cur.fetchone()[0]

# get the actor_id of the given actor name
def id_of_actor(cur, actorname):
    cur.execute('''SELECT actor.id
                   FROM actor
                   WHERE UPPER(actor.name) = {}
      '''.format(actorname.upper())
    )
    t = cur.fetchone()
    if t == None:
        return -10000
    return t[0]

def name_of_actor(cur,actorid):
    cur.execute('''SELECT actor.name
                   FROM actor
                   WHERE actor.id = {}
      '''.format(actorid)
    )
    return cur.fetchone()[0]

# get the adjacant actor id of the given actor
def get_adjacent(cur,u):
    cur.execute('''SELECT DISTINCT acting.actor_id
                   FROM acting 
                   WHERE acting.movie_id IN(
                        SELECT acting.movie_id
                        FROM acting
                        WHERE acting.actor_id = {}
                   ) 
      '''.format(u)
    )
    list_of_adj = []
    for t in cur.fetchall():
          if t == None:
              break
          list_of_adj.append(t[0])
    return list_of_adj
 
# Function which performs bfs
# from the given souce vertex
def bfs(cur,parent,n,start,end):
    # dist will contain shortest distance
    # from start to every other vertex
    dist = [maxsize for _ in range(n)]
    q = deque()
 
    # Insert source vertex in queue and make
    # its parent -1 and distance 0
    q.append(start)
    parent[start] = [-1]
    dist[start] = 0
 
    # Untill Queue is empty
    while q:
        u = q[0]
        q.popleft()
        adj_of_u = get_adjacent(cur,u)
        for v in adj_of_u:
            if (dist[v] > dist[u] + 1):
 
                # A shorter distance is found
                # So erase all the previous parents
                # and insert new parent u in parent[v]
                dist[v] = dist[u] + 1
                q.append(v)
                parent[v].clear()
                parent[v].append(u)
 
            elif (dist[v] == dist[u] + 1):
 
                # Another candidate parent for
                # shortes path found
                parent[v].append(u)
    if dist[end] > 6:
        exit()
    

# Function which finds all the paths
# and stores it in paths array
def find_paths(paths, path,parent, n, u):
    # Base Case
    if (u == -1):
        paths.append(path.copy())
        return
 
    # Loop for all the parents
    # of the given vertex
    for par in parent[u]:
 
        # Insert the current
        # vertex in path
        path.append(u)
 
        # Recursive call for its parent
        find_paths(paths, path, parent, n, par)
 
        # Remove the current vertex
        path.pop()

def func_combination(list1):
    current = []
    if len(list1) == 1:
        return (1,list1)
    else:
        return (2,itertools.product(*list1))

def convert_to_string1(cur,l,one_path):
    string = ''
    string += name_of_actor(cur,one_path[0]) + ' was in ' + l[0]
    if l[1] != None:
            string += ' (' + str(l[1]) + ') with '
    string += name_of_actor(cur,one_path[1])
    return string

def convert_to_string2(cur,l,one_path):
    counter = 0
    string = ''
    while counter < len(one_path) - 1:
        # t0 = title t1 = year 
        string += name_of_actor(cur,one_path[counter]) + ' was in ' + l[counter][0]
        if l[1] != None:
            string += ' (' + str(l[counter][1]) + ') with '
        if counter < len(one_path) - 2:
            string += name_of_actor(cur,one_path[counter + 1]) + '; '
        else:
            string += name_of_actor(cur,one_path[counter + 1])

        counter += 1
    return string




def re_format(cur,newlist, one_path):
    counter = 0
    list_of_movie = []
    while counter < len(one_path) - 1:
        cur.execute('''SELECT DISTINCT movie.title,movie.year
                   FROM acting 
                   JOIN movie ON (acting.movie_id = movie.id)
                   WHERE acting.actor_id = {} AND acting.movie_id IN(
                        SELECT acting.movie_id
                        FROM acting
                        WHERE acting.actor_id = {}
                   ) '''.format(one_path[counter],one_path[counter + 1])
        )
        current_list = []
        for t in cur.fetchall():
            if t == None:
              break
            # t0 = title t1 = year 
            current_list.append([t[0],t[1]])
        #list of list of list
        list_of_movie.append(current_list)
        counter += 1

    
    # get the combination of the path 
    result = func_combination(list_of_movie)
  
    # convert each element in the result to the string and append it to movie_list
    #print(result)
    if result[0] == 1:
        for element in result[1][0]:
            newlist.append(convert_to_string1(cur,element,one_path))
    else:
        for element in result[1]:
            newlist.append(convert_to_string2(cur,element,one_path))


        


 

# Function which prints all the paths
# from start to end
def print_paths(cur,n,start,end):
    paths = []
    path = []
    parent = [[] for _ in range(n)]

    # Function call to bfs
    bfs(cur,parent, n, start,end)

    # Function call to find_paths
    find_paths(paths, path, parent, n, end)
    print_list = []
    for v in paths:
        # Since paths contain each
        # path in reverse order,
        # so reverse it
        v.reverse()

        # get all the path between two nodes
        re_format(cur,print_list,v)

    # sort the list
    print_list.sort()

    counter = 1
    for item in print_list:
        print(str(counter) + '. ' + item)
        counter += 1

    
      
if len(sys.argv) != 3:
      sys.exit(1)

node_init_start = sys.argv[1]
node_final_dest = sys.argv[2]

con = sqlite3.connect('a2.db')

cur = con.cursor()
if node_init_start.upper() != node_final_dest.upper():
    # count total number of nodes
    num_of_nodes = num_of_actors(cur) 

    # get id of the start and dest
    node_start = id_of_actor(cur,"'" + node_init_start + "'")
    node_dest = id_of_actor(cur,"'" + node_final_dest + "'") 

    if node_start == -10000 or node_dest == -10000:
        sys.exit()

    # note number of node started at 1 not 0
    print_paths(cur,num_of_nodes + 1,node_start,node_dest)
else:
        single_actor_id = id_of_actor(cur,"'" + node_init_start + "'")
        if single_actor_id == -10000:
            sys.exit()
        cur.execute('''SELECT DISTINCT movie.title,movie.year
                   FROM acting
                   JOIN movie ON (acting.movie_id = movie.id)
                   WHERE acting.actor_id = {}
                   '''.format(single_actor_id)
        )
        current_list_else = []
        for t in cur.fetchall():
            if t == None:
              break
            # t0 = title t1 = year 
            current_list_else.append([t[0],t[1]])
        print_else = []
        counter_else = 0
        while counter_else <= len(current_list_else) - 1:
            result_else = node_init_start + ' was in ' + current_list_else[counter_else][0]
            if current_list_else[counter_else][1] != None:
                result_else += ' (' + str(current_list_else[counter_else][1]) + ') with '
            result_else += node_init_start
            print_else.append(result_else)
            counter_else += 1
        print_else.sort()
        counter_else = 0
        while counter_else <= len(print_else) - 1:
            result_final_else = str(counter_else + 1) + '. ' + print_else[counter_else]
            print(result_final_else)
            counter_else += 1
        
        
    


































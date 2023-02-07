#!/usr/bin/python3
# q1

import sqlite3,sys
import operator
from collections import OrderedDict

def fix_the_none(l):
      if l[2] == None:
         l[2] = -1
      if l[4] == None:
         l[4] = -1
      if l[3] == None:
         l[3] = ''
      if l[5] == None:
         l[5] = ''

def func_complete_table(movie_list,cur):
    #movie.id, movie.title, movie.year, movie.content_rating,movie.director_id
    new_list = []
    for one_movie in movie_list:
          one_movie
          # get the imdb score
          cur.execute('''SELECT rating.imdb_score 
                         FROM movie
                         JOIN rating ON (movie.id = rating.movie_id)
                         WHERE movie.id = {}
            '''.format(one_movie[0])
          )
          t = cur.fetchone()
          if t == None:
            imdb_score = None
          else:
            imdb_score = t[0]
          # get the imdb score
          cur.execute('''SELECT director.name
                         FROM movie
                         JOIN director ON (director.id = movie.director_id)
                         WHERE movie.id = {}
            '''.format(one_movie[0])
          )
          t = cur.fetchone()
          if t == None:
            director_name = None
          else:
            director_name = t[0]
          the_list = [one_movie[0],one_movie[1],one_movie[2],one_movie[3],imdb_score,director_name]
          fix_the_none(the_list)
          new_list.append(the_list)
    return new_list

           


def func_filter(cur,substring, movie_list):
    # 0    1       2     3             4         5
    # .id, title, year, content_rating,imdb_score,director_name
    # for every stored movie check wether substring is movie title or dir or actor, if not delet it
    counter = 0
    new_list = []
    for one_movie in movie_list:
          is_dir = False
          is_actor = False
          is_title = False
          # check wether substring is title
          if substring.upper() in one_movie[1].upper():
                is_title = True
          #check wether substring is actorname of the given movie
          list_of_actor_names = []
          cur.execute('''SELECT actor.name
                        FROM movie
                        JOIN acting ON (movie.id = acting.movie_id)
                        JOIN actor ON (acting.actor_id = actor.id)
                        WHERE movie_id = {}
                      '''.format(one_movie[0])
          )
          # get the list of actorname for given movie
          while True:
              t = cur.fetchone()
              if t == None:
                break
              list_of_actor_names.append(t[0])
          #check wether substring is actorname of the given movie
          for item in list_of_actor_names:
              if substring.upper() in item.upper():
                  is_actor = True
          #  check wether substring is directorname
          if substring.upper() in one_movie[5].upper():
              is_dir = True
          if(is_actor == True or is_dir == True or is_title == True):
              new_list.append(one_movie)  
    return new_list
    

if len(sys.argv) < 2 :
      sys.exit(1)

# first find all the movie and related information for substring one
# which could be title,actorname or directorname

con = sqlite3.connect('a2.db')

cur = con.cursor()

movie_list = []

argv = sys.argv

edited_arg1 = '"%' + argv[1] + '%"'

# assume argv[1] is movie title
cur.execute('''SELECT movie.id, movie.title, movie.year, movie.content_rating,movie.director_id
                FROM movie
                WHERE movie.title LIKE {}
              '''.format(edited_arg1)
            )
while True:
  t = cur.fetchone()
  if t == None:
    break
  if [t[0],t[1],t[2],t[3],t[4]] not in movie_list:
        movie_list.append([t[0],t[1],t[2],t[3],t[4]])

# assume argv[1] is actorname
cur.execute('''SELECT movie.id, movie.title, movie.year, movie.content_rating,movie.director_id
                FROM actor
                JOIN acting ON (actor.id = acting.actor_id)
                JOIN movie ON (acting.movie_id = movie.id)
                WHERE actor.name Like {}
              '''.format(edited_arg1)
)
while True:
  t = cur.fetchone()
  if t == None:
    break
  if [t[0],t[1],t[2],t[3],t[4]] not in movie_list:
        movie_list.append([t[0],t[1],t[2],t[3],t[4]])

# assume argv[1] is directorname
cur.execute('''SELECT movie.id, movie.title, movie.year, movie.content_rating,movie.director_id
                FROM director
                JOIN movie ON (director.id = movie.director_id)
                WHERE director.name Like {}
              '''.format(edited_arg1)
)
while True:
  t = cur.fetchone()
  if t == None:
    break
  if [t[0],t[1],t[2],t[3],t[4]] not in movie_list:
    movie_list.append([t[0],t[1],t[2],t[3],t[4]])

new_list = func_complete_table(movie_list,cur)

# started from argv[2]
# filter the result obtained from last phase
counter = 2
while counter < len(argv):
      new_list = func_filter(cur,argv[counter], new_list)
      counter += 1

# fix the new_list


# find the genres of each movie and print the them out
counter = 1
new_list.sort(key = operator.itemgetter(1))
new_list.sort(key = operator.itemgetter(4),reverse=True)
new_list.sort(key = operator.itemgetter(2),reverse=True)

counter = 1
for one_movie in new_list:
      list_of_genres = []
      cur.execute('''SELECT genre.genre
                      FROM movie
                      JOIN genre ON (genre.movie_id = movie.id)
                      WHERE movie.id = {}
            '''.format(one_movie[0])
      )
      # moviename + ' ' +(year, content_rating,IMDB) + ' ' + [list of genres]
      # 0    1       2     3             4          5
      # .id, title, year, content_rating,imdb_score,director_name
      while True:
        t = cur.fetchone()
        if t == None:
          break
        list_of_genres.append(t[0])
      list_of_genres.sort()
      middle = [] 
      if one_movie[2] != -1:
            middle.append(one_movie[2])
      if one_movie[3] != '':
            middle.append(one_movie[3])
      if one_movie[4] != -1:
            middle.append(float(one_movie[4]))
      genre_string = ''
      if len(list_of_genres) != 0: 
        genre_string = '['
        for item in list_of_genres:
              genre_string += item +','
        genre_string = genre_string[:-1]
        genre_string += ']'
            
      if len(middle) == 1:
        middle = str(tuple(middle)).replace(',','')
      elif len(middle) == 0:
        middle = ''
      else:
        middle = str(tuple(middle))

      new_middle = ''
      i = 0
      j = 0
      while i < len(middle):
          if middle[i] == "'":
              i += 1
          else:
              new_middle += middle[i]
              i += 1
              j += 1

      final_result = str(counter) + '. ' + one_movie[1]
      if new_middle != "":
            final_result += ' ' + new_middle
      if genre_string != "":
            final_result += ' ' + genre_string
            
      print(final_result)
      counter += 1









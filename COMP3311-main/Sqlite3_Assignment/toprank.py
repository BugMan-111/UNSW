#!/usr/bin/python3
# q1

import sqlite3,sys
import operator
from collections import OrderedDict


def get_rid_of_quotes(string):
      new_string = ''
      i = 0
      j = 0
      while i < len(string):
          if string[i] == "'":
              i += 1
          else:
              new_string += string[i]
              i += 1
              j += 1
      return new_string

#a0 = id, a1 = title,a2 = year, a3 = content_rating,a4 = lang,a5 = imdb_score,a6 = rating.num_voted_users
def func_get_movie(cur,movie_list,the_genre):
      new_list = []
      for one_movie in movie_list:
            #filter our the one without the new genre
            cur.execute('''SELECT movie.id
                           FROM movie
                           JOIN genre ON (UPPER(genre.genre) = {} and genre.movie_id = movie.id)
                           WHERE movie.id = {}
              '''.format(the_genre.upper(),one_movie[0])
            )
            if cur.fetchone() != None:
                  # keep that movie: that is, assign that movie to the new list
                  # fix none imdb and none votes
                  new_element = [one_movie[0],one_movie[1],one_movie[2],one_movie[3],one_movie[4],one_movie[5],one_movie[6]]
                  if new_element[5] == None:
                        new_element[5] = -1
                  if new_element[6] == None:
                        new_element = -1
                  new_list.append(new_element)
      del movie_list
      return new_list


def func_fix_list(movie_list):
      #The output is ranked by IMDB score and then by the number of votes (both in descending order).
      #fix the string into format
      #a0 = id, a1 = title,a2 = year, a3 = content_rating,a4 = lang,a5 = imdb_score,a6 = rating.num_voted_users
      result = []
      # first sort the list
      movie_list.sort(key = operator.itemgetter(6),reverse=True)
      movie_list.sort(key = operator.itemgetter(5),reverse=True)

      counter = 1
      for one_movie in movie_list:
            middle = []
            if one_movie[2] != None:
                  middle.append(one_movie[2])
            if one_movie[3] != None:
                  middle.append(one_movie[3])
            if one_movie[4] != None:
                  middle.append(one_movie[4])
            
            if len(middle) == 1:
                  middle = str(tuple(middle)).replace(',','')
            elif len(middle) == 0:
                  middle = ""
            else:
                  middle = str(tuple(middle))
            

            postfix_list = []
            if one_movie[5] != -1:
                  postfix_list.append(float(one_movie[5]))
            if one_movie[6] != -1:
                  postfix_list.append(one_movie[6])

            if len(postfix_list) == 1:
                  postfix_list = str(postfix_list).replace(',','')
            elif len(postfix_list) == 0:
                  postfix_list = ""
            else:
                  postfix_list = str(postfix_list)
            

            middle = get_rid_of_quotes(middle)
            new_string = str(counter) + '. ' + one_movie[1]
            if middle != "":
                  new_string += ' ' + middle
            new_string += ' ' + postfix_list
            result.append(new_string)
            counter += 1     
      return result       


      

if len(sys.argv) != 3:
      print('Usage: ./toprank Genres MinRating')
      sys.exit(1)

# first find all the movie and related information for substring one
# which could be title,actorname or directorname

all_movies = False

searchstring = sys.argv[1]
minRank = float(sys.argv[2])

search_list = []

if searchstring == "":
  all_movies = True
else:
  search_list = searchstring.split('&')

con = sqlite3.connect('a2.db')

cur = con.cursor()

movie_list = []

if all_movies == False:
      edited_searchword_1 = '"' + search_list[0] + '"'
      cur.execute('''SELECT movie.id, movie.title, movie.year, movie.content_rating,movie.lang,rating.imdb_score,rating.num_voted_users
                  FROM genre
                  JOIN movie ON (genre.movie_id = movie.id)
                  JOIN rating ON (rating.movie_id = movie.id AND rating.imdb_score >= {})
                  WHERE UPPER(genre.genre) = {}
                  '''.format(minRank,edited_searchword_1.upper())
                  )
      while True:
            t = cur.fetchone()
            if t == None:
                  break
            if [t[0],t[1],t[2],t[3],t[4],t[5],t[6]] not in movie_list:
                  movie_list.append([t[0],t[1],t[2],t[3],t[4],t[5],t[6]])
else:
      cur.execute('''SELECT movie.id, movie.title, movie.year, movie.content_rating,movie.lang,rating.imdb_score,rating.num_voted_users
                  FROM genre
                  JOIN movie ON (genre.movie_id = movie.id)
                  JOIN rating ON (rating.movie_id = movie.id AND rating.imdb_score >= {})
                  '''.format(minRank)
                  )
      while True:
            t = cur.fetchone()
            if t == None:
                  break
            if [t[0],t[1],t[2],t[3],t[4],t[5],t[6]] not in movie_list:
                  movie_list.append([t[0],t[1],t[2],t[3],t[4],t[5],t[6]])



# filter the result
counter = 1
while counter < len(search_list):
      movie_list = func_get_movie(cur,movie_list,'"' + search_list[counter] + '"')
      counter += 1

# get the new movie list ready
list_of_prepared_strings = []
list_of_prepared_strings = func_fix_list(movie_list)

# print the final result
for one_string in list_of_prepared_strings:
      print(one_string)







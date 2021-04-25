import sys
import sqlite3


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

#find all recipes that can be made
GETALL  = 'SELECT tag_name FROM Tags'
all     = set(cursor.execute(GETALL)) #list of all recipe_ids

s = ""
for t in all :
    s = s + str(t[0]) + " "
    
#write string to file
f = open("tags.txt", "w")    
f.write(s)
    
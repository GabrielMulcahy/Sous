import sys
import sqlite3
import random

#checks if the last recipe created has already been rated in the DB

# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

#id of last recipe 
last = open("last_recipe.txt", "r") 
id = int(last.read(1)) 
    
#check if the recipe has been rated or not
CHECK = 'SELECT rated FROM Recipes WHERE recipe_id = ?'
cursor.execute(CHECK, (id,))
result = cursor.fetchall()
result = int(result[0][0]) #take the result and turn it into a single integer
print(result)

#write answer to file
f = open("check_rated.txt", "w")    
if result == 0 :
    f.write("false")
else :
    f.write("true")
    
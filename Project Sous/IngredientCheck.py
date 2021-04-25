import sys
import sqlite3


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

#ingredient
try:
    ingredient = sys.argv[1]
except:
    print("Please provide one ingredient")
    sys.exit(1)
    

GETAMOUNT = 'SELECT amount FROM Ingredients WHERE ingredient_name = ?'
cursor.execute(GETAMOUNT, (ingredient,))
amount = cursor.fetchall()[0][0]

GETUNIT = 'SELECT unit FROM Ingredients WHERE ingredient_name = ?'
cursor.execute(GETUNIT, (ingredient,))
unit = cursor.fetchall()[0][0]

if (unit == "none") :
    unit = ""

s = str(amount) + " " + unit

#write string to file
f = open("check.txt", "w")    
f.write(s)
    
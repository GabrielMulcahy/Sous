import sys
import sqlite3


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

# Dictionary matching short hand to full english units
unitdict = {
    "g"    : "grams",
    "ml"   : "milliliters",
    "kg"   : "kilograms",
    "l"    : "liters",
    "tsp"  : "teaspoons",
    "tbsp" : "tablespoons"
}

# find all units that can be made
GETALL  = 'SELECT unit FROM Ingredients'
all     = set(cursor.execute(GETALL)) # all units

# take all of the units and add them to one string
s = ""
for u in all :
    # check if the unit has a longhand way of being written
    if str(u[0]) in unitdict :
        s = s + unitdict[str(u[0])] + " " + str(u[0]) + " "
    else :
        s = s + str(u[0]) + " "
    
# write string to file
f = open("units.txt", "w")    
f.write(s)
    
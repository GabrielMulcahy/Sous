import sys
import sqlite3
import random


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

try:
    meals = int(sys.argv[1])
except:
    print("Please provide number of meals")
    sys.exit(1)
    
    
GETALL  = 'SELECT recipe_id FROM Recipes'
all     = set(cursor.execute(GETALL)) # setof all recipe_ids

# take all recipes and add them to a list
recipes = []
for r in all :
    recipes.append(r[0])
  
# put recipes in a random order  
random.shuffle(recipes)

# list with as many recipes as the user requested
result = []
for i in range(0, meals):
    result.append(recipes[i])
    
# open shopping list
list = open("shopping.txt", "a")    

for r in result :
    recipe = open("recipes/" + str(r) + ".txt", "r")
    for line in recipe:
        if "**INGREDIENTS START**" in line :
            for line in recipe:
                if "**INGREDIENTS END**" in line : 
                    break
        
                list.write(line.replace(" -",""))

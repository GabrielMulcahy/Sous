import sys
import sqlite3

# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

rating = sys.argv[1] #rating of recipe

file = open("last_recipe.txt", "r") 
recipe_id = int(file.read(1)) 

print(rating)
print(recipe_id);

#set new rating
RATE = 'UPDATE Recipes SET rating = ? WHERE recipe_id = ?'
cursor.execute(RATE, (rating, recipe_id,))

#update the recipe to be 'rated'
RATED = 'UPDATE Recipes SET rated = ? WHERE recipe_id = ?'
cursor.execute(RATED, (1, recipe_id,))


db.commit()
db.close()

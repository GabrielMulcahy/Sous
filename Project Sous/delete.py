import sqlite3
import os.path



# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

cursor.execute('DELETE FROM Ingredients')
cursor.execute('DELETE FROM Recipes')
cursor.execute('DELETE FROM RecipesIngredients')
cursor.execute('DELETE FROM RecipesTags')
cursor.execute('DELETE FROM Tags')
import sys
import sqlite3

# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

ingredient = sys.argv[1] #name of ingredient
quantity   = int(sys.argv[2]) #amount of ingredient


#get current ingredient amount
GET = 'SELECT amount FROM Ingredients WHERE ingredient_name = ?'
cursor.execute(GET, (ingredient,))
result = cursor.fetchall()
result = int(result[0][0]) #take the result and turn it into a single integer

#set new ingredient amount
amount = quantity + result 
SET = 'UPDATE Ingredients SET amount = ? WHERE ingredient_name = ?'
cursor.execute(SET, (amount, ingredient,))

#get the ingredient's id
ID = 'SELECT ingredient_id FROM Ingredients WHERE ingredient_name = ?'
cursor.execute(ID, (ingredient,))
id = (cursor.fetchall())[0][0]


#update amount in junction table
UPDATE = 'UPDATE RecipesIngredients SET available = ? WHERE ingredient_id = ?'
cursor.execute(UPDATE, (amount, id,))


db.commit()
db.close()

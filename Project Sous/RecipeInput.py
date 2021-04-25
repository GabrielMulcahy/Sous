import sqlite3
import os.path



# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

r = 1 #counter for recipe_id and name of the file

while os.path.isfile("recipes/" + str(r) + '.txt.') :

    file = open("recipes/" + str(r) + ".txt", "r")

    ingredients = []
    tags = []
    
    for line in file:
        if "Title: "       in line: title       = line.replace("Title: ", "")
        if "Time: "        in line: time        = line.replace("Time: ", "")
        if "Calories: "    in line: calories    = line.replace("Calories: ", "")
        if "Serves: "      in line: serves      = line.replace("Serves: ", "")
        
        if "**TAGS START" in line :
        
            for line in file:
            
                if "**TAGS END**" in line :
                    break
                
                t = line.rstrip() #remove new line character
                tags.append(t)    #add to list of ingredients
    
        if "**INGREDIENTS START**" in line :
    
            for line in file:
            
                if "**INGREDIENTS END**" in line : 
                    break
        
                x = line.rstrip()                 #remove new line character
                a = x.split(' ')                  #seperate the ingredient from the quantity
                if len(a) == 4 :                  #check to see if there is a unit attached to the ingredient
                    b = (a[0], int(a[2]), a[3])   #create a tuple of the ingredient, the amount and the unit
                else :
                    b = (a[0], int(a[2]), 'none') #create a tuple of the ingredient name and the amount noting that there is no unit available
                ingredients.append(b)             #add to list of ingredients
                
    #convert values to correct format for entry to the database
    title       = title.rstrip()
    time        = int(time)
    calories    = int(calories)
    serves      = int(serves)
    

    #enter the values into the database
    cursor.execute('''INSERT OR IGNORE INTO Recipes(recipe_id, title, time, calories, serves)
                  VALUES(?,?,?,?,?)''', (r, title, time, calories, serves))
                  
    
    for j in ingredients :
        
        #reset autoincrement for Ingredients table
        cursor.execute('DELETE FROM sqlite_sequence WHERE name = "Ingredients"')
        
        #insert new ingredient
        cursor.execute('INSERT OR IGNORE INTO Ingredients (ingredient_name, unit) VALUES(?,?)', (j[0], j[2]))
               
        #get correct ingredient_id in the case where the ingredient already exists
        GET = 'SELECT ingredient_id FROM Ingredients WHERE ingredient_name = ?'
        cursor.execute(GET, (j[0],))
        id = cursor.fetchall()
        id = id[0][0]
        
        #check if a recipe-ingredient relationship already exists
        CHECK ='SELECT EXISTS(SELECT 1 FROM RecipesIngredients WHERE recipe_id = ? AND ingredient_id =?)'
        cursor.execute(CHECK, (str(r),str(id)))
        check = cursor.fetchall()
        check = check[0][0]
        
        
        #only enter a new relationship if it doesn't already exist
        if check == 0 :    
            cursor.execute('INSERT OR IGNORE INTO RecipesIngredients (recipe_id, ingredient_id, needed) VALUES(?,?,?)', (r, id, j[1]))
        
        
        
    for j in tags :
        
        #reset autoincrement for Ingredients table
        cursor.execute('DELETE FROM sqlite_sequence WHERE name = "Tags"')
        
        #insert new tag
        cursor.execute('INSERT OR IGNORE INTO Tags (tag_name) VALUES(?)', (j,))
        
        #get correct tag_id in the case where the tag already exists
        GET = 'SELECT tag_id FROM Tags WHERE tag_name = ?'
        cursor.execute(GET, (j,))
        id = cursor.fetchall()
        id = id[0][0]
        
        #check if a recipe-tag relationship already exists
        CHECK ='SELECT EXISTS(SELECT 1 FROM RecipesTags WHERE recipe_id = ? AND tag_id =?)'
        cursor.execute(CHECK, (str(r),str(id)))
        check = cursor.fetchall()
        check = check[0][0]
        
        
        if check == 0 : 
            cursor.execute('INSERT OR IGNORE INTO RecipesTags(recipe_id, tag_id) VALUES(?,?)', (r, id))
        
        
                  
    db.commit()
    
    print(title)
    
    r += 1 #increment recipe_id and file number


db.close()



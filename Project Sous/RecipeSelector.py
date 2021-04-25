import sys
import sqlite3


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

#time in minutes and maxiumum number of calories
try:
    time     = int(sys.argv[1])
    calories = int(sys.argv[2])
except:
    print("Please provide tags, time and caloriesl.")
    sys.exit(1)
 
#find all recipes that cannot be made
REXCLUDE = 'SELECT recipe_id FROM RecipesIngredients WHERE available < needed'
cursor.execute(REXCLUDE)
rex = set(cursor.fetchall()) #recipes that cannot be made and must be excluded

#find all recipes that can be made
GETALL  = 'SELECT recipe_id FROM Recipes'
all     = set(cursor.execute(GETALL)) #list of all recipe_ids
rinc    = [] #list of recipes that can be made
for a in all :
    if a not in rex :
        rinc.append(a[0])
        

#find all recipes that don't have too many calories
GETCAL = 'SELECT recipe_id FROM Recipes WHERE calories <= ?'
cals = set(cursor.execute(GETCAL, (calories,)))
calinc = [] #list of recipes that fit calorie criterion
for c in cals :
    calinc.append(c[0])
    

#find all recipes that fit the time constraint
GETTIME = 'SELECT recipe_id FROM Recipes WHERE time <= ?'
times = set(cursor.execute(GETTIME, (time,)))
timeinc = [] #list of recipes that fit the time 
for t in times :
    timeinc.append(t[0])
    
    
#find intersection of all lists
rinc    = set(rinc)   
calinc  = set(calinc)
timeinc = set(timeinc)    
result  = rinc & calinc & timeinc

#weight the results based on the ratings
rated = []
WEIGHT = 'SELECT rating FROM Recipes WHERE recipe_id =?'
for r in result :
    cursor.execute(WEIGHT, (r,))
    rating = cursor.fetchall()[0][0]
    #add them to the list the number of times according to their rating
    for i in range(rating) :
        rated.append(r)

        
#create a string of all of the recipes that can be made
s = ""
for r in rated:
    s = s + str(r) + " "
    
    
#write string to file
f = open("result.txt", "w")    
f.write(s)
    

    

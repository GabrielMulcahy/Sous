import sys
import sqlite3


# Create a database in RAM
db = sqlite3.connect(':memory:')

# Creates or opens a file called database with a SQLite3 DB
db = sqlite3.connect('database.db')

# Get a cursor object
cursor = db.cursor()

#string of comma seperated tags, time in minutes and maxiumum number of calories
try:
    tags     = sys.argv[1]
    time     = int(sys.argv[2])
    calories = int(sys.argv[3])
except:
    print("Please provide tags, time and caloriesl.")
    sys.exit(1)
tags   = tags.split(',')
amount = len(tags) #number of tags supplied, used to determine if a recipe is a complete match or not




#find all tag_ids for provided tags
tag_ids = []
FIND    = 'SELECT tag_id FROM Tags WHERE tag_name = ?'
for t in tags :
    cursor.execute(FIND, (t,))
    id = cursor.fetchall()
    tag_ids.append(id[0][0])
        
#find all recipes that fit the tags
match    = [] #list of recipe_ids, occurring the number of times they match a tag
MATCH    = 'SELECT recipe_id FROM RecipesTags WHERE tag_id = ?' 
for i in tag_ids :
    cursor.execute(MATCH, (i,))
    rec_ids = set(cursor.fetchall()) #all of the recipes that match a single tag
    for r in rec_ids :
        match.append(r[0])
       
#create a list of recipes that match all tags
taginc = [] #list of tags to be included     
for m in match :
    if match.count(m) == amount and m not in taginc :
        taginc.append(m)


        
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
rinc     = set(rinc)
taginc   = set(taginc) 
calinc   = set(calinc)    
timeinc  = set(timeinc)   
result   = rinc & taginc & calinc & timeinc


#weight the results based on the ratings
rated = []
WEIGHT = 'SELECT rating FROM Recipes WHERE recipe_id =?'
for r in result :
    cursor.execute(WEIGHT, (r,))
    rating = cursor.fetchall()[0][0]
    
    for i in range(rating) :
        rated.append(r)
        
    

#create a string of all of the recipes that can be made and ones that fit the tags
s = ""
for r in rated :
    s = s + str(r) + " "

    
#write string to file
f = open("result.txt", "w")    
f.write(s)
    

    

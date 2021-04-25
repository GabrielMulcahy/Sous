import smtplib, ssl, sys


try:
    receiver_email = sys.argv[1]
except:
    print("Please provide an email address")
    sys.exit(1)

port           = 587  # For starttls
smtp_server    = "smtp.gmail.com"
sender_email   = "projectsous@gmail.com"
password       = "Sous1234?"

file         = open("shopping.txt", "r")   
shoppinglist = file.read()
message      = "Subject: Shopping List\n\n" + shoppinglist


context = ssl.create_default_context()
with smtplib.SMTP(smtp_server, port) as server:
    server.ehlo()  # Can be omitted
    server.starttls(context=context)
    server.ehlo()  # Can be omitted
    server.login(sender_email, password)
    server.sendmail(sender_email, receiver_email, message)
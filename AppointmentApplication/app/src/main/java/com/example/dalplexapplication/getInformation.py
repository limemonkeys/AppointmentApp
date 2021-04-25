from bs4 import BeautifulSoup
import requests

# Make a request to https://codedamn-classrooms.github.io/webscraper-python-codedamn-classroom-website/
# Store the result in 'res' variable
'''
res = requests.get(
    'https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=334cbe24-fb36-4d94-84f8-f052f1327a7a')
txt = res.text
status = res.status_code
'''
page = requests.get("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=334cbe24-fb36-4d94-84f8-f052f1327a7a")
soup = BeautifulSoup(page.content, 'html.parser')
'''
get_day = soup.find("label", {"class": "program-schedule-card-header"}) # gets you the text of the <title>(...)</title>

txt = get_day.text
txt = txt .replace("\t", "")
print(txt)

avaiable_spots = soup.find("span", {"class": "pull-right"}) # gets you the text of the <title>(...)</title>
check_appt = avaiable_spots.find("small")

txt = check_appt.text
txt = txt .replace("\t", "")
print(txt)


avaiable_spots = soup.find("div", {"class": "program-schedule-card-caption"}) # gets you the text of the <title>(...)</title>
check_appt = avaiable_spots.find("small")

txt = check_appt.text
txt = txt .replace("\t", "")
print(txt)
'''

print("----------------------")
all_appointments = soup.find_all("div", {"class": "program-schedule-card"}) # gets you the text of the <title>(...)</title>

for appointment in all_appointments:

    get_day = appointment.find("label",
                                  {
                                      "class": "program-schedule-card-header"})  # gets you the text of the <title>(...)</title>

    avaiable_spots = appointment.find("div", {
        "class": "program-schedule-card-caption"})  # gets you the text of the <title>(...)</title>
    check_appt = appointment.find("small")
    txt = check_appt.text
    txt = txt.replace("\t", "")
    txt = txt.replace("\n", "")
    #print(txt)


    txt = appointment.text
    txt = txt.replace("Register", "")
    txt = txt.replace("\t", "")
    txt = txt.replace("\r", "\t")
    txt = txt.replace("\n", "")
    txt = txt.split("\t")
    '''
    print(txt[1])
    print(txt[3])
    print(txt[4])

    print("-------------")
    '''

    if (txt[4] != "No Spots Available"):
        number_spots = txt[4].split(" ")[0]
        print(number_spots + " spot(s) available at " + txt[3] + " on " + txt[1])







I would like to define the purpose of this new project and build a roadmap for it.

the purpose of this project is to handle work hours and have the option to send an excel file with a summary them to a specified list of emails.

for that I need the following features:
1. an option to set the email I'm sending the messages from (gmail)
2. an option to set the list of the recievers.
3. the email should contain the work hours of the current month (from first day until last day).
no data for future days.
4. the work hours can be split on each day.
5. an option to mark a day as a holiday, holdiay evening, or a day off.
6. an option to set the work days, and total work hours of a work day. default is sunday-thursday. 9 hours a day.
7. an option to set how much time I'm suposed to work on holiday evenings.

so for example:
I've worked from 8:00 until 11:00. then from 13:30 until 20:15. and then from 21:45 until 23:30.
I need an option to insert this data. see a total time worked. how much over time or missing time I have (consider day off, holiday evenings, and holidays).





###

1. Date, Day of week, start-end (separated by a comma if you have multiple sessions of work), total hours, comment (day-off/holiday eve/holiday)
2. start stop with an option to correct manually, or just insert it manually from the start.
3. separate. can't be both. in holiday, we are not supposed to work at all. On holiday evenings, we are supposed to work about 6 hours by default, but users can change that.
4. show calculation of each day. and show a total summary of the total time (overtime and missing time complete each other)
5. OAuth2
6. Option to toggle, default is manual. Also an option to set the subject line and body, no default value here.
7. store historical months. possible to view and edit. also an option to resend that month.
8. days of the week that are not set as work days are weekends. a dayoff would be a day I marked as a dayoff (usually taken by an employee to do personal stuff and they get a limited amount of such days).
9. an option to set the deduct break time, default 30 min.
10. full edit delete options. no validations.
11. current design. make it pretty and nice to work with.

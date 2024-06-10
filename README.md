README file for project updates.
Created a Working branch.
Set up Room dependencies in build.gradle app.
Created UI, database, entities, and dao packages. Refactored MainActivity to move into UI package.
Created new activities VacationDetails, VacationList, and ExcursionDetails.
Added an 'add' button to the activity_vacation_list layout, which will allow a user to enter a new vacation.
Added a scroll view in activity_vacation_details layout, which will allow a user to view their vacations.
Added an 'add' button to the activity_vacation_details layout, which will allow a user to enter a new excursion.
Mapped each activity to a parent activity to allow the user to go back from the app bar.
Created a menu resource in res and added items for user navigation functionality.
Created a database and repository. Created a dao package with Excursion and Vacation DAOs.
Added sample data to test database.
Created new vacation_list_item and excursion_list_item layouts to use for recycler view.
Added recycler view to activity_vacation_details and activity_vacation_list.
Created VacationAdapter class to implement view holders.
Added code to see recycler view in VacationList class.
Added code to see recycler view in VacationDetails class.
Added code to save new vacations and update current vacations.
Created a new menu_excursion_details to provide Save, Share, and Notify capability.
Created ExcursionAdapter class to implement view holders.
Created a MyReceiver broadcaster to create and send notifications.
Added code to ExcursionDetails class to pick a date for the excursion.
Generated a random int to pass in the Intent "notify", instead of starting from 0.
Added code to have the DatePicker start from the current date, instead of a hard-coded date.
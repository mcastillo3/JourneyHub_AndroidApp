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
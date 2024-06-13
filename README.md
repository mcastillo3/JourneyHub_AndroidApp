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
Display a detailed view of the vacation, including all vacation details. View can be used to add and update vacation information.
Added validation to the vacation end date to check it is after the start date.
Added notification and alarm trigger to VacationDetails to allow user to set start and end date notification.
Added validation to start date notification.
Added a sharing feature so the user can share all vacation details via a sharing feature.
Added notification and alarm trigger to ExcursionDetails to allow user to set start date notification.
Added validation to excursion date to ensure it is between the start and end of the vacation dates.
Created options_menu and added SearchView to app bar.
Created searchable xml file. Added searchable activity to Manifest.
Added searchview to activity list menu. Created VacationView Model to handle search functionality and displaying vacations.
Optimized code for "adding samples" in VacationList.
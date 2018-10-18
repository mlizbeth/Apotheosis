## Apotheosis
Apotheosis is a custom in-house application designed to replace our current software solution, Sierra.
Apotheosis aims to be more robust and user friendly than Sierra while including all previous functionality 
and various improvements/bug fixes.

This is a web app so no information is stored on the local machine. Database written in mySQL to learn the basics, but considering moving to a noSQL to store POJOs.

## Features
Check-ins, Check-outs, Renewals and Catalogging.

### Check-Ins
Simply scan the item barcode and the system will make the changes for you.

### Check-Outs
Scan the tiger card, the system checks to see if they exist in the database, if not you will be asked to create a new record for them.
Once the patron is verified you scan the item you want to check out.
From there simply select a due date (logic has been added to prevent assigning a due date to a past date)

### Renewals
Just scan the item barcode and select a new due date! (Logic prevents assigning a date that has already passed)

### Catalogging
Quickly see a list of all of the items in the database, sorted by: Name, Type, Barcode

## Bugs
?

## TODO
Themeing

Patron Catalogging

Scheduling System for employees/supervisors

Time tracking system

Create a record that keeps a list of every item a patron has ever checked out (purge after 6months if the item has been returned)

Create a record that keeps a list of every patron an item has been checked out to (purge after 6months if the item has been returned)

Query the database to run and generate reports that can be exported to a spreadsheet, csv, google drive, etc.

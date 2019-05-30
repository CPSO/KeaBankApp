# KeaBankApp
This is an Android App for a fictional bank, that allows users to create bank accounts, manage funds and pay bills. All connected to the Firebase Firestore. 

## Usages
### Firestore
The App uses the Firestore to store, manage and control users and data. This means that in order for the app to work, an internet connection is required. 
#### Use case
* When you open the app you are presented with a login page.
  * A default user information is already inserted in the email and password fields for quick login
  * If no user is available you can create a new account.
    * Filling out the form with the required information will create a new account
    * if you forgot your password you can request a new one
* The Main Activity page (after login) shows a list of all the users accounts with balance and account type
  * Pressing on one of the accounts will bring you to that accounts detail activity
  * Pressing the top menu will show a menu with links to different activities
  * Pressing the FOB will take the user to createAccount Activity
* The account details page will show a list of transactions and allow the user to deposit or transfer money
  * Deposit money prompts the user for an amount and a submit button
* The Transfer Activity allows the user to either send money between accounts or other users using the app.
  * The activity has a switch to switch between modes.
  

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
  * Each deposit or transfer action will updates the accounts transaction history
* On the main activity menu bar will you find the Bills tap, this activity allows you to pay a bill and sign up for monthly payment
 * The Bill activity shows a list of Auto payments
 * Pressing the FAB will take you to the make payment activity
* The Bill Payment Activity allows the user to make a payment to a account.
  * The user writes a name for the payment to identify it for later use
  * Date for payment 
  * The amount
  * What account
   * A balance is shown for the selected account
  * The account numbe to recive the payment
  * Last the user can select if they want automate payment.
 * Based on the date selected, if it is the current day, the money will get payed when the submit is pressed, it will wait until the day set is the current day.

## Automated Payment
This app features a semi automatic payment system, on payments that has the satus isPayed = false.
When the user lands on the main activity page, a method is called to get all payments that has not been payed.
It then checks the payment date on the bills to see if the date is the current date. if not nothing happens.
If the paydate is the current date, a payment is made and a transaction history is made.
It then checks to see if the payment is a auto payment, if not nothing happens and it stops.
If it is a auto payment, it setes the next payment to next month. and setes isPayed = true.

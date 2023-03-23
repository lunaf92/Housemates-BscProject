package x.lunacode.housemates.use_cases

import x.lunacode.housemates.use_cases.group.AddGroup
import x.lunacode.housemates.use_cases.group.DeleteGroup
import x.lunacode.housemates.use_cases.group.GetGroups
import x.lunacode.housemates.use_cases.inventory.*
import x.lunacode.housemates.use_cases.login.AddUser
import x.lunacode.housemates.use_cases.login.GetUser
import x.lunacode.housemates.use_cases.login.LoginWithGoogle
import x.lunacode.housemates.use_cases.transaction.*


data class UseCases(
    val addItem: AddItem,
    val decreaseQty: DecreaseQty,
    val deleteItem: DeleteItem,
    val getItems: GetItems,
    val increaseQty: IncreaseQty,

    val addGroup: AddGroup,
    val deleteGroup: DeleteGroup,
    val getGroups: GetGroups,

    val loginWithGoogle: LoginWithGoogle,
    val addUser: AddUser,
    val getUser: GetUser,

    val changeUserBalance: ChangeUserBalance,
    val addExpenseToFirestore: AddExpenseToFirestore,
    val getTransactions: GetTransactions,
    val getUsersInGroup: GetUsersInGroup,
    val addPaymentToFirestore: AddPaymentToFirestore
)

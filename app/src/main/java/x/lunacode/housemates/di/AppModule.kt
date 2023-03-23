package x.lunacode.housemates.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import x.lunacode.housemates.data.repositories.AuthRepository
import x.lunacode.housemates.data.repositories.GroupRepository
import x.lunacode.housemates.data.repositories.ItemsRepository
import x.lunacode.housemates.data.repositories.TransactionRepository
import x.lunacode.housemates.data.repositoryImpl.AuthRepositoryImplementation
import x.lunacode.housemates.data.repositoryImpl.GroupRepositoryImplementation
import x.lunacode.housemates.data.repositoryImpl.ItemsRepositoryImplementation
import x.lunacode.housemates.data.repositoryImpl.TransactionsRepositoryImplementation
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.use_cases.group.AddGroup
import x.lunacode.housemates.use_cases.group.DeleteGroup
import x.lunacode.housemates.use_cases.group.GetGroups
import x.lunacode.housemates.use_cases.inventory.*
import x.lunacode.housemates.use_cases.login.AddUser
import x.lunacode.housemates.use_cases.login.GetUser
import x.lunacode.housemates.use_cases.login.LoginWithGoogle
import x.lunacode.housemates.use_cases.transaction.*


@Module
@ExperimentalCoroutinesApi
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideInventoryRepository(
        db: FirebaseFirestore
    ): ItemsRepository = ItemsRepositoryImplementation(db)

    @Provides
    fun provideGroupsRepository(
        db: FirebaseFirestore
    ): GroupRepository = GroupRepositoryImplementation(db)

    @Provides
    fun provideAuthRepository(db: FirebaseFirestore): AuthRepository =
        AuthRepositoryImplementation(db)

    @Provides
    fun provideTransactionRepository(db: FirebaseFirestore): TransactionRepository =
        TransactionsRepositoryImplementation(db)

    @Provides
    fun provideUseCases(
        inventoryRepository: ItemsRepository,
        authRepository: AuthRepository,
        groupRepository: GroupRepository,
        transactionRepository: TransactionRepository
    ) = UseCases(
        getItems = GetItems(repository = inventoryRepository),
        addItem = AddItem(repository = inventoryRepository),
        decreaseQty = DecreaseQty(repository = inventoryRepository),
        deleteItem = DeleteItem(repository = inventoryRepository),
        increaseQty = IncreaseQty(repository = inventoryRepository),
        loginWithGoogle = LoginWithGoogle(repository = authRepository),
        addUser = AddUser(repository = authRepository),
        getUser = GetUser(repository = authRepository),
        addGroup = AddGroup(repository = groupRepository),
        deleteGroup = DeleteGroup(repository = groupRepository),
        getGroups = GetGroups(repository = groupRepository),
        changeUserBalance = ChangeUserBalance(repository = transactionRepository),
        addExpenseToFirestore = AddExpenseToFirestore(repository = transactionRepository),
        getTransactions = GetTransactions(repository = transactionRepository),
        getUsersInGroup = GetUsersInGroup(repository = transactionRepository),
        addPaymentToFirestore = AddPaymentToFirestore(repository = transactionRepository),
    )
}
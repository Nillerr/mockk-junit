# MockK for JUnit 5

Provides the `@MockKTest` annotation for running `checkUnnecessaryStub()` on all [MockK](https://mockk.io/) mock 
instances stored in the member properties of the test class the annotation is present on.

## Installation

```kotlin
dependencies {
    implementation("io.github.nillerr:mockk-junit5:1.0.0")
}
```

## Usage

Annotate a test class with the `@MockKTest` annotation:

```kotlin
@MockKTest
class UserServiceTests {
    // Mocks
    private val repository: UserRepository = mockk()
    
    // SUT
    private val service = UserService(repository)
    
    @Test
    fun test() {
        // Given
        val id = "5"
        val account = AccountRecords.default
        every { repository.find(id) }.returnsMany(account)
        
        // When
        val result = service.get(id)
        
        // Then
        assertEquals(result).isEqualTo(Accounts.default)
    }
}
```

JUnit will now call `checkUnnecessaryStub()` on the `UserRepository` after every test.

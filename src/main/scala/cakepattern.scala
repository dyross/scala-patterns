object cake {

    trait UserServiceComponent {
        val userService: UserService

        trait UserService {
            def deleteUser(id: String)
        }
    }

    trait RealUserServiceComponent extends UserServiceComponent {
        self: MySqlComponent => // needs some mysql client
        override val userService: UserService = new UserService {
            override def deleteUser(id: String) {
                // has access to mysqlClient in the component
                mysqlClient.deleteById(id)
            }
        }
    }

    trait MySqlComponent {
        val mysqlClient: MySqlClient

        trait MySqlClient {
            def deleteById(id: String)
        }
    }

    trait RealMySqlComponent extends MySqlComponent {
        override val mysqlClient: MySqlClient = new MySqlClient {
            override def deleteById(id: String) {
                // whatever
            }
        }
    }

    //object broken extends RealUserServiceComponent // does not compile
    
    object real extends RealUserServiceComponent with RealMySqlComponent

    object app {
        real.userService.deleteUser("dave")
    }

    object fake extends RealUserServiceComponent with MySqlComponent {
        override val mysqlClient = sys.error("mock me!")
    }

    object test {
        fake.userService.deleteUser("jieren")
    }
}

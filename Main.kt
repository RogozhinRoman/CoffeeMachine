import java.util.*

fun main() {
    val coffeeMachine = CoffeeMachine()
    val scanner = Scanner(System.`in`)

    do {
        val result = coffeeMachine.tryHandleInput(scanner.next())
    } while (result)
}

enum class CoffeeType(val value: Int, val water: Int, val milkMl: Int, val beansGr: Int) {
    Espresso(1, 250, 0, 16),
    Latte(2, 350, 75, 20),
    Cappuccino(3, 200, 100, 12),
}

enum class CoffeeMachineState {
    BUYING,
    FILLING_WATER,
    FILLING_MILK,
    FILLING_BEANS,
    FILLING_CUPS,
    TAKING,
    REMAINING,
    EXIT,
    NONE
}

class CoffeeMachine {
    init {
        println("Write action (buy, fill, take, remaining, exit):")
    }

    private var water: Int = 400
    private var milkMl: Int = 540
    private var beansGr: Int = 120
    private var cups: Int = 9
    private var money: Int = 550
    private var state = CoffeeMachineState.NONE

    fun tryHandleInput(input: String): Boolean {
        state = when (state) {
            CoffeeMachineState.NONE -> processMainMenu(input)
            CoffeeMachineState.BUYING -> handleBuyAction(input)
            CoffeeMachineState.FILLING_WATER -> fillWater(input)
            CoffeeMachineState.FILLING_MILK -> fillMilk(input)
            CoffeeMachineState.FILLING_BEANS -> fillBeans(input)
            CoffeeMachineState.FILLING_CUPS -> fillCups(input)
            else -> CoffeeMachineState.NONE
        }
        printMenu(state)

        return state != CoffeeMachineState.EXIT
    }

    private fun fillCups(input: String): CoffeeMachineState {
        cups += input.toInt()
        return CoffeeMachineState.NONE
    }

    private fun fillBeans(input: String): CoffeeMachineState {
        beansGr += input.toInt()
        return CoffeeMachineState.FILLING_CUPS
    }

    private fun fillMilk(input: String): CoffeeMachineState {
        milkMl += input.toInt()
        return CoffeeMachineState.FILLING_BEANS
    }

    private fun fillWater(input: String): CoffeeMachineState {
        water += input.toInt()
        return CoffeeMachineState.FILLING_MILK
    }

    private fun printMenu(currentState: CoffeeMachineState) {
        when (currentState) {
            CoffeeMachineState.NONE -> println("Write action (buy, fill, take, remaining, exit):")
            CoffeeMachineState.BUYING -> println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:")
            CoffeeMachineState.FILLING_WATER -> println("Write how many ml of water do you want to add:")
            CoffeeMachineState.FILLING_MILK -> println("Write how many ml of milk do you want to add:")
            CoffeeMachineState.FILLING_BEANS -> println("Write how many grams of coffee beans do you want to add:")
            CoffeeMachineState.FILLING_CUPS -> println("Write how many disposable cups of coffee do you want to add:")
            CoffeeMachineState.TAKING -> println("I gave you $$money")
            CoffeeMachineState.REMAINING -> printCurrentState()
            CoffeeMachineState.EXIT -> println()
        }
    }

    private fun processMainMenu(input: String): CoffeeMachineState {
        return when (input) {
            "back" -> CoffeeMachineState.NONE
            "buy" -> CoffeeMachineState.BUYING
            "fill" -> CoffeeMachineState.FILLING_WATER
            "take" -> {
                printMenu(CoffeeMachineState.TAKING)
                money = 0
                return CoffeeMachineState.NONE
            }
            "remaining" -> {
                printMenu(CoffeeMachineState.REMAINING)
                return CoffeeMachineState.NONE
            }
            "exit" -> {
                printMenu(CoffeeMachineState.EXIT)
                return CoffeeMachineState.EXIT
            }
            else -> {
                throw Exception("Unexpected input")
            }
        }
    }

    private fun handleBuyAction(userInput: String): CoffeeMachineState {
        if (userInput != "back" && userInput.toIntOrNull() != null) {
            val coffeeType = CoffeeType.values().find { it.value == userInput.toInt() }
                ?: throw Exception("Unexpected input")
            val result = makeCoffeeAndGetResult(coffeeType)
            println(result)
        }

        return CoffeeMachineState.NONE
    }

    private fun printCurrentState() {
        println("The coffee machine has:")
        println("$water ml of water")
        println("$milkMl ml of milk")
        println("$beansGr g of coffee beans")
        println("$cups disposable cups")
        println("$money of money")
    }

    private fun canMakeCoffee(coffeeType: CoffeeType): Boolean {
        return haveEnoughResources(coffeeType)
    }

    private fun makeCoffeeAndGetResult(coffeeType: CoffeeType): String {
        if (canMakeCoffee(coffeeType)) {
            when (coffeeType) {
                CoffeeType.Cappuccino -> makeCoffee(coffeeType, 6)
                CoffeeType.Latte -> makeCoffee(coffeeType, 7)
                CoffeeType.Espresso -> makeCoffee(coffeeType, 4)
            }

            return "I have enough resources, making you a coffee!"
        }

        val insufficientResource = getInsufficientResource(coffeeType)
        return "Sorry, not enough $insufficientResource!"
    }

    private fun haveEnoughResources(coffeeType: CoffeeType): Boolean {
        return getInsufficientResource(coffeeType) == null
    }

    private fun getInsufficientResource(coffeeType: CoffeeType): String? {
        if (water < coffeeType.water) {
            return "water"
        } else if (milkMl < coffeeType.milkMl) {
            return "milk"
        } else if (beansGr < coffeeType.beansGr) {
            return "beans"
        } else if (cups < 1) {
            return "cups"
        }

        return null
    }

    private fun makeCoffee(coffeeType: CoffeeType, price: Int) {
        water -= coffeeType.water
        beansGr -= coffeeType.beansGr
        milkMl -= coffeeType.milkMl
        money += price
        cups -= 1
    }
}

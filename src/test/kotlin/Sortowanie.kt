import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.util.*




class Sortowanie {
    enum class Cloth(val cloth: String) {
        SHOES("shoes"),
        SOCKS("socks"),
        GLOVES("gloves"),
        JACKET("jacket"),
        TROUSERS("trousers"),
        PANTS("pants"),
        SHIRT("shirt"),
        VEST("vest"),
    }

    fun sort(map: Map<Cloth, Set<Cloth>>): List<Cloth> {
        return if (map.isEmpty()) {
            emptyList()
        } else {
            val (ready, notReady) = map.entries.partition { entry -> entry.value.isEmpty() }
            val readyToTakeOff = ready.map { it.key }

            val index = readyToTakeOff.toSet()
            val next = notReady.associate { entry -> entry.key to entry.value.minus(index) }

            return readyToTakeOff + sort(next)
        }
    }

    val dees = mapOf(
        Cloth.SOCKS to EnumSet.of(Cloth.SHOES),
        Cloth.GLOVES to emptySet<Cloth>(),
        Cloth.JACKET to emptySet<Cloth>(),
        Cloth.SHOES to emptySet<Cloth>(),
        Cloth.TROUSERS to EnumSet.of(Cloth.SHOES),
        Cloth.PANTS to EnumSet.of(Cloth.TROUSERS),
        Cloth.SHIRT to EnumSet.of(Cloth.VEST),
        Cloth.VEST to EnumSet.of(Cloth.JACKET),
    )



    @Test
    fun name() {
        val sort = sort(dees)

        assertThat(sort.toSet(), Matchers.iterableWithSize(8))

        assertThat(sort.indexOf(Cloth.PANTS), Matchers.greaterThan(sort.indexOf(Cloth.TROUSERS)))
        assertThat(sort.indexOf(Cloth.VEST), Matchers.greaterThan(sort.indexOf(Cloth.JACKET)))
        assertThat(sort.indexOf(Cloth.SHIRT), Matchers.greaterThan(sort.indexOf(Cloth.VEST)))
        assertThat(sort.indexOf(Cloth.SHOES), Matchers.lessThan(sort.indexOf(Cloth.SOCKS)))
        assertThat(sort.indexOf(Cloth.TROUSERS), Matchers.greaterThan(sort.indexOf(Cloth.SHOES)))
    }
}
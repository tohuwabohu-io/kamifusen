package io.tohuwabohu.kamifusen.crud.dto

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Tuple
import java.time.LocalDateTime
import java.util.*

/**
 * Data Transfer Object (DTO) representing a page visit data aggregation of two tables.
 *
 * @property id A unique identifier for the page visit.
 * @property path The URL path of the visited page.
 * @property domain The domain of the visited page, which might be nullable.
 * @property pageAdded The timestamp indicating when the page was added.
 * @property visits The number of visits the page has received.
 */
data class PageVisitDto(
    var id: UUID,
    var path: String,
    var domain: String?,
    var pageAdded: LocalDateTime,
    var visits: Long
)

/**
 * Repository for managing PageVisitDto entities. It does not possess a JPA setup.
 *
 * This repository uses a native query to join the [io.tohuwabohu.kamifusen.crud.Page] and
 * [io.tohuwabohu.kamifusen.PageVisit] entities for receiving a total hit count per page.
 */
@ApplicationScoped
class PageVisitDtoRepository() : PanacheRepository<PageVisitDto> {
    val query = """
            SELECT p.id, p.path, p.pageAdded, COUNT(pv.visitorId), p.domain AS visits
            FROM Page p
            LEFT JOIN PageVisit pv ON p.id = pv.pageId
            GROUP BY p.id
            ORDER BY p.domain, p.path, p.pageAdded DESC
        """

    fun getAllPageVisits() : Uni<List<PageVisitDto>> =
        Panache.getSession().flatMap { session ->
            session.createQuery(query, Tuple::class.java).resultList
                .onItem().transform { it.map ( Tuple::toPageVisitDto ) }
        }

}

/**
 * Extension function to convert a JPA Tuple object to a PageVisitDto.
 *
 * This function assumes that the Tuple contains the following data at the specified positions:
 * - Position 0: The unique identifier of the page visit (UUID).
 * - Position 1: The URL path of the visited page (String).
 * - Position 2: The timestamp indicating when the page was added (LocalDateTime).
 * - Position 3: The number of visits the page has received (Long).
 * - Position 4: The domain of the visited page, which can be nullable (String).
 *
 * @receiver Tuple The JPA Tuple object containing the necessary data.
 * @return PageVisitDto The PageVisitDto object created from the Tuple data.
 */
fun Tuple.toPageVisitDto() = PageVisitDto(
    id = this.get(0, UUID::class.java),
    path = this.get(1, String::class.java),
    pageAdded = this.get(2, LocalDateTime::class.java),
    visits = this.get(3, Long::class.javaObjectType),
    domain = this.get(4, String::class.java)
)